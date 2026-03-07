package sim.core.metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FlightCsvWriter implements AutoCloseable {
  private final BufferedWriter out;

  public FlightCsvWriter(Path path) throws IOException {
    this.out = Files.newBufferedWriter(path);
  }

  public void writeHeader() throws IOException {
    out.write(
        "callsign,type,scheduled_time_s,actual_runway_time_s,delay_s," +
        "completed,outcome,emergency,diverted,cancelled,fuel\n"
    );
    out.flush();
  }

  public void writeFlight(
      String callsign,
      String type,
      double scheduledTime,
      Double actualRunwayTime,
      Double delay,
      boolean completed,
      String outcome,
      String emergency,
      boolean diverted,
      boolean cancelled,
      int fuel
  ) throws IOException {
    out.write(String.format(
        "%s,%s,%.0f,%s,%s,%s,%s,%s,%s,%s,%d%n",
        callsign,
        type,
        scheduledTime,
        actualRunwayTime == null ? "" : String.format("%.0f", actualRunwayTime),
        delay == null ? "" : String.format("%.0f", delay),
        completed,
        outcome,
        emergency,
        diverted,
        cancelled,
        fuel
    ));
    out.flush();
  }

  @Override
  public void close() throws IOException {
    out.close();
  }
}