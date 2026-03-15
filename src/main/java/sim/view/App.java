package sim.view;

import sim.core.viewmodel.SimController;
import sim.view.pages.*;

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
        SimulationResultsPage resultsPage = new SimulationResultsPage(this, "Simulation Results", resultColumns, resultData);
        PostProcessingPage postProcessingPage = new PostProcessingPage(this, "Post Processing Flights", postProcessColumns, postProcessData);
        BasePanel soonArrivingPage = new BasePanel(this, "Flights Soon Arriving", soonArrivingColumns, soonArrivingData);
        BasePanel soonDepartingPage = new BasePanel(this, "Flights Soon Departing", soonDepartingColumns, soonDepartingData);
        BasePanel holdingPatternPage = new BasePanel(this, "Holding Pattern", soonArrivingColumns, soonArrivingData);
        BasePanel takeoffQueuePage = new BasePanel(this, "Take-off Queue", soonDepartingColumns, soonDepartingData);

        // Register pages with CardLayout using string identifiers
        mainPanel.add(inputPage, "INPUT");
        mainPanel.add(simulationPage, "SIMULATION");
        mainPanel.add(resultsPage, "RESULT");
        mainPanel.add(postProcessingPage, "POST");
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


    // Sample data for results
    String[] resultColumns = {
            "Arrived",
            "Departed",
            "Max Holding",
            "Max Queue",
            "Avg Hold",
            "Avg Delay",
            "Diverted",
            "Canceled"
    };

    String[][] resultData = {
            {"12","12","7","7","16m","9m","6","6"},
    };


    // Sample data for post-processing flights
    String[] postProcessColumns = {
            "Callsign", "Operator", "Origin", "Destination",
            "Departure", "Arrival", "Altitude", "Speed",
            "Fuel", "StatusFlag"
    };

    String[][] postProcessData = {
            {"QR2101","Qatar Airways","DOH","LHR","06:00","12:00","1800m","180 knots","30000 L","true"},
            {"EK432","Emirates","DXB","JFK","08:30","18:45","2500m","340 knots","52000 L","false"},
            {"LH789","Lufthansa","FRA","DOH","09:15","15:30","2000m","310 knots","41000 L","true"},
            {"TK102","Turkish Airlines","IST","CDG","10:00","12:45","1500m","170 knots","26000 L","true"},
            {"BA215","British Airways","LHR","DOH","11:20","19:10","2200m","320 knots","45000 L","false"},
            {"AF990","Air France","CDG","DXB","12:40","21:00","2300m","330 knots","47000 L","true"},
    };


    // Sample data for flights soon arriving and departing
    String[] soonArrivingColumns = {
            "Callsign", "Operator", "Origin", "Destination",
            "Departure", "Arrival", "Altitude", "Speed",
            "Fuel"
    };

    String[][] soonArrivingData = {
            {"QR2101","Qatar Airways","DOH","LHR","06:00","12:00","1800m","180 knots","30000 L"},
            {"EK432","Emirates","DXB","JFK","08:30","18:45","2500m","340 knots","52000 L"},
            {"LH789","Lufthansa","FRA","DOH","09:15","15:30","2000m","310 knots","41000 L"},
            {"TK102","Turkish Airlines","IST","CDG","10:00","12:45","1500m","170 knots","26000 L"},
            {"BA215","British Airways","LHR","DOH","11:20","19:10","2200m","320 knots","45000 L"},
            {"AF990","Air France","CDG","DXB","12:40","21:00","2300m","330 knots","47000 L"},
    };


    // Sample data for flights soon arriving and departing
    String[] soonDepartingColumns = {
            "Callsign", "Operator", "Origin", "Destination",
            "Departure", "Arrival", "Altitude", "Speed",
            "Fuel"
    };

    String[][] soonDepartingData = {
            {"QR2101","Qatar Airways","DOH","LHR","06:00","12:00","1800m","180 knots","30000 L"},
            {"EK432","Emirates","DXB","JFK","08:30","18:45","2500m","340 knots","52000 L"},
            {"LH789","Lufthansa","FRA","DOH","09:15","15:30","2000m","310 knots","41000 L"},
            {"TK102","Turkish Airlines","IST","CDG","10:00","12:45","1500m","170 knots","26000 L"},
            {"BA215","British Airways","LHR","DOH","11:20","19:10","2200m","320 knots","45000 L"},
            {"AF990","Air France","CDG","DXB","12:40","21:00","2300m","330 knots","47000 L"},
    };


}
