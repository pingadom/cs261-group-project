package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.core.metrics.MetricsCsvWriter;
import sim.core.metrics.FlightCsvWriter;
import sim.model.stores.HoldingPattern;
import sim.model.stores.LinkedListElement;
import sim.model.stores.Aircraft;
import sim.model.stores.List;
import sim.model.stores.Runway;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;
import sim.core.events.ArrivalSchedule;
import sim.core.events.DepartureSchedule;
import sim.core.viewmodel.SimState;
import sim.core.viewmodel.RunwayState;

import java.util.ArrayList;
import java.io.IOException;
import java.time.Instant;
import java.util.Random;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

/**
 * Core simulation engine for the airport runway model.
 *
 * <p>This class is responsible for:
 * <ul>
 *   <li>initialising arrival and departure schedules,</li>
 *   <li>advancing simulated time,</li>
 *   <li>releasing aircraft into queues,</li>
 *   <li>updating runway usage,</li>
 *   <li>handling fuel burn, diversions, and cancellations,</li>
 *   <li>recording metrics and CSV outputs,</li>
 *   <li>providing snapshots for the frontend/view layer.</li>
 * </ul>
 *
 * <p>The engine acts as the central coordinator between the clock,
 * generated flight events, runway handling logic, and reporting/output logic.
 */
public final class Engine {

  /** Maximum time a departure may wait in the take-off queue before being cancelled. */
  private static final int TAKEOFF_CANCEL_THRESHOLD_SECONDS = 1800;

  /** Lookup table for arrival events by callsign, used for fast event updates. */
  private final Map<String, ArrivalEvent> arrivalEventByCallsign = new HashMap<>();

  /** Lookup table for departure events by callsign, used for fast event updates. */
  private final Map<String, DepartureEvent> departureEventByCallsign = new HashMap<>();

  /** Writer for per-flight CSV output. */
  private FlightCsvWriter flightCsv;

  /** Pre-generated outbound flight events. */
  private ArrayList<DepartureEvent> outboundEvents;

  /** Pointer to the next outbound event waiting to be released into the queue. */
  private int outboundPtr = 0;

  /** Pre-generated inbound flight events. */
  private ArrayList<ArrivalEvent> inboundEvents;

  /** Pointer to the next inbound event waiting to be released into the holding pattern. */
  private int inboundPtr = 0;

  /** Holding pattern for arriving aircraft, split internally by emergency priority. */
  private final HoldingPattern<Aircraft> holdingPattern = new HoldingPattern<>();

  /** List of available runways in the simulation. */
  private final List<Runway> runways = new List<>();

  /** Aircraft that have finished processing, diverted, or been cancelled. */
  private final List<Aircraft> postProcessing = new List<>();

  /** Queue of aircraft waiting for take-off. */
  private final List<Aircraft> takeOffQueue = new List<>();

  /** Time-series record of delay trend points for reporting and graph output. */
  private final ArrayList<DelayTrendPoint> delayTrend = new ArrayList<>();

  /** Simulation configuration loaded from external config. */
  private final SimConfig cfg;

  /** Runtime options such as duration, speed, CSV path, and random seed. */
  private final EngineOptions opts;

  /** Simulation clock used to control stepped simulation time. */
  private final SimClock clock;

  /** Random generator used for schedule generation. */
  private Random rng;

  /** Current simulation speed multiplier. */
  private volatile double currentSpeedMultiplier;

  /** Flag indicating that a reset has been requested. */
  private volatile boolean resetRequested = false;

  /** Aggregated simulation metrics. */
  private final Metrics metrics = new Metrics();

  /** Writer for metrics CSV output. */
  private MetricsCsvWriter csv;

  /** Runway allocation and handling logic. */
  private final RunwayHandling runwayHandling = new RunwayHandling();

  /**
   * Immutable point used to record average and maximum delay values over time.
   */
  private static final class DelayTrendPoint {
    final double simTime;
    final double avgArrDelay;
    final double maxArrDelay;
    final double avgDepDelay;
    final double maxDepDelay;

