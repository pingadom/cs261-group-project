package sim.core;

public final class SimClock {
  private final double dt;
  private double simTimeSeconds = 0.0;
  private double accumulator = 0.0;
  private boolean paused = false;

  public SimClock(double dtSeconds) {
    if (dtSeconds <= 0) throw new IllegalArgumentException("dt must be > 0");
    this.dt = dtSeconds;
  }

  public void reset() { simTimeSeconds = 0.0; accumulator = 0.0; paused = false; }
  public void pause() { paused = true; }
  public void resume() { paused = false; }
  public boolean isPaused() { return paused; }

  public double now() { return simTimeSeconds; }
  public double dt() { return dt; }

  public void advanceRealTime(double realDeltaSeconds, double speedMultiplier) {
    if (paused) return;
    if (realDeltaSeconds < 0) return;
    accumulator += realDeltaSeconds * speedMultiplier;
  }

  public boolean hasStep() { return !paused && accumulator >= dt; }

  public void stepOnce() {
    if (!hasStep()) return;
    accumulator -= dt;
    simTimeSeconds += dt;
  }

  public static String formatHHMM(double simSeconds) {
    long totalMinutes = (long) Math.floor(simSeconds / 60.0);
    long hh = (totalMinutes / 60) % 24;
    long mm = totalMinutes % 60;
    return String.format("%02d:%02d", hh, mm);
  }
}

