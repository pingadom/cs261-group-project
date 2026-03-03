package sim.core;

import sim.model.stores.Aircraft;

public final class DepartureEvent {
    public final Aircraft aircraft;
    public final double releaseTimeSeconds;

    public DepartureEvent(Aircraft aircraft, double releaseTimeSeconds) {
        this.aircraft = aircraft;
        this.releaseTimeSeconds = releaseTimeSeconds;
    }
}