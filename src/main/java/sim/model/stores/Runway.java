package sim.model.stores;

import sim.config.SimConfig;

public final class Runway {
    private final int id;
    private final String code;
    private String occupied = "";

    private SimConfig.RunwayMode mode;
    private SimConfig.RunwayStatus status;

    private final int serviceTimeSeconds;
    private int timeRemainingSeconds = 0;

    public Runway(int id,
                  String code,
                  SimConfig.RunwayMode mode,
                  SimConfig.RunwayStatus status,
                  int serviceTimeSeconds) {
        this.id = id;
        this.code = code;
        this.mode = mode;
        this.status = status;
        this.serviceTimeSeconds = Math.max(1, serviceTimeSeconds);
    }

    public int getID() { return id; }
    public String getCode() { return code; }
    public String getOccupied() { return occupied; }
    public SimConfig.RunwayMode getMode() { return mode; }
    public SimConfig.RunwayStatus getStatus() { return status; }
    public int getTimeRemaining() { return timeRemainingSeconds; }
    public int getServiceTimeSeconds() { return serviceTimeSeconds; }

    public void setMode(SimConfig.RunwayMode mode) { this.mode = mode; }
    public void setStatus(SimConfig.RunwayStatus status) { this.status = status; }

    public boolean isIdle() {
        return occupied.isEmpty() && timeRemainingSeconds == 0;
    }

    public boolean isAvailableNow() {
        return isIdle() && status == SimConfig.RunwayStatus.AVAILABLE;
    }

    public void occupy(String callsign) {
        this.occupied = callsign == null ? "" : callsign;
        this.timeRemainingSeconds = serviceTimeSeconds;
    }

    public void clearCurrentOperation() {
        this.occupied = "";
        this.timeRemainingSeconds = 0;
    }

    public boolean tick(int dtSeconds) {
        if (occupied.isEmpty() || timeRemainingSeconds <= 0) return false;

        timeRemainingSeconds -= Math.max(1, dtSeconds);
        if (timeRemainingSeconds <= 0) {
            timeRemainingSeconds = 0;
            occupied = "";
            return true;
        }
        return false;
    }
}