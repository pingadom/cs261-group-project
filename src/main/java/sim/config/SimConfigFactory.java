package sim.config;

import sim.core.EngineOptions;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimulationSetup;

import java.util.ArrayList;

public final class SimConfigFactory {
    private SimConfigFactory() {}

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