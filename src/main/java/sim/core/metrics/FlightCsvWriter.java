package sim.core.metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes per-flight simulation results to a CSV file.
 *
 * <p>This class records one row per aircraft, including its scheduled time,
 * actual runway time, delay, completion state, outcome, emergency state,
 * diversion/cancellation status, and fuel level.
 *
 * <p>The output is intended for later analysis of individual aircraft outcomes,
 * rather than aggregated simulation-wide metrics.
 */
public final class FlightCsvWriter implements AutoCloseable {
  /** Writer used to output CSV rows. */
  private final BufferedWriter out;

  /**
   * Creates a new CSV writer for flight-level output.
   *
   * @param path output file path
   * @throws IOException if the file cannot be opened
   */
  public FlightCsvWriter(Path path) throws IOException {
    this.out = Files.newBufferedWriter(path);
  }

  /**
   * Writes the CSV header row.
   *
   * @throws IOException if writing fails
   */
  public void writeHeader() throws IOException {
    out.write(
        "callsign,type,scheduled_time_s,actual_runway_time_s,delay_s," +
        "completed,outcome,emergency,diverted,cancelled,fuel\n"
    );
    out.flush();
  }

  /**
   * Writes a single flight result row to the CSV file.
   *
   * @param callsign aircraft callsign
   * @param type flight type, such as ARRIVAL or DEPARTURE
   * @param scheduledTime scheduled release time in seconds
   * @param actualRunwayTime actual time the aircraft reached the runway, or null if none
   * @param delay delay in seconds, or null if not available
   * @param completed whether the flight completed runway processing
   * @param outcome textual outcome such as LANDED, TOOK_OFF, DIVERTED, or CANCELLED
   * @param emergency emergency type or status
   * @param diverted whether the flight diverted
   * @param cancelled whether the flight was cancelled
   * @param fuel recorded fuel value at the end of processing
   * @throws IOException if writing fails
   */
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

  /**
   * Closes the underlying CSV writer.
   *
   * @throws IOException if closing fails
   */
  @Override
  public void close() throws IOException {
    out.close();
  }
}