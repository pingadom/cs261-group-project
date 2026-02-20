package sim;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import sim.config.SimConfig;
import sim.config.SimConfigLoader;
import sim.core.Engine;
import sim.core.EngineOptions;
import sim.core.SimClock;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(
    name = "sim",
    mixinStandardHelpOptions = true,
    description = "Airport simulation prototype (headless).",
    version = "1.0.0"
)
public class SimCommand implements Runnable {

  @Option(names = {"-c", "--config"}, required = true, description = "Path to config JSON.")
  private Path configPath;

  @Option(names = {"--duration"}, defaultValue = "3600", description = "Simulation duration in simulated seconds.")
  private long durationSeconds;

  @Option(names = {"--dt"}, defaultValue = "1.0", description = "Fixed simulation timestep in seconds.")
  private double dtSeconds;

  @Option(names = {"--speed"}, defaultValue = "1.0", description = "Speed multiplier (1,2,5,10...).")
  private double speed;

  @Option(names = {"--seed"}, defaultValue = "42", description = "Random seed for deterministic runs.")
  private long seed;

  @Option(names = {"--csv"}, description = "Optional path to write CSV metrics.")
  private Path csvPath;

  @Option(names = {"--printEvery"}, defaultValue = "60", description = "Print status every N simulated seconds.")
  private long printEverySeconds;

  @Override
  public void run() {
    // Test
    Path testPath = Paths.get("src/main/resources/config.json");
    System.out.println("File exists? " + Files.exists(testPath));
    System.out.println("Absolute path: " + testPath.toAbsolutePath());

    SimConfig config = SimConfigLoader.loadAndValidate(configPath);

    EngineOptions opts = new EngineOptions(
        durationSeconds,
        dtSeconds,
        speed,
        seed,
        csvPath,
        printEverySeconds
    );

    SimClock clock = new SimClock(dtSeconds);
    Engine engine = new Engine(config, opts, clock);

    engine.run();
  }
}
