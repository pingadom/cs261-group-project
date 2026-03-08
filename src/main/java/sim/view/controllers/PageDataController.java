package sim.view.controllers;

import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimController;
import sim.model.stores.Runway;

import java.util.ArrayList;
import java.util.List;

public class PageDataController {
    private int inboundRate;
    private int outboundRate;
    private int duration;
    private int numRunways;

    private final List<Runway> runways;
    private final List<RunwaySetup> runwaySetups;

    private SimController simController;

    // Constructor
    public PageDataController() {
        this.runways = new ArrayList<>();
        this.runwaySetups = new ArrayList<>();
    }

    // Setters & Getters
    public void addAllRunways(List<Runway> runways) {
        this.runways.clear();
        this.runways.addAll(runways);
    }

    public void addAllRunwaySetups(List<RunwaySetup> runways) {
        this.runwaySetups.clear();
        this.runwaySetups.addAll(runways);
    }

    public void setSimulationParams(int inbound, int outbound, int duration, int numRunways) {
        this.inboundRate = inbound;
        this.outboundRate = outbound;
        this.duration = duration;
        this.numRunways = numRunways;
    }

    public void setSimController(SimController simController) {
        this.simController = simController;
    }

    public int getInboundRate() { return inboundRate; }
    public int getOutboundRate() { return outboundRate; }
    public int getDuration() { return duration; }
    public int getNumRunways() { return numRunways; }

    public List<Runway> getAllRunways() {
        return runways;
    }

    public List<RunwaySetup> getAllRunwaySetups() {
        return runwaySetups;
    }

    public SimController getSimController() {
        return simController;
    }

}
