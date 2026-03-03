package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.core.metrics.MetricsCsvWriter;
import sim.model.stores.HoldingPattern;
import sim.model.stores.LinkedListElement;
import sim.model.stores.Aircraft;
import sim.model.stores.List;
import sim.model.stores.Runway;

import java.util.ArrayList;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public final class Engine {


  private ArrayList<DepartureEvent> outboundEvents;
  private int outboundPtr = 0;
  private final HoldingPattern<Aircraft> holdingPattern = new HoldingPattern<>();

  // Runtime runway list built from config:
  private final List<Runway> runways = new List<>();

  // keeps record of landed aircraft if you want
  private final List<Aircraft> postProcessing = new List<>();

  private final ArrayList<Aircraft> generatedDepartures = new ArrayList<>();
  private final List<Aircraft> takeOffQueue = new List<>();
  private int depSeq = 0;

  private ArrayList<ArrivalEvent> inboundEvents;
  private int inboundPtr = 0;

  private final SimConfig cfg;
  private final EngineOptions opts;
  private final SimClock clock;
  private final Random rng;
  private final Metrics metrics = new Metrics();
  private MetricsCsvWriter csv;

  private final RunwayHandling runwayHandling = new RunwayHandling();

  public Engine(SimConfig cfg, EngineOptions opts, SimClock clock) {
    this.cfg = cfg;
    this.opts = opts;
    this.clock = clock;
    this.rng = (opts.seed() == null) ? new Random() : new Random(opts.seed());

    // Build runtime Runway objects from config.
    // Choose a constant service time for now (easy to parameterize later).
    final int DEFAULT_SERVICE_TIME_SECONDS = 60; // 1 minute landing time

    for (SimConfig.RunwayConfig r : cfg.runways) {

      int id;
      try {
        id = Integer.parseInt(r.id.replaceAll("[^0-9]", ""));
      } catch (Exception e) {
        id = runways.getSize() + 1;
      }

      Runway rw = new Runway(id, r.mode, r.status, 60);
      runways.addValue(rw);
    }

    System.out.println("=== RUNWAYS LOADED ===");
    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null) {
        ptr = ptr.getNext();
    }
    System.out.println("=== END RUNWAYS ===");
  }
}

  public void run() {
    System.out.println("Loaded config: runways=" + cfg.runways.size()
        + ", arrivals/hr=" + cfg.arrivalRatePerHour
        + ", departures/hr=" + cfg.departureRatePerHour);
    System.out.println("Options: duration=" + opts.durationSeconds() + "s, dt=" + opts.dtSeconds()
        + "s, speed=" + opts.speedMultiplier() + "x, seed=" + opts.seed());

    inboundEvents = ArrivalSchedule.preGenerateInbound(
        cfg.arrivalRatePerHour,
        opts.durationSeconds(),
        rng
    );
    System.out.println("Pre-generated inbound flights: " + inboundEvents.size());

    System.out.println("=== ALL INBOUND EVENTS (sorted by releaseTimeSeconds) ===");
    for (int i = 0; i < inboundEvents.size(); i++) {
      ArrivalEvent e = inboundEvents.get(i);
      System.out.printf(
          "#%03d callsign=%s target=%ds actual=%.0fs%n",
          i,
          e.aircraft.getCallsign(),
          e.aircraft.getTime().toSecondOfDay(),
          e.releaseTimeSeconds
      );
    }
    System.out.println("=== END INBOUND EVENTS ===");

    outboundEvents = DepartureSchedule.preGenerateOutbound(
      cfg.departureRatePerHour,
      opts.durationSeconds(),
      rng
    );
    System.out.println("Pre-generated outbound flights: " + outboundEvents.size());

    System.out.println("=== ALL OUTBOUND EVENTS (sorted by releaseTimeSeconds) ===");
    for (int i = 0; i < outboundEvents.size(); i++) {
      DepartureEvent e = outboundEvents.get(i);
      System.out.printf(
          "#%03d callsign=%s target=%ds actual=%.0fs%n",
          i,
          e.aircraft.getCallsign(),
          e.aircraft.getTime().toSecondOfDay(),
          e.releaseTimeSeconds
      );
    }
    System.out.println("=== END OUTBOUND EVENTS ===");

    if (opts.csvPath() != null) {
      try {
        csv = new MetricsCsvWriter(opts.csvPath());
        csv.writeHeader();
      } catch (IOException e) {
        throw new RuntimeException("Failed to open CSV: " + opts.csvPath(), e);
      }
    }

    long lastMillis = System.currentTimeMillis();
    double endTime = opts.durationSeconds();
    double nextPrintAt = opts.printEverySeconds();

    while (clock.now() < endTime) {
      long nowMillis = System.currentTimeMillis();
      double realDelta = (nowMillis - lastMillis) / 1000.0;
      lastMillis = nowMillis;

      clock.advanceRealTime(realDelta, opts.speedMultiplier());

      while (clock.hasStep() && clock.now() < endTime) {
        clock.stepOnce();
        step(clock.dt());

        if (clock.now() >= nextPrintAt) {
          printStatus();
          nextPrintAt += opts.printEverySeconds();
          if (csv != null) tryWriteCsvRow();
        }
      }

      try { Thread.sleep(2); } catch (InterruptedException ignored) {}
    }

    printStatus();
    if (csv != null) {
      try { csv.close(); } catch (IOException ignored) {}
      System.out.println("CSV written to: " + opts.csvPath());
    }
    System.out.println("Finished at real time: " + Instant.now());
  }

  private void step(double dt) {
    // 0) update queue metrics each tick (before changes)
    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    // 1) Tick runways
    tickRunways(dt);

    // 2) Release inbound aircraft into holding
    while (inboundPtr < inboundEvents.size()
        && inboundEvents.get(inboundPtr).releaseTimeSeconds <= clock.now()) {

      Aircraft ac = inboundEvents.get(inboundPtr).aircraft;

      System.out.printf("[t=%.0fs] RELEASE to holding: %s (emergency=%s)%n",
          clock.now(),
          ac.getCallsign(),
          ac.getEmergency());

      LinkedListElement<Aircraft> node = new LinkedListElement<>();
      node.setValue(ac);

      int priority = ("None".equals(ac.getEmergency()) ? 0 : 1);
      node.setPriority(priority);

      holdingPattern.add(node);

      metrics.arrivalsGenerated++;
      inboundPtr++;
    }

    // 3) Generate departures BEFORE assigning runways
    // Release outbound aircraft into takeoff queue when their release time is reached
    while (outboundPtr < outboundEvents.size()
        && outboundEvents.get(outboundPtr).releaseTimeSeconds <= clock.now()) {

      Aircraft dep = outboundEvents.get(outboundPtr).aircraft;

      LinkedListElement<Aircraft> node = new LinkedListElement<>();
      node.setValue(dep);

      takeOffQueue.add(node);
      metrics.departuresGenerated++;

      System.out.printf("[t=%.0fs] RELEASE to takeoffQ: %s%n", clock.now(), dep.getCallsign());

      outboundPtr++;
    }

    // 4) Update queue metrics after enqueues
    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    // 5) Assign BOTH inbound + outbound to runways
    runwayHandling.handle(
        holdingPattern,
        takeOffQueue,
        runways,
        postProcessing,
        clock,
        metrics
    );
  }

  private void tickRunways(double dt) {
    int delta = (int) Math.round(dt);
    if (delta <= 0) delta = 1;

    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();

      if (rw != null) {
        boolean freed = rw.tick(delta);
        if (freed) {
          System.out.printf("[t=%.0fs] RUNWAY #%d now free%n", clock.now(), rw.getID());
        }
      }

      ptr = ptr.getNext();
    }
  }

  private void printStatus() {
    System.out.printf("[%s | t=%.0fs] Holding=%d TakeoffQ=%d ArrGen=%d ArrProc=%d DepGen=%d DepProc=%d%n",
    SimClock.formatHHMM(clock.now()), clock.now(),
    holdingPattern.getSize(),
    takeOffQueue.getSize(),
    metrics.arrivalsGenerated, metrics.arrivalsProcessed,
    metrics.departuresGenerated, metrics.departuresProcessed
);
  }

  private void tryWriteCsvRow() {
    // keep queue sizes updated for csv rows too
    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    try {
      csv.writeRow(clock.now(), metrics);
    } catch (IOException e) {
      System.err.println("CSV write failed: " + e.getMessage());
    }
  }

  private Aircraft makeDeparture(double nowSeconds, int seq) {
    // Simple placeholder departure creation (like arrivals did)
    String callsign = "DEP" + (100 + seq);
    String operator = "ZZ";
    String origin = "HOME";
    java.time.LocalTime time = java.time.LocalTime.MIDNIGHT.plusSeconds((long) nowSeconds);
    int altitude = 0;
    int groundSpeed = 0;
    int fuel = 0;
    String emergency = "None";
    return new Aircraft(callsign, operator, origin, time, altitude, groundSpeed, fuel, emergency);
  }
}

