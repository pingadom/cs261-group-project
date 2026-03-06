package sim.core;

import sim.model.stores.Aircraft;

public final class DepartureEvent {
    public final Aircraft aircraft;
    public final double releaseTimeSeconds;

    public Double actualRunwayTimeSeconds;
    public Double delaySeconds;
    public boolean completed;
    public boolean diverted;
    public boolean cancelled;
    public Double cancellationTimeSeconds;
    public int fuelOnRelease;
    public Integer fuelOnRunway;

    public DepartureEvent(Aircraft aircraft, double releaseTimeSeconds) {
        this.aircraft = aircraft;
        this.releaseTimeSeconds = releaseTimeSeconds;
        this.actualRunwayTimeSeconds = null;
        this.delaySeconds = null;
        this.completed = false;
        this.diverted = false;
        this.cancelled = false;
        this.cancellationTimeSeconds = null;
        this.fuelOnRelease = aircraft.getFuel();
        this.fuelOnRunway = null;
    }

    public void markRunwayTime(double simTimeSeconds) {
        this.actualRunwayTimeSeconds = simTimeSeconds;
        this.delaySeconds = simTimeSeconds - releaseTimeSeconds;
        this.completed = true;
    }

    public void markCancelled(double simTimeSeconds) {
        this.cancelled = true;
        this.cancellationTimeSeconds = simTimeSeconds;
    }
}