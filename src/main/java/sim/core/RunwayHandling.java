package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.model.stores.*;

import java.util.Map;

public final class RunwayHandling {

  /** keep assigning while there are free runways and waiting planes. */
  public void handle(
      HoldingPattern<Aircraft> holdingPattern,
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      Map<String, ArrivalEvent> arrivalEventByCallsign,
      Map<String, DepartureEvent> departureEventByCallsign
  ) {
    boolean didSomething = true;
    while (didSomething) {
      didSomething = false;

      // 1) LANDING runways
      didSomething |= assignLandingToMode(
          holdingPattern, runways, postProcessing, clock, metrics,
          SimConfig.RunwayMode.LANDING, arrivalEventByCallsign
      );

      // 2) TAKEOFF runways
      didSomething |= assignTakeoffToMode(
          takeOffQueue, runways, postProcessing, clock, metrics,
          SimConfig.RunwayMode.TAKEOFF, departureEventByCallsign
      );

      // 3) MIXED runways (policy: prefer landings, else takeoffs)
      didSomething |= assignMixed(
          holdingPattern, takeOffQueue, runways, postProcessing, clock, metrics,
          arrivalEventByCallsign, departureEventByCallsign
      );
    }
  }

  private boolean assignLandingToMode(
      HoldingPattern<Aircraft> holdingPattern,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      SimConfig.RunwayMode mode,
      Map<String, ArrivalEvent> arrivalEventByCallsign
  ) {
    if (holdingPattern.getSize() == 0) return false;

    Runway rw = findAvailableRunway(runways, mode);
    if (rw == null) return false;

    LinkedListElement<Aircraft> arrival = holdingPattern.pop();
    postProcessing.add(arrival);

    rw.occupy(arrival.getValue().getCallsign());
    metrics.arrivalsProcessed++;

    ArrivalEvent ev = arrivalEventByCallsign.get(arrival.getValue().getCallsign());
    if (ev != null && !ev.completed) {
      ev.markRunwayTime(clock.now());
      metrics.totalArrivalDelaySeconds += ev.delaySeconds;
      metrics.maxArrivalDelaySeconds = Math.max(metrics.maxArrivalDelaySeconds, ev.delaySeconds);
    }

    System.out.printf("[t=%.0fs] LAND start: %s on runway #%d delay=%.0fs%n",
        clock.now(),
        arrival.getValue().getCallsign(),
        rw.getID(),
        (ev != null && ev.delaySeconds != null) ? ev.delaySeconds : 0.0
    );
    return true;
  }

  private boolean assignTakeoffToMode(
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      SimConfig.RunwayMode mode,
      Map<String, DepartureEvent> departureEventByCallsign
  ) {
    if (takeOffQueue.getSize() == 0) return false;

    Runway rw = findAvailableRunway(runways, mode);
    if (rw == null) return false;

    LinkedListElement<Aircraft> dep = takeOffQueue.pop(0);
    postProcessing.add(dep);

    rw.occupy(dep.getValue().getCallsign());
    metrics.departuresProcessed++;

    DepartureEvent ev = departureEventByCallsign.get(dep.getValue().getCallsign());
    if (ev != null && !ev.completed) {
      ev.markRunwayTime(clock.now());
      metrics.totalDepartureDelaySeconds += ev.delaySeconds;
      metrics.maxDepartureDelaySeconds = Math.max(metrics.maxDepartureDelaySeconds, ev.delaySeconds);
    }

    System.out.printf("[t=%.0fs] TOFF start: %s on runway #%d delay=%.0fs%n",
        clock.now(),
        dep.getValue().getCallsign(),
        rw.getID(),
        (ev != null && ev.delaySeconds != null) ? ev.delaySeconds : 0.0
    );
    return true;
  }

  private boolean assignMixed(
      HoldingPattern<Aircraft> holdingPattern,
      List<Aircraft> takeOffQueue,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      Map<String, ArrivalEvent> arrivalEventByCallsign,
      Map<String, DepartureEvent> departureEventByCallsign
  ) {
    Runway rw = findAvailableRunway(runways, SimConfig.RunwayMode.MIXED);
    if (rw == null) return false;

    // prefer landing first
    if (holdingPattern.getSize() > 0) {
      LinkedListElement<Aircraft> arrival = holdingPattern.pop();
      postProcessing.add(arrival);

      rw.occupy(arrival.getValue().getCallsign());
      metrics.arrivalsProcessed++;

      ArrivalEvent ev = arrivalEventByCallsign.get(arrival.getValue().getCallsign());
      if (ev != null && !ev.completed) {
        ev.markRunwayTime(clock.now());
        metrics.totalArrivalDelaySeconds += ev.delaySeconds;
        metrics.maxArrivalDelaySeconds = Math.max(metrics.maxArrivalDelaySeconds, ev.delaySeconds);
      }

      System.out.printf("[t=%.0fs] LAND start: %s on mixed runway #%d delay=%.0fs%n",
          clock.now(),
          arrival.getValue().getCallsign(),
          rw.getID(),
          (ev != null && ev.delaySeconds != null) ? ev.delaySeconds : 0.0
      );
      return true;
    }

    if (takeOffQueue.getSize() > 0) {
      LinkedListElement<Aircraft> dep = takeOffQueue.pop(0);
      postProcessing.add(dep);

      rw.occupy(dep.getValue().getCallsign());
      metrics.departuresProcessed++;

      DepartureEvent ev = departureEventByCallsign.get(dep.getValue().getCallsign());
      if (ev != null && !ev.completed) {
        ev.markRunwayTime(clock.now());
        metrics.totalDepartureDelaySeconds += ev.delaySeconds;
        metrics.maxDepartureDelaySeconds = Math.max(metrics.maxDepartureDelaySeconds, ev.delaySeconds);
      }

      System.out.printf("[t=%.0fs] TOFF start: %s on mixed runway #%d delay=%.0fs%n",
          clock.now(),
          dep.getValue().getCallsign(),
          rw.getID(),
          (ev != null && ev.delaySeconds != null) ? ev.delaySeconds : 0.0
      );
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