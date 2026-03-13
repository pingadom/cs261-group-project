package sim.core.viewmodel;

import sim.config.SimConfig;

/**
 * Lightweight view model representing the current state of a single runway.
 *
 * <p>This class is used by the frontend or presentation layer so it can display
 * the important properties of a runway without needing direct access to the
 * full runway object.
 */
public final class RunwayState {
    /** Numeric runway identifier. */
    private final int id;

    /** Human-readable runway code. */
    private final String code;

    /** Current operating mode of the runway. */
    private final SimConfig.RunwayMode mode;

    /** Current availability status of the runway. */
    private final SimConfig.RunwayStatus status;

    /** Callsign of the aircraft currently occupying the runway, if any. */
    private final String occupied;

    /** Remaining service time in seconds before the runway becomes free. */
    private final int timeRemaining;

    /**
     * Creates a runway state snapshot.
     *
     * @param id runway numeric ID
     * @param code runway code
     * @param mode runway operating mode
     * @param status runway availability status
     * @param occupied callsign of occupying aircraft, if any
     * @param timeRemaining remaining occupied time in seconds
     */
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

    /** @return runway numeric ID */
    public int getId() { return id; }

    /** @return runway code */
    public String getCode() { return code; }

    /** @return current runway mode */
    public SimConfig.RunwayMode getMode() { return mode; }

    /** @return current runway status */
    public SimConfig.RunwayStatus getStatus() { return status; }

    /** @return callsign of occupying aircraft, or empty/current stored value if idle */
    public String getOccupied() { return occupied; }

    /** @return remaining runway service time in seconds */
    public int getTimeRemaining() { return timeRemaining; }
}