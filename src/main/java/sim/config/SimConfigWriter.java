package sim.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Writes simulation configuration objects to JSON files.
 *
 * <p>This class is used to persist a {@link SimConfig} object so it can be
 * saved, shared, or loaded again later.
 */
public final class SimConfigWriter {
    /** Shared Jackson object mapper configured for indented JSON output. */
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    /** Prevents instantiation of this utility class. */
    private SimConfigWriter() {}

    /**
     * Writes the given simulation configuration to the specified file path.
     *
     * @param path output file path
     * @param cfg simulation configuration to write
     * @throws IOException if the file cannot be written
     */
    public static void write(Path path, SimConfig cfg) throws IOException {
        MAPPER.writeValue(path.toFile(), cfg);
    }
}