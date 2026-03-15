package sim.core.viewmodel;

import sim.core.metrics.Metrics;
import sim.model.stores.Aircraft;
import sim.model.stores.HoldingPattern;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;

import java.util.List;
import java.util.Map;

/**
 * Immutable-style snapshot of the current simulation state.
 *
 * <p>This class is used as a view model between the simulation backend and the
 * frontend or presentation layer. It collects the important pieces of state
 * at a given moment in time, such as:
 * <ul>
 *   <li>current simulation time,</li>
 *   <li>pause state and speed,</li>
 *   <li>holding and take-off queue contents,</li>
 *   <li>runway states,</li>
 *   <li>generated arrival and departure schedules,</li>
 *   <li>post-processed aircraft,</li>
 *   <li>event lookup tables,</li>
 *   <li>simulation metrics.</li>
 * </ul>
 *
 * <p>This allows the frontend to read the state of the simulation without
 * directly controlling the internal engine logic.
 */
public final class SimState {
    /** Current simulation time in seconds from the start of the simulation. */
    private final double simTimeSeconds;

    /** Current simulation time formatted as HH:MM. */
    private final String simTimeHHMM;

    /** Whether the simulation is currently paused. */
    private final boolean paused;

    /** Current simulation speed multiplier. */
    private final double speedMultiplier;

    /** Number of aircraft currently in the holding pattern. */
    private final int holdingCount;

    /** Number of aircraft currently in the take-off queue. */
    private final int takeoffQueueCount;

    /** Current holding pattern state. */
    private final HoldingPattern<Aircraft> holdingPattern;

    /** Current take-off queue state. */
    private final sim.model.stores.List<Aircraft> takeoffQueue;

    /** Snapshot of all runway states. */
    private final List<RunwayState> runways;

    /** Pre-generated arrival events for the simulation. */
    private final List<ArrivalEvent> generatedArrivals;

    /** Pre-generated departure events for the simulation. */
    private final List<DepartureEvent> generatedDepartures;

    /** Aircraft that have completed processing, diverted, or been cancelled. */
    private final List<Aircraft> postProcessing;

    /** Lookup table of arrival events by aircraft callsign. */
    private final Map<String, ArrivalEvent> arrivalEventByCallsign;

    /** Lookup table of departure events by aircraft callsign. */
    private final Map<String, DepartureEvent> departureEventByCallsign;

    /** Aggregated simulation metrics. */
    private final Metrics metrics;

    /**
     * Creates a new simulation state snapshot.
     *
     * @param simTimeSeconds current simulation time in seconds
     * @param simTimeHHMM current simulation time formatted as HH:MM
     * @param paused whether the simulation is currently paused
     * @param speedMultiplier current speed multiplier
     * @param holdingPattern current holding pattern
     * @param takeoffQueue current take-off queue
     * @param holdingCount number of aircraft in the holding pattern
     * @param takeoffQueueCount number of aircraft in the take-off queue
     * @param runways snapshot of runway states
     * @param generatedArrivals all generated arrival events
     * @param generatedDepartures all generated departure events
     * @param postProcessing aircraft already processed by the simulation
     * @param arrivalEventByCallsign lookup table of arrival events by callsign
     * @param departureEventByCallsign lookup table of departure events by callsign
     * @param metrics simulation metrics snapshot
     */
    public SimState(
            double simTimeSeconds,
            String simTimeHHMM,
            boolean paused,
            double speedMultiplier,
            HoldingPattern<Aircraft> holdingPattern,
            sim.model.stores.List<Aircraft> takeoffQueue,
            int holdingCount,
            int takeoffQueueCount,
            List<RunwayState> runways,
            List<ArrivalEvent> generatedArrivals,
            List<DepartureEvent> generatedDepartures,
            List<Aircraft> postProcessing,
            Map<String, ArrivalEvent> arrivalEventByCallsign,
            Map<String, DepartureEvent> departureEventByCallsign,
            Metrics metrics
    ) {
        this.simTimeSeconds = simTimeSeconds;
        this.simTimeHHMM = simTimeHHMM;
        this.paused = paused;
        this.speedMultiplier = speedMultiplier;
        this.holdingCount = holdingCount;
        this.takeoffQueueCount = takeoffQueueCount;
        this.holdingPattern = holdingPattern;
        this.takeoffQueue = takeoffQueue;
        this.runways = runways;
        this.generatedArrivals = generatedArrivals;
        this.generatedDepartures = generatedDepartures;
        this.postProcessing = postProcessing;
        this.arrivalEventByCallsign = arrivalEventByCallsign;
        this.departureEventByCallsign = departureEventByCallsign;
        this.metrics = metrics;
    }

    /** @return current simulation time in seconds */
    public double getSimTimeSeconds() { return simTimeSeconds; }

    /** @return current simulation time formatted as HH:MM */
    public String getSimTimeHHMM() { return simTimeHHMM; }

    /** @return true if the simulation is currently paused */
    public boolean isPaused() { return paused; }

    /** @return current speed multiplier */
    public double getSpeedMultiplier() { return speedMultiplier; }

    /** @return current holding pattern */
    public HoldingPattern<Aircraft> getHoldingPattern(){ return holdingPattern;}

    /** @return current take-off queue */
    public sim.model.stores.List<Aircraft> getTakeoffQueue(){ return takeoffQueue;}

    /** @return number of aircraft in the holding pattern */
    public int getHoldingCount() { return holdingCount; }

    /** @return number of aircraft in the take-off queue */
    public int getTakeoffQueueCount() { return takeoffQueueCount; }

    /** @return list of runway view states */
    public List<RunwayState> getRunways() { return runways; }

    /** @return all generated arrival events */
    public List<ArrivalEvent> getGeneratedArrivals() { return generatedArrivals; }

    /** @return all generated departure events */
    public List<DepartureEvent> getGeneratedDepartures() { return generatedDepartures; }

    /** @return list of aircraft that have finished processing */
    public List<Aircraft> getPostProcessing() { return postProcessing; }

    /** @return arrival-event lookup table by callsign */
    public Map<String, ArrivalEvent> getArrivalEventByCallsign() { return arrivalEventByCallsign; }

    /** @return departure-event lookup table by callsign */
    public Map<String, DepartureEvent> getDepartureEventByCallsign() { return departureEventByCallsign; }

    /** @return simulation metrics */
    public Metrics getMetrics() { return metrics; }
}