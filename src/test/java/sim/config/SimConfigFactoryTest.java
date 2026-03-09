package sim.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sim.core.EngineOptions;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimulationSetup;

class SimConfigFactoryTest {

  private SimulationSetup createValidSetup() {
    SimulationSetup setup = new SimulationSetup();
    setup.setArrivalRatePerHour(30); 
    setup.setDepartureRatePerHour(10);
    setup.setMaxRunways(10);
    setup.setDurationSeconds(3600);
    setup.setDtSeconds(1.0);
    setup.setSpeedMultiplier(1.0);
    RunwaySetup runway = new RunwaySetup("RWY01", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    return setup;
  }  

  @Test
  // Check valid simulation configuration accepted
  void acceptValidSimConfig() {
    SimulationSetup setup = createValidSetup();
    SimConfig config = SimConfigFactory.fromSetup(setup);
    
    assertNotNull(config);
    assertEquals(30, config.arrivalRatePerHour);
    assertEquals(10, config.departureRatePerHour);
    assertEquals(10, config.maxRunways);
    assertEquals("RWY01", config.runways.get(0).id);
    assertEquals(SimConfig.RunwayMode.LANDING, config.runways.get(0).mode);
    assertEquals(SimConfig.RunwayStatus.AVAILABLE, config.runways.get(0).status);
    assertEquals(1, config.runways.size());
  }
          
  @Test
  // Check valid engine options accepted
  void acceptValidEngineOptions() {
    SimulationSetup setup = new SimulationSetup();
    setup.setDurationSeconds(3600);
    setup.setDtSeconds(1.0);
    setup.setSpeedMultiplier(1.0);

    RunwaySetup runway = new RunwaySetup("RWY01", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    
    EngineOptions options = SimConfigFactory.engineOptionsFromSetup(setup);
    
    assertNotNull(options);
    assertEquals(3600, options.durationSeconds());
    assertEquals(1.0, options.dtSeconds());
    assertEquals(1.0, options.speedMultiplier());
  }

  @Test
  // Check null sim config setup rejected
  void rejectNullSimConfigSetup() {
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(null);
    });
    // Check for error message Simulation setup must not be null
    assertTrue(exception.getMessage().contains("Simulation setup must not be null"));
  }
  
  @Test
  // Check null engine options setup rejected
  void rejectNullEngineOptionsSetup() {
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.engineOptionsFromSetup(null);
    });
    // Check for error message Simulation setup must not be null
    assertTrue(exception.getMessage().contains("Simulation setup must not be null"));
  }

  @Test
  // Check setups with no runways are rejected
  void rejectNoRunways() {
    SimulationSetup setup = createValidSetup();
    setup.clearRunways();
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message At least one runway must be provided
    assertTrue(exception.getMessage().contains("At least one runway must be provided"));
  }    
  
  @Test
  // Check runway count over max rejected
  void rejectMaxRunway() {
    SimulationSetup setup = createValidSetup();
    setup.setMaxRunways(1);
    RunwaySetup runway = new RunwaySetup("RWY02", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway count exceeds maxRunways
    assertTrue(exception.getMessage().contains("Runway count exceeds maxRunways"));
  }

  @Test
  // Check arrivalRatePerHour that is negative is rejected
  void rejectNegativeArrivalRatePerHour() {
    SimulationSetup setup = createValidSetup();
    setup.setArrivalRatePerHour(-10); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message arrivalRatePerHour must be positive
    assertTrue(exception.getMessage().contains("arrivalRatePerHour must be positive"));
  }

  @Test
  // Check arrivalRatePerHour that is 0 is rejected
  void rejectZeroArrivalRatePerHour() {
    SimulationSetup setup = createValidSetup();
    setup.setArrivalRatePerHour(0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message arrivalRatePerHour must be positive
    assertTrue(exception.getMessage().contains("arrivalRatePerHour must be positive"));
  }
  
  @Test
  // Check departureRatePerHour that is negative is rejected
  void rejectNegativeDepartureRatePerHour() {
    SimulationSetup setup = createValidSetup();
    setup.setDepartureRatePerHour(-10); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message departureRatePerHour must be positive
    assertTrue(exception.getMessage().contains("departureRatePerHour must be positive"));
  }

  @Test
  // Check departureRatePerHour that is 0 is rejected
  void rejectZeroDepartureRatePerHour() {
    SimulationSetup setup = createValidSetup();
    setup.setDepartureRatePerHour(0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message departureRatePerHour must be positive
    assertTrue(exception.getMessage().contains("departureRatePerHour must be positive"));
  }

  @Test
  // Check durationSeconds that is negative is rejected
  void rejectNegativeDurationSeconds() {
    SimulationSetup setup = createValidSetup();
    setup.setDurationSeconds(-10); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message durationSeconds must be positive
    assertTrue(exception.getMessage().contains("durationSeconds must be positive"));
  }

  @Test
  // Check durationSeconds that is 0 is rejected
  void rejectZeroDurationSeconds() {
    SimulationSetup setup = createValidSetup();
    setup.setDurationSeconds(0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message durationSeconds must be positive
    assertTrue(exception.getMessage().contains("durationSeconds must be positive"));
  }

  @Test
  // Check dtSeconds that is negative is rejected
  void rejectNegativeDtSeconds() {
    SimulationSetup setup = createValidSetup();
    setup.setDtSeconds(-10.0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message dtSeconds must be positive
    assertTrue(exception.getMessage().contains("dtSeconds must be positive"));
  }

  @Test
  // Check dtSeconds that is 0 is rejected
  void rejectZeroDtSeconds() {
    SimulationSetup setup = createValidSetup();
    setup.setDtSeconds(0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message dtSeconds must be positive
    assertTrue(exception.getMessage().contains("dtSeconds must be positive"));
  }

  @Test
  // Check speedMultiplier that is negative is rejected
  void rejectNegativeSpeedMultiplier() {
    SimulationSetup setup = createValidSetup();
    setup.setSpeedMultiplier(-10.0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message speedMultiplier must be positive
    assertTrue(exception.getMessage().contains("speedMultiplier must be positive"));
  }

  @Test
  // Check speedMultiplier that is 0 is rejected
  void rejectZeroSpeedMultiplier() {
    SimulationSetup setup = createValidSetup();
    setup.setSpeedMultiplier(0); 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message speedMultiplier must be positive
    assertTrue(exception.getMessage().contains("speedMultiplier must be positive"));
  }

  @Test
  // Check null runway rejected
  void rejectNullRunway() {
    SimulationSetup setup = createValidSetup();
    setup.clearRunways();
    setup.getRunways().add(null);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway at index 0 is null
    assertTrue(exception.getMessage().contains("Runway at index 0 is null"));
  }

  @Test
  // Check null runway id rejected
  void rejectNullRunwayID() {
    SimulationSetup setup = createValidSetup();
    RunwaySetup runway = new RunwaySetup(null, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway id missing at index i
    assertTrue(exception.getMessage().contains("Runway id missing at index"));
  }

  @Test
  // Check empty runway id rejected
  void rejectEmptyRunwayID() {
    SimulationSetup setup = createValidSetup();
    RunwaySetup runway = new RunwaySetup("", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway id missing at index i
    assertTrue(exception.getMessage().contains("Runway id missing at index"));
  }


  @Test
  // Check missing runway mode rejected
  void rejectMissingMode() {
    SimulationSetup setup = createValidSetup();
    RunwaySetup runway = new RunwaySetup("RWY01", null, SimConfig.RunwayStatus.AVAILABLE); 
    setup.addRunway(runway);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway mode missing for r.getId()
    assertTrue(exception.getMessage().contains("Runway mode missing for"));
  }  
  
  @Test
  // Check missing runway status rejected
  void rejectMissingStatus() {
    SimulationSetup setup = createValidSetup();
    RunwaySetup runway = new RunwaySetup("RWY01", SimConfig.RunwayMode.LANDING, null); 
    setup.addRunway(runway);
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigFactory.fromSetup(setup);
    });
    // Check for error message Runway status missing for r.getId()
    assertTrue(exception.getMessage().contains("Runway status missing for"));
  } 
  
  
}
