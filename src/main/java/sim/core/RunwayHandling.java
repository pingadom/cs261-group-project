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

  /** Waiting time at which a departure is treated as urgent on mixed runways. */
  private static final int DEPARTURE_URGENT_THRESHOLD_SECONDS = 1500;

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
 * <p>Mixed runways prioritise the longer queue:
 * <ul>
 *   <li>if the holding pattern is larger, assign an arrival,</li>
 *   <li>if the take-off queue is larger, assign a departure,</li>
 *   <li>if both queues are equal, arrivals are preferred as the tie-break.</li>
 * </ul>
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
/**
 * Assigns one aircraft to an available mixed-mode runway.
 *
 * <p>Mixed runways apply urgency-based priority:
 * <ol>
 *   <li>emergency arrivals are always prioritised,</li>
 *   <li>departures waiting at least 1500 seconds are treated as urgent,</li>
 *   <li>otherwise the longer queue is prioritised, with arrivals winning ties.</li>
 * </ol>
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

      int holdingSize = holdingPattern.getSize();
      int takeoffSize = takeOffQueue.getSize();

      if (holdingSize == 0 && takeoffSize == 0) {
          return false;
      }

      // Priority 1: emergency arrivals always first
      if (holdingPattern.getEmergency().getSize() > 0) {
          return assignArrivalFromMixedRunway(
              holdingPattern, rw, postProcessing, clock, metrics, arrivalEventByCallsign
          );
      }

      // Priority 2: urgent departures (waited >= 1500s)
      int urgentDepartureIndex = findUrgentDepartureIndex(
          takeOffQueue, departureEventByCallsign, clock.now()
      );
      if (urgentDepartureIndex >= 0) {
          return assignDepartureFromMixedRunway(
              takeOffQueue, urgentDepartureIndex, rw, postProcessing, clock, metrics, departureEventByCallsign
          );
      }

      // Priority 3: longer queue wins, arrivals win ties
      if (holdingSize >= takeoffSize && holdingSize > 0) {
          return assignArrivalFromMixedRunway(
              holdingPattern, rw, postProcessing, clock, metrics, arrivalEventByCallsign
          );
      }

      if (takeoffSize > 0) {
          return assignDepartureFromMixedRunway(
              takeOffQueue, 0, rw, postProcessing, clock, metrics, departureEventByCallsign
          );
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

  /**
 * Finds the first departure in the take-off queue that has waited at least the urgent threshold.
 *
 * @param takeOffQueue queue of aircraft waiting for take-off
 * @param departureEventByCallsign lookup table of departure events by callsign
 * @param now current simulation time in seconds
 * @return index of the first urgent departure, or {@code -1} if none are urgent
 */
  private int findUrgentDepartureIndex(
      List<Aircraft> takeOffQueue,
      Map<String, DepartureEvent> departureEventByCallsign,
      double now
  ) {
      for (int i = 0; i < takeOffQueue.getSize(); i++) {
          LinkedListElement<Aircraft> node = takeOffQueue.get(i);
          if (node == null || node.getValue() == null) continue;

          Aircraft ac = node.getValue();
          DepartureEvent ev = departureEventByCallsign.get(ac.getCallsign());
          if (ev == null || ev.completed || ev.cancelled) continue;

          double waited = now - ev.releaseTimeSeconds;
          if (waited >= DEPARTURE_URGENT_THRESHOLD_SECONDS) {
              return i;
          }
      }

      return -1;
  }

  /**
 * Assigns the next arrival from the holding pattern onto a mixed runway.
 *
 * @param holdingPattern holding pattern containing inbound aircraft
 * @param rw mixed runway to occupy
 * @param postProcessing list receiving the aircraft after assignment
 * @param clock simulation clock
 * @param metrics simulation metrics to update
 * @param arrivalEventByCallsign lookup table of arrival events by callsign
 * @return {@code true} once assignment succeeds
 */
  private boolean assignArrivalFromMixedRunway(
      HoldingPattern<Aircraft> holdingPattern,
      Runway rw,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      Map<String, ArrivalEvent> arrivalEventByCallsign
  ) {
      if (holdingPattern.getSize() == 0) return false;

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

  /**
 * Assigns a departure from the take-off queue onto a mixed runway.
 *
 * @param takeOffQueue queue of aircraft waiting for take-off
 * @param index index of the departure to remove from the queue
 * @param rw mixed runway to occupy
 * @param postProcessing list receiving the aircraft after assignment
 * @param clock simulation clock
 * @param metrics simulation metrics to update
 * @param departureEventByCallsign lookup table of departure events by callsign
 * @return {@code true} once assignment succeeds
 */
  private boolean assignDepartureFromMixedRunway(
      List<Aircraft> takeOffQueue,
      int index,
      Runway rw,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics,
      Map<String, DepartureEvent> departureEventByCallsign
  ) {
      if (takeOffQueue.getSize() == 0) return false;

      LinkedListElement<Aircraft> dep = takeOffQueue.pop(index);
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
  
}