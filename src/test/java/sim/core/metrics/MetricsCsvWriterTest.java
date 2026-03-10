package sim.core.metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class MetricsCSVWriterTest {
    @Test
    // T-26 - FR5.2 Constructor rejects invalid path
    void rejectInvalidPath(@TempDir Path tempDir) {
        // Create directory instead of a file
        Path invalidPath;
        invalidPath = tempDir.resolve("metrics.csv");
        assertDoesNotThrow(() -> {
          Files.createDirectory(invalidPath);
        });
        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            new MetricsCsvWriter(invalidPath);
        }, "Should throw IOEXception when path is directory" );
    }

    @Test
    // T-26 - FR5.2 Constructor rejects non writable path
    void rejectWritablePath(@TempDir Path tempDir) throws IOException {
        // Create read only path
        Path path = tempDir.resolve("metrics.csv");
        Files.createFile(path);
        path.toFile().setReadOnly();

        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            new MetricsCsvWriter(path);
        }, "Should throw IOEXception when file is read only");
        path.toFile().setWritable(true);
    }    

    @Test
    // T-26 - FR5.2 writeHeader rejects closed writer
    void rejectClosedWriterHeader(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("metrics.csv");
        MetricsCsvWriter writer = new MetricsCsvWriter(path);
        writer.close();

        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            writer.writeHeader();
        }, "Should throw IOEXception when writer is closed");
    }    

    @Test
    // T-26 FR5.2 writeRow rejects closed writer
    void rejectClosedWriterRow(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("metrics.csv");
        MetricsCsvWriter writer = new MetricsCsvWriter(path);
        writer.close();

        Metrics metrics = new Metrics();
        // Test it throws an exception
        Exception exception = assertThrows(IOException.class, () -> {
            writer.writeRow(100.0, metrics);
        }, "Should throw IOEXception when writer is closed");
    }   

    @Test
    // T-26 - FR5.2 Check closing writer is accepted
    void acceptCloseWriter(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("flights.csv");
        MetricsCsvWriter writer = new MetricsCsvWriter(path);
        // Test it does not throw an exception
        assertDoesNotThrow(() -> {
          writer.close();
          writer.close();
        },"Multiple close() should not change result") ;        
        

    }   

}
