package sim.model.stores;

import java.time.LocalTime;

public class Clock {
    public LocalTime simulationTime = LocalTime.MIDNIGHT;

    public void setFromSeconds(long simSeconds) {
        simulationTime = LocalTime.MIDNIGHT.plusSeconds(simSeconds);
    }
}