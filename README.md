# cs261-group-project

## About
## Installation
## Usage
java -jar target/airport-sim-prototype-1.0.0.jar --config config.json --duration 6000 --speed 100 --csv output.csv
## Team

##
example code

SimulationSetup setup = new SimulationSetup();
setup.setArrivalRatePerHour(600);
setup.setDepartureRatePerHour(30);
setup.setMaxRunways(10);
setup.setDurationSeconds(3600);
setup.setDtSeconds(1.0);
setup.setSpeedMultiplier(1.0);
setup.setSeed(42L);
setup.setPrintEverySeconds(60);
setup.setCsvPath(java.nio.file.Path.of("output.csv"));

setup.addRunway(new RunwaySetup("RWY-01", SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE));
setup.addRunway(new RunwaySetup("RWY-02", SimConfig.RunwayMode.TAKEOFF, SimConfig.RunwayStatus.AVAILABLE));
setup.addRunway(new RunwaySetup("RWY-03", SimConfig.RunwayMode.MIXED, SimConfig.RunwayStatus.AVAILABLE));





SimConfig cfg = SimConfigFactory.fromSetup(setup);
EngineOptions opts = SimConfigFactory.engineOptionsFromSetup(setup);
SimClock clock = new SimClock(setup.getDtSeconds());

Engine engine = new Engine(cfg, opts, clock);
SimController controller = new SimController(engine);
controller.startSimulation();


SimConfig cfg = SimConfigFactory.fromSetup(setup);
SimConfigWriter.write(java.nio.file.Path.of("config.json"), cfg);