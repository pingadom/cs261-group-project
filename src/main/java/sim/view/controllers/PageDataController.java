package sim.view.controllers;

import sim.model.stores.Runway;

import java.util.ArrayList;
import java.util.List;

public class PageDataController {
    private int inboundRate;
    private int outboundRate;
    private int duration;
    private int numRunways;

    private List<Runway> runways;

    // Constructor
    public PageDataController() {
        this.runways = new ArrayList<>();
    }

    // Setters & Getters
    public void addAllRunways(List<Runway> runways) {
        this.runways.clear();
        this.runways.addAll(runways);
    }

    public void setSimulationParams(int inbound, int outbound, int duration, int numRunways) {
        this.inboundRate = inbound;
        this.outboundRate = outbound;
        this.duration = duration;
        this.numRunways = numRunways;
    }

    public int getInboundRate() { return inboundRate; }
    public int getOutboundRate() { return outboundRate; }
    public int getDuration() { return duration; }
    public int getNumRunways() { return numRunways; }

    public List<Runway> getAllRunways() {
        if (runways == null) {
            System.out.println("Runway empty");
            return new ArrayList<>();
        }
        System.out.println("Runway not empty");
        return runways;
    }

}
