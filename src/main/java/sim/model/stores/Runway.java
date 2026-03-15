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
    public String getOccupied() { return occupied; }
    public SimConfig.RunwayMode getMode() { return mode; }
    public SimConfig.RunwayStatus getStatus() { return status; }
    public int getTimeRemaining() { return timeRemainingSeconds; }
    public int getServiceTimeSeconds() { return serviceTimeSeconds; }

    public void setMode(SimConfig.RunwayMode mode) { this.mode = mode; }
    public void setStatus(SimConfig.RunwayStatus status) { this.status = status; }

    /**
     * checks whether the runway is actively being used by an aircraft
     * 
     * @return true if the runway is not being used by an aircraft, false otherwise
     */
    public boolean isIdle() {
        return occupied.isEmpty() && timeRemainingSeconds == 0;
    }

    /**
     * Checks that a runway is unoccupied and is not closed
     * 
     * @return true if the runway is unoccupied and not closed (can be assigned to a new aircraft), false otherwse
     */
    public boolean isAvailableNow() {
        return isIdle() && status == SimConfig.RunwayStatus.AVAILABLE;
    }

    /**
     * Assigns an aircraft to the runway and marks it as unavailable for a period of time
     * 
     * @param callsign callsign of the occupying aircraft
     */
    public void occupy(String callsign) {
        this.occupied = callsign == null ? "" : callsign;
        this.timeRemainingSeconds = serviceTimeSeconds;
    }

    public void clearCurrentOperation() {
        this.occupied = "";
        this.timeRemainingSeconds = 0;
    }

    /**
     * Decrements the time remaining until the runway is available.
     * If the time remaining is 0, the runway is made unoccupied
     * 
     * @param dtseconds the number of seconds (simulation time) since this function was last run
     * 
     * @return true if the runway was occupied but has now been freed. False otherwise
     */
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

    /**
     * Formats the runway id (int) as a name/code (string)
     */
    public String getCode() {
        return String.format("RWY-%02d", id);
    }
}