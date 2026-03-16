package sim.core.events;

import sim.model.stores.Aircraft;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/**
 * Utility class for pre-generating arrival events before the simulation starts.
 *
 * <p>Arrivals are generated using an evenly spaced target schedule, then each
 * target time is shifted using a normal distribution to simulate early or late
 * arrivals. Times that move outside the simulation period are wrapped around
 * the duration rather than clamped, which avoids clustering at the start
 * and end of the simulation window.
 */
public final class ArrivalSchedule {

    /** Standard deviation used when varying arrival times from their ideal spacing. */
    private static final double SD_SECONDS = 5 * 60.0; // 5 minutes

    /** Probability that an arriving aircraft is generated with an emergency. */
    private static final double EMERGENCY_PROB = 0.02; // 2% of inbound flights

    /** Prevents instantiation of this utility class. */
    private ArrivalSchedule() {}

    /**
     * Pre-generates a list of inbound arrival events for the full simulation duration.
     *
     * <p>The number of events is based on the requested arrivals per hour and the
     * total simulation length. Each aircraft is given a generated callsign,
     * placeholder flight data, fuel level, and a small probability of emergency status.
     *
     * @param arrivalsPerHour target arrival rate per hour
     * @param durationSeconds total simulation duration in seconds
     * @param rng random generator used for timing and emergency generation
     * @return sorted list of generated arrival events
     */
    public static ArrayList<ArrivalEvent> preGenerateInbound(
            int arrivalsPerHour,
            long durationSeconds,
            Random rng
    ) {
        int n = (int) Math.round(arrivalsPerHour * (durationSeconds / 3600.0));
        n = Math.max(n, 1);

        double spacing = 3600.0 / arrivalsPerHour; // target spacing

        ArrayList<ArrivalEvent> events = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double target = i * spacing;
            double actual = target + rng.nextGaussian() * SD_SECONDS;
            actual = wrap(actual, durationSeconds);
            actual = Math.ceil(actual);
            if (actual >= durationSeconds) actual = durationSeconds - 1;

            String callsign = "ARR" + (100 + i);
            String operator = "ARR";
            String origin = "AAA";

            // store time as HH:MM:SS from midnight
            LocalTime time = LocalTime.MIDNIGHT.plusSeconds((long) actual);

            int altitude = 10000;      
            int groundSpeed = 250;     
            int fuelMinutes = 30 + rng.nextInt(30); // 30–60 min
            int fuel = fuelMinutes * 60;             // convert to seconds)
            String emergency = (rng.nextDouble() < EMERGENCY_PROB) ? randomEmergencyType(rng) : "None";

            Aircraft ac = new Aircraft(callsign, operator, origin, time, altitude, groundSpeed, fuel, emergency);
            events.add(new ArrivalEvent(ac, actual));
        }

        events.sort(Comparator.comparingDouble(e -> e.releaseTimeSeconds));
        return events;
    }

    /**
     * Wraps a generated time into the valid simulation interval.
     *
     * <p>This uses modular arithmetic so that values below 0 or above the
     * simulation duration re-enter the valid time range smoothly.
     *
     * @param value generated time value
     * @param duration simulation duration
     * @return wrapped time within the valid interval
     */
    private static double wrap(double value, double duration) {
        return ((value % duration) + duration) % duration;
    }

    /**
     * Generates a random emergency type for an arrival.
     *
     * <p>Medical emergencies are the most common, followed by fuel and engine issues.
     *
     * @param rng random generator
     * @return generated emergency type
     */
    private static String randomEmergencyType(Random rng) {
        double x = rng.nextDouble();
        if (x < 0.55) return "Medical";
        if (x < 0.75) return "Fuel";
        if (x < 0.90) return "Engine";
        return "Other";
    }
}