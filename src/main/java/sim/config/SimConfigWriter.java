package sim.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Path;

public final class SimConfigWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private SimConfigWriter() {}

    public static void write(Path path, SimConfig cfg) throws IOException {
        MAPPER.writeValue(path.toFile(), cfg);
    }
}