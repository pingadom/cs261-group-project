package sim.config;

import java.util.List;

public final class SimConfig {
  public List<RunwayConfig> runways;
  public int arrivalRatePerHour;
  public int departureRatePerHour;

  //capacity constraint
  public int maxRunways = 10;

  public static final class RunwayConfig {
    public String id;
    public RunwayMode mode;
    public RunwayStatus status;
  }

  public enum RunwayMode { LANDING, TAKEOFF, MIXED }
  public enum RunwayStatus { AVAILABLE, INSPECTION, SNOW, FAILURE }
}
