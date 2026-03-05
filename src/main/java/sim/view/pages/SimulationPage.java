package sim.view.pages;

import sim.view.App;
import sim.view.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationPage extends BasicPage {
    // Constants
    private static final int SPACER_SIZE_10 = 10;
    private static final int SPACER_SIZE_5 = 5;
    private static final int CONTROL_BUTTON_HEIGHT = 40;
    private static final int CONTROL_BUTTON_WIDTH = 120;

    private static final int CONTENT_PANEL_HEIGHT = 520;
    private static final int STATS_PANEL_WIDTH = 200;
    private static final int CENTER_COLUMN_WIDTH = 670;
    private static final int BUTTONS_PANEL_WIDTH = 200;

    private static final Color COLOR_RED = new Color(0xE00A0A);
    private static final Color COLOR_ORANGE = new Color(0xFF8C0A);
    private static final Color COLOR_GREEN = new Color(0x0AE04E);

    private static final Font ARIAL_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font ARIAL_BOLD_16 = new Font("Arial", Font.BOLD, 16);

    // Instance variables
    private final App app;

    // UI Components
    StyledButton startPauseButton;
    StyledButton resetButton;
    JLabel startPauseLabel;

    // Static variables
    int toggleStartPause = 0;
    int simulationSpeed = 1;

    // Constructor
    public SimulationPage(App app) {
        this.app = app;
        buildPage(createContentPanel());
    }

    // Content Panel
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(Color.white);

        JPanel leftContentColumn = createStatsPanel();  // LEFT Column - Stats
        JPanel centerContentColumn = createCenterColumnPanel(); // CENTER Column - Control + Runways
        JPanel rightContentColumn = createRightColumnPanel();   // RIGHT Column - Clock + Buttons

        // Adding each columns into contentPanel
        addPanelXAxis(contentPanel, leftContentColumn);
        addPanelXAxis(contentPanel, centerContentColumn);
        contentPanel.add(rightContentColumn);

        return contentPanel;
    }

    // Footer Panel
    protected void customizeFooter() {
        StyledButton buttonBack = new StyledButton("Back", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(ARIAL_BOLD_14);
        buttonBack.addActionListener(e -> app.showInputPage());

        footerPanel.add(buttonBack);
    }

    // LEFT Column - Stats
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setMinimumSize(new Dimension(STATS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));
        panel.setMaximumSize(new Dimension(STATS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));

        // Panels for all stats
        JPanel cancelledStats = new StatsPanel(COLOR_RED, "Cancelled", 0);
        JPanel divertedStats = new StatsPanel(COLOR_RED, "Diverted", 0);
        JPanel avgQueueStats = new StatsPanel(COLOR_ORANGE, "Avg Queue Delay", 0);
        JPanel avgHoldingStats = new StatsPanel(COLOR_ORANGE, "Avg Holding Delay", 0);
        JPanel maxQueueStats = new StatsPanel(COLOR_ORANGE, "Max Queue Delay", 0);
        JPanel maxHoldingStats = new StatsPanel(COLOR_ORANGE, "Max Holding Delay", 0);
        JPanel departedStats = new StatsPanel(COLOR_GREEN, "Departed", 0);
        JPanel arrivedStats = new StatsPanel(COLOR_GREEN, "Arrived", 0);

        panel.add(Box.createVerticalGlue());
        addPanelYAxis(panel, cancelledStats);
        addPanelYAxis(panel, divertedStats);
        addPanelYAxis(panel, avgQueueStats);
        addPanelYAxis(panel, avgHoldingStats);
        addPanelYAxis(panel, maxQueueStats);
        addPanelYAxis(panel, maxHoldingStats);
        addPanelYAxis(panel, departedStats);
        panel.add(arrivedStats);

        return panel;
    }

    // CENTER Column - Control + Runways
    private JPanel createCenterColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, CONTENT_PANEL_HEIGHT));

        JPanel controlPanel = createControlPanel();
        JPanel runwayPanel = createRunwayPanel();

        // Adding into topCenterColumn
        panel.add(Box.createVerticalGlue());
        panel.add(controlPanel);
        panel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_5)));
        panel.add(runwayPanel);

        return panel;
    }

    // Control panel
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.white);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 40));
        controlPanel.setMaximumSize(new Dimension(CENTER_COLUMN_WIDTH, 40));
        controlPanel.setMinimumSize(new Dimension(CENTER_COLUMN_WIDTH, 40));

        // Start button
        startPauseButton = new StyledButton("", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        startPauseButton.setLayout(new BorderLayout());
        startPauseButton.setFocusPainted(false);
        startPauseButton.setBackground(Color.black);
        startPauseButton.setButtonSize(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT);
        startPauseButton.addActionListener(e -> toggleStartButton());

        // Add an icon
        startPauseLabel = new JLabel("Pause", JLabel.CENTER);
        startPauseLabel.setFont(ARIAL_BOLD_16);
        startPauseLabel.setForeground(Color.white);
        startPauseButton.add(startPauseLabel, BorderLayout.CENTER);

        // Reset button
        resetButton = new StyledButton("Reset", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        resetButton.setButtonSize(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT);
        resetButton.setFont(ARIAL_BOLD_16);
        resetButton.addActionListener(e -> resetSimulation());

        JPanel speedupPanel = createSpeedPanel();

        // Adding components into controlPanel
        addPanelXAxis(controlPanel, startPauseButton);
        addPanelXAxis(controlPanel, resetButton);
        controlPanel.add(speedupPanel);

        return controlPanel;
    }

    // Functions
    private void toggleStartButton() {
        if (toggleStartPause == 0) {
            startPauseLabel.setText("Start");
            System.out.println("System paused!");
            toggleStartPause = 1;
        } else if (toggleStartPause == 1) {
            startPauseLabel.setText("Pause");
            System.out.println("System resumed!");
            toggleStartPause = 0;
        }
    }

    private void resetSimulation() {
        System.out.println("System reset");
    }

    // Speedup Panel
    private JPanel createSpeedPanel() {
        JPanel speedupPanel = new JPanel(new GridBagLayout());
        speedupPanel.setBackground(Color.white);

        JLabel speedupLabel = new JLabel("Speed: ");
        speedupLabel.setForeground(Color.black);
        speedupLabel.setFont(ARIAL_BOLD_16);

        // Create radio buttons
        JToggleButton x1Button = new JToggleButton("x1");
        JToggleButton x2Button = new JToggleButton("x2");
        JToggleButton x5Button = new JToggleButton("x5");
        JToggleButton x10Button = new JToggleButton("x10");

        List<JToggleButton> speedButtons = new ArrayList<>();
        speedButtons.add(x1Button);
        speedButtons.add(x2Button);
        speedButtons.add(x5Button);
        speedButtons.add(x10Button);

        ButtonGroup speedGroup = new ButtonGroup();

        // Style the radio button to look like buttons and add into button group
        for (JToggleButton btn : speedButtons) {
            speedGroup.add(btn);

            btn.setFont(ARIAL_BOLD_14);
            btn.setPreferredSize(new Dimension(50, 35));
            btn.setBackground(Color.white);
            btn.setForeground(Color.black);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);

            // Changes appearance when selected
            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    // System.out.println("Button " + btn.getText() + " selected");
                    btn.setBackground(Color.black);
                    btn.setForeground(Color.white);
                } else {
                    btn.setBackground(Color.white);
                    btn.setForeground(Color.black);
                }
            });
        }

        // Default selection
        x1Button.setSelected(true);

        // Add ActionListeners for each
        x1Button.addActionListener(e -> setSimulationSpeed(1));
        x2Button.addActionListener(e -> setSimulationSpeed(2));
        x5Button.addActionListener(e -> setSimulationSpeed(5));
        x10Button.addActionListener(e -> setSimulationSpeed(10));

        // Add labels and buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        speedupPanel.add(speedupLabel);

        gbc.gridx = 1; speedupPanel.add(Box.createRigidArea(new Dimension(5, 0)), gbc); // Spacer
        gbc.gridx = 2; speedupPanel.add(x1Button);
        gbc.gridx = 3; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 4; speedupPanel.add(x2Button);
        gbc.gridx = 5; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 6; speedupPanel.add(x5Button);
        gbc.gridx = 7; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 8; speedupPanel.add(x10Button);

        return speedupPanel;
    }

    private void setSimulationSpeed(int speed) {
        simulationSpeed = speed;
        System.out.println("Simulation speed is: " + simulationSpeed);
    }

    // Runway Panel
    private JPanel createRunwayPanel() {
        JPanel runwayPanel = new JPanel();
        runwayPanel.setBackground(Color.white);
        runwayPanel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 475));

        // Main container for all the runways
        JPanel runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add runway card for each runway
        runwaysContainer.add(new RunwayCard(1, "Available", "Landing", "AA100", true, this));
        runwaysContainer.add(new RunwayCard(2, "Available", "Take-off", "AA104", true, this));
        runwaysContainer.add(new RunwayCard(3, "Available", "Landing", "AA140", false, this));
        runwaysContainer.add(new RunwayCard(4, "Available", "Take-off", "AA141", false, this));
        runwaysContainer.add(new RunwayCard(5, "Available", "Landing", "AA120", false, this));
        runwaysContainer.add(new RunwayCard(6, "Available", "Mixed", "BB140", true, this));

        JScrollPane scrollPaneRunwaysContainer = new JScrollPane(runwaysContainer);
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 470));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        runwayPanel.add(scrollPaneRunwaysContainer);

        return runwayPanel;
    }

    // RIGHT Column - Clock + Buttons
    private JPanel createRightColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));

        // Clock panel
        JPanel clockPanel = new JPanel();
        clockPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        clockPanel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, 80));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(Color.white);
        buttonsPanel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(7, 5, 7, 5);

        JButton flightsSoonArrivingButton = new StyledButton("Flights Soon Arriving", new Color(0x1B30A6), new Color(0x2A45C9), new Color(0x0F1F73), new Color(0x8799E0));
        flightsSoonArrivingButton.addActionListener(e -> showFlightsSoonArrivingPage());
        buttonsPanel.add(flightsSoonArrivingButton, gbc);

        gbc.gridy = 1;
        JButton flightsSoonDepartingButton = new StyledButton("Flights Soon Departing", new Color(0x1B30A6), new Color(0x2A45C9), new Color(0x0F1F73), new Color(0x8799E0));
        flightsSoonDepartingButton.addActionListener(e -> showFlightsSoonDepartingPage());
        buttonsPanel.add(flightsSoonDepartingButton, gbc);

        gbc.gridy = 2;
        JButton holdingPatternButton = new StyledButton("Holding Pattern", new Color(0x4A1073), new Color(0x621A96), new Color(0x320A4F), new Color(0x9B6BCE));
        holdingPatternButton.addActionListener(e -> showHoldingPatternPage());
        buttonsPanel.add(holdingPatternButton, gbc);

        gbc.gridy = 3;
        JButton takeoffQueueButton = new StyledButton("Takeoff Queue", new Color(0x4A1073), new Color(0x621A96), new Color(0x320A4F), new Color(0x9B6BCE));
        takeoffQueueButton.addActionListener(e -> showTakeoffQueuePage());
        buttonsPanel.add(takeoffQueueButton, gbc);

        gbc.gridy = 4;
        JButton processedFlightsButton = new StyledButton("Processed Flights", new Color(0x141E54), new Color(0x1E2D7A), new Color(0x0B1238), new Color(0x5A6AB0));
        processedFlightsButton.addActionListener(e -> showProcessedFlightsPage());
        buttonsPanel.add(processedFlightsButton, gbc);

        // Adding control panel and buttons panel into panel
        panel.add(Box.createVerticalGlue());
        panel.add(clockPanel);
        panel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));    // Gap in between
        panel.add(buttonsPanel);

        return panel;
    }


    // Navigation to other pages
    private void showFlightsSoonArrivingPage() {
        // Show the page
        System.out.println("Flights soon arriving");
    }

    private void showFlightsSoonDepartingPage() {
        // Show the page
        System.out.println("Flights soon departing");
    }

    private void showHoldingPatternPage() {
        // Show the page
        System.out.println("Holding Pattern");
    }

    private void showTakeoffQueuePage() {
        // Show the page
        System.out.println("Takeoff Queue");
    }

    private void showProcessedFlightsPage() {
        // Show the page
        System.out.println("Processed Flights");
    }


    // Helper methods
    private void addPanelYAxis(JPanel container, Component comp) {
        container.add(comp);
        container.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
    }

    private void addPanelXAxis(JPanel container, Component comp) {
        container.add(comp);
        container.add(Box.createRigidArea(new Dimension(SPACER_SIZE_10, 0)));
    }
}
