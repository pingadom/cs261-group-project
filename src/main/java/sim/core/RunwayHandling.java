package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.model.stores.*;

public final class RunwayHandling {

  /** keep assigning while there are free runways and waiting planes. */
  public void handle(
      HoldingPattern<Aircraft> holdingPattern,
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics
  ) {
    boolean didSomething = true;
    while (didSomething) {
      didSomething = false;

      // 1) LANDING runways
      didSomething |= assignLandingToMode(holdingPattern, runways, postProcessing, clock, metrics, SimConfig.RunwayMode.LANDING);

      // 2) TAKEOFF runways
      didSomething |= assignTakeoffToMode(takeOffQueue, runways, postProcessing, clock, metrics, SimConfig.RunwayMode.TAKEOFF);

      // 3) MIXED runways (policy: prefer landings, else takeoffs)
      didSomething |= assignMixed(holdingPattern, takeOffQueue, runways, postProcessing, clock, metrics);
    }
  }

  private boolean assignLandingToMode(
      HoldingPattern<Aircraft> holdingPattern,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      SimConfig.RunwayMode mode
  ) {
    if (holdingPattern.getSize() == 0) return false;

    Runway rw = findAvailableRunway(runways, mode);
    if (rw == null) return false;

    LinkedListElement<Aircraft> arrival = holdingPattern.pop();
    postProcessing.add(arrival);

    rw.occupy(arrival.getValue().getCallsign());
    metrics.arrivalsProcessed++;

    System.out.printf("[t=%.0fs] LAND start: %s on runway #%d%n",
        clock.now(), arrival.getValue().getCallsign(), rw.getID());

    return true;
  }

  private boolean assignTakeoffToMode(
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      SimConfig.RunwayMode mode
  ) {
    if (takeOffQueue.getSize() == 0) return false;

    Runway rw = findAvailableRunway(runways, mode);
    if (rw == null) return false;

    LinkedListElement<Aircraft> dep = takeOffQueue.pop(0);
    postProcessing.add(dep);

    rw.occupy(dep.getValue().getCallsign());
    metrics.departuresProcessed++;

    System.out.printf("[t=%.0fs] TOFF start: %s on runway #%d%n",
        clock.now(), dep.getValue().getCallsign(), rw.getID());

    return true;
  }

  private boolean assignMixed(
      HoldingPattern<Aircraft> holdingPattern,
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics
  ) {
    Runway rw = findAvailableRunway(runways, SimConfig.RunwayMode.MIXED);
    if (rw == null) return false;

    // prefer landing first
    if (holdingPattern.getSize() > 0) {
      LinkedListElement<Aircraft> arrival = holdingPattern.pop();
      postProcessing.add(arrival);

      rw.occupy(arrival.getValue().getCallsign());
      metrics.arrivalsProcessed++;

      System.out.printf("[t=%.0fs] LAND start: %s on mixed runway #%d%n",
          clock.now(), arrival.getValue().getCallsign(), rw.getID());
      return true;
    }

    if (takeOffQueue.getSize() > 0) {
      LinkedListElement<Aircraft> dep = takeOffQueue.pop(0);
      postProcessing.add(dep);

      rw.occupy(dep.getValue().getCallsign());
      metrics.departuresProcessed++;

      System.out.printf("[t=%.0fs] TOFF start: %s on mixed runway #%d%n",
          clock.now(), dep.getValue().getCallsign(), rw.getID());
      return true;
    }

    return false;
  }

  private Runway findAvailableRunway(List<Runway> runways, SimConfig.RunwayMode mode) {
    LinkedListElement<Runway> ptr = runways.getHead();
    while (ptr != null) {
      Runway rw = ptr.getValue();
      if (rw != null && rw.getMode() == mode && rw.isAvailableNow()) return rw;
      ptr = ptr.getNext();
    }
    return null;
  }
}