    /**
     * Creates a delay trend point.
     *
     * @param simTime simulation time in seconds
     * @param avgArrDelay average arrival delay at this time
     * @param maxArrDelay maximum arrival delay at this time
     * @param avgDepDelay average departure delay at this time
     * @param maxDepDelay maximum departure delay at this time
     */
    DelayTrendPoint(double simTime, double avgArrDelay, double maxArrDelay, double avgDepDelay, double maxDepDelay) {
      this.simTime = simTime;
      this.avgArrDelay = avgArrDelay;
      this.maxArrDelay = maxArrDelay;
      this.avgDepDelay = avgDepDelay;
      this.maxDepDelay = maxDepDelay;
    }
  }

  /**
   * Constructs a new simulation engine.
   *
   * @param cfg simulation configuration including runways and traffic rates
   * @param opts runtime options such as duration, speed, CSV path, and seed
   * @param clock simulation clock used to advance and track simulation time
   */
  public Engine(SimConfig cfg, EngineOptions opts, SimClock clock) {
    this.cfg = cfg;
    this.opts = opts;
    this.clock = clock;
    this.currentSpeedMultiplier = opts.speedMultiplier();
    this.rng = (opts.seed() == null) ? new Random() : new Random(opts.seed());

    rebuildRunwaysFromConfig();
    System.out.println("=== RUNWAYS LOADED ===");
    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null) {
        System.out.printf("Runway #%d code=%s mode=%s status=%s service=%ds%n",
            rw.getID(), rw.getCode(), rw.getMode(), rw.getStatus(), rw.getServiceTimeSeconds());
      }
      ptr = ptr.getNext();
    }
    System.out.println("=== END RUNWAYS ===");
  }

  /**
   * Runs the simulation from start until the configured duration is reached.
   *
   * <p>This method:
   * <ul>
   *   <li>starts the console control thread,</li>
   *   <li>initialises arrival and departure schedules,</li>
   *   <li>opens CSV outputs if enabled,</li>
   *   <li>advances time in real-time scaled steps,</li>
   *   <li>processes simulation steps until the end time.</li>
   * </ul>
   */
  public void run() {
    startConsoleControlThread();
    initialiseSchedules();

    if (opts.csvPath() != null) {
      try {
        csv = new MetricsCsvWriter(opts.csvPath());
        csv.writeHeader();
      } catch (IOException e) {
        throw new RuntimeException("Failed to open CSV: " + opts.csvPath(), e);
      }
    }

    if (opts.csvPath() != null) {
      try {
        Path flightPath = opts.csvPath().resolveSibling("flights.csv");
        flightCsv = new FlightCsvWriter(flightPath);
        flightCsv.writeHeader();
      } catch (IOException e) {
        throw new RuntimeException("Failed to open flight CSV", e);
      }
    }

    long lastMillis = System.currentTimeMillis();
    double endTime = opts.durationSeconds();
    double nextPrintAt = opts.printEverySeconds();

    while (clock.now() < endTime) {
      if (resetRequested) {
        doReset();
        nextPrintAt = opts.printEverySeconds();
        lastMillis = System.currentTimeMillis();
      }

      long nowMillis = System.currentTimeMillis();
      double realDelta = (nowMillis - lastMillis) / 1000.0;
      lastMillis = nowMillis;

      clock.advanceRealTime(realDelta, currentSpeedMultiplier);

      while (clock.hasStep() && clock.now() < endTime) {
        if (resetRequested) break;

        clock.stepOnce();
        step(clock.dt());

        if (clock.now() >= nextPrintAt) {
          printStatus();
          recordDelayTrendPoint();
          nextPrintAt += opts.printEverySeconds();
          if (csv != null) tryWriteCsvRow();
        }
      }

      try { Thread.sleep(2); } catch (InterruptedException ignored) {}
    }

    printStatus();
    recordDelayTrendPoint();

    if (csv != null) {
      try { csv.close(); } catch (IOException ignored) {}
      System.out.println("CSV written to: " + opts.csvPath());
    }

    writeFlightCsvRows();
    if (flightCsv != null) {
      try { flightCsv.close(); } catch (IOException ignored) {}
    }

    writeDelayTrendOutputs();

    System.out.println("Finished at real time: " + Instant.now());
  }

  /**
   * Pre-generates inbound and outbound aircraft schedules for the full simulation duration.
   *
   * <p>The generated events are sorted by release time and stored for later release
   * into the holding pattern or take-off queue as simulation time advances.
   */
  private void initialiseSchedules() {
    inboundEvents = ArrivalSchedule.preGenerateInbound(
        cfg.arrivalRatePerHour,
        opts.durationSeconds(),
        rng
    );

    arrivalEventByCallsign.clear();
    for (ArrivalEvent e : inboundEvents) {
      arrivalEventByCallsign.put(e.aircraft.getCallsign(), e);
    }

    System.out.println("Pre-generated inbound flights: " + inboundEvents.size());
    System.out.println("=== ALL INBOUND EVENTS (sorted by releaseTimeSeconds) ===");
    for (int i = 0; i < inboundEvents.size(); i++) {
      ArrivalEvent e = inboundEvents.get(i);
      System.out.printf("#%03d callsign=%s target=%ds actual=%.0fs%n",
          i, e.aircraft.getCallsign(), e.aircraft.getTime().toSecondOfDay(), e.releaseTimeSeconds);
    }
    System.out.println("=== END INBOUND EVENTS ===");

    outboundEvents = DepartureSchedule.preGenerateOutbound(
        cfg.departureRatePerHour,
        opts.durationSeconds(),
        rng
    );

    departureEventByCallsign.clear();
    for (DepartureEvent e : outboundEvents) {
      departureEventByCallsign.put(e.aircraft.getCallsign(), e);
    }

    System.out.println("Pre-generated outbound flights: " + outboundEvents.size());
    System.out.println("=== ALL OUTBOUND EVENTS (sorted by releaseTimeSeconds) ===");
    for (int i = 0; i < outboundEvents.size(); i++) {
      DepartureEvent e = outboundEvents.get(i);
      System.out.printf("#%03d callsign=%s target=%ds actual=%.0fs%n",
          i, e.aircraft.getCallsign(), e.aircraft.getTime().toSecondOfDay(), e.releaseTimeSeconds);
    }
    System.out.println("=== END OUTBOUND EVENTS ===");
  }

  /**
   * Rebuilds the runway list from the configuration file.
   *
   * <p>If a runway ID cannot be parsed numerically, a fallback sequential ID is used.
   * Each runway is assigned a default service time.
   */
  private void rebuildRunwaysFromConfig() {
    runways.clear();
    final int DEFAULT_SERVICE_TIME_SECONDS = 60;

    for (SimConfig.RunwayConfig r : cfg.runways) {
      int id;
      try {
        id = Integer.parseInt(r.id.replaceAll("[^0-9]", ""));
      } catch (Exception e) {
        id = runways.getSize() + 1;
      }

      Runway rw = new Runway(id, r.id, r.mode, r.status, DEFAULT_SERVICE_TIME_SECONDS);
      runways.addValue(rw);
    }
  }

  /**
   * Executes a single simulation step.
   *
   * <p>This includes:
   * <ul>
   *   <li>updating queue sizes,</li>
   *   <li>ticking runway service timers,</li>
   *   <li>releasing scheduled arrivals and departures,</li>
   *   <li>burning fuel and adjusting altitudes,</li>
   *   <li>cancelling overdue departures,</li>
   *   <li>running runway assignment logic.</li>
   * </ul>
   *
   * @param dt time step in simulation seconds
   */
  private void step(double dt) {
    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    tickRunways(dt);

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

    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    fuelConsumption(holdingPattern, dt, postProcessing);
    adjustAltitude(holdingPattern);

    cancelOverdueDepartures();

    runwayHandling.handle(
        holdingPattern,
        takeOffQueue,
        runways,
        postProcessing,
        clock,
        metrics,
        arrivalEventByCallsign,
        departureEventByCallsign
    );
  }

  /**
   * Cancels departures that have exceeded the maximum allowed waiting time
   * in the take-off queue.
   *
   * <p>Cancelled aircraft are moved to post-processing and their event record
   * is updated accordingly.
   */
  private void cancelOverdueDepartures() {
    int i = 0;
    while (i < takeOffQueue.getSize()) {
      LinkedListElement<Aircraft> node = takeOffQueue.get(i);
      if (node == null || node.getValue() == null) break;

      Aircraft ac = node.getValue();
      DepartureEvent ev = departureEventByCallsign.get(ac.getCallsign());

      if (ev != null && !ev.completed && !ev.cancelled) {
        double waited = clock.now() - ev.releaseTimeSeconds;
        if (waited > TAKEOFF_CANCEL_THRESHOLD_SECONDS) {
          LinkedListElement<Aircraft> removed = takeOffQueue.pop(i);
          ev.markCancelled(clock.now());
          postProcessing.add(removed);
          metrics.departuresCancelled++;

          System.out.printf("[t=%.0fs] CANCEL departure: %s waited=%.0fs%n",
              clock.now(), ac.getCallsign(), waited);
          continue;
        }
      }

      i++;
    }
  }

  /**
   * Reduces the remaining service time of all occupied runways.
   *
   * <p>If a runway completes its current operation during this tick, it becomes free.
   *
   * @param dt time step in seconds
   */
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

  /**
   * Prints a summary of the current simulation state to the console.
   */
  private void printStatus() {
    double avgArrDelay = metrics.arrivalsProcessed == 0 ? 0.0
        : metrics.totalArrivalDelaySeconds / metrics.arrivalsProcessed;
    double avgDepDelay = metrics.departuresProcessed == 0 ? 0.0
        : metrics.totalDepartureDelaySeconds / metrics.departuresProcessed;

    System.out.printf(
        "[ %s | t=%.0fs ] Holding=%d TakeoffQ=%d ArrGen=%d ArrProc=%d ArrDiv=%d DepGen=%d DepProc=%d DepCan=%d AvgArrDelay=%.1fs AvgDepDelay=%.1fs%n",
        SimClock.formatHHMM(clock.now()), clock.now(),
        holdingPattern.getSize(),
        takeOffQueue.getSize(),
        metrics.arrivalsGenerated,
        metrics.arrivalsProcessed,
        metrics.arrivalsDiverted,
        metrics.departuresGenerated,
        metrics.departuresProcessed,
        metrics.departuresCancelled,
        avgArrDelay,
        avgDepDelay
    );
  }

  /**
   * Writes the current metrics row to the metrics CSV file, if enabled.
   */
  private void tryWriteCsvRow() {
    metrics.arrivalQueue = holdingPattern.getSize();
    metrics.departureQueue = takeOffQueue.getSize();

    try {
      csv.writeRow(clock.now(), metrics);
    } catch (IOException e) {
      System.err.println("CSV write failed: " + e.getMessage());
    }
  }

  /**
   * Assigns altitude levels to aircraft in the holding pattern.
   *
   * <p>Emergency aircraft are assigned first, followed by non-emergency aircraft,
   * in 1000-foot vertical separation increments.
   *
   * @param holdingPattern the holding pattern to update
   */
  private void adjustAltitude(HoldingPattern<Aircraft> holdingPattern) {
    int i = 1;

    LinkedListElement<Aircraft> ptr = holdingPattern.getEmergency().getHead();
    while (ptr != null) {
      Aircraft ac = ptr.getValue();
      if (ac != null) ac.setAltitude(i * 1000);
      i++;
      ptr = ptr.getNext();
    }

    ptr = holdingPattern.getNonEmergency().getHead();
    while (ptr != null) {
      Aircraft ac = ptr.getValue();
      if (ac != null) ac.setAltitude(i * 1000);
      i++;
      ptr = ptr.getNext();
    }
  }

  /**
   * Applies fuel burn to aircraft in the holding pattern and handles
   * emergency promotion or diversion when fuel becomes critically low.
   *
   * <p>Aircraft with low fuel are promoted to emergency priority.
   * Aircraft below the diversion threshold are removed from the holding pattern
   * and marked as diverted.
   *
   * @param holdingPattern holding pattern containing inbound aircraft
   * @param dtSeconds elapsed step time in seconds
   * @param postProcessing list receiving diverted aircraft
   */
  private void fuelConsumption(
      HoldingPattern<Aircraft> holdingPattern,
      double dtSeconds,
      List<Aircraft> postProcessing
  ) {
    final int burnPerSecond = 1;
    final int divertThreshold = 600;
    final int promoteThreshold = 1200;

    int burn = (int) Math.max(1, Math.round(dtSeconds * burnPerSecond));

    int i = 0;
    while (i < holdingPattern.getEmergency().getSize()) {
      LinkedListElement<Aircraft> node = holdingPattern.getEmergency().get(i);
      if (node == null || node.getValue() == null) break;

      Aircraft ac = node.getValue();
      ac.setFuel(ac.getFuel() - burn);

      if (ac.getFuel() < divertThreshold) {
        LinkedListElement<Aircraft> removed = holdingPattern.getEmergency().pop(i);
        removed.getValue().setEmergency("Diverted");
        postProcessing.add(removed);
        metrics.arrivalsDiverted++;

        ArrivalEvent ev = arrivalEventByCallsign.get(removed.getValue().getCallsign());
        if (ev != null) ev.diverted = true;

        System.out.printf("[t=%.0fs] DIVERT: %s fuel=%d%n",
            clock.now(),
            removed.getValue().getCallsign(),
            removed.getValue().getFuel());
      } else {
        i++;
      }
    }

    i = 0;
    while (i < holdingPattern.getNonEmergency().getSize()) {
      LinkedListElement<Aircraft> node = holdingPattern.getNonEmergency().get(i);
      if (node == null || node.getValue() == null) break;

      Aircraft ac = node.getValue();
      ac.setFuel(ac.getFuel() - burn);

      if (ac.getFuel() < promoteThreshold) {
        LinkedListElement<Aircraft> removed = holdingPattern.getNonEmergency().pop(i);
        removed.getValue().setEmergency("Fuel");
        removed.setPriority(1);
        holdingPattern.add(removed);
      } else {
        i++;
      }
    }
  }

  /**
   * Writes per-flight output rows for all arrival and departure events.
   *
   * <p>Each row records the flight type, release time, runway time, delay,
   * completion state, outcome, emergency state, and final fuel value.
   */
  private void writeFlightCsvRows() {
    if (flightCsv == null) return;

    try {
      for (ArrivalEvent e : inboundEvents) {
        String outcome;
        if (e.diverted) {
          outcome = "DIVERTED";
        } else if (e.completed) {
          outcome = "LANDED";
        } else {
          outcome = "UNFINISHED";
        }

        flightCsv.writeFlight(
            e.aircraft.getCallsign(),
            "ARRIVAL",
            e.releaseTimeSeconds,
            e.actualRunwayTimeSeconds,
            e.delaySeconds,
            e.completed,
            outcome,
            e.aircraft.getEmergency(),
            e.diverted,
            false,
            e.completed && e.fuelOnRunway != null ? e.fuelOnRunway : e.aircraft.getFuel()
        );
      }

      for (DepartureEvent e : outboundEvents) {
        String outcome;
        if (e.cancelled) {
          outcome = "CANCELLED";
        } else if (e.completed) {
          outcome = "TOOK_OFF";
        } else {
          outcome = "UNFINISHED";
        }

        flightCsv.writeFlight(
            e.aircraft.getCallsign(),
            "DEPARTURE",
            e.releaseTimeSeconds,
            e.actualRunwayTimeSeconds,
            e.delaySeconds,
            e.completed,
            outcome,
            e.aircraft.getEmergency(),
            false,
            e.cancelled,
            e.completed && e.fuelOnRunway != null ? e.fuelOnRunway : e.aircraft.getFuel()
        );
      }
    } catch (IOException e) {
      System.err.println("Flight CSV write failed: " + e.getMessage());
    }
  }

  /**
   * Records a point in the delay trend series for later CSV/SVG output.
   */
  private void recordDelayTrendPoint() {
    double avgArrDelay = metrics.arrivalsProcessed == 0 ? 0.0
        : metrics.totalArrivalDelaySeconds / metrics.arrivalsProcessed;
    double avgDepDelay = metrics.departuresProcessed == 0 ? 0.0
        : metrics.totalDepartureDelaySeconds / metrics.departuresProcessed;

    delayTrend.add(new DelayTrendPoint(
        clock.now(),
        avgArrDelay,
        metrics.maxArrivalDelaySeconds,
        avgDepDelay,
        metrics.maxDepartureDelaySeconds
    ));
  }

  /**
   * Writes delay trend outputs as both CSV data and a simple SVG graph.
   */
  private void writeDelayTrendOutputs() {
    if (opts.csvPath() == null || delayTrend.isEmpty()) return;

    Path csvOut = opts.csvPath().resolveSibling("delay_trend.csv");
    try (var out = Files.newBufferedWriter(csvOut)) {
      out.write("sim_time_s,avg_arrival_delay_s,max_arrival_delay_s,avg_departure_delay_s,max_departure_delay_s\n");
      for (DelayTrendPoint p : delayTrend) {
        out.write(String.format("%.0f,%.2f,%.2f,%.2f,%.2f%n",
            p.simTime, p.avgArrDelay, p.maxArrDelay, p.avgDepDelay, p.maxDepDelay));
      }
    } catch (IOException e) {
      System.err.println("Delay trend CSV write failed: " + e.getMessage());
    }

    Path svgOut = opts.csvPath().resolveSibling("delay_trend.svg");
    try (var out = Files.newBufferedWriter(svgOut)) {
      int width = 900, height = 450, pad = 50;
      double maxTime = delayTrend.get(delayTrend.size() - 1).simTime;
      double maxDelay = 1.0;
      for (DelayTrendPoint p : delayTrend) {
        maxDelay = Math.max(maxDelay, Math.max(Math.max(p.avgArrDelay, p.maxArrDelay), Math.max(p.avgDepDelay, p.maxDepDelay)));
      }

      StringBuilder arrLine = new StringBuilder();
      StringBuilder depLine = new StringBuilder();

      for (DelayTrendPoint p : delayTrend) {
        double x = pad + (p.simTime / Math.max(1.0, maxTime)) * (width - 2.0 * pad);
        double yArr = height - pad - (p.avgArrDelay / maxDelay) * (height - 2.0 * pad);
        double yDep = height - pad - (p.avgDepDelay / maxDelay) * (height - 2.0 * pad);
        arrLine.append(String.format("%.1f,%.1f ", x, yArr));
        depLine.append(String.format("%.1f,%.1f ", x, yDep));
      }

      out.write("<svg xmlns='http://www.w3.org/2000/svg' width='" + width + "' height='" + height + "'>");
      out.write("<rect x='0' y='0' width='" + width + "' height='" + height + "' fill='white'/>");
      out.write("<line x1='" + pad + "' y1='" + (height - pad) + "' x2='" + (width - pad) + "' y2='" + (height - pad) + "' stroke='black'/>");
      out.write("<line x1='" + pad + "' y1='" + pad + "' x2='" + pad + "' y2='" + (height - pad) + "' stroke='black'/>");
      out.write("<polyline fill='none' stroke='blue' stroke-width='2' points='" + arrLine + "'/>");
      out.write("<polyline fill='none' stroke='red' stroke-width='2' points='" + depLine + "'/>");
      out.write("<text x='60' y='25' font-size='14'>Delay Trend</text>");
      out.write("<text x='60' y='45' font-size='12' fill='blue'>Arrival Avg Delay</text>");
      out.write("<text x='220' y='45' font-size='12' fill='red'>Departure Avg Delay</text>");
      out.write("</svg>");
    } catch (IOException e) {
      System.err.println("Delay trend SVG write failed: " + e.getMessage());
    }
  }

  /**
   * Starts a daemon thread that listens for console commands while the simulation runs.
   *
   * <p>Supported commands include pause, resume, reset, speed changes,
   * runway status updates, and runway mode changes.
   */
  private void startConsoleControlThread() {
    Thread t = new Thread(() -> {
      Scanner scanner = new Scanner(System.in);
      while (true) {
        try {
          if (!scanner.hasNextLine()) break;
          String line = scanner.nextLine().trim();
          if (!line.isEmpty()) handleConsoleCommand(line);
        } catch (Exception e) {
          System.err.println("Console control error: " + e.getMessage());
        }
      }
    });
    t.setDaemon(true);
    t.start();
  }

  /**
   * Parses and executes a console command entered by the user.
   *
   * @param line raw command line input
   */
  private void handleConsoleCommand(String line) {
    String[] parts = line.split("\\s+");
    if (parts.length == 0) return;

    switch (parts[0].toLowerCase()) {
      case "pause" -> {
        clock.pause();
        System.out.println("Simulation paused.");
      }
      case "resume" -> {
        clock.resume();
        System.out.println("Simulation resumed.");
      }
      case "reset" -> {
        resetRequested = true;
        System.out.println("Simulation reset requested.");
      }
      case "speed" -> {
        if (parts.length >= 2) {
          try {
            currentSpeedMultiplier = Double.parseDouble(parts[1]);
            System.out.println("Speed set to " + currentSpeedMultiplier + "x");
          } catch (NumberFormatException e) {
            System.out.println("Invalid speed.");
          }
        }
      }
      case "runway" -> {
        if (parts.length >= 3) {
          setRunwayStatus(parts[1], parts[2]);
        } else {
          System.out.println("Usage: runway <id/code> <AVAILABLE|INSPECTION|SNOW|FAILURE>");
        }
      }
      case "mode" -> {
        if (parts.length >= 3) {
          try {
            SimConfig.RunwayMode mode = SimConfig.RunwayMode.valueOf(parts[2].toUpperCase());
            updateRunwayMode(parts[1], mode);
          } catch (IllegalArgumentException e) {
            System.out.println("Invalid runway mode. Use LANDING, TAKEOFF, or MIXED.");
          }
        } else {
          System.out.println("Usage: mode <id/code> <LANDING|TAKEOFF|MIXED>");
        }
      }
      case "show" -> {
        if (parts.length >= 2 && parts[1].equalsIgnoreCase("runways")) {
          printRunways();
        } else {
          System.out.println("Usage: show runways");
        }
      }
      default -> System.out.println("Commands: pause, resume, reset, speed <x>, runway <id/code> <status>, mode <id/code> <mode>");
    }
  }

  /**
   * Updates the status of a runway by ID or code.
   *
   * <p>If a runway is made unavailable, any current operation on it is cleared.
   *
   * @param idOrCode runway numeric ID or code
   * @param statusText new runway status as text
   */
  private void setRunwayStatus(String idOrCode, String statusText) {
    SimConfig.RunwayStatus status;
    try {
      status = SimConfig.RunwayStatus.valueOf(statusText.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid runway status.");
      return;
    }

    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null) {
        boolean match = rw.getCode().equalsIgnoreCase(idOrCode)
            || Integer.toString(rw.getID()).equals(idOrCode);
        if (match) {
          rw.setStatus(status);
          if (status != SimConfig.RunwayStatus.AVAILABLE) {
            rw.clearCurrentOperation();
          }
          System.out.printf("Runway %s (#%d) status set to %s%n", rw.getCode(), rw.getID(), status);
          return;
        }
      }
      ptr = ptr.getNext();
    }

    System.out.println("Runway not found.");
  }

  /**
   * Resets the simulation back to its initial state.
   *
   * <p>This clears queues, resets counters, rebuilds runways, recreates schedules,
   * and restores the random generator state.
   */
  private void doReset() {
    resetRequested = false;

    clock.reset();

    holdingPattern.getEmergency().clear();
    holdingPattern.getNonEmergency().clear();
    takeOffQueue.clear();
    postProcessing.clear();

    inboundPtr = 0;
    outboundPtr = 0;

    metrics.arrivalQueue = 0;
    metrics.departureQueue = 0;
    metrics.arrivalsGenerated = 0;
    metrics.departuresGenerated = 0;
    metrics.arrivalsProcessed = 0;
    metrics.departuresProcessed = 0;
    metrics.arrivalsDiverted = 0;
    metrics.departuresCancelled = 0;
    metrics.totalArrivalDelaySeconds = 0.0;
    metrics.totalDepartureDelaySeconds = 0.0;
    metrics.maxArrivalDelaySeconds = 0.0;
    metrics.maxDepartureDelaySeconds = 0.0;

    delayTrend.clear();

    rng = (opts.seed() == null) ? new Random() : new Random(opts.seed());
    rebuildRunwaysFromConfig();
    initialiseSchedules();

    System.out.println("Simulation reset.");
  }

  /** Pauses the simulation clock. */
  public void pauseSimulation() {
    clock.pause();
  }

  /** Resumes the simulation clock. */
  public void resumeSimulation() {
    clock.resume();
  }

  /** Requests that the simulation be reset on the next main loop cycle. */
  public void requestReset() {
    resetRequested = true;
  }

  /**
   * Updates the simulation speed multiplier.
   *
   * @param speed new speed multiplier; must be greater than zero
   */
  public void setSimulationSpeed(double speed) {
    if (speed > 0) {
      currentSpeedMultiplier = speed;
    }
  }

  /**
   * Updates a runway's operational status.
   *
   * @param idOrCode runway numeric ID or code
   * @param status new status to apply
   */
  public void updateRunwayStatus(String idOrCode, SimConfig.RunwayStatus status) {
    setRunwayStatus(idOrCode, status.name());
  }

  /**
   * Updates a runway's mode if the runway is currently idle.
   *
   * <p>Mode changes are blocked while a runway is occupied to avoid
   * changing its operating rules mid-operation.
   *
   * @param idOrCode runway numeric ID or code
   * @param mode new runway mode
   */
  public void updateRunwayMode(String idOrCode, SimConfig.RunwayMode mode) {
    LinkedListElement<Runway> ptr = runways.getHead();

    while (ptr != null) {
      Runway rw = ptr.getValue();

      if (rw != null) {
        boolean match = rw.getCode().equalsIgnoreCase(idOrCode)
            || Integer.toString(rw.getID()).equals(idOrCode);

        if (match) {
          if (!rw.isIdle()) {
            System.out.printf(
                "Cannot change mode of runway %s (#%d) while occupied by %s%n",
                rw.getCode(), rw.getID(), rw.getOccupied()
            );
            return;
          }

          rw.setMode(mode);
          System.out.printf(
              "[t=%.0fs] RUNWAY %s (#%d) mode set to %s%n",
              clock.now(), rw.getCode(), rw.getID(), mode
          );
          return;
        }
      }

      ptr = ptr.getNext();
    }

    System.out.println("Runway not found.");
  }

  /**
   * Creates a snapshot of the current simulation state for the frontend/view layer.
   *
   * <p>The snapshot includes time information, queue contents, runway states,
   * pre-generated event lists, processed aircraft, event lookup maps, and copied metrics.
   *
   * @return immutable-style view of the current simulation state
   */
  public synchronized SimState snapshot() {
    java.util.List<RunwayState> runwayStates = new ArrayList<>();
    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null) {
        runwayStates.add(new RunwayState(
            rw.getID(),
            rw.getCode(),
            rw.getMode(),
            rw.getStatus(),
            rw.getOccupied(),
            rw.getTimeRemaining()
        ));
      }
      ptr = ptr.getNext();
    }

    java.util.List<Aircraft> postProcessedAircraft = new ArrayList<>();
    LinkedListElement<Aircraft> aptr = postProcessing.getHead();
    while (aptr != null) {
      Aircraft ac = aptr.getValue();
      if (ac != null) postProcessedAircraft.add(ac);
      aptr = aptr.getNext();
    }

    Metrics m = new Metrics();
    m.arrivalQueue = metrics.arrivalQueue;
    m.departureQueue = metrics.departureQueue;
    m.arrivalsGenerated = metrics.arrivalsGenerated;
    m.departuresGenerated = metrics.departuresGenerated;
    m.arrivalsProcessed = metrics.arrivalsProcessed;
    m.departuresProcessed = metrics.departuresProcessed;
    m.arrivalsDiverted = metrics.arrivalsDiverted;
    m.departuresCancelled = metrics.departuresCancelled;
    m.totalArrivalDelaySeconds = metrics.totalArrivalDelaySeconds;
    m.totalDepartureDelaySeconds = metrics.totalDepartureDelaySeconds;
    m.maxArrivalDelaySeconds = metrics.maxArrivalDelaySeconds;
    m.maxDepartureDelaySeconds = metrics.maxDepartureDelaySeconds;

    return new SimState(
        clock.now(),
        SimClock.formatHHMM(clock.now()),
        clock.isPaused(),
        currentSpeedMultiplier,
        holdingPattern,
        takeOffQueue,
        holdingPattern.getSize(),
        takeOffQueue.getSize(),
        new ArrayList<>(runwayStates),
        inboundEvents == null ? new ArrayList<>() : new ArrayList<>(inboundEvents),
        outboundEvents == null ? new ArrayList<>() : new ArrayList<>(outboundEvents),
        postProcessedAircraft,
        new HashMap<>(arrivalEventByCallsign),
        new HashMap<>(departureEventByCallsign),
        m
    );
  }

  /**
   * Prints the current state of every runway to the console.
   */
  private void printRunways() {
    System.out.println("=== RUNWAY STATE ===");
    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null) {
        System.out.printf(
            "%s (#%d) mode=%s status=%s occupied='%s' remaining=%ds%n",
            rw.getCode(),
            rw.getID(),
            rw.getMode(),
            rw.getStatus(),
            rw.getOccupied(),
            rw.getTimeRemaining()
        );
      }
      ptr = ptr.getNext();
    }
    System.out.println("=== END RUNWAY STATE ===");
  }
}