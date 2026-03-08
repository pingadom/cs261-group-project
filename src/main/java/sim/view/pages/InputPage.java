package sim.view.pages;


import sim.config.SimConfig;
import sim.config.SimConfig.RunwayStatus;
import sim.config.SimConfigFactory;
import sim.core.Engine;
import sim.core.EngineOptions;
import sim.core.SimClock;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimulationSetup;
import sim.model.stores.Runway;
import sim.view.components.*;
import sim.view.App;
import sim.view.controllers.PageDataController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputPage extends BasicPage {
    // Constants
    private static final int SPACER_SIZE_10 = 10;
    private static final int FORM_CONTENT_WIDTH = 640;
    private final int MAX_RUNWAYS = 10;

    private static final Color BACKGROUND_FORM_COLOR = new Color(100, 150, 200);
    private static final Font ARIAL_BOLD_14 = new Font("Arial", Font.BOLD, 14);
    private static final Font ARIAL_BOLD_18 = new Font("Arial", Font.BOLD, 18);
    private static final Font ARIAL_PLAIN_18 = new Font("Arial", Font.PLAIN, 18);

    // Instance variables
    private final App app;
    private final PageDataController dataController;
    private final Map<Integer, RunwayInputPanel> runwayPanels = new HashMap<>();
    private final List<Runway> runways = new ArrayList<>();
    private final List<RunwaySetup> runwaySetups = new ArrayList<>();

    private final sim.model.stores.List<Runway> runwaysList = new sim.model.stores.List<>();

    // UI Components
    JPanel runwaysContainer;
    StyledButton addRunwayButton;
    StyledButton removeRunwayButton;
    JTextField inboundRateField;
    JTextField outboundRateField;
    JTextField durationField;

    // Static variables
    int numRunways = 1;
    String[] runwayIDs = {
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

    // Constructor
    public InputPage(App app, PageDataController dataController) {
        this.app = app;
        this.dataController = dataController;
        buildPage(createContentPanel());
    }

    // Content Panel
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);

        // formPanel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(700, 500));
        formPanel.setBackground(BACKGROUND_FORM_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        JPanel titlePanel = createTitlePanel();     // 1. TITLE PANEL
        JPanel simConfigPanel = createSimConfigPanel();     // 2. SIMULATION CONFIG PANEL
        JPanel runwayConfigPanel = createRunwayConfigPanel();   // 3. RUNWAY CONFIG PANEL
        JPanel startSimPanel = createStartSimPanel();   // 4. START SIMULATION BUTTON

        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(simConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(runwayConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, SPACER_SIZE_10)));
        formPanel.add(startSimPanel);

        // add formPanel into the contentPanel
        contentPanel.add(formPanel);
        return contentPanel;
    }

    // TITLE Panel
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


    // SIMULATION CONFIG Panel
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
        inboundRateField = new JTextField("8");
        addFormField(panel, gbc, "Inbound Rate (aircraft/hour)", inboundRateField, 1);
        // Row 3: Outbound rate
        outboundRateField = new JTextField("8");
        addFormField(panel, gbc, "Outbound Rate (aircraft/hour)", outboundRateField, 2);
        // Row 4: Duration
        durationField = new JTextField("8");
        addFormField(panel, gbc, "Duration (hour)", durationField, 3);

        return panel;
    }

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


    // RUNWAY CONFIG Panel
    private JPanel createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_FORM_COLOR);
        panel.setPreferredSize(new Dimension(FORM_CONTENT_WIDTH, 270));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // TITLE Panel
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

        // RUNWAY PANEL
        runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add the default Runway 1
        int newId = getNextAvailableId();
        String runwayId = runwayIDs[newId - 1];
        Runway runway = new Runway(newId, runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE, 0);
        runways.add(runway);    // Add Runway object into list

        RunwaySetup runwaySetup = new RunwaySetup(runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE);
        runwaySetups.add(runwaySetup);

        RunwayInputPanel runwayInputPanel = new RunwayInputPanel(runway, runwaySetup);
        runwayPanels.put(newId, runwayInputPanel);
        runwaysContainer.add(runwayInputPanel);

        JScrollPane scrollPaneRunways = new JScrollPane(runwaysContainer);
        scrollPaneRunways.setPreferredSize(new Dimension(FORM_CONTENT_WIDTH, 255));
        scrollPaneRunways.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(titlePanel);
        panel.add(scrollPaneRunways);
        return panel;
    }

    // Runway addition
    private void addNewRunway() {
        if (numRunways < MAX_RUNWAYS) {
            removeRunwayButton.setEnabled(true);    // Enable the delete button once add
            numRunways++;

            int newId = getNextAvailableId();
            String runwayId = runwayIDs[newId - 1];
            Runway runway = new Runway(newId, runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE, 0);
            runways.add(runway);    // Add Runway object into list
            RunwaySetup runwaySetup = new RunwaySetup(runwayId, SimConfig.RunwayMode.LANDING, SimConfig.RunwayStatus.AVAILABLE);
            runwaySetups.add(runwaySetup);

            // Creating the RunwayPanel for that runway
            RunwayInputPanel panel = new RunwayInputPanel(runway, runwaySetup);
            runwayPanels.put(newId, panel);

            // JPanel newRunway = createRunwayPanel(numRunways);
            runwaysContainer.add(panel);

            // Refresh the UI
            runwaysContainer.revalidate();
            runwaysContainer.repaint();

            // Disable the add button if number of runways reach max
            if (numRunways >= MAX_RUNWAYS) {
                addRunwayButton.setEnabled(false);
                // Add a tooltip
            }
        }
    }

    // Runway deletion
    private void deleteRunway(int id) {
        if (numRunways > 1) {
            RunwayInputPanel runwayRemoved = runwayPanels.get(id); // Get the highest ID RunwayPanel

            // Remove from the runwaysContainer
            if (runwayRemoved != null) {
                runwaysContainer.remove(runwayRemoved);
            }

            runwayPanels.remove(id);    // Remove from the runwayPanels hashmap

            // Remove from the list
            for (Runway runway: runways) {
                if (runway.getID() == id) {
                    runways.remove(runway);
                    break;
                }
            }

            addRunwayButton.setEnabled(true);   // Enable the addRunwayButton
            numRunways--;   // Decrement number of runways

            runwaysContainer.revalidate();
            runwaysContainer.repaint();

            if (numRunways < 2) {
                removeRunwayButton.setEnabled(false);
                // Add a tooltip
            }
        }
    }

    // Getting the next available runway ID
    private int getNextAvailableId() {
        for (int id = 1; id <= MAX_RUNWAYS; id++) {
            boolean found = false;

            for (Runway runway : runways) {
                if (runway.getID() == id) {
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


    // Debug - Function to print entry of Runway
    private void printRunwayObjects() {
        System.out.println("=== Runway List Contents ===");
        for (Runway runway : runways) {
            System.out.println("  - Runway ID: " + runway.getID());
            System.out.println("  - Mode: " + runway.getMode());
            System.out.println("  - Status: " + runway.getStatus());
            System.out.println("  - Time Remaining: " + runway.getTimeRemaining());
            System.out.println();
        }

        System.out.println("Total elements: " + numRunways);
        System.out.println("============================");
    }


    // START SIM Panel
    private JPanel createStartSimPanel()  {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_FORM_COLOR);
        panel.setMinimumSize(new Dimension(FORM_CONTENT_WIDTH, 30));

        StyledButton button = new StyledButton("Start Simulation", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        button.setFont(ARIAL_BOLD_18);
        button.setButtonSize(200, 30);
        button.addActionListener(e -> startSimulation());

        panel.add(button);
        return panel;
    }

    // When the Submit Button is clicked
    private void startSimulation() {
        try {
            // Check mode and status for each runway configured
//            if (!allRunwaysConfigured()) {
//                JOptionPane.showMessageDialog(
//                        this,
//                        "Please configure all runways before starting the simulation",
//                        "Configuration Incomplete",
//                        JOptionPane.WARNING_MESSAGE
//                );
//                return;
//            }

            // Get the text from fields and convert to integers
            int inboundRate = Integer.parseInt(inboundRateField.getText());
            int outboundRate = Integer.parseInt(outboundRateField.getText());
            int duration = Integer.parseInt(durationField.getText());

            // Check if negative value
            if (inboundRate < 1 || outboundRate < 1 || duration < 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Each field should be at least 1",
                        "Configuration Incomplete",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

//            // Debugging by printing
//            System.out.println("Duration: " + duration);
//            System.out.println("Inbound rate: " + inboundRate);
//            System.out.println("Outbound rate: " + outboundRate);
//            System.out.println();
//            printRunwayObjects();

            // Passing information into PageDataController
            dataController.setSimulationParams(inboundRate, outboundRate, duration, numRunways);
            dataController.addAllRunways(runways);
            dataController.addAllRunwaySetups(runwaySetups);

            // ============ SIMULATION SETUP ============
            SimulationSetup setup = new SimulationSetup();
            setup.setArrivalRatePerHour(inboundRate);
            setup.setDepartureRatePerHour(outboundRate);
            setup.setMaxRunways(10);
            long seconds = duration * 3600L;
            setup.setDurationSeconds(seconds);
            setup.setDtSeconds(1.0);
            setup.setSpeedMultiplier(1.0);
            setup.setSeed(42L);
            setup.setPrintEverySeconds(60);
            setup.setCsvPath(java.nio.file.Path.of("output.csv"));

            // Adding runways
            for (RunwaySetup runwaySetup : runwaySetups) {
                setup.addRunway(runwaySetup);
            }

            // Simulation Configuration
            SimConfig cfg = SimConfigFactory.fromSetup(setup);
            EngineOptions opts = SimConfigFactory.engineOptionsFromSetup(setup);
            SimClock clock = new SimClock(setup.getDtSeconds());

            // Simulation Engine
            Engine engine = new Engine(cfg, opts, clock);
            SimController controller = new SimController(engine);
            dataController.setSimController(controller);

            // Start simulation
            controller.startSimulation();

            // Output the results
//            SimConfig cfg = SimConfigFactory.fromSetup(setup);
//            SimConfigWriter.write(java.nio.file.Path.of("config.json"), cfg);

            app.showSimulationPage();   // move to SimulationPage

        } catch (NumberFormatException ex) {
            // Handle case
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

//    private boolean allRunwaysConfigured() {
//        for (Runway runway: runways) {
//            if (runway.getMode() == null || runway.getStatus() == sim.config.SimConfig.RunwayStatus.UNAVAIALABLE) {
//                return false;
//            }
//        }
//        return true;
//    }

}

