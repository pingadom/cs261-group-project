package sim.core.viewmodel;

import sim.core.metrics.Metrics;
import sim.model.stores.Aircraft;
import sim.model.stores.HoldingPattern;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;

import java.util.List;
import java.util.Map;

public final class SimState {
    private final double simTimeSeconds;
    private final String simTimeHHMM;
    private final boolean paused;
    private final double speedMultiplier;

    private final int holdingCount;
    private final int takeoffQueueCount;

    private final HoldingPattern<Aircraft> holdingPattern;
    private final sim.model.stores.List<Aircraft> takeoffQueue;

    private final List<RunwayState> runways;

    private final List<ArrivalEvent> generatedArrivals;
    private final List<DepartureEvent> generatedDepartures;
    private final List<Aircraft> postProcessing;

    private final Map<String, ArrivalEvent> arrivalEventByCallsign;
    private final Map<String, DepartureEvent> departureEventByCallsign;

    private final Metrics metrics;

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

    public double getSimTimeSeconds() { return simTimeSeconds; }
    public String getSimTimeHHMM() { return simTimeHHMM; }
    public boolean isPaused() { return paused; }
    public double getSpeedMultiplier() { return speedMultiplier; }

    public HoldingPattern<Aircraft> getHoldingPattern(){ return holdingPattern;}
    public sim.model.stores.List<Aircraft> getTakeoffQueue(){ return takeoffQueue;}

    public int getHoldingCount() { return holdingCount; }
    public int getTakeoffQueueCount() { return takeoffQueueCount; }

    public List<RunwayState> getRunways() { return runways; }
    public List<ArrivalEvent> getGeneratedArrivals() { return generatedArrivals; }
    public List<DepartureEvent> getGeneratedDepartures() { return generatedDepartures; }
    public List<Aircraft> getPostProcessing() { return postProcessing; }

    public Map<String, ArrivalEvent> getArrivalEventByCallsign() { return arrivalEventByCallsign; }
    public Map<String, DepartureEvent> getDepartureEventByCallsign() { return departureEventByCallsign; }

    public Metrics getMetrics() { return metrics; }
}