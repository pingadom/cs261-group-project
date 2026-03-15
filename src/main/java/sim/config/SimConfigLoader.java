package sim.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Loads a simulation configuration from a JSON file and validates it.
 *
 * <p>This class is responsible for reading external configuration files into
 * {@link SimConfig} objects using Jackson, then checking that the result
 * satisfies the minimum rules required by the simulation.
 */
public final class SimConfigLoader {
  /** Shared Jackson object mapper configured to reject unknown properties. */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

  /** Prevents instantiation of this utility class. */
  private SimConfigLoader() {}

  /**
   * Loads a simulation configuration from the given file path and validates it.
   *
   * @param path path to the configuration file
   * @return validated simulation configuration
   * @throws IllegalArgumentException if the file cannot be read or the config is invalid
   */
  public static SimConfig loadAndValidate(Path path) {
    SimConfig cfg;
    try {
      cfg = MAPPER.readValue(path.toFile(), SimConfig.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read config: " + path + " (" + e.getMessage() + ")", e);
    }
    validate(cfg);
    return cfg;
  }

  /**
   * Validates the contents of a loaded simulation configuration.
   *
   * <p>This checks:
   * <ul>
   *   <li>that at least one runway exists,</li>
   *   <li>that runway count does not exceed the configured maximum,</li>
   *   <li>that arrival and departure rates are positive,</li>
   *   <li>that each runway has an ID, mode, and status.</li>
   * </ul>
   *
   * @param cfg configuration to validate
   * @throws IllegalArgumentException if the configuration is invalid
   */
  private static void validate(SimConfig cfg) {
    if (cfg.runways == null || cfg.runways.isEmpty()) {
      throw new IllegalArgumentException("Config invalid: runways must be provided (size >= 1).");
    }
    if (cfg.runways.size() > cfg.maxRunways) {
      throw new IllegalArgumentException("Config invalid: runways exceed maxRunways (" + cfg.maxRunways + ").");
    }
    if (cfg.arrivalRatePerHour <= 0) {
      throw new IllegalArgumentException("Config invalid: arrivalRatePerHour must be positive.");
    }
    if (cfg.departureRatePerHour <= 0) {
      throw new IllegalArgumentException("Config invalid: departureRatePerHour must be positive.");
    }

    for (int i = 0; i < cfg.runways.size(); i++) {
      SimConfig.RunwayConfig r = cfg.runways.get(i);
      if (r == null) throw new IllegalArgumentException("Config invalid: runway at index " + i + " is null.");
      if (isBlank(r.id)) throw new IllegalArgumentException("Config invalid: runway id missing at index " + i + ".");
      Objects.requireNonNull(r.mode, "Config invalid: runway mode missing for " + r.id);
      Objects.requireNonNull(r.status, "Config invalid: runway status missing for " + r.id);
    }
  }

  /**
   * Checks whether a string is null, empty, or contains only whitespace.
   *
   * @param s string to test
   * @return true if the string is blank
   */
  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}