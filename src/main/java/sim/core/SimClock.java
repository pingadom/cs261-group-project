package sim.core;

/**
 * Fixed-step simulation clock used to control the progression of simulated time.
 *
 * <p>This clock separates real time from simulation time. Real elapsed time is
 * accumulated and converted into simulation steps using a configurable speed
 * multiplier. The engine then consumes these steps one at a time.
 *
 * <p>This design gives the simulation:
 * <ul>
 *   <li>stable fixed-size updates,</li>
 *   <li>pause and resume support,</li>
 *   <li>adjustable speed control,</li>
 *   <li>predictable time progression for the engine.</li>
 * </ul>
 */
public final class SimClock {
  /** Fixed simulation step size in seconds. */
  private final double dt;

  /** Current simulation time in seconds. */
  private double simTimeSeconds = 0.0;

  /** Accumulated scaled real time waiting to be converted into simulation steps. */
  private double accumulator = 0.0;

  /** Whether the simulation clock is currently paused. */
  private boolean paused = false;

  /**
   * Creates a new simulation clock with a fixed time step.
   *
   * @param dtSeconds fixed simulation step size in seconds
   * @throws IllegalArgumentException if {@code dtSeconds <= 0}
   */
  public SimClock(double dtSeconds) {
    if (dtSeconds <= 0) throw new IllegalArgumentException("dt must be > 0");
    this.dt = dtSeconds;
  }

  /**
   * Resets the clock back to its initial state.
   *
   * <p>This sets simulation time and accumulated time back to zero
   * and clears the paused state.
   */
  public void reset() {
    simTimeSeconds = 0.0;
    accumulator = 0.0;
    paused = false;
  }

  /** Pauses the clock so no new simulation time is accumulated or stepped. */
  public void pause() { paused = true; }

  /** Resumes the clock so time accumulation and stepping can continue. */
  public void resume() { paused = false; }

  /**
   * Returns whether the clock is currently paused.
   *
   * @return true if paused, otherwise false
   */
  public boolean isPaused() { return paused; }

  /**
   * Returns the current simulation time.
   *
   * @return simulation time in seconds
   */
  public double now() { return simTimeSeconds; }

  /**
   * Returns the fixed simulation step size.
   *
   * @return step size in seconds
   */
  public double dt() { return dt; }

  /**
   * Adds scaled real time into the accumulator.
   *
   * <p>If the clock is paused, no time is added. Negative real-time inputs are ignored.
   * The speed multiplier allows the simulation to run faster or slower than real time.
   *
   * @param realDeltaSeconds real elapsed time in seconds
   * @param speedMultiplier multiplier applied to the real elapsed time
   */
  public void advanceRealTime(double realDeltaSeconds, double speedMultiplier) {
    if (paused) return;
    if (realDeltaSeconds < 0) return;
    accumulator += realDeltaSeconds * speedMultiplier;
  }

  /**
   * Returns whether enough accumulated time exists to perform one simulation step.
   *
   * @return true if at least one fixed step can be taken
   */
  public boolean hasStep() { return !paused && accumulator >= dt; }

  /**
   * Advances the simulation by exactly one fixed step if available.
   *
   * <p>This consumes one step from the accumulator and increases simulation time
   * by {@code dt}.
   */
  public void stepOnce() {
    if (!hasStep()) return;
    accumulator -= dt;
    simTimeSeconds += dt;
  }

  /**
   * Formats simulation seconds as a 24-hour HH:MM string.
   *
   * <p>This is mainly used for user-facing displays and status output.
   *
   * @param simSeconds simulation time in seconds
   * @return formatted time in HH:MM form
   */
  public static String formatHHMM(double simSeconds) {
    long totalMinutes = (long) Math.floor(simSeconds / 60.0);
    long hh = (totalMinutes / 60) % 24;
    long mm = totalMinutes % 60;
    return String.format("%02d:%02d", hh, mm);
  }
}