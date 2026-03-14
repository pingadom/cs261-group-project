# CS261 Group Project: Airport Simulation System

## About
A Java-based simulation prototype for airport runway handling and air traffic control. The simulation manages aircraft arrivals, departures, queues, and configurable simulation parameters.

## Installation & Build
- **Java**
- **Maven**

Clone the repository and build the project using Maven:
```bash
mvn clean install
```
This will comile the source code, run the unit tests, and create an executable JAR file. 

## Execution
The simulation runs with a visual GUI. You must provide the configuration file via the command line to start. To run the compiled simulation from the terminal and open the UI, use 
```bash
java -jar target/airport-sim-prototype-1.0.0.jar --config config.json --duration 6000 --speed 100 --csv output.csv
```

## Configuration
The simulation is highly customisable when the GUI launches. Key parameters that can be configured include:
- `arrivalRatePerHour`: The average number of arriving flights per hour i.e. 15
- `departureRatePerHour`: The average number of departing flights per hour
- `maxRunways`: The maximum number of runways allowed in the simulation
- `runways`: A list of runway configurations:
  - `id`: Unique identifier string i.e. "RWY-01"
  - `mode`: The operational mode of the runway (`LANDING`, `TAKEOFF`, or `MIXED`)
  - `status`: The current status of the runway (`AVAILABLE` or `UNAVAILABLE`)

## Features
- Interactive GUI for simulating aiport traffic
- Configurable simulation parameters (arrival/departure rates, speed, duration)
- Dynamic runway assignment (Landing, Take-Off, Mixed Use) and status management
- Queuing systems including holding patterns and take-off queues
- Real-time logging of flight events, delays, diversions, and emergencies
- CSV metrics generation for post-simulation analysis

## Example Code

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
  
## Contributors
- **[Omar Alenezi]**
- **[Asyraf Bin Kamal]**
- **[Georgia Chikumbirike]**
- **[Ben Heaton]**
- **[Aaryan Hussain]**
- **[Akshaya Thavananthan]**
