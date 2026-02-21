package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.core.metrics.MetricsCsvWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

public final class Engine {

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

    if (rng.nextDouble() < arrivalsPerSec * dt) metrics.arrivalsGenerated++;
    if (rng.nextDouble() < depsPerSec * dt) metrics.departuresGenerated++;

    long available = cfg.runways.stream().filter(r -> r.status == SimConfig.RunwayStatus.AVAILABLE).count();
    int capacityPerStep = (int) Math.max(1, available);

    int processArr = Math.min(capacityPerStep, metrics.arrivalsGenerated - metrics.arrivalsProcessed);
    int processDep = Math.min(capacityPerStep, metrics.departuresGenerated - metrics.departuresProcessed);

    metrics.arrivalsProcessed += Math.max(0, processArr);
    metrics.departuresProcessed += Math.max(0, processDep);

    metrics.arrivalQueue = (int) (metrics.arrivalsGenerated - metrics.arrivalsProcessed);
    metrics.departureQueue = (int) (metrics.departuresGenerated - metrics.departuresProcessed);
  }

  private void printStatus() {
    String hhmm = SimClock.formatHHMM(clock.now());
    System.out.printf(
        "[%s | t=%.0fs] ArrQ=%d DepQ=%d ArrGen=%d ArrProc=%d DepGen=%d DepProc=%d%n",
        hhmm, clock.now(),
        metrics.arrivalQueue, metrics.departureQueue,
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

