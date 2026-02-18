package sim.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class SimConfigLoader {
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

  private SimConfigLoader() {}

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

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}
