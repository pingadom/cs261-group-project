package sim.view;

import sim.view.pages.InputPage;
import sim.view.pages.SimulationPage;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // Dimension constants
    private static final int APP_HEIGHT = 720;
    private static final int APP_WIDTH = 1280;

    public App() {
        setTitle("Airport Simulator");
        setSize(APP_WIDTH, APP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Setup CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create pages
        InputPage inputPage = new InputPage(this);
        SimulationPage simulationPage = new SimulationPage(this);

        // Add pages to CardLayout
        mainPanel.add(inputPage, "INPUT");
        mainPanel.add(simulationPage, "SIMULATION");

        add(mainPanel);
        setVisible(true);
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
}
