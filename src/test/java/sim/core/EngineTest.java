package sim.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sim.config.SimConfig;
import sim.config.SimConfigFactory;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimulationSetup;


class EngineTest {

    private SimConfig cfg;
    private EngineOptions opts;
    private SimClock clock;

    private SimulationSetup createInvalidSetup(String id) {
        SimulationSetup setup = new SimulationSetup();
        setup.setArrivalRatePerHour(30); 
        setup.setDepartureRatePerHour(10);
        setup.setMaxRunways(10);
        setup.setDurationSeconds(1);
        setup.setDtSeconds(1.0);
        setup.setSpeedMultiplier(1.0);
        RunwaySetup runway = new RunwaySetup(id, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
        setup.addRunway(runway);
        return setup;
    }      
    
    @BeforeEach
    void createValidSetup() {
        SimulationSetup setup = new SimulationSetup();
        setup.setArrivalRatePerHour(30); 
        setup.setDepartureRatePerHour(10);
        setup.setMaxRunways(10);
        setup.setDurationSeconds(1);
        setup.setDtSeconds(1.0);
        setup.setSpeedMultiplier(1.0);

        RunwaySetup runway = new RunwaySetup("RWY01", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
        setup.addRunway(runway);

        cfg = SimConfigFactory.fromSetup(setup);
        opts = new EngineOptions(1, 1.0, 1.0, null, null, 60);
        clock = new SimClock(1.0);
    }
    
    @Test
    // Check invalid CSV rejected
    void rejectInvalidCSVPath(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("test.csv");
        Files.createDirectory(csvPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);
        // Test it throws an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            engine.run();
        });
        // Check for error message Failed to open CSV
        assertTrue(exception.getMessage().contains("Failed to open CSV"));
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    // Check invalid flight CSV rejected
    void rejectInvalidFlightCSVPath(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("metrics.csv");
        Path flightPath = tempDir.resolve("flights.csv");
        Files.createDirectory(flightPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);

        // Test it throws an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            engine.run();
        });
        // Check for error message Failed to open flight CSV
        assertTrue(exception.getMessage().contains("Failed to open flight CSV"));
        assertTrue(exception.getCause() instanceof IOException);
    }    

    @Test
    // Check exception from CSV write failure caught
    void handleCSVWriteFailure(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("metrics.csv");
        Files.createDirectory(csvPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          engine.run();
        }, "tryWriteCsvRow() should catch IOException and continue");
    }

    @Test
    // Check exception from Flight CSV write failure caught
    void handleFlightCSVWriteFailure(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("metrics.csv");
        Path flightPath = tempDir.resolve("flights.csv");
        Files.createDirectory(flightPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          engine.run();
        }, "writeFlightCsvRows() should catch IOException and continue");
    }

    @Test
    // Check exception from delay trend CSV write failure caught
    void handleDelayTrendCSVWriteFailure(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("metrics.csv");
        Path delayPath = tempDir.resolve("delay_trend.csv");
        Files.createDirectory(delayPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);

        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          engine.run();
        }, "writeDelayTrendOutputs() CSV writing should catch IOException and continue");
    }

    @Test
    // Check exception from delay trend SVG write failure caught
    void handleDelayTrendSVGWriteFailure(@TempDir Path tempDir) throws IOException {
        // Create directory instead of a file
        Path csvPath = tempDir.resolve("metrics.csv");
        Path svgPath = tempDir.resolve("delay_trend.svg");
        Files.createDirectory(svgPath);

        EngineOptions options = new EngineOptions(1, 1.0, 1.0, null, csvPath, 60);

        Engine engine = new Engine(cfg, options, clock);

        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          engine.run();
        }, "writeDelayTrendOutputs() SVG writing should catch IOException and continue");
    }

    @Test
    // Check exception from invalid simulation speed caught
    void handleInvalidSpeed(@TempDir Path tempDir) throws IOException {
        Engine engine = new Engine(cfg, opts, clock);
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          engine.setSimulationSpeed(-1.0);
        }, "Negative simulation speed that causes NumberFormatException should be caught");

        assertDoesNotThrow(() -> {
          engine.setSimulationSpeed(0.0);
        }, "Simulation speed of 0 that causes NumberFormatException should be caught");
    }

    @Test
    // Check exception from invalid runway mode caught
    void handleInvalidMode(@TempDir Path tempDir) throws IOException {
        Engine engine = new Engine(cfg, opts, clock);
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
          engine.updateRunwayMode("RWY101", null);
        }, "Invalid runway mode that causes IllegalArgumentException should be caught");
        // Test valid does not throw an exception
        assertDoesNotThrow(() -> {
          engine.updateRunwayMode("RWY101", SimConfig.RunwayMode.LANDING);
        }, "Valid runway mode should not cause");
    }

    @Test
    // Check exception from invalid runway status caught
    void handleInvalidStatus(@TempDir Path tempDir) throws IOException {
        Engine engine = new Engine(cfg, opts, clock);
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
          engine.updateRunwayStatus("RWY101", null);
        }, "Invalid runway status that causes IllegalArgumentException should be caught");
        // Test valid does not throw an exception
        assertDoesNotThrow(() -> {
          engine.updateRunwayStatus("RWY101", SimConfig.RunwayStatus.AVAILABLE);
        }, "Valid runway status should not cause");
    }

    @Test
    // Check exception from no numbers in runway ids caught
    void handleNonNumberID(@TempDir Path tempDir) {
        SimulationSetup setup = createInvalidSetup("RUNWAY");
        cfg = SimConfigFactory.fromSetup(setup);
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
          new Engine(cfg, opts, clock);
        }, "Exceptions from no numbers in a runway ID should be caught");
    }

    @Test
    // Check exception from special characters in runway ids caught
    void handleSpecialCharID(@TempDir Path tempDir) {
        SimulationSetup setup = createInvalidSetup("&(*(@))");
        cfg = SimConfigFactory.fromSetup(setup);
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
          new Engine(cfg, opts, clock);
        }, "Exceptions from special characters in a runway ID should be caught");
    }
    
}


