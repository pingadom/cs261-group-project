package sim.core.viewmodel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SimulationSetup {
    private int arrivalRatePerHour;
    private int departureRatePerHour;
    private int maxRunways = 10;

    private long durationSeconds = 3600;
    private double dtSeconds = 1.0;
    private double speedMultiplier = 1.0;
    private Long seed;
    private long printEverySeconds = 60;
    private Path csvPath;

    private final List<RunwaySetup> runways = new ArrayList<>();

    public int getArrivalRatePerHour() {
        return arrivalRatePerHour;
    }

    public void setArrivalRatePerHour(int arrivalRatePerHour) {
        this.arrivalRatePerHour = arrivalRatePerHour;
    }

    public int getDepartureRatePerHour() {
        return departureRatePerHour;
    }

    public void setDepartureRatePerHour(int departureRatePerHour) {
        this.departureRatePerHour = departureRatePerHour;
    }

    public int getMaxRunways() {
        return maxRunways;
    }

    public void setMaxRunways(int maxRunways) {
        this.maxRunways = maxRunways;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public double getDtSeconds() {
        return dtSeconds;
    }

    public void setDtSeconds(double dtSeconds) {
        this.dtSeconds = dtSeconds;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public Long getSeed() {
        return null;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public long getPrintEverySeconds() {
        return printEverySeconds;
    }

    public void setPrintEverySeconds(long printEverySeconds) {
        this.printEverySeconds = printEverySeconds;
    }

    public Path getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(Path csvPath) {
        this.csvPath = csvPath;
    }

    public List<RunwaySetup> getRunways() {
        return runways;
    }

    public void addRunway(RunwaySetup runway) {
        this.runways.add(runway);
    }

    public void clearRunways() {
        this.runways.clear();
    }
}