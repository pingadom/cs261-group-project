package sim.core.viewmodel;

import sim.config.SimConfig;
import sim.core.Engine;

public final class SimController {
    private final Engine engine;
    private Thread engineThread;

    public SimController(Engine engine) {
        this.engine = engine;
    }

    public synchronized void startSimulation() {
        if (engineThread != null && engineThread.isAlive()) return;
        engineThread = new Thread(engine::run, "simulation-thread");
        engineThread.start();
    }

    public void pauseSimulation() {
        engine.pauseSimulation();
    }

    public void resumeSimulation() {
        engine.resumeSimulation();
    }

    public void resetSimulation() {
        engine.requestReset();
    }

    public void setSpeed(double speed) {
        engine.setSimulationSpeed(speed);
    }

    public void setRunwayStatus(String runwayIdOrCode, SimConfig.RunwayStatus status) {
        engine.updateRunwayStatus(runwayIdOrCode, status);
    }

    public SimState getStateSnapshot() {
        return engine.snapshot();
    }
    public void setRunwayMode(String runwayIdOrCode, SimConfig.RunwayMode mode) {
        engine.updateRunwayMode(runwayIdOrCode, mode);
    }
}