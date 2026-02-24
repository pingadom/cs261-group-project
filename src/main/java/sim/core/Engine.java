package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.core.metrics.MetricsCsvWriter;
import sim.model.stores.HoldingPattern;
import sim.model.stores.LinkedListElement;
import sim.model.stores.Aircraft;
import java.util.ArrayList;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

import sim.core.ArrivalEvent;
import sim.core.ArrivalSchedule;

public final class Engine {

  private final HoldingPattern<Aircraft> holdingPattern = new HoldingPattern<>();
  private ArrayList<ArrivalEvent> inboundEvents;
  private int inboundPtr = 0;

  private final SimConfig cfg;
  private final EngineOptions opts;
  private final SimClock clock;
  private final Random rng;
  private final Metrics metrics = new Metrics();
  private MetricsCsvWriter csv;

  public Engine(SimConfig cfg, EngineOptions opts, SimClock clock) {
    this.cfg = cfg;
    this.opts = opts;
    this.clock = clock;
    this.rng = new Random(opts.seed());
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
    System.out.println("First 10 inbound events (actual seconds):");
    for (int i = 0; i < Math.min(10, inboundEvents.size()); i++) {
        ArrivalEvent e = inboundEvents.get(i);
        System.out.printf("  #%02d %s target=%.0fs actual=%.0fs%n",
                i,
                e.aircraft.getCallsign(),
                e.aircraft.getTime().toSecondOfDay() * 1.0,  // if you stored LocalTime
                e.releaseTimeSeconds
        );
      }

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
    double arrivalsPerSec = cfg.arrivalRatePerHour / 3600.0;
    double depsPerSec = cfg.departureRatePerHour / 3600.0;

    // Release inbound aircraft into holding pattern when their (actual) time is reached
    while (inboundPtr < inboundEvents.size() && inboundEvents.get(inboundPtr).releaseTimeSeconds <= clock.now()) {

      Aircraft ac = inboundEvents.get(inboundPtr).aircraft;
      System.out.printf("[t=%.0fs] RELEASE to holding: %s (emergency=%s)%n",
        clock.now(),
        ac.getCallsign(),
        ac.getEmergency());

      LinkedListElement<Aircraft> node = new LinkedListElement<>();
      node.setValue(ac);

      // priority: emergency=1 else 0 (can improve this later)
      int priority = ("None".equals(ac.getEmergency()) ? 0 : 1);
      node.setPriority(priority);

      holdingPattern.add(node);

      metrics.arrivalsGenerated++;  // now means "arrived into holding"
      inboundPtr++;
    }
  }

  private void printStatus() {
    String hhmm = SimClock.formatHHMM(clock.now());
    System.out.printf("[%s | t=%.0fs] Holding=%d ArrGen=%d ArrProc=%d DepGen=%d DepProc=%d%n",
        SimClock.formatHHMM(clock.now()), clock.now(),
        holdingPattern.getSize(),
        metrics.arrivalsGenerated, metrics.arrivalsProcessed,
        metrics.departuresGenerated, metrics.departuresProcessed
);
  }

  private void tryWriteCsvRow() {
    try {
      csv.writeRow(clock.now(), metrics);
    } catch (IOException e) {
      System.err.println("CSV write failed: " + e.getMessage());
    }
  }
}

