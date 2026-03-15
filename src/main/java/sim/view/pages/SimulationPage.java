package sim.view.pages;

import sim.core.metrics.Metrics;
import sim.core.viewmodel.RunwayState;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;
import sim.view.App;
import sim.view.components.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;

/**
 * The main simulation visualisation page where the active simulation is displayed
 * <p>
 *     This page shows:
 *     <ul>
 *         <li>Real-time stats panels (cancelled, diverted, queue delays, etc.)</li>
 *         <li>Dynamic runway cards showing individual runway information</li>
 *         <li>Control buttons (Pause/Resume, Reset, Finish)</li>
 *         <li>Speed multiplier toggles</li>
 *         <li>Elapsed time display</li>
 *         <li>Navigation button to other pages</li>
 *     </ul>
 * </p>
 *
 * The UI refreshes every 500ms to refect the latest simulation state from the {@link SimController}.
 * The refresh timer is automatically started when the page becomes visible and stopped when hidden.
 *
 * @see BasicPage
 * @see SimController
 * @see RunwayCard
 * @see StatsPanel
 */
public class SimulationPage extends BasicPage {

    // ===================== CONSTANTS =====================
    private static final int SPACER_SIZE_10 = 10;
    private static final int SPACER_SIZE_5 = 5;
    private static final int CONTROL_BUTTON_HEIGHT = 40;
    private static final int CONTROL_BUTTON_WIDTH = 90;

    private static final int CONTENT_PANEL_HEIGHT = 520;
    private static final int STATS_PANEL_WIDTH = 200;
    private static final int CENTER_COLUMN_WIDTH = 670;
    private static final int BUTTONS_PANEL_WIDTH = 200;

    private static final Color COLOR_RED = new Color(0xE00A0A);
    private static final Color COLOR_ORANGE = new Color(0xFF8C0A);
    private static final Color COLOR_GREEN = new Color(0x0AE04E);

    private static final Font ARIAL_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font ARIAL_BOLD_16 = new Font("Arial", Font.BOLD, 16);

    private static final int REFRESH_INTERVAL_MS = 500;

    // ===================== INSTANCE VARIABLES =====================
    private final App app;
    private final SimController simController;
    private final List<RunwayCard> runwayCards = new ArrayList<>();
    private final Timer updateTimer;

    // ===================== UI COMPONENTS =====================
    StyledButton startPauseButton;
    StyledButton resetButton;
    StyledButton finishButton;
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
    JToggleButton x10Button;
    JToggleButton x50Button;
    JToggleButton x100Button;

    JLabel timeHourLabel;
    JLabel timeMinuteLabel;
    JLabel timeSecondLabel;
    String simHour;
    String simMinute;
    String simSecond;

    // ===================== STATE VARIABLES =====================
    private boolean simulationEnded = false;


    // ===================== CONSTRUCTOR =====================

    /**
     * Constructs a new SimulationPage with the specified application and controller
     *
     * @param app the main application instance for navigation
     * @param simController the controller providing simulation state and controller
     */
    public SimulationPage(App app, SimController simController) {
        this.app = app;
        this.simController = simController;

        buildPage(createContentPanel());
        customizeFooter();

        // Initialise timer
        updateTimer = new Timer(REFRESH_INTERVAL_MS, e -> {
            if (!simulationEnded) {
                refreshUI();
                checkSimulationEnded();
            }
        });

        // Component listener to manage timer based on page visibility
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!simulationEnded) {
                    refreshFromController();
                    refreshControlPanel();
                    startTimer();
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                stopTimer();
            }
        });
    }

    // ===================== TIMER CONTROL =====================

    /**
     * Starts the UI refresh timer if it is not already running.
     * The time will trigger {@link #refreshUI()} at each interval.
     */
    public void startTimer() {
        if (updateTimer != null && !updateTimer.isRunning()) {
            updateTimer.start();
        }
    }

    /**
     * Stops the UI refresh timer if it is currently running.
     * This prevents unnecessary updates when page is not visible.
     */
    public void stopTimer() {
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

    // ===================== INITIAL REFRESH (PAGE SHOWN) =====================

    /**
     * Performs a complete refresh at all UI components when the page becomes visible.
     * This includes rebuilding runway displays and updating control panel states.
     */
    private void refreshFromController() {
        if (simController != null) {
            refreshRunwayDisplay();     // Refresh the runway display
            refreshUI();
        } else {
            System.err.println("Controller not set yet");
        }
    }

    /**
     * Rebuilds the runway display by recreating all runway cards from the current simulation state.
     * This is called when the page is first shown.
     */
    private void refreshRunwayDisplay() {
        runwaysContainer.removeAll();
        runwayCards.clear();

        // Use list of RunwayStates to pass in runways to each card
        List<RunwayState> runwayStates = simController.getStateSnapshot().getRunways();
        for (RunwayState runway : runwayStates) {
            RunwayCard card = new RunwayCard(runway, this, simController);
            runwayCards.add(card);
            runwaysContainer.add(card);
            runwaysContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        runwaysContainer.revalidate();
        runwaysContainer.repaint();
    }

    /**
     * Updates the control panel to reflect the current simulation state.
     * This includes setting the correct speed button and pause/resume button text.
     */
    private void refreshControlPanel() {
        double speed = simController.getStateSnapshot().getSpeedMultiplier();

        if (speed == 1) {
            x1Button.setSelected(true);
            setButtonClicked(x1Button);
        } else if (speed == 100) {
            x100Button.setSelected(true);
            setButtonClicked(x100Button);
        } else if (speed == 10) {
            x10Button.setSelected(true);
            setButtonClicked(x10Button);
        } else if (speed == 50) {
            x50Button.setSelected(true);
            setButtonClicked(x50Button);
        }

        if (simController.getStateSnapshot().isPaused()) {
            startPauseLabel.setText("Resume");
        } else {
            startPauseLabel.setText("Pause");
        }
    }

    // ================== PERIODIC UI REFRESH (EVERY 500MS) ==================

    /**
     * Performs incremental UI updates that should happen every refresh interval.
     * This includes updating runway occupancy, stats panels, and the clock.
     */
    private void refreshUI() {
        // Refresh the runway card every second using recent data
        refreshRunwayDisplayEverySec();
        refreshStatsPanelEverySec();
        refreshClock();

        revalidate();
        repaint();
    }

    /**
     * Updates the occupancy status of all runway cards.
     * This is called on every refresh cycle to show real-time changes.
     */
    private void refreshRunwayDisplayEverySec() {
        for (RunwayCard runwayCard : runwayCards) {
            runwayCard.updateOccupiedLabel();
        }
    }

    /**
     * Updates all statistics panel with the latest metrics from simulation.
     * Performs calculations for average delays and format values appropriately.
     */
    private void refreshStatsPanelEverySec() {
        Metrics currentMetrics = simController.getStateSnapshot().getMetrics();

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

    /**
     * Updates the clock display with the current simulation time.
     * Formats time as HHh MMm SSs
     */
    private void refreshClock() {
        SimState state = simController.getStateSnapshot();

        String hhmm = state.getSimTimeHHMM();
        String[] hhmmParts = hhmm.split(":");

        if (hhmmParts.length == 2) {
            simHour = hhmmParts[0] + "h";
            simMinute = hhmmParts[1] + "m";

            // Extract seconds from total seconds
            double totalSeconds = state.getSimTimeSeconds();
            int seconds = (int) totalSeconds % 60;
            simSecond = String.format("%02ds", seconds);

            timeHourLabel.setText(simHour);
            timeMinuteLabel.setText(simMinute);
            timeSecondLabel.setText(simSecond);
        }
    }

    // ================== SIMULATION END DETECTION ==================

    /**
     * Checks weather the simulation has reached its configured duration.
     * If so, stops the timer, sets the completion flag, and navigates to the results page.
     */
    private void checkSimulationEnded() {
        SimState state = simController.getStateSnapshot();
        if (state == null) return;

        double currentTime = state.getSimTimeSeconds();
        long duration = simController.getDurationSim();

        if (currentTime >= duration) {
            simulationEnded = true;
            updateTimer.stop();
            app.showResultsPage();
        }
    }

    /**
     * Resets the simulation ended flag, allowing page to be reused for a new simulation.
     * This should be called when starting a new simulation from input page
     * or when user wants to navigate back to simulation page from results.
     */
    public void resetSimulationEndedFlag() {
        simulationEnded = false;
    }


    // ===================== CONTENT PANEL CREATION =====================

    /**
     * Creates the main content panel for simulation page.
     *
     * <p>
     *     The content panel uses a horizontal BoxLayout to arrange three columns:
     *     <ul>
     *         <li><b>Left column:</b> Statistics panel</li>
     *         <li><b>Center column:</b> Control buttons and runway display</li>
     *         <li><b>Right column:</b> Clock display and additional navigation buttons</li>
     *     </ul>
     * </p>
     *
     * @return a JPanel containing the three-column layout for the simulation display
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(Color.white);

        // Create the three main columns
        JPanel leftContentColumn = createStatsPanel();          // LEFT - Stats
        JPanel centerContentColumn = createCenterColumnPanel(); // CENTER - Control + Runways
        JPanel rightContentColumn = createRightColumnPanel();   // RIGHT - Clock + Nav. Buttons

        // Adding columns with appropriate spacing
        addPanelXAxis(contentPanel, leftContentColumn);
        addPanelXAxis(contentPanel, centerContentColumn);
        contentPanel.add(rightContentColumn);   // Last column doesn't need spacer

        return contentPanel;
    }

    // ===================== STATS PANEL =====================

    /**
     * Creates the left statistics column containing all simulation metrics.
     * The panel uses a vertical BoxLayout to stack statistics panels.
     * The panels are arrange with vertical glue to glue them to the bottom,
     * and consistent spacing between panels.
     *
     * @return a JPanel containing all statistics displays
     * @see StatsPanel
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setMinimumSize(new Dimension(STATS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));
        panel.setMaximumSize(new Dimension(STATS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));

        // Initialise all statistics panel
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

    // ===================== CENTRE PANEL CREATION =====================

    /**
     * Creates the centre column containing simulation controls and runway display.
     * The panel uses a vertical BoxLayout to stack:
     * <ol>
     *     <li>Control panel</li>
     *     <li>Runway display area</li>
     * </ol>
     *
     * @return a JPanel containing the centre column components
     * @see #createControlPanel()
     * @see #createRunwayPanel()
     */
    private JPanel createCenterColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, CONTENT_PANEL_HEIGHT));

        // Create sub-panels
        JPanel controlPanel = createControlPanel();
        JPanel runwayPanel = createRunwayPanel();

        // Assemble with spacing
        panel.add(Box.createVerticalGlue());
        panel.add(controlPanel);
        panel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_5)));
        panel.add(runwayPanel);

        return panel;
    }

    // ===================== CONTROL PANEL =====================

    /**
     * Creates the control panel containing simulation control buttons and speed controls
     * This panel uses a horizontal BoxLayout to arrange:
     * <ul>
     *     <li>Pause/Resume button</li>
     *     <li>Reset button</li>
     *     <li>Finish Simulation button</li>
     *     <li>Speed mutiplier controls</li>
     * </ul>
     *
     * @return a JPanel containing all simulation control components
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.white);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 40));
        controlPanel.setMaximumSize(new Dimension(CENTER_COLUMN_WIDTH, 40));
        controlPanel.setMinimumSize(new Dimension(CENTER_COLUMN_WIDTH, 40));

        // Start button
        startPauseButton = new StyledButton("", Color.black, new Color(0x333333), Color.black, Color.black);
        startPauseButton.setLayout(new BorderLayout());
        startPauseButton.setFocusPainted(false);
        startPauseButton.setBackground(Color.black);
        startPauseButton.setButtonSize(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT);
        startPauseButton.addActionListener(e -> toggleStartButton());

        startPauseLabel = new JLabel("Pause", JLabel.CENTER);
        startPauseLabel.setFont(ARIAL_BOLD_16);
        startPauseLabel.setForeground(Color.white);
        startPauseButton.add(startPauseLabel, BorderLayout.CENTER);

        // Reset button
        resetButton = new StyledButton("Reset", Color.black, new Color(0x333333), Color.black, Color.black);
        resetButton.setButtonSize(CONTROL_BUTTON_WIDTH, CONTROL_BUTTON_HEIGHT);
        resetButton.setFont(ARIAL_BOLD_16);
        resetButton.addActionListener(e -> resetSimulation());

        // Finish button
        finishButton = new StyledButton("Finish Sim.", new Color(0, 128, 128), new Color(0, 150, 150), new Color(0, 100, 100), new Color(0, 70, 70));
        finishButton.setButtonSize(120, CONTROL_BUTTON_HEIGHT);
        finishButton.setFont(ARIAL_BOLD_16);
        finishButton.addActionListener(e -> finishSimulation());

        // Create speed control panel
        JPanel speedupPanel = createSpeedPanel();

        // Adding components with appropriate spacing
        addPanelXAxis(controlPanel, startPauseButton);
        addPanelXAxis(controlPanel, resetButton);
        addPanelXAxis(controlPanel, finishButton);
        controlPanel.add(speedupPanel);

        return controlPanel;
    }

    // ===================== BUTTON ACTION HANDLERS (CONTROL) =====================

    /**
     * Toggles the simulation between paused and running states.
     * Updates the button label to reflect the current state.
     */
    private void toggleStartButton() {
        boolean isPaused = simController.getStateSnapshot().isPaused();
        if (!isPaused) {
            simController.pauseSimulation();
            startPauseLabel.setText("Resume");
        } else {
            simController.resumeSimulation();
            startPauseLabel.setText("Pause");
        }
    }

    /**
     * Resets the simulation to its initial state.
     * Ensures th pause button shows the correct "Pause" text after reset.
     */
    private void resetSimulation() {
        simController.resetSimulation();
        startPauseLabel.setText("Pause");
    }

    /**
     * Handles the finish simulation button action.
     * If the simulation has already ended naturally, immediately shows the results page.
     * Otherwise, sets the simulation speed to a very high value to rapidly complete simulation.
     */
    private void finishSimulation() {
        if (simulationEnded) {
            app.showResultsPage();
            return;
        }
        simController.setSpeed(10000);
    }

    // ===================== SPEEDUP PANEL =====================

    /**
     * Creates the speed multiplier control panel with toggle buttons.
     * The panel provides four speed options (x1, x10, x50, x100).
     * The buttons are styled to show a black background when selected.
     *
     * @return a JPanel containing speed control buttons
     */
    private JPanel createSpeedPanel() {
        JPanel speedupPanel = new JPanel(new GridBagLayout());
        speedupPanel.setBackground(Color.white);

        JLabel speedupLabel = new JLabel("Speed: ");
        speedupLabel.setForeground(Color.black);
        speedupLabel.setFont(ARIAL_BOLD_16);

        // Create radio buttons
        x1Button = new JToggleButton("x1");
        x10Button = new JToggleButton("x10");
        x50Button = new JToggleButton("x50");
        x100Button = new JToggleButton("x100");

        List<JToggleButton> speedButtons = new ArrayList<>();
        speedButtons.add(x1Button);
        speedButtons.add(x10Button);
        speedButtons.add(x50Button);
        speedButtons.add(x100Button);

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

        // Add ActionListeners for speed changes
        x1Button.addActionListener(e -> setSimulationSpeed(1));
        x10Button.addActionListener(e -> setSimulationSpeed(10));
        x50Button.addActionListener(e -> setSimulationSpeed(50));
        x100Button.addActionListener(e -> setSimulationSpeed(100));

        // Layout components using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        speedupPanel.add(speedupLabel);

        gbc.gridx = 1; speedupPanel.add(Box.createRigidArea(new Dimension(5, 0)), gbc); // Spacer
        gbc.gridx = 2; speedupPanel.add(x1Button);
        gbc.gridx = 3; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 4; speedupPanel.add(x10Button);
        gbc.gridx = 5; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 6; speedupPanel.add(x50Button);
        gbc.gridx = 7; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 8; speedupPanel.add(x100Button);

        return speedupPanel;
    }

    /**
     * Updates the appearance of a speed button based on its selection state.
     *
     * @param btn the toggle button to update
     */
    private void setButtonClicked(JToggleButton btn) {
        if (btn.isSelected()) {
            btn.setBackground(Color.black);
            btn.setForeground(Color.white);
        } else {
            btn.setBackground(Color.white);
            btn.setForeground(Color.black);
        }
    }

    /**
     * Sets the simulation speed multiplier in the controller
     *
     * @param speed the speed multipler value
     */
    private void setSimulationSpeed(int speed) {
        simController.setSpeed(speed);
    }

    // ===================== RUNWAY DISPLAY PANEL =====================

    /**
     * Creates the panel that displays all runways in a scrollable container.
     * The panel contains a {@link JScrollPane} wrapping a vertical BoxLayout container
     * that holds individual {@link RunwayCard} components for each runway.
     */
    private JPanel createRunwayPanel() {
        JPanel runwayPanel = new JPanel();
        runwayPanel.setBackground(Color.white);
        runwayPanel.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 475));

        // Main container for all the runways
        runwaysContainer = new JPanel();
        runwaysContainer.setBackground(Color.white);
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Scroll pane for the runway container
        JScrollPane scrollPaneRunwaysContainer = new JScrollPane(runwaysContainer);
        scrollPaneRunwaysContainer.setBackground(Color.white);
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(CENTER_COLUMN_WIDTH, 470));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        runwayPanel.add(scrollPaneRunwaysContainer);
        return runwayPanel;
    }

    // ===================== RIGHT PANEL CREATION =====================

    /**
     * Creates the right column containing the clock display and navigation buttons.
     * The panel uses a vertical BoxLayout to arrange:
     * <ul>
     *     <li>Clock display showing elapsed simulation time</li>
     *     <li>Navigation buttons for other pages</li>
     * </ul>
     *
     * @return a JPanel containing the right column components
     */
    private JPanel createRightColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, CONTENT_PANEL_HEIGHT));

        // Clock panel
        JPanel clockPanel = createClockPanel();

        // Buttons panel
        JPanel buttonsPanel = createNavigationButtonPanel();

        // Adding control panel and buttons panel into panel
        panel.add(Box.createVerticalGlue());
        panel.add(clockPanel);
        panel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));    // Gap in between
        panel.add(buttonsPanel);

        return panel;
    }

    /**
     * Creates the panel containing the clock simulation showing the elapsed simulation time.
     * The clock is formatted in HHh MMm SSs format.
     *
     * @return a JPanel containing the clock
     */
    private JPanel createClockPanel() {
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.X_AXIS));
        clockPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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

    /**
     * Creates the panel containing navigation buttons for different flight views.
     *
     * @return a JPanel containing all navigation buttons.
     */
    private JPanel createNavigationButtonPanel() {
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

        return buttonsPanel;
    }


    // ===================== NAVIGATION METHODS =====================

    /**
     * Navigates to the page displaying flights that will soon arrive.
     */
    private void showFlightsSoonArrivingPage() {
        app.showSoonArrivingPage();
    }

    /**
     * Navigates to the page displaying flights that will soon depart.
     */
    private void showFlightsSoonDepartingPage() {
        app.showSoonDepartingPage();
    }

    /**
     * Navigates to the page displaying aircraft in holding pattern.
     */
    private void showHoldingPatternPage() {
        app.showHoldingPatternPage();
    }

    /**
     * Navigates to the page displaying aircraft in takeoff queue.
     */
    private void showTakeoffQueuePage() {
        app.showTakeoffQueuePage();
    }

    /**
     * Navigates to the page displaying flights that been processed.
     */
    private void showProcessedFlightsPage() {
        app.showPostProcessingPage();
    }


    // ===================== LAYOUT HELPER METHODS =====================

    /**
     *  Adds a component to a vertically-oriented BoxLayout container with automatic spacing below it.
     *
     * @param container the JPanel using BoxLayout.Y_AXIS
     * @param comp the component to add to the container
     */
    private void addPanelYAxis(JPanel container, Component comp) {
        container.add(comp);
        container.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
    }

    /**
     *  Adds a component to a horizontally-oriented BoxLayout container with automatic spacing to the right of it.
     *
     * @param container the JPanel using BoxLayout.Y_AXIS
     * @param comp the component to add to the container
     */
    private void addPanelXAxis(JPanel container, Component comp) {
        container.add(comp);
        container.add(Box.createRigidArea(new Dimension(SPACER_SIZE_10, 0)));
    }
}
