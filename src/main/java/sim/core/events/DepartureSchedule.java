package sim.core.events;

import sim.model.stores.Aircraft;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Utility class for pre-generating departure events before the simulation starts.
 *
 * <p>Departures are generated from evenly spaced target times, then varied using
 * a normal distribution to simulate real-life timing differences. Times that fall
 * outside the simulation period are wrapped back into range instead of clamped,
 * which helps avoid clustering near the start and end of the simulation window.
 */
public final class DepartureSchedule {

    /** Standard deviation used when varying departure times from their ideal spacing. */
    private static final double SD_SECONDS = 5 * 60.0; // 5 minutes (same as arrivals)

    /** Prevents instantiation of this utility class. */
    private DepartureSchedule() {}

    /**
     * Pre-generates a list of outbound departure events for the full simulation duration.
     *
     * <p>The number of events is based on the requested departures per hour and the
     * total simulation length. Each aircraft is assigned basic placeholder values
     * and is then wrapped in a {@link DepartureEvent}.
     *
     * @param departuresPerHour target departure rate per hour
     * @param durationSeconds total simulation duration in seconds
     * @param rng random generator used for timing variation
     * @return sorted list of generated departure events
     */
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
            actual = Math.ceil(actual);
            if (actual >= durationSeconds) actual = durationSeconds - 1;

            String callsign = "DEP" + (100 + i);
            String operator = "ZZ";
            String origin = "HOME";

            LocalTime time = LocalTime.MIDNIGHT.plusSeconds((long) actual);

            int altitude = 0;
            int groundSpeed = 0;

            // Randomised departure fuel, stored in seconds like arrivals.
            // Mean around 35 minutes, with variation, clamped to a sensible range.
            int fuelSeconds = (int) Math.round((35 * 60) + rng.nextGaussian() * (8 * 60));
            fuelSeconds = Math.max(15 * 60, Math.min(55 * 60, fuelSeconds));

            int fuel = fuelSeconds;
            String emergency = "None";

            Aircraft ac = new Aircraft(callsign, operator, origin, time, altitude, groundSpeed, fuel, emergency);
            events.add(new DepartureEvent(ac, actual));
        }

        events.sort(Comparator.comparingDouble(e -> e.releaseTimeSeconds));
        return events;
    }

    /**
     * Wraps a generated time into the valid simulation interval.
     *
     * <p>This uses modular arithmetic so that values below 0 or above the
     * simulation duration are re-entered into the valid range without creating
     * artificial clustering at the boundaries.
     *
     * @param value generated time value
     * @param duration simulation duration
     * @return wrapped time within the valid interval
     */
    private static double wrap(double value, double duration) {
        return ((value % duration) + duration) % duration;
    }
}