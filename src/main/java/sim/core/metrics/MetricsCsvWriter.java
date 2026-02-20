package sim.core.metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MetricsCsvWriter implements AutoCloseable {
  private final BufferedWriter out;

  public MetricsCsvWriter(Path path) throws IOException {
    this.out = Files.newBufferedWriter(path);
  }

  public void writeHeader() throws IOException {
    out.write("sim_time_s,arrival_queue,departure_queue,arrivals_generated,arrivals_processed,departures_generated,departures_processed\n");
    out.flush();
  }

  public void writeRow(double simTimeSeconds, Metrics m) throws IOException {
    out.write(String.format(
        "%.0f,%d,%d,%d,%d,%d,%d%n",
        simTimeSeconds,
        m.arrivalQueue, m.departureQueue,
        m.arrivalsGenerated, m.arrivalsProcessed,
        m.departuresGenerated, m.departuresProcessed
    ));
    out.flush();
  }

  @Override
  public void close() throws IOException {
    out.close();
  }
}

