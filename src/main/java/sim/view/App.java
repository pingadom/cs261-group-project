package sim.view;

import sim.view.pages.InputPage;
import sim.view.pages.SimulationPage;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Pages for reference
    private InputPage inputPage;
    private SimulationPage simulationPage;

    public App() {
        setTitle("Airport Simulator");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Setup CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create pages
        inputPage = new InputPage(this);
        simulationPage = new SimulationPage(this);

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
        simulationPage.refreshData();
        this.setTitle("Airport Simulator - Simulation");
        cardLayout.show(mainPanel, "SIMULATION");
    }
}
