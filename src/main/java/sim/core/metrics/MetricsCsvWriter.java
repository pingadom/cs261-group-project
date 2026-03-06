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
    out.write(
        "sim_time_s,arrival_queue,departure_queue," +
        "arrivals_generated,arrivals_processed," +
        "departures_generated,departures_processed," +
        "total_arrival_delay_s,max_arrival_delay_s," +
        "total_departure_delay_s,max_departure_delay_s," +
        "avg_arrival_delay_s,avg_departure_delay_s,"+
        "arrivals_diverted\n"
    );
    out.flush();
  }

  public void writeRow(double simTimeSeconds, Metrics m) throws IOException {
    double avgArrDelay = (m.arrivalsProcessed == 0)
        ? 0.0
        : m.totalArrivalDelaySeconds / m.arrivalsProcessed;

    double avgDepDelay = (m.departuresProcessed == 0)
        ? 0.0
        : m.totalDepartureDelaySeconds / m.departuresProcessed;

out.write(String.format(
    "%.0f,%d,%d,%d,%d,%d,%d,%d,%.0f,%.0f,%.0f,%.0f%n",
    simTimeSeconds,
    m.arrivalQueue,
    m.departureQueue,
    m.arrivalsGenerated,
    m.arrivalsProcessed,
    m.arrivalsDiverted,
    m.departuresGenerated,
    m.departuresProcessed,
    m.totalArrivalDelaySeconds,
    m.maxArrivalDelaySeconds,
    m.totalDepartureDelaySeconds,
    m.maxDepartureDelaySeconds
));
    out.flush();
  }

  @Override
  public void close() throws IOException {
    out.close();
  }
}