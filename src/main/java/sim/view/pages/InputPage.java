package sim.view.pages;


import sim.model.stores.Runway;
import sim.view.components.*;
import sim.view.App;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputPage extends JPanel {
    private App app;

    // Global parameters
    int numRunways = 1;
    private final int MAX_RUNWAYS = 10;
    JPanel runwaysContainer;
    StyledButton addRunwayButton;
    StyledButton removeRunwayButton;

    private Map<Integer, RunwayPanel> runwayPanels = new HashMap<>();
    private List<Runway> runways = new ArrayList<>();

    // User input fields
    JTextField flightsField;
    JTextField inboundRateField;
    JTextField outboundRateField;
    JTextField durationField;

    // Constructor
    public InputPage(App app) {
        this.app = app;
        setupUI();
    }

    // Setting up the UI
    private void setupUI() {
        // set LayoutManager
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.white);

        // Creating subpanels inside this page
        JPanel headerPanel = new HeaderPanel();
        JPanel leftPanel = new SidePanel();
        JPanel rightPanel = new SidePanel();
        JPanel footerPanel = new FooterPanel();

        // CONTENT PANEL ----------------------------------------
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);

        // formPanel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(700, 500));
        //formPanel.setBackground(new Color(0xBDBDBD));
        formPanel.setBackground(new Color(100, 150, 200));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));

        // 1. TITLE PANEL
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setMaximumSize(new Dimension(500, 70));
        titlePanel.setPreferredSize(new Dimension(500, 70));
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        JLabel titleLabel = new JLabel("Create a new Simulation");
        titleLabel.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 30));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        formPanel.add(titlePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 2. SIMULATION CONFIG PANEL
        JPanel simConfigPanel = createSimConfigPanel();
        formPanel.add(simConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. RUNWAY CONFIG PANEL
        JPanel runwayConfigPanel = createRunwayConfigPanel();
        formPanel.add(runwayConfigPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 4. START SIMULATION BUTTON
        JPanel startSimPanel = createStartSimPanel();
        formPanel.add(startSimPanel);

        // add formPanel into the contentPanel
        contentPanel.add(formPanel);

        // Add main panels
        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // SIMULATION CONFIG PANEL
    private JPanel createSimConfigPanel() {
        JPanel panel = new JPanel();
        // panel.setBackground(new Color(0xBDBDBD));
        panel.setBackground(new Color(100, 150, 200));
        panel.setLayout(new GridBagLayout());

        Font labelFont = new Font("Arial", Font.PLAIN, 18);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 2, 0);

        // Row 1: Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel simConfigTitle = new JLabel("Simulation Configuration");
        simConfigTitle.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));
        simConfigTitle.setForeground(Color.black);
        panel.add(simConfigTitle, gbc);

        // Row 2: Inbound rate
        gbc.insets = new Insets(5, 13, 2, 0);
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel inboundRateLabel = new JLabel("Inbound Rate (aircraft/hour)");
        inboundRateLabel.setFont(labelFont);
        inboundRateLabel.setForeground(Color.black);
        panel.add(inboundRateLabel, gbc);

        gbc.gridx = 1;
        inboundRateField = new JTextField("8");
        inboundRateField.setColumns(15);
        inboundRateField.setFont(labelFont);
        panel.add(inboundRateField, gbc);

        // Row 3: Outbound rate
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel outboundRateLabel = new JLabel("Outbound Rate (aircraft/hour)");
        outboundRateLabel.setFont(labelFont);
        outboundRateLabel.setForeground(Color.black);
        panel.add(outboundRateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        outboundRateField = new JTextField("8");
        outboundRateField.setColumns(15);
        outboundRateField.setFont(labelFont);
        panel.add(outboundRateField, gbc);

        // Row 4: Duration
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel durationLabel = new JLabel("Duration (hour)");
        durationLabel.setFont(labelFont);
        durationLabel.setForeground(Color.black);
        panel.add(durationLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        durationField = new JTextField("8");
        durationField.setColumns(15);
        durationField.setFont(labelFont);
        panel.add(durationField, gbc);

        return panel;
    }

    // RUNWAY CONFIG PANEL
    private JPanel createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        // panel.setBackground(new Color(0xBDBDBD));
        panel.setBackground(new Color(70, 130, 180));
        panel.setPreferredSize(new Dimension(640, 270));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // TITLE PANEL
        JPanel titlePanel = new JPanel();
        // titlePanel.setBackground(new Color(0xBDBDBD));
        titlePanel.setBackground(new Color(100, 150, 200));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setPreferredSize(new Dimension(640, 30));

        JLabel titleLabel = new JLabel("Runway Configuration");
        titleLabel.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));
        titleLabel.setForeground(Color.black);

        addRunwayButton = new StyledButton("+ Add Runways", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        addRunwayButton.setFont(new Font("Arial", Font.BOLD, 14));
        addRunwayButton.setButtonsize(150, 25);
        addRunwayButton.addActionListener(e -> {
            addNewRunway();
        });

        removeRunwayButton = new StyledButton("Remove Runway", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        removeRunwayButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeRunwayButton.setButtonsize(150, 25);
        removeRunwayButton.setEnabled(false);
        removeRunwayButton.addActionListener(e -> {
            deleteRunway(numRunways);
        });

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(addRunwayButton);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(removeRunwayButton);

        // RUNWAY PANEL
        runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add the default Runway 1
        int newId = getNextAvailableId();
        Runway runway = new Runway(newId, "None", "None", 0);
        runways.add(runway);    // Add Runway object into list

        RunwayPanel runwayPanel = new RunwayPanel(runway);
        runwayPanels.put(newId, runwayPanel);
        runwaysContainer.add(runwayPanel);

        JScrollPane scrollPaneRunways = new JScrollPane(runwaysContainer);
        scrollPaneRunways.setPreferredSize(new Dimension(640, 255));
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

            int newId = getNextAvailableId();   // Get the nextId
            Runway runway = new Runway(newId, "None", "None", 0 );   // Create a new runway object
            runways.add(runway);    // Add the runway object into the list
            // printRunwayObjects();   // Debugging

            // Creating the RunwayPanel for that runway
            RunwayPanel panel = new RunwayPanel(runway);
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
            // Get the highest ID RunwayPanel
            RunwayPanel runwayRemoved = runwayPanels.get(id);

            // Remove from the runwaysContainer
            if (runwayRemoved != null) {
                runwaysContainer.remove(runwayRemoved);
            }

            // Remove from the runwayPanels hashmap
            runwayPanels.remove(id);

            // Remove from the list
            for (Runway runway: runways) {
                if (runway.getID() == id) {
                    runways.remove(runway);
                    break;
                }
            }
            // printRunwayObjects();   // Debugging

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
        StringBuilder runwayList = new StringBuilder("[");
        for (Runway runway : runways) {
            runwayList.append("Runway ID: ").append(runway.getID()).append("\n");
            runwayList.append("Mode: ").append(runway.getMode()).append("\n");
            runwayList.append("Status: ").append(runway.getStatus()).append(",").append("\n");
        }
        runwayList.append("]");

        System.out.println(runwayList);
    }

    // START SIM PANEL
    private JPanel createStartSimPanel()  {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //panel.setBackground(new Color(0xBDBDBD));
        panel.setBackground(new Color(100, 150, 200));
        panel.setMinimumSize(new Dimension(640, 30));

        StyledButton button = new StyledButton("Start Simulation", Color.black, new Color(0x333333), new Color(0x555555), Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setButtonsize(200, 30);

        button.addActionListener(e -> {
            startSimulation();
        });

        panel.add(button);
        return panel;
    }

    // When the Submit Button is clicked
    private void startSimulation() {
        try {
            // Check mode and status for each runway configured
            if (!allRunwaysConfigured()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please configure all runways before starting the simulation",
                        "Configuration Incomplete",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Get the text from fields and convert to integers
            int inboundRate = Integer.parseInt(inboundRateField.getText());
            int outboundRate = Integer.parseInt(outboundRateField.getText());
            int duration = Integer.parseInt(durationField.getText());

            // Debugging by printing
            System.out.println("Duration: " + duration);
            System.out.println("Inbound rate: " + inboundRate);
            System.out.println("Outbound rate: " + outboundRate);
            printRunwayObjects();

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

    private boolean allRunwaysConfigured() {
        for (Runway runway: runways) {
            if (runway.getMode().equals("None") || runway.getStatus().equals("None")) {
                return false;
            }
        }
        return true;
    }

}

