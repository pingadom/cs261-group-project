package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.model.stores.*;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;

import java.util.Map;

/**
 * Handles runway assignment for arrivals and departures during the simulation.
 *
 * <p>This class is responsible for deciding which aircraft should be moved
 * from the holding pattern or take-off queue onto a runway, based on:
 * <ul>
 *   <li>runway mode,</li>
 *   <li>runway availability,</li>
 *   <li>queue contents,</li>
 *   <li>arrival priority over mixed runways.</li>
 * </ul>
 *
 * <p>The class updates metrics and event tracking data whenever an aircraft
 * starts using a runway.
 */
public final class RunwayHandling {

  /**
   * Repeatedly assigns aircraft to available runways until no further assignment
   * can be made in the current simulation step.
   *
   * <p>The order of assignment is:
   * <ol>
   *   <li>landing-only runways take arrivals,</li>
   *   <li>take-off-only runways take departures,</li>
   *   <li>mixed runways take arrivals first, otherwise departures.</li>
   * </ol>
   *
   * @param holdingPattern holding pattern containing inbound aircraft
   * @param takeOffQueue queue containing outbound aircraft waiting for take-off
   * @param runways list of configured runways
   * @param postProcessing list receiving aircraft once they are assigned to a runway
   * @param clock simulation clock
   * @param metrics simulation metrics to update
   * @param arrivalEventByCallsign lookup table of arrival events by aircraft callsign
   * @param departureEventByCallsign lookup table of departure events by aircraft callsign
   */
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

      didSomething |= assignLandingToMode(
          holdingPattern, runways, postProcessing, clock, metrics,
          SimConfig.RunwayMode.LANDING, arrivalEventByCallsign
      );

      didSomething |= assignTakeoffToMode(
          takeOffQueue, runways, postProcessing, clock, metrics,
          SimConfig.RunwayMode.TAKEOFF, departureEventByCallsign
      );

      didSomething |= assignMixed(
          holdingPattern, takeOffQueue, runways, postProcessing, clock, metrics,
          arrivalEventByCallsign, departureEventByCallsign
      );
    }
  }

  /**
   * Assigns one arriving aircraft to an available runway of the specified landing mode.
   *
   * <p>If both an aircraft and a suitable runway are available, the aircraft is removed
   * from the holding pattern, added to post-processing, placed onto the runway,
   * and its arrival event and metrics are updated.
   *
   * @param holdingPattern holding pattern containing inbound aircraft
   * @param runways list of runways to search
   * @param postProcessing list receiving the aircraft after assignment
   * @param clock simulation clock
   * @param metrics simulation metrics to update
   * @param mode runway mode to match, normally {@code LANDING}
   * @param arrivalEventByCallsign lookup table of arrival events by callsign
   * @return {@code true} if an aircraft was assigned, otherwise {@code false}
   */
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
      ev.fuelOnRunway = arrival.getValue().getFuel();
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

  /**
   * Assigns one departing aircraft to an available runway of the specified take-off mode.
   *
   * <p>If both an aircraft and a suitable runway are available, the aircraft is removed
   * from the take-off queue, added to post-processing, placed onto the runway,
   * and its departure event and metrics are updated.
   *
   * @param takeOffQueue queue of aircraft waiting for take-off
   * @param runways list of runways to search
   * @param postProcessing list receiving the aircraft after assignment
   * @param clock simulation clock
   * @param metrics simulation metrics to update
   * @param mode runway mode to match, normally {@code TAKEOFF}
   * @param departureEventByCallsign lookup table of departure events by callsign
   * @return {@code true} if an aircraft was assigned, otherwise {@code false}
   */
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
      ev.fuelOnRunway = dep.getValue().getFuel();
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

  /**
   * Assigns one aircraft to an available mixed-mode runway.
   *
   * <p>Mixed runways prioritise arrivals first. If no arrivals are waiting,
   * the runway is given to the next departure in the take-off queue.
   *
   * @param holdingPattern holding pattern containing inbound aircraft
   * @param takeOffQueue queue containing outbound aircraft
   * @param runways list of configured runways
   * @param postProcessing list receiving the aircraft after assignment
   * @param clock simulation clock
   * @param metrics simulation metrics to update
   * @param arrivalEventByCallsign lookup table of arrival events by callsign
   * @param departureEventByCallsign lookup table of departure events by callsign
   * @return {@code true} if an aircraft was assigned, otherwise {@code false}
   */
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

    if (holdingPattern.getSize() > 0) {
      LinkedListElement<Aircraft> arrival = holdingPattern.pop();
      postProcessing.add(arrival);

      rw.occupy(arrival.getValue().getCallsign());
      metrics.arrivalsProcessed++;

      ArrivalEvent ev = arrivalEventByCallsign.get(arrival.getValue().getCallsign());
      if (ev != null && !ev.completed) {
        ev.markRunwayTime(clock.now());
        ev.fuelOnRunway = arrival.getValue().getFuel();
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
        ev.fuelOnRunway = dep.getValue().getFuel();
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

  /**
   * Finds the first runway that matches the requested mode and is currently available.
   *
   * @param runways list of runways to search
   * @param mode runway mode to match
   * @return the first available matching runway, or {@code null} if none are available
   */
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