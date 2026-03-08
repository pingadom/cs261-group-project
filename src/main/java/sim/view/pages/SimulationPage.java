package sim.view.pages;

import sim.core.metrics.Metrics;
import sim.core.viewmodel.RunwayState;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;
import sim.view.App;
import sim.view.components.*;
import sim.view.controllers.PageDataController;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
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
    private final PageDataController dataController;
    private SimState simState;
    private final List<RunwayCard> runwayCards = new ArrayList<>();

    // UI Components
    StyledButton startPauseButton;
    StyledButton resetButton;
    JLabel startPauseLabel;
    JPanel runwaysContainer;

    StatsPanel cancelledStats;
    StatsPanel divertedStats;
    StatsPanel avgQueueStats;
    StatsPanel avgHoldingStats;
    StatsPanel maxQueueStats;
    StatsPanel maxHoldingStats;
    StatsPanel departedStats;
    StatsPanel arrivedStats;

    JToggleButton x1Button;
    JToggleButton x5Button;
    JToggleButton x10Button;
    JToggleButton x50Button;

    JLabel timeHourLabel;
    JLabel timeMinuteLabel;
    JLabel timeSecondLabel;

    // Static variables
    private final Timer updateTimer;
    int simulationSpeed = 1;

    String simHour;
    String simMinute;
    String simSecond;

    // Constructor
    public SimulationPage(App app, PageDataController dataController) {
        this.app = app;
        this.dataController = dataController;

        buildPage(createContentPanel());
        customizeFooter();

        // Timer (for every second)
        updateTimer = new Timer(1000, e -> refreshUI());

        // Detects when page becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshFromController();
                refreshControlPanel();
                startTimer();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                stopTimer();
            }
        });
    }

    // ================== REFRESH UI ==================
    private void startTimer() {
        if (!updateTimer.isRunning()) {
            updateTimer.start();
        }
    }

    private void stopTimer() {
        if (updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

    private void refreshFromController() {
        SimController simController = dataController.getSimController();

        if (simController != null) {
            this.simState = simController.getStateSnapshot();

            // Refresh the runway display
            refreshRunwayDisplay();
            refreshUI();
        } else {
            System.out.println("Controller not set yet");
        }
    }

    private void refreshRunwayDisplay() {
        runwaysContainer.removeAll();
        runwayCards.clear();

        // System.out.println("Refreshing display with " + runways.size() + " runways");

        // Use list of RunwayStates to pass in runways to each card
        List<RunwayState> runwayStates = simState.getRunways();
        for (RunwayState runway : runwayStates) {
            RunwayCard card = new RunwayCard(runway, this, dataController.getSimController());
            runwayCards.add(card);
            runwaysContainer.add(card);
        }

        // Refresh UI
        runwaysContainer.revalidate();
        runwaysContainer.repaint();
    }

    private void refreshControlPanel() {
        double speed = dataController.getSimController().getStateSnapshot().getSpeedMultiplier();

        if (speed == 1) {
            x1Button.setSelected(true);
            setButtonClicked(x1Button);
        } else if (speed == 5) {
            x5Button.setSelected(true);
            setButtonClicked(x5Button);
        } else if (speed == 10) {
            x10Button.setSelected(true);
            setButtonClicked(x10Button);
        } else if (speed == 50) {
            x50Button.setSelected(true);
            setButtonClicked(x50Button);
        }

        if (dataController.getSimController().getStateSnapshot().isPaused()) {
            startPauseLabel.setText("Resume");
        } else {
            startPauseLabel.setText("Pause");
        }
    }

    // Methods that is called every second
    private void refreshUI() {
        //System.out.println("Updated: " + new Date() + ". Speedup: " + dataController.getSpeedUp());

        // Refresh the runway card every second using recent data
        refreshRunwayDisplayEverySec();
        refreshStatsPanelEverySec();
        refreshClock();

        revalidate();
        repaint();
    }

    private void refreshRunwayDisplayEverySec() {
        //System.out.println("Refreshing each runway card. Date: " + new Date());
        for (RunwayCard runwayCard : runwayCards) {
            runwayCard.updateOccupiedLabel();
        }
    }

    private void refreshStatsPanelEverySec() {
        Metrics currentMetrics = dataController.getSimController().getStateSnapshot().getMetrics();

        int arrivalsGenerated = currentMetrics.arrivalsGenerated;
        int departuresGenerated = currentMetrics.departuresGenerated;

        int cancelled = currentMetrics.departuresCancelled;
        int diverted = currentMetrics.arrivalsDiverted;

        double totalQueue = currentMetrics.totalArrivalDelaySeconds;
        double avgQueueDelay = arrivalsGenerated > 0 ? totalQueue / arrivalsGenerated : 0;
        double roundedQueueDelay = Math.round(avgQueueDelay * 10) / 10.0;

        double totalHolding = currentMetrics.totalDepartureDelaySeconds;
        double avgHoldingDelay = arrivalsGenerated > 0 ? totalHolding / departuresGenerated : 0;
        double roundedHoldingDelay = Math.round(avgHoldingDelay * 10) / 10.0;

        double maxQueue = currentMetrics.maxArrivalDelaySeconds;
        double maxHolding = currentMetrics.maxDepartureDelaySeconds;

        int departed = currentMetrics.departuresProcessed;
        int arrived = currentMetrics.arrivalsProcessed;

        // Set the text on each labels
        cancelledStats.setValue(cancelled);
        divertedStats.setValue(diverted);
        avgQueueStats.setValue(roundedQueueDelay);
        avgHoldingStats.setValue(roundedHoldingDelay);
        maxQueueStats.setValue(maxQueue);
        maxHoldingStats.setValue(maxHolding);
        departedStats.setValue(departed);
        arrivedStats.setValue(arrived);
    }

    private void refreshClock() {
        SimState state = dataController.getSimController().getStateSnapshot();

        String hhmm = state.getSimTimeHHMM();
        String[] hhmmParts = hhmm.split(":");

        if (hhmmParts.length == 2) {
            simHour = hhmmParts[0] + "h";
            simMinute = hhmmParts[1] + "m";

            // Extract seconds
            double totalSeconds = state.getSimTimeSeconds();
            int seconds = (int) totalSeconds % 60;
            simSecond = String.format("%02ds", seconds);

            timeHourLabel.setText(simHour);
            timeMinuteLabel.setText(simMinute);
            timeSecondLabel.setText(simSecond);
        }
    }

    // ================== CONTENT ==================
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
        buttonBack.addActionListener(e -> {
            stopTimer();

            SimController controller = dataController.getSimController();

            if (controller != null) {
                // Pause the simulation
                controller.pauseSimulation();
            }

            app.showInputPage();
        });

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
        cancelledStats = new StatsPanel(COLOR_RED, "Cancelled", 0);
        divertedStats = new StatsPanel(COLOR_RED, "Diverted", 0);
        avgQueueStats = new StatsPanel(COLOR_ORANGE, "Avg Queue Delay", 0.0);
        avgHoldingStats = new StatsPanel(COLOR_ORANGE, "Avg Holding Delay", 0.0);
        maxQueueStats = new StatsPanel(COLOR_ORANGE, "Max Queue Delay", 0.0);
        maxHoldingStats = new StatsPanel(COLOR_ORANGE, "Max Holding Delay", 0.0);
        departedStats = new StatsPanel(COLOR_GREEN, "Departed", 0);
        arrivedStats = new StatsPanel(COLOR_GREEN, "Arrived", 0);

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

        boolean isPaused = dataController.getSimController().getStateSnapshot().isPaused();
        if (!isPaused) {
            // Pause the simulation
            dataController.getSimController().pauseSimulation();
            startPauseLabel.setText("Resume");
        } else {
            // Resume the simulation
            dataController.getSimController().resumeSimulation();
            startPauseLabel.setText("Pause");
        }
    }

    private void updateStartButtonToMatchState() {
        boolean isPaused = dataController.getSimController().getStateSnapshot().isPaused();
        startPauseLabel.setText(isPaused ? "Resume" : "Pause");
    }

    private void resetSimulation() {
        // Reset simulation
        dataController.getSimController().resetSimulation();
        updateStartButtonToMatchState();
    }

    // Speedup Panel
    private JPanel createSpeedPanel() {
        JPanel speedupPanel = new JPanel(new GridBagLayout());
        speedupPanel.setBackground(Color.white);

        JLabel speedupLabel = new JLabel("Speed: ");
        speedupLabel.setForeground(Color.black);
        speedupLabel.setFont(ARIAL_BOLD_16);

        // Create radio buttons
        x1Button = new JToggleButton("x1");
        x5Button = new JToggleButton("x5");
        x10Button = new JToggleButton("x10");
        x50Button = new JToggleButton("x50");

        List<JToggleButton> speedButtons = new ArrayList<>();
        speedButtons.add(x1Button);
        speedButtons.add(x5Button);
        speedButtons.add(x10Button);
        speedButtons.add(x50Button);

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
            btn.addItemListener(e -> setButtonClicked(btn));
        }

        // Default selection
        x1Button.setSelected(true);

        // Add ActionListeners for each
        x1Button.addActionListener(e -> setSimulationSpeed(1));
        x5Button.addActionListener(e -> setSimulationSpeed(5));
        x10Button.addActionListener(e -> setSimulationSpeed(10));
        x50Button.addActionListener(e -> setSimulationSpeed(50));

        // Add labels and buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        speedupPanel.add(speedupLabel);

        gbc.gridx = 1; speedupPanel.add(Box.createRigidArea(new Dimension(5, 0)), gbc); // Spacer
        gbc.gridx = 2; speedupPanel.add(x1Button);
        gbc.gridx = 3; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 4; speedupPanel.add(x5Button);
        gbc.gridx = 5; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 6; speedupPanel.add(x10Button);
        gbc.gridx = 7; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 8; speedupPanel.add(x50Button);

        return speedupPanel;
    }

    private void setButtonClicked(JToggleButton btn) {
        if (btn.isSelected()) {
            // System.out.println("Button " + btn.getText() + " selected");
            btn.setBackground(Color.black);
            btn.setForeground(Color.white);
        } else {
            btn.setBackground(Color.white);
            btn.setForeground(Color.black);
        }
    }

    private void setSimulationSpeed(int speed) {
        simulationSpeed = speed;
        System.out.println("Simulation speed is: " + simulationSpeed);

        // Set the speed of data controller
        dataController.setSimulationSpeedUp(speed);
    }

    // Runway Panel
    private JPanel createRunwayPanel() {
        JPanel runwayPanel = new JPanel();
        runwayPanel.setBackground(Color.white);
        runwayPanel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 475));

        // Main container for all the runways
        runwaysContainer = new JPanel();
        runwaysContainer.setBackground(Color.white);
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPaneRunwaysContainer = new JScrollPane(runwaysContainer);
        scrollPaneRunwaysContainer.setBackground(Color.white);
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
        JPanel clockPanel = createClockPanel();

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

    private JPanel createClockPanel() {
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.X_AXIS));
        clockPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // clockPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        clockPanel.setBackground(Color.white);
        clockPanel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, 80));

        simHour = "00h";
        timeHourLabel = new JLabel(simHour);
        timeHourLabel.setFont(new Font("Arial", Font.BOLD, 32));
        timeHourLabel.setHorizontalAlignment(JLabel.CENTER);

        simMinute = "00m";
        timeMinuteLabel = new JLabel(simMinute);
        timeMinuteLabel.setFont(new Font("Arial", Font.BOLD, 32));
        timeMinuteLabel.setHorizontalAlignment(JLabel.CENTER);

        simSecond = "00s";
        timeSecondLabel = new JLabel(simSecond);
        timeSecondLabel.setFont(new Font("Arial", Font.BOLD, 32));
        timeSecondLabel.setHorizontalAlignment(JLabel.CENTER);

        clockPanel.add(timeHourLabel);
        clockPanel.add(Box.createRigidArea(new Dimension(SPACER_SIZE_5, 0)));
        clockPanel.add(timeMinuteLabel);
        clockPanel.add(Box.createRigidArea(new Dimension(SPACER_SIZE_5, 0)));
        clockPanel.add(timeSecondLabel);

        return clockPanel;
    }


    // Navigation to other pages
    private void showFlightsSoonArrivingPage() {
        app.showSoonArrivingPage();
        //System.out.println("Flights soon arriving");
    }

    private void showFlightsSoonDepartingPage() {
        app.showSoonDepartingPage();
    }

    private void showHoldingPatternPage() {
        //app.showResultsPage();
    }

    private void showTakeoffQueuePage() {
    }

    private void showProcessedFlightsPage() {
        app.showPostProcessingPage();
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
