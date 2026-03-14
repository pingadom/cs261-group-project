package sim.config;

import java.util.List;

/**
 * Core configuration model for the simulation.
 *
 * <p>This class stores the main simulation settings that define how the engine
 * should run, including:
 * <ul>
 *   <li>the runway definitions,</li>
 *   <li>the arrival rate per hour,</li>
 *   <li>the departure rate per hour,</li>
 *   <li>the maximum number of runways allowed.</li>
 * </ul>
 *
 * <p>This configuration can be created either from a user-facing setup object
 * or loaded from an external configuration file.
 */
public final class SimConfig {
  /** List of runway configurations used by the simulation. */
  public List<RunwayConfig> runways;

  /** Number of arriving aircraft generated per hour. */
  public int arrivalRatePerHour;

  /** Number of departing aircraft generated per hour. */
  public int departureRatePerHour;

  /** Maximum number of runways allowed in the simulation. */
  public int maxRunways = 10;

  /**
   * Configuration for a single runway.
   *
   * <p>Each runway has:
   * <ul>
   *   <li>an identifier,</li>
   *   <li>an operating mode,</li>
   *   <li>an availability status.</li>
   * </ul>
   */
  public static final class RunwayConfig {
    /** Runway identifier or code. */
    public String id;

    /** Operational mode of the runway. */
    public RunwayMode mode;

    /** Availability status of the runway. */
    public RunwayStatus status;
  }

  /**
   * Operating modes that determine what type of traffic a runway can handle.
   */
  public enum RunwayMode {
    /** Runway may only be used for arrivals/landings. */
    LANDING,

    /** Runway may only be used for departures/take-offs. */
    TAKEOFF,

    /** Runway may be used for both arrivals and departures. */
    MIXED
  }

  /**
   * Availability states that determine whether a runway can currently be used.
   */
  public enum RunwayStatus {
    /** Runway is fully operational and can be assigned aircraft. */
    AVAILABLE,

    /** Runway is unavailable because it is under inspection. */
    INSPECTION,

    /** Runway is unavailable because of snow clearance or snow-related conditions. */
    SNOW,

    /** Runway is unavailable because of equipment or operational failure. */
    FAILURE,

    /** Runway is unavailable for general use. */
    UNAVAIALABLE
  }
}