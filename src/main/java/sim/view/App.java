package sim.view;

import sim.core.viewmodel.SimController;
import sim.view.pages.*;

import sim.core.viewmodel.SimState;
import sim.model.stores.Aircraft;
import sim.model.stores.LinkedListElement;
import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Airport Simulator.
 * Manages the CardLayout navigation between different pages and holds the main controller
 */
public class App extends JFrame {

    /** Layout manager for switching between different application pages */
    private final CardLayout cardLayout;

    /** Main container panel that holds all pages using CardLayout */
    private final JPanel mainPanel;

    SimulationPage simulationPage;  // Reference to the simulation page

    // Constants
    private static final int APP_HEIGHT = 720;
    private static final int APP_WIDTH = 1280;

    /**
     * Constructs the main application window and initialises all pages.
     * Set up the window properties, loads the app. icon, creates the simulation controller,
     * and initialises all pages before adding them to the CardLayout for navigation.
     */
    public App() {
        setTitle("Airport Simulator");
        setSize(APP_WIDTH, APP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load and set application icon
        java.net.URL imgPath = getClass().getResource("/images/airport_icon.png");
        if (imgPath != null) {
            ImageIcon icon = new ImageIcon(imgPath);
            setIconImage(icon.getImage());
        } else {
            System.out.println("Path not found");
        }

        // Initialise simulation controller
        SimController simController = new SimController();

        // Setup CardLayout for page navigation
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create all application pages
        InputPage inputPage = new InputPage(this, simController);
        simulationPage = new SimulationPage(this, simController);
        //SimulationResultsPage resultsPage = new SimulationResultsPage(this, "Simulation Results", resultColumns, resultData);
        //PostProcessingPage postProcessingPage = new PostProcessingPage(this, "Post Processing Flights", postProcessColumns, postProcessData);
        LiveTablePage soonArrivingPage = new LiveTablePage(
                this,
                simController,
                "Flights Soon Arriving",
                soonArrivingColumns,
                this::buildSoonArrivingRows
        );

        LiveTablePage soonDepartingPage = new LiveTablePage(
                this,
                simController,
                "Flights Soon Departing",
                soonDepartingColumns,
                this::buildSoonDepartingRows
        );

        LiveTablePage holdingPatternPage = new LiveTablePage(
                this,
                simController,
                "Holding Pattern",
                holdingPatternColumns,
                this::buildHoldingPatternRows
        );

        LiveTablePage takeoffQueuePage = new LiveTablePage(
                this,
                simController,
                "Take-off Queue",
                takeoffQueueColumns,
                this::buildTakeoffQueueRows
        );
        // Register pages with CardLayout using string identifiers
        mainPanel.add(inputPage, "INPUT");
        mainPanel.add(simulationPage, "SIMULATION");
        //mainPanel.add(resultsPage, "RESULT");
        //mainPanel.add(postProcessingPage, "POST");
        mainPanel.add(soonArrivingPage, "SOON_ARRIVING");
        mainPanel.add(soonDepartingPage, "SOON_DEPARTING");
        mainPanel.add(holdingPatternPage, "HOLDING_PATTERN");
        mainPanel.add(takeoffQueuePage, "TAKEOFF_QUEUE");

        add(mainPanel);
        setVisible(true);
    }


    /**
     * Returns a reference to the simulation page
     *
     * @return the SimulationPage instance currently displayed
     */
    public SimulationPage getSimulationPage() {
        return simulationPage;
    }


    // Navigation methods
    /**
     * Switches the display to the Input page where users can configure simulation parameters.
     * Updates the window title to reflect Input page.
     */
    public void showInputPage() {
        this.setTitle("Airport Simulator");
        cardLayout.show(mainPanel, "INPUT");
    }

    /**
     * Switches the display to the Simulation page where active simulation is visualised.
     * Updates the window title to reflect Simulation page.
     */
    public void showSimulationPage() {
        this.setTitle("Airport Simulator - Simulation");
        cardLayout.show(mainPanel, "SIMULATION");
    }

    /**
     * Switches the display to the Results page showing final simulation statistics and outcomes.
     * Updates the window title to reflect the results view.
     */
    public void showResultsPage() {
        this.setTitle("Simulation Results");
        cardLayout.show(mainPanel, "RESULT");
    }

    /**
     * Switches the display to the Post-Processing page showing flights that has been processed
     * Updates the window title to reflect the post-processing view.
     */
    public void showPostProcessingPage() {
        this.setTitle("Post Processing Flights");
        cardLayout.show(mainPanel, "POST");
    }

    /**
     * Switches the display to the page showing flights that will arrive soon.
     * Updates the window title to reflect the page.
     */
    public void showSoonArrivingPage() {
        this.setTitle("Flights Soon Arriving");
        cardLayout.show(mainPanel, "SOON_ARRIVING");
    }

    /**
     * Switches the display to the page showing flights that will depart soon.
     * Updates the window title to reflect the page.
     */
    public void showSoonDepartingPage() {
        this.setTitle("Flights Soon Departing");
        cardLayout.show(mainPanel, "SOON_DEPARTING");
    }

    /**
     * Switches the display to the page showing aircraft currently in holding pattern.
     * Updates the window title to reflect the page.
     */
    public void showHoldingPatternPage() {
        this.setTitle("Holding Pattern");
        cardLayout.show(mainPanel, "HOLDING_PATTERN");
    }

    /**
     * Switches the display to the page showing aircraft queued for takeoff
     * Updates the window title to reflect the page.
     */
    public void showTakeoffQueuePage() {
        this.setTitle("Take-off Queue");
        cardLayout.show(mainPanel, "TAKEOFF_QUEUE");
    }

    private java.util.List<String[]> buildHoldingPatternRows(SimState state) {
    java.util.List<String[]> rows = new java.util.ArrayList<>();

    LinkedListElement<Aircraft> ptr = state.getHoldingPattern().getEmergency().getHead();
    while (ptr != null) {
        Aircraft ac = ptr.getValue();
        if (ac != null) {
            rows.add(buildAircraftRow(ac));
        }
        ptr = ptr.getNext();
    }

    ptr = state.getHoldingPattern().getNonEmergency().getHead();
    while (ptr != null) {
        Aircraft ac = ptr.getValue();
        if (ac != null) {
            rows.add(buildAircraftRow(ac));
        }
        ptr = ptr.getNext();
    }

    return rows;
}

    private java.util.List<String[]> buildTakeoffQueueRows(SimState state) {
        java.util.List<String[]> rows = new java.util.ArrayList<>();

        LinkedListElement<Aircraft> ptr = state.getTakeoffQueue().getHead();
        while (ptr != null) {
            Aircraft ac = ptr.getValue();
            if (ac != null) {
                rows.add(buildAircraftRow(ac));
            }
            ptr = ptr.getNext();
        }

        return rows;
    }

    private java.util.List<String[]> buildSoonArrivingRows(SimState state) {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        double now = state.getSimTimeSeconds();

        java.util.List<ArrivalEvent> arrivals = new java.util.ArrayList<>(state.getGeneratedArrivals());
        arrivals.sort(java.util.Comparator.comparingDouble(a -> a.releaseTimeSeconds));

        for (ArrivalEvent event : arrivals) {
            if (event == null || event.aircraft == null) continue;

            if (!event.completed && !event.diverted && event.releaseTimeSeconds >= now) {
                rows.add(buildAircraftRow(event.aircraft));
            }

            if (rows.size() >= 20) break;
        }

        return rows;
    }

    private java.util.List<String[]> buildSoonDepartingRows(SimState state) {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        double now = state.getSimTimeSeconds();

        java.util.List<DepartureEvent> departures = new java.util.ArrayList<>(state.getGeneratedDepartures());
        departures.sort(java.util.Comparator.comparingDouble(d -> d.releaseTimeSeconds));

        for (DepartureEvent event : departures) {
            if (event == null || event.aircraft == null) continue;

            if (!event.completed && !event.cancelled && event.releaseTimeSeconds >= now) {
                rows.add(buildAircraftRow(event.aircraft));
            }

            if (rows.size() >= 20) break;
        }

        return rows;
    }

    private String[] buildAircraftRow(Aircraft ac) {
        return new String[] {
                safe(ac.getCallsign()),
                safe(ac.getOperator()),
                safe(ac.getOrigin()),
                ac.getTime() != null ? ac.getTime().toString() : "",
                Integer.toString(ac.getAltitude()),
                Integer.toString(ac.getGroundspeed()),
                Integer.toString(ac.getFuel()),
                safe(ac.getEmergency())
        };
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    String[] soonArrivingColumns = {
        "Callsign", "Operator", "Origin", "Scheduled Arrival",
        "Altitude", "Speed", "Fuel", "Emergency"
    };

    String[] soonDepartingColumns = {
            "Callsign", "Operator", "Origin", "Scheduled Departure",
            "Altitude", "Speed", "Fuel", "Emergency"
    };

    String[] holdingPatternColumns = {
            "Callsign", "Operator", "Origin", "Scheduled Time",
            "Altitude", "Speed", "Fuel", "Emergency"
    };

    String[] takeoffQueueColumns = {
            "Callsign", "Operator", "Origin", "Scheduled Time",
            "Altitude", "Speed", "Fuel", "Emergency"
    };
}
