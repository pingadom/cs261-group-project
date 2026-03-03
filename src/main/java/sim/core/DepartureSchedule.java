package sim.core;

import sim.model.stores.Aircraft;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public final class DepartureSchedule {

    private static final double SD_SECONDS = 5 * 60.0; // 5 minutes (same as arrivals)

    private DepartureSchedule() {}

    public static ArrayList<DepartureEvent> preGenerateOutbound(
            int departuresPerHour,
            long durationSeconds,
            Random rng
    ) {
        int n = (int) Math.round(departuresPerHour * (durationSeconds / 3600.0));
        n = Math.max(n, 1);

        double spacing = 3600.0 / departuresPerHour;

        ArrayList<DepartureEvent> events = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double target = i * spacing;
            double actual = target + rng.nextGaussian() * SD_SECONDS;
            actual = wrap(actual, durationSeconds);

            String callsign = "DEP" + (100 + i);
            String operator = "ZZ";
            String origin = "HOME";

            LocalTime time = LocalTime.MIDNIGHT.plusSeconds((long) actual);

            int altitude = 0;
            int groundSpeed = 0;
            int fuel = 0;
            String emergency = "None";

            Aircraft ac = new Aircraft(callsign, operator, origin, time, altitude, groundSpeed, fuel, emergency);
            events.add(new DepartureEvent(ac, actual));
        }

        events.sort(Comparator.comparingDouble(e -> e.releaseTimeSeconds));
        return events;
    }

    private static double wrap(double value, double duration) {
        return ((value % duration) + duration) % duration;
    }
}