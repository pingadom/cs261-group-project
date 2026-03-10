package sim.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Path;

class SimConfigLoaderTest {
  @Test
  // Check invalid json rejected
  void rejectInvalidJson(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write invalid JSON
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write("{ invalid json }");
    } 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message Failed to read config
    assertTrue(exception.getMessage().contains("Failed to read config"));
  }
  
  @Test
  // T-01 - FR1.1 Check empty config rejected
  void verifyMandatoryInputs(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write empty JSON
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write("{}");
    } 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runways must be provided
    assertTrue(exception.getMessage().contains("runways must be provided"));
  }
  
  @Test
  // T-02 - FR1.1 Check 0 runways are rejected
  void reject0Runways(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with empty runways list, arrival rate of 30, departure rate of 30
    String configJson = """
      {
        "runways": [],
        "arrivalRatePerHour": 30,
        "departureRatePerHour": 30
      }
      """;
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(configJson);
    } 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runways must be provided
    assertTrue(exception.getMessage().contains("runways must be provided"));
  }

  @Test
  // T-02 - FR1.1 Check over max runways are rejected
  void rejectOveMaxRunways(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with 11 runways, arrival rate of 30, departure rate of 30
    StringBuilder runways = new StringBuilder();
    for (int i = 1; i<= 11; i++) {
      runways.append("""
                     {"id": "AA%d", "mode": "LANDING", "status": "AVAILABLE"},
          """.formatted(i));
        }
    String configJson = """
        {
          "runways": [%s],
          "arrivalRatePerHour": 30,
          "departureRatePerHour": 30,
          "maxRunways":10
        }
        """.formatted(runways.toString().replaceAll(",$",""));
    
      try (FileWriter writer = new FileWriter(configFile)) {
        writer.write(configJson);
      } 
      // Test it throws an exception
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SimConfigLoader.loadAndValidate(configFile.toPath());
      });
    // Check for error message runways exceed maxRunways
    assertTrue(exception.getMessage().contains("runways exceed maxRunways"));
  }

  @Test
  // T-02 - FR1.1 Check over runways 1-10 are accepted
  void acceptValidRunways(@TempDir Path tempDir) throws IOException {
    // Test 1 through 10 runways
    for (int count = 1; count <= 10; count++) {
      // Create temporary files for each test
      File configFile = new File(tempDir.toFile(), "config" + count + ".json");
      // Write JSON string with count runways, arrival rate of 30, departure rate of 30
      StringBuilder runways = new StringBuilder();
      for (int i = 1; i<= count; i++) {
        runways.append("""
                       {"id": "AA%d", "mode": "LANDING", "status": "AVAILABLE"},
            """.formatted(i));
      }
      String configJson = """
          {
            "runways": [%s],
            "arrivalRatePerHour": 30,
            "departureRatePerHour": 30,
            "maxRunways":10
          }
          """.formatted(runways.toString().replaceAll(",$",""));
      
        try (FileWriter writer = new FileWriter(configFile)) {
          writer.write(configJson);
        } 
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          SimConfigLoader.loadAndValidate(configFile.toPath());
        }, "Config with " + count + " runways should be valid");
    }
  }

  @Test
  // T-02 - FR1.1 Null runway rejected
  void rejectNullRunway(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with null runway, negative arrival rate of 30, departure rate of 30
    String configJson = """
      {
        "runways": [null],
        "arrivalRatePerHour": 30,
        "departureRatePerHour": 30
      }
      """;
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(configJson);
    } 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runway at index " + i + " is null
    assertTrue(exception.getMessage().contains("runway at index"));
  }   


  @Test
  // T-02 - FR1.1 Check null runway ID rejected
  void rejectNullRunwayID(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with null runway ID, arrival rate of 30, departure rate of 30
    String configJson = """
      {
        "runways": [{"id": null, "mode": "LANDING", "status": "AVAILABLE"}],
        "arrivalRatePerHour": 30,
        "departureRatePerHour": 30
      }
      """;
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(configJson);
    } 
    // Test it throws an exception
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runway id missing at index
    assertTrue(exception.getMessage().contains("runway id missing at index"));
  }  


  @Test
  // T-06 - FR1.5 Check missing runway mode rejected
  void rejectNullRunwayMode(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with null runway mode, arrival rate of 30, departure rate of 30
    String configJson = """
      {
        "runways": [{"id": "AA140", "mode": null, "status": "AVAILABLE"}],
        "arrivalRatePerHour": 30,
        "departureRatePerHour": 30
      }
      """;
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(configJson);
    } 
    // Test it throws an exception
    Exception exception = assertThrows(NullPointerException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runway mode missing for " + r.id
    assertTrue(exception.getMessage().contains("runway mode missing"));
  }  


  @Test
  // T-05 - FR1.4 Check missing runway status rejected
  void rejectNullRunwayStatus(@TempDir Path tempDir) throws IOException {
    // Create temporary file
    File configFile = new File(tempDir.toFile(), "config.json");

    // Write JSON string with null runway status, arrival rate of 30, departure rate of 30
    String configJson = """
      {
        "runways": [{"id": "AA140", "mode": "LANDING", "status": null}],
        "arrivalRatePerHour": 30,
        "departureRatePerHour": 30
      }
      """;
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(configJson);
    } 
    // Test it throws an exception
    Exception exception = assertThrows(NullPointerException.class, () -> {
      SimConfigLoader.loadAndValidate(configFile.toPath());
    });
    // Check for error message runway status missing for " + r.id
    assertTrue(exception.getMessage().contains("runway status missing"));
  }    
  
  @Test
  // T-03 - FR1.2 Check negative arrival rate rejected
  void rejectNegativeArrivalRate(@TempDir Path tempDir) throws IOException {
    // Create temporary file
      File configFile = new File(tempDir.toFile(), "config.json");

      // Write JSON string with one valid runway, negative arrival rate of -10, departure rate of 30
      String configJson = """
        {
          "runways": [{"id": "AA140", "mode": "LANDING", "status": "AVAILABLE"}],
          "arrivalRatePerHour": -10,
          "departureRatePerHour": 30
        }
        """;
      try (FileWriter writer = new FileWriter(configFile)) {
        writer.write(configJson);
      } 
      // Test it throws an exception
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SimConfigLoader.loadAndValidate(configFile.toPath());
      });
    // Check for error message arrivalRatePerHour must be positive
    assertTrue(exception.getMessage().contains("arrivalRatePerHour must be positive"));
  }     

  @Test
    // T-03 - FR1.2 Check arrival rate of zero rejected
    void rejectZeroArrivalRate(@TempDir Path tempDir) throws IOException {
      // Create temporary file
        File configFile = new File(tempDir.toFile(), "config.json");

        // Write JSON string with one valid runway, arrival rate of 0, departure rate of 30
        String configJson = """
          {
            "runways": [{"id": "AA140", "mode": "LANDING", "status": "AVAILABLE"}],
            "arrivalRatePerHour": 0,
            "departureRatePerHour": 30
          }
          """;
        try (FileWriter writer = new FileWriter(configFile)) {
          writer.write(configJson);
        } 
        // Test it throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
          SimConfigLoader.loadAndValidate(configFile.toPath());
        });
      // Check for error message arrivalRatePerHour must be positive
      assertTrue(exception.getMessage().contains("arrivalRatePerHour must be positive"));
  }  
      
    @Test
    // T-03 - FR1.2 Check negative departure rate rejected
    void rejectNegativeDepartureRate(@TempDir Path tempDir) throws IOException {
      // Create temporary file
        File configFile = new File(tempDir.toFile(), "config.json");

        // Write JSON string with one valid runway, arrival rate of 30, negative departure rate of -10
        String configJson = """
          {
            "runways": [{"id": "AA140", "mode": "LANDING", "status": "AVAILABLE"}],
            "arrivalRatePerHour": 30,
            "departureRatePerHour": -10
          }
          """;
        try (FileWriter writer = new FileWriter(configFile)) {
          writer.write(configJson);
        } 
        // Test it throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
          SimConfigLoader.loadAndValidate(configFile.toPath());
        });
      // Check for error message departureRatePerHour must be positive
      assertTrue(exception.getMessage().contains("departureRatePerHour must be positive"));
  }  

  @Test
    // T-03 - FR1.2 Check departure rate of zero rejected
    void rejectZeroDepartureRate(@TempDir Path tempDir) throws IOException {
      // Create temporary file
        File configFile = new File(tempDir.toFile(), "config.json");

        // Write JSON string with one valid runway, arrival rate of 30, departure rate of 0
        String configJson = """
          {
            "runways": [{"id": "AA140", "mode": "LANDING", "status": "AVAILABLE"}],
            "arrivalRatePerHour": 30,
            "departureRatePerHour": 0
          }
          """;
        try (FileWriter writer = new FileWriter(configFile)) {
          writer.write(configJson);
        } 
        // Test it throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
          SimConfigLoader.loadAndValidate(configFile.toPath());
        });
      // Check for error message departureRatePerHour must be positive
      assertTrue(exception.getMessage().contains("departureRatePerHour must be positive"));
  }   
  
}

