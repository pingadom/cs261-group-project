package sim.core.viewmodel;

import sim.config.SimConfig;

public final class RunwaySetup {
    private String id;
    private SimConfig.RunwayMode mode;
    private SimConfig.RunwayStatus status;

    public RunwaySetup() {}

    public RunwaySetup(String id, SimConfig.RunwayMode mode, SimConfig.RunwayStatus status) {
        this.id = id;
        this.mode = mode;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SimConfig.RunwayMode getMode() {
        return mode;
    }

    public void setMode(SimConfig.RunwayMode mode) {
        this.mode = mode;
    }

    public SimConfig.RunwayStatus getStatus() {
        return status;
    }

    public void setStatus(SimConfig.RunwayStatus status) {
        this.status = status;
    }
}