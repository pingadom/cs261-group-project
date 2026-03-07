package sim.core.viewmodel;

import sim.config.SimConfig;

public final class RunwayState {
    private final int id;
    private final String code;
    private final SimConfig.RunwayMode mode;
    private final SimConfig.RunwayStatus status;
    private final String occupied;
    private final int timeRemaining;

    public RunwayState(
            int id,
            String code,
            SimConfig.RunwayMode mode,
            SimConfig.RunwayStatus status,
            String occupied,
            int timeRemaining
    ) {
        this.id = id;
        this.code = code;
        this.mode = mode;
        this.status = status;
        this.occupied = occupied;
        this.timeRemaining = timeRemaining;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public SimConfig.RunwayMode getMode() { return mode; }
    public SimConfig.RunwayStatus getStatus() { return status; }
    public String getOccupied() { return occupied; }
    public int getTimeRemaining() { return timeRemaining; }
}