package sim.view;

import com.fasterxml.jackson.databind.ser.Serializers;
import sim.core.viewmodel.SimController;
import sim.view.pages.*;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    SimulationPage simulationPage;

    // Dimension constants
    private static final int APP_HEIGHT = 720;
    private static final int APP_WIDTH = 1280;

    public App() {
        setTitle("Airport Simulator");
        setSize(APP_WIDTH, APP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Controller
        SimController simController = new SimController();

        // Setup CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create pages
        InputPage inputPage = new InputPage(this, simController);
        simulationPage = new SimulationPage(this, simController);
        SimulationResultsPage resultsPage = new SimulationResultsPage(this, "Simulation Results", resultColumns, resultData);
        PostProcessingPage postProcessingPage = new PostProcessingPage(this, "Post Processing Flights", postProcessColumns, postProcessData);
        BasePanel soonArrivingPage = new BasePanel(this, "Flights Soon Arriving", soonArrivingColumns, soonArrivingData);
        BasePanel soonDepartingPage = new BasePanel(this, "Flights Soon Departing", soonDepartingColumns, soonDepartingData);
        BasePanel holdingPatternPage = new BasePanel(this, "Holding Pattern", soonArrivingColumns, soonArrivingData);
        BasePanel takeoffQueuePage = new BasePanel(this, "Take-off Queue", soonDepartingColumns, soonDepartingData);

        // Add pages to CardLayout
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

    // Getters
    public SimulationPage getSimulationPage() {
        return simulationPage;
    }


    // Navigation methods
    public void showInputPage() {
        this.setTitle("Airport Simulator");
        cardLayout.show(mainPanel, "INPUT");
    }

    public void showSimulationPage() {
        this.setTitle("Airport Simulator - Simulation");
        cardLayout.show(mainPanel, "SIMULATION");
    }

    public void showResultsPage() {
        this.setTitle("Simulation Results");
        cardLayout.show(mainPanel, "RESULT");
    }

    public void showPostProcessingPage() {
        this.setTitle("Post Processing Flights");
        cardLayout.show(mainPanel, "POST");
    }

    public void showSoonArrivingPage() {
        this.setTitle("Flights Soon Arriving");
        cardLayout.show(mainPanel, "SOON_ARRIVING");
    }

    public void showSoonDepartingPage() {
        this.setTitle("Flights Soon Departing");
        cardLayout.show(mainPanel, "SOON_DEPARTING");
    }

    public void showHoldingPatternPage() {
        this.setTitle("Holding Pattern");
        cardLayout.show(mainPanel, "HOLDING_PATTERN");
    }

    public void showTakeoffQueuePage() {
        this.setTitle("Take-off Queue");
        cardLayout.show(mainPanel, "TAKEOFF_QUEUE");
    }


    // Sample data for results
    String[] resultColumns = {
            "Name",
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
            {"Test1","12","12","7","7","16m","9m","6","6"},
            {"Test2","14","14","6","8","12m","7m","2","1"},
            {"Test3","11","11","5","6","10m","6m","1","0"},
            {"Test4","18","18","9","10","20m","11m","4","2"},
            {"Test5","13","13","6","7","14m","8m","3","1"},
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
