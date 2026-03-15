package sim.view.pages;


import sim.config.SimConfig;
import sim.config.SimConfigFactory;
import sim.config.SimConfigWriter;
import sim.core.Engine;
import sim.core.EngineOptions;
import sim.core.SimClock;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimulationSetup;
import sim.view.components.*;
import sim.view.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The input configuration page where users set up simulation parameters and runways.
 *
 * <p>
 *     This page allows users to:
 *     <ul>
 *          <li>Configure inbound/outbound rates and simulation duration</li>
 *          <li>Add and remove runways dynamically (up to 10)</li>
 *          <li>Configure individual runway settings</li>
 *          <li>Start the simulation with specified parameters</li>
 *     </ul>
 * </p>
 *
 * @see BasicPage
 * @see SimController
 * @see RunwaySetup
 */
public class InputPage extends BasicPage {
    // ===================== CONSTANTS =====================
    private static final int SPACER_SIZE_10 = 10;
    private static final int FORM_CONTENT_WIDTH = 640;
    private final int MAX_RUNWAYS = 10;
    String[] RUNWAY_IDS = {
            "RWY-01",
            "RWY-02",
            "RWY-03",
            "RWY-04",
            "RWY-05",
            "RWY-06",
            "RWY-07",
            "RWY-08",
            "RWY-09",
            "RWY-10"
    };
    private static final Color BACKGROUND_FORM_COLOR = new Color(100, 150, 200);
    private static final Font ARIAL_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font ARIAL_BOLD_18 = new Font("Arial", Font.BOLD, 18);
    private static final Font ARIAL_PLAIN_18 = new Font("Arial", Font.PLAIN, 18);

    // ===================== INSTANCE VARIABLES =====================
    private final App app;
    private final SimController simController;
    private final Map<Integer, RunwayInputPanel> runwayPanels = new HashMap<>();
    private final List<RunwaySetup> runwaySetups = new ArrayList<>();

    // ===================== UI COMPONENTS =====================
    JPanel runwaysContainer;
    StyledButton addRunwayButton;
    StyledButton removeRunwayButton;
    JTextField inboundRateField;
    JTextField outboundRateField;
    JTextField durationField;

    // ===================== STATE VARIABLES =====================
    int numRunways = 1;

    // ===================== CONSTRUCTOR =====================

    /**
     * Constructs a new InputPage with the specified application and controller references.
     *
     * @param app the main application instance for navigation
     * @param simController the controller for managing simulation state
     */
    public InputPage(App app, SimController simController) {
        this.app = app;
        this.simController = simController;

        buildPage(createContentPanel());
    }

    // ===================== CONTENT PANEL CREATION =====================

    /**
     * Creates the main content panel containing all input forms and controls
     * The panel uses a vertical BoxLayout to stack:
     * <ol>
     *     <li>Title panel</li>
     *     <li>Simulation configuration panel</li>
     *     <li>Runway configuration panel</li>
     *     <li>Start simulation button</li>
     * </ol>
     *
     * @return the complete content panel for the input page
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);

        // Main form container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(700, 500));
        formPanel.setBackground(BACKGROUND_FORM_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        // Create and assemble all sub-panels
        JPanel titlePanel = createTitlePanel();                 // 1. TITLE PANEL
        JPanel simConfigPanel = createSimConfigPanel();         // 2. SIMULATION CONFIG PANEL
        JPanel runwayConfigPanel = createRunwayConfigPanel();   // 3. RUNWAY CONFIG PANEL
        JPanel startSimPanel = createStartSimPanel();           // 4. START SIMULATION BUTTON

        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(simConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(runwayConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(startSimPanel);

        contentPanel.add(formPanel);
        return contentPanel;
    }

    /**
     * Creates the title panel for the input page.
     *
     * @return a panel containing the page title
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(500, 70));
        panel.setPreferredSize(new Dimension(500, 70));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        JLabel titleLabel = new JLabel("Create a new Simulation");
        titleLabel.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 30));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }


    /**
     * Creates the simulation configuration panel containing input fields for
     * inbound rate, outbound rate, and duration
     *
     * @return a panel with simulation parameter input fields
     */
    private JPanel createSimConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_FORM_COLOR);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 2, 0);

        // Row 1: Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel simConfigTitle = new JLabel("Simulation Configuration");
        simConfigTitle.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));
        simConfigTitle.setForeground(Color.black);
        panel.add(simConfigTitle, gbc);

        gbc.insets = new Insets(5, 13, 2, 0);

        // Row 2: Inbound rate
        inboundRateField = new JTextField("15");
        addFormField(panel, gbc, "Inbound Rate (aircraft/hour)", inboundRateField, 1);

        // Row 3: Outbound rate
        outboundRateField = new JTextField("15");
        addFormField(panel, gbc, "Outbound Rate (aircraft/hour)", outboundRateField, 2);

        // Row 4: Duration
        durationField = new JTextField("2");
        addFormField(panel, gbc, "Duration (hour)", durationField, 3);

        return panel;
    }

    /**
     * Helper method to add a labelled text field to a GridBagLayout container
     * This method creates a label and a text field in adjacent columns at the specified row,
     * with appropriate weight distribution.
     *
     * @param container the panel to which the form field will be added
     * @param gbc the GridBagConstraints object
     * @param labelText the text to display on label
     * @param field the JTextField component to add
     * @param y the grid row position
     */
    private void addFormField(JPanel container, GridBagConstraints gbc, String labelText, JTextField field, int y) {
        // Label
        gbc.gridx = 0; gbc.gridy = y;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(ARIAL_PLAIN_18);
        label.setForeground(Color.black);
        container.add(label, gbc);

        // Field
        gbc.gridx = 1; gbc.gridy = y;
        gbc.weightx = 0.7;
        field.setColumns(15);
        field.setFont(ARIAL_PLAIN_18);
        container.add(field, gbc);
    }


    /**
     * Creates the runway configuration panel that allows users to dynamically
     * add and remove runways. The panel includes:
     * <ul>
     *     <li>A title with "Add" and "Remove" buttons</li>
     *     <li>A scrollable container for runway input panels</li>
     * </ul>
     *
     * @return a panel for configuring runways
     */
    private JPanel createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_FORM_COLOR);
        panel.setPreferredSize(new Dimension(FORM_CONTENT_WIDTH, 270));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_FORM_COLOR);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setPreferredSize(new Dimension(FORM_CONTENT_WIDTH, 30));

        JLabel titleLabel = new JLabel("Runway Configuration");
        titleLabel.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));
        titleLabel.setForeground(Color.black);

        addRunwayButton = new StyledButton("+ Add Runways", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        addRunwayButton.setFont(ARIAL_BOLD_14);
        addRunwayButton.setButtonSize(150, 25);
        addRunwayButton.addActionListener(e -> addNewRunway());

        removeRunwayButton = new StyledButton("Remove Runway", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        removeRunwayButton.setFont(ARIAL_BOLD_14);
        removeRunwayButton.setButtonSize(150, 25);
        removeRunwayButton.setEnabled(false);
        removeRunwayButton.addActionListener(e -> deleteRunway(numRunways));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(addRunwayButton);
        titlePanel.add(Box.createRigidArea(new Dimension(SPACER_SIZE_10, 0)));
        titlePanel.add(removeRunwayButton);

        // runway Panel
        runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add the default Runway 1
        int newId = getNextAvailableId();
        String runwayId = RUNWAY_IDS[newId - 1];
        RunwaySetup runwaySetup = new RunwaySetup(runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE);
        runwaySetups.add(runwaySetup);

        RunwayInputPanel runwayInputPanel = new RunwayInputPanel(runwaySetup);
        runwayPanels.put(newId, runwayInputPanel);
        runwaysContainer.add(runwayInputPanel);

        JScrollPane scrollPaneRunways = new JScrollPane(runwaysContainer);
        scrollPaneRunways.setPreferredSize(new Dimension(FORM_CONTENT_WIDTH, 255));
        scrollPaneRunways.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(titlePanel);
        panel.add(scrollPaneRunways);
        return panel;
    }

    /**
     * Adds a new runway to the configuration
     *
     * <p>
     *     This method:
     *     <ul>
     *         <li>Checks if the maximum number of runway has not been reached</li>
     *         <li>Generates the new available runway ID</li>
     *         <li>Creates a new {@link RunwaySetup} object</li>
     *         <li>Creates and adds a corresponding {@link RunwayInputPanel} to the UI</li>
     *     </ul>
     * </p>
     */
    private void addNewRunway() {
        if (numRunways < MAX_RUNWAYS) {
            removeRunwayButton.setEnabled(true);    // Enable the delete button once add
            removeRunwayButton.setToolTipText("Remove the last runway");

            int newId = getNextAvailableId();
            String runwayId = RUNWAY_IDS[newId - 1];
            System.out.println("Adding runway panel for: " + runwayId);
            RunwaySetup runwaySetup = new RunwaySetup(runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE);
            runwaySetups.add(runwaySetup);

            // Creating the RunwayPanel for that runway
            RunwayInputPanel panel = new RunwayInputPanel(runwaySetup);
            runwayPanels.put(newId, panel);
            runwaysContainer.add(panel);

            numRunways++;

            // Refresh the UI
            runwaysContainer.revalidate();
            runwaysContainer.repaint();

            // Disable the add button if number of runways reach max
            if (numRunways >= MAX_RUNWAYS) {
                addRunwayButton.setEnabled(false);
                addRunwayButton.setToolTipText("Maximum number of runways reached (10)");
            }
        }
    }

    /**
     * Removes the specified runway from the configuration.
     *
     * <p>
     *     This method:
     *     <ul>
     *         <li>Ensures at least one runway remains</li>
     *         <li>Removes the runway panel from the UI container</li>
     *         <li>Removes the runway from the {@link #runwayPanels} map</li>
     *         <li>Removes the corresponding {@link RunwaySetup} from the list</li>
     *         <li>Updates button states and tooltips based on the new runway count</li>
     *     </ul>
     * </p>
     *
     * @param id the numeric identifier of the runway to remove
     */
    private void deleteRunway(int id) {
        if (numRunways > 1) {
            RunwayInputPanel runwayRemoved = runwayPanels.get(id); // Remove and get the highest ID RunwayPanel

            // Remove from the runwaysContainer
            if (runwayRemoved != null) {
                runwaysContainer.remove(runwayRemoved);
            }
            runwayPanels.remove(id);

            // Remove from runwaySetups List
            String targetId = RUNWAY_IDS[id - 1];
            System.out.println("Deleting runway panel for: " + targetId);

            for (RunwaySetup runwaySetup : runwaySetups) {
                if (runwaySetup.getId().equals(targetId)) {
                    runwaySetups.remove(runwaySetup);
                    break;
                }
            }

            addRunwayButton.setEnabled(true);   // Enable the addRunwayButton
            addRunwayButton.setToolTipText("Add a new runway");
            numRunways--;   // Decrement number of runways

            // Refresh the UI
            runwaysContainer.revalidate();
            runwaysContainer.repaint();

            // Update the button state
            if (numRunways < 2) {
                removeRunwayButton.setEnabled(false);
                removeRunwayButton.setToolTipText("Minimum one runway required");
            }
        }
    }

    /**
     * Finds the next available numeric ID for a new runway.
     * <p>
     *     IDs range from 1 to 10 and correspond to the {@link #RUNWAY_IDS} array.
     *     An ID is considered available if no existing {@link RunwaySetup} has that ID.
     * </p>
     *
     * This method ensures that IDs are reused when runways are deleted, maintaining a compact sequence
     *
     * @return the smallest unused runway ID, or -1 if no IDs are available (should only happen if
     * {@link #MAX_RUNWAYS} is exceeded)
     */
    private int getNextAvailableId() {
        for (int id = 1; id <= MAX_RUNWAYS; id++) {
            boolean found = false;
            String targetId = RUNWAY_IDS[id - 1];

            for (RunwaySetup runwaySetup: runwaySetups) {
                if (runwaySetup.getId().equals(targetId)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return id;
            }
        }
        return -1;  // no id found
    }


    /**
     * Prints the current list of runway configurations to the console for debugging.
     * This method is useful for verifying that runway configurations are being created
     * and removed correctly during development.
     */
    private void printRunwayObjects() {
        System.out.println("=== Runway List Contents ===");
        for (RunwaySetup runway : runwaySetups) {
            System.out.println("  - Runway ID: " + runway.getId());
            System.out.println("  - Mode: " + runway.getMode());
            System.out.println("  - Status: " + runway.getStatus());
            //System.out.println("  - Time Remaining: " + runway.getTimeRemaining());
            System.out.println();
        }

        System.out.println("Total elements: " + numRunways);
        System.out.println("============================");
    }


    /**
     * Creates the panel containing the "Start Simulation" button.
     *
     * @return a JPanel containing the styled start button
     * */
    private JPanel createStartSimPanel()  {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_FORM_COLOR);
        panel.setMinimumSize(new Dimension(FORM_CONTENT_WIDTH, 30));

        StyledButton button = new StyledButton(
                "Start Simulation",
                Color.black,
                new Color(0x333333),
                new Color(0x555555),
                Color.black
        );
        button.setFont(ARIAL_BOLD_18);
        button.setButtonSize(200, 30);
        button.addActionListener(e -> startSimulation());

        panel.add(button);
        return panel;
    }


    // ===================== SIMULATION INITIALISATION =====================

    private static final int MAX_RATE = 150;
    private static final int MAX_DURATION = 100;
    private static final int MIN_VALUE = 1;

    /**
     * Validate user inputs and initialises a new simulation
     *
     * <p>
     *     This method performs the following steps:
     *     <ol>
     *         <li>Validates at least one runway is available</li>
     *         <li>Parses and validates input fields (inbound, outbound, duration)</li>
     *         <li>Checks values against the defined minimum and maximum thresholds</li>
     *         <li>Creates a {@link SimulationSetup} object with the configured parameters</li>
     *         <li>Adds all configured runways to the setup</li>
     *         <li>Initialises the simulation engine and controller</li>
     *         <li>Navigates to the simulation page</li>
     *     </ol>
     * </p>
     *
     * @throws NumberFormatException if input fields contain non-numeric values
     */
    private void startSimulation() {
        try {
            // Step 1: Validate runway availability
            if (!atLeastRunwayAvailable()) {
                showWarningDialog(
                        "At least one runway must be available"
                );
                return;
            }

            // Step 2: Parse input values
            int inboundRate = Integer.parseInt(inboundRateField.getText());
            int outboundRate = Integer.parseInt(outboundRateField.getText());
            int duration = Integer.parseInt(durationField.getText());

            // Step 3: Validate numeric ranges
            if (!validateInputRanges(inboundRate, outboundRate, duration)) {
                return;
            }

            // Step 4: Debug output
            printSimulationParameters(inboundRate, outboundRate, duration);
            printRunwayObjects();

            // Step 5: Create and configure simulation setup
            SimulationSetup setup = createSimulationSetup(inboundRate, outboundRate, duration);

            // Step 6: Initialise engine and start simulation
            initialiseAndStartEngine(setup);

            // Step 7: Navigate to simulation page
            app.showSimulationPage();

        } catch (NumberFormatException ex) {
            showErrorDialog(
                    "Please enter a valid number."
            );
        }
    }

    /**
     * Validates that all input values are within acceptable ranges
     *
     * @param inboundRate the inbound aircraft rate per hour
     * @param outboundRate the outbound aircraft rate per hour
     * @param duration the simulation duration in hours
     * @return true if all values are valid, false otherwise
     * */
    private boolean validateInputRanges(int inboundRate, int outboundRate, int duration) {
        // Check minimum values
        if (inboundRate < MIN_VALUE || outboundRate < MIN_VALUE || duration < MIN_VALUE) {
            showWarningDialog(
                    "Each field should be at least " + MIN_VALUE
            );
            return false;
        }

        // Check maximum values
        if (inboundRate > MAX_RATE) {
            showWarningDialog(
                    "Inbound Rate exceeds maximum (" + MAX_RATE + ")"
            );
            return false;
        }

        if (outboundRate > MAX_RATE) {
            showWarningDialog(
                    "Outbound Rate exceeds maximum (" + MAX_RATE + ")"
            );
            return false;
        }

        if (duration > MAX_DURATION) {
            showWarningDialog(
                    "Duration exceeds maximum (" + MAX_DURATION + ")"
            );
            return false;
        }
        return true;
    }

    /**
     * Creates and configures a SimulationSetup object with the provided parameters.
     *
     * @param inboundRate the inbound aircraft rate per hour
     * @param outboundRate the outbound aircraft rate per hour
     * @param duration the simulation duration in hours
     * @return a filly configured SimulationSetup instance
     * */
    private SimulationSetup createSimulationSetup(int inboundRate, int outboundRate, int duration) {
        SimulationSetup setup = new SimulationSetup();

        // Basic Parameters
        setup.setArrivalRatePerHour(inboundRate);
        setup.setDepartureRatePerHour(outboundRate);
        setup.setMaxRunways(MAX_RUNWAYS);

        // Time parameters
        long seconds = duration * 3600L;
        simController.setDurationSim(seconds);
        setup.setDurationSeconds(seconds);
        setup.setDtSeconds(1.0);

        setup.setSpeedMultiplier(1.0);
        setup.setSeed(42L);
        setup.setPrintEverySeconds(60);
        setup.setCsvPath(java.nio.file.Path.of("output.csv"));

        // Adding configured runways
        for (RunwaySetup runwaySetup : runwaySetups) {
            setup.addRunway(runwaySetup);
        }

        return setup;
    }

    /**
     * Initializes the simulation engine and starts the simulation
     *
     * @param setup the configured SimulationSetup object
     * */
    private void initialiseAndStartEngine(SimulationSetup setup) {
        // Create configuration objects
        SimConfig cfg = SimConfigFactory.fromSetup(setup);
        EngineOptions opts = SimConfigFactory.engineOptionsFromSetup(setup);
        SimClock clock = new SimClock(setup.getDtSeconds());

        // Create and start engine
        Engine engine = new Engine(cfg, opts, clock);
        simController.setEngine(engine);
        simController.startSimulation();
    }


    /**
     * Checks whether at least one configured runway has AVAILABLE status.
     *
     * @return true if at least one runway is available, false otherwise
     */
    private boolean atLeastRunwayAvailable() {
        for (RunwaySetup runwaySetup : runwaySetups) {
            if (runwaySetup.getStatus() == SimConfig.RunwayStatus.AVAILABLE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shows a warning dialog with the specified message and title
     *
     * @param message the message to display
     */
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Configuration Incomplete",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Shows a warning dialog with the specified message
     *
     * @param message the message to display
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Prints the simulation parameters to the console for debugging purposes
     *
     * @param inboundRate the inbound aircraft rate per hour
     * @param outboundRate the outbound aircraft rate per hour
     * @param duration the simulation duration in hours
     * */
    private void printSimulationParameters(int inboundRate, int outboundRate, int duration) {
        System.out.println("=== Simulation Parameters ===");
        System.out.println("Duration: " + duration + "hours");
        System.out.println("Inbound rate: " + inboundRate + "aircraft/hour");
        System.out.println("Outbound rate: " + outboundRate + "aircraft/hour");
        System.out.println("Runways configured: " + numRunways);
        System.out.println("=============================");
    }

}

