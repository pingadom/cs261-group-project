package sim.core.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FlightCSVWriterTest {
    @Test
    // T-29 - FR5.5 Constructor rejects invalid path
    void rejectInvalidPath(@TempDir Path tempDir) {
        // Create directory instead of a file
        Path invalidPath = tempDir.resolve("test.csv");
        assertDoesNotThrow(() -> {
          Files.createDirectory(invalidPath);
        });
        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            new FlightCsvWriter(invalidPath);
        }, "Should throw IOEXception when path is directory");
    }

    @Test
    // T-29 - FR5.5 Constructor rejects non writable path
    void rejectWritablePath(@TempDir Path tempDir) throws IOException {
        // Create read only path
        Path path = tempDir.resolve("test.csv");
        Files.createFile(path);
        path.toFile().setReadOnly();

        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            new FlightCsvWriter(path);
        }, "Should throw IOEXception when file is read only");
        path.toFile().setWritable(true);
    }    

    @Test
    // T-29 - FR5.5 writeHeader rejects closed writer
    void rejectClosedWriterHeader(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("flights.csv");
        FlightCsvWriter writer = new FlightCsvWriter(path);
        writer.close();

        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            writer.writeHeader();
        }, "Should throw IOEXception when writer is closed");
    }    

    @Test
    // T-29 - FR5.5 writeFlight rejects closed writer
    void rejectClosedWriterFlight(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("flights.csv");
        FlightCsvWriter writer = new FlightCsvWriter(path);
        writer.close();

        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            writer.writeFlight("QR2101", "ARRIVAL", 100.0, 110.0, 10.0, true, "LANDED", "None", false, false, 500);
        }, "Should throw IOEXception when writer is closed");
    }   

    @Test
    // T-29 FR5.5 Check closing writer is accepted
    void acceptCloseWriter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("flights.csv");
        FlightCsvWriter writer = new FlightCsvWriter(path);
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          writer.close();
          writer.close();
        },"Multiple close() should not change result") ;        
        

    }   

}
