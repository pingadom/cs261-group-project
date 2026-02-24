package sim.core;

import sim.model.stores.Aircraft;

public final class ArrivalEvent {
    public final Aircraft aircraft;
    public final double releaseTimeSeconds;

    public ArrivalEvent(Aircraft aircraft, double releaseTimeSeconds) {
        this.aircraft = aircraft;
        this.releaseTimeSeconds = releaseTimeSeconds;
    }
}