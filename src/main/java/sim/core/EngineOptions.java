package sim.core;

import java.nio.file.Path;

public record EngineOptions(
    long durationSeconds,
    double dtSeconds,
    double speedMultiplier,
    Long seed,
    Path csvPath,
    long printEverySeconds
) {}
