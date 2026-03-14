package sim.config;

import sim.core.EngineOptions;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimulationSetup;

import java.util.ArrayList;

/**
 * Factory class for converting frontend/user setup objects into backend
 * simulation configuration objects.
 *
 * <p>This class acts as a bridge between the setup entered by the user and the
 * internal objects needed by the simulation engine. It:
 * <ul>
 *   <li>validates the supplied setup,</li>
 *   <li>builds a {@link SimConfig} object,</li>
 *   <li>builds matching {@link EngineOptions} for runtime execution.</li>
 * </ul>
 */
public final class SimConfigFactory {
    /** Prevents instantiation of this utility class. */
    private SimConfigFactory() {}

    /**
     * Builds a simulation configuration from a user-facing setup object.
     *
     * <p>This method copies the traffic settings and runway definitions from the
     * provided setup into a backend {@link SimConfig} object.
     *
     * @param setup user-provided simulation setup
     * @return validated simulation configuration
     * @throws IllegalArgumentException if the setup is invalid
     */
    public static SimConfig fromSetup(SimulationSetup setup) {
        validateSetup(setup);

        SimConfig cfg = new SimConfig();
        cfg.arrivalRatePerHour = setup.getArrivalRatePerHour();
        cfg.departureRatePerHour = setup.getDepartureRatePerHour();
        cfg.maxRunways = setup.getMaxRunways();
        cfg.runways = new ArrayList<>();

        for (RunwaySetup r : setup.getRunways()) {
            SimConfig.RunwayConfig rc = new SimConfig.RunwayConfig();
            rc.id = r.getId();
            rc.mode = r.getMode();
            rc.status = r.getStatus();
            cfg.runways.add(rc);
        }

        return cfg;
    }

    /**
     * Builds runtime engine options from a user-facing setup object.
     *
     * @param setup user-provided simulation setup
     * @return engine options derived from the setup
     * @throws IllegalArgumentException if the setup is invalid
     */
    public static EngineOptions engineOptionsFromSetup(SimulationSetup setup) {
        validateSetup(setup);

        return new EngineOptions(
            setup.getDurationSeconds(),
            setup.getDtSeconds(),
            setup.getSpeedMultiplier(),
            setup.getSeed(),
            setup.getCsvPath(),
            setup.getPrintEverySeconds()
        );
    }

    /**
     * Validates that a simulation setup contains all required values and that
     * those values fall within acceptable limits.
     *
     * <p>This checks:
     * <ul>
     *   <li>that the setup exists,</li>
     *   <li>that at least one runway is provided,</li>
     *   <li>that runway count does not exceed the maximum,</li>
     *   <li>that traffic rates and timing values are positive,</li>
     *   <li>that each runway has an ID, mode, and status.</li>
     * </ul>
     *
     * @param setup simulation setup to validate
     * @throws IllegalArgumentException if any required value is missing or invalid
     */
    private static void validateSetup(SimulationSetup setup) {
        if (setup == null) {
            throw new IllegalArgumentException("Simulation setup must not be null.");
        }
        if (setup.getRunways() == null || setup.getRunways().isEmpty()) {
            throw new IllegalArgumentException("At least one runway must be provided.");
        }
        if (setup.getRunways().size() > setup.getMaxRunways()) {
            throw new IllegalArgumentException("Runway count exceeds maxRunways.");
        }
        if (setup.getArrivalRatePerHour() <= 0) {
            throw new IllegalArgumentException("arrivalRatePerHour must be positive.");
        }
        if (setup.getDepartureRatePerHour() <= 0) {
            throw new IllegalArgumentException("departureRatePerHour must be positive.");
        }
        if (setup.getDurationSeconds() <= 0) {
            throw new IllegalArgumentException("durationSeconds must be positive.");
        }
        if (setup.getDtSeconds() <= 0) {
            throw new IllegalArgumentException("dtSeconds must be positive.");
        }
        if (setup.getSpeedMultiplier() <= 0) {
            throw new IllegalArgumentException("speedMultiplier must be positive.");
        }

        for (int i = 0; i < setup.getRunways().size(); i++) {
            RunwaySetup r = setup.getRunways().get(i);
            if (r == null) {
                throw new IllegalArgumentException("Runway at index " + i + " is null.");
            }
            if (r.getId() == null || r.getId().trim().isEmpty()) {
                throw new IllegalArgumentException("Runway id missing at index " + i + ".");
            }
            if (r.getMode() == null) {
                throw new IllegalArgumentException("Runway mode missing for " + r.getId() + ".");
            }
            if (r.getStatus() == null) {
                throw new IllegalArgumentException("Runway status missing for " + r.getId() + ".");
            }
        }
    }
}