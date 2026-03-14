package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.model.stores.*;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;



import java.util.Map;

public final class RunwayHandling {

  /** Assigns aircraft to runway if there are aircraft/runways available.
   * Repeats this process until no more aircraft/available runways remain.
   * Priority is given to assigning single mode runways to prevent process starvation.
   * 
   * @param holdingPattern FIFO data structure holding aircraft waiting to land
   * @param takeOffQueue FIFO data structure holding all aircraft waiting to take off
   * @param runways dynamic data structure holding all runways within the simulation
   * @param postProcessing store of all departed, arrived, cancelled, or diverted planes
   * @param clock simulation clock
   * @param metrics store of all information regarding how many planes exist/have taken off/have departed etc.
   * @param arrivalEventByCallsign mapping of aircraft callsign to an event representing its moment of arrival
   * @param departureEventByCallsign mapping of aircraft callsign to to an event representing its moment of landing
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
   * Assigns the next aircraft from the holding pattern to a runway
   * If there exists an aircraft and an available landing-mode runway
   * 
   * @param holdingPattern FIFO data structure holding aircraft waiting to land
   * @param runways dynamic data structure holding all runways within the simulation
   * @param postProcessing store of all departed, arrived, cancelled, or diverted planes
   * @param clock simulation clock
   * @param metrics store of all information regarding how many planes exist/have taken off/have departed etc.
   * @param mode enum representing what mode the runway is in
   * @param arrivalEventByCallsign mapping of aircraft callsign to an event representing its moment of arrival
   * 
   * @return true if an aircraft is assigned a runway, false otherwise
   * 
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
   * Assigns the next aircraft from the take-off queue to a runway
   * If there exists an aircraft and an available landing-mode runway
   * 
   * @param takeOffQueue FIFO data structure holding aircraft waiting to take off
   * @param runways dynamic data structure holding all runways within the simulation
   * @param postProcessing store of all departed, arrived, cancelled, or diverted planes
   * @param clock simulation clock
   * @param metrics store of all information regarding how many planes exist/have taken off/have departed etc.
   * @param mode enum representing what mode the runway is in
   * @param departureEventByCallsign mapping of aircraft callsign to an event representing its moment of departure
   * 
   * @return true if an aircraft is assigned a runway, false otherwise
   * 
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
   * 
   * Attempts to assign an aircraft from the holding pattern to a mixed runway, and then 
   * repeats this for the take off queue
   * 
   * @param holdingPattern FIFO data structure holding aircraft waiting to land
   * @param takeOffQueue FIFO data structure holding aircraft waiting to take off
   * @param runways dynamic data structure holding all runways within the simulation
   * @param postProcessing store of all departed, arrived, cancelled, or diverted planes
   * @param clock simulation clock
   * @param metrics store of all information regarding how many planes exist/have taken off/have departed etc.
   * @param arrivalEventByCallsign mapping of aircraft callsign to an event representing its moment of departure
   * @param departureEventByCallsign mapping of aircraft callsign to an event representing its moment of departure
   * 
   * @return true if an aircraft is assigned a runway, false otherwise
   * 
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
   * Searches the runway store for the next runway which is operational, unoccupied and in the correct mode
   * 
   * @param runways dynamic data structure holding all runways within the simulation
   * @param mode enum representing the mode of the runway to be found
   * 
   * @return the runway searched for from the list, null if none are found
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
