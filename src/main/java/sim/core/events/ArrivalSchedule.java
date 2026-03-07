package sim.core.events;

import sim.model.stores.Aircraft;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public final class ArrivalSchedule {

    private static final double SD_SECONDS = 5 * 60.0; // 5 minutes
    private static final double EMERGENCY_PROB = 0.02; // 2% of inbound flights

    private ArrivalSchedule() {}

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

            String callsign = "BA" + (100 + i);
            String operator = "BA";
            String origin = "AAA";

            // store time as HH:MM:SS from midnight
            LocalTime time = LocalTime.MIDNIGHT.plusSeconds((long) actual);

            int altitude = 10000;      // placeholder
            int groundSpeed = 250;     // placeholder
            int fuelMinutes = 30 + rng.nextInt(30); // 30–60 min
            int fuel = fuelMinutes * 60;             // convert to seconds)
            String emergency = (rng.nextDouble() < EMERGENCY_PROB) ? randomEmergencyType(rng) : "None";

            Aircraft ac = new Aircraft(callsign, operator, origin, time, altitude, groundSpeed, fuel, emergency);
            events.add(new ArrivalEvent(ac, actual));
        }

        events.sort(Comparator.comparingDouble(e -> e.releaseTimeSeconds));
        return events;
    }

    private static double wrap(double value, double duration) {
        return ((value % duration) + duration) % duration;
    }

    private static String randomEmergencyType(Random rng) {
        // most are medical, fewer engine/fuel/etc.
        double x = rng.nextDouble();
        if (x < 0.55) return "Medical";
        if (x < 0.75) return "Fuel";
        if (x < 0.90) return "Engine";
        return "Other";
    }
}