package sim.view.pages;


import sim.view.App;
import sim.view.components.FooterPanel;
import sim.view.components.HeaderPanel;
import sim.view.components.SidePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class InputPage extends JPanel {
    private App app;

    // Global parameters
    int numRunways = 1;
    private final int MAX_RUNWAYS = 10;
    JPanel runwaysContainer;
    JButton addRunwayButton;

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
        formPanel.setPreferredSize(new Dimension(700, 480));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 1. TITLE PANEL
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // titlePanel.setBackground(Color.red);
        JLabel titleLabel = new JLabel("Create a new Simulation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titlePanel.add(titleLabel);
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
        panel.setLayout(new GridBagLayout());
        // panel.setBackground(Color.blue);

        Font labelFont = new Font("Arial", Font.PLAIN, 18);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 2, 0);

        // Row 1: Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel simConfigTitle = new JLabel("Simulation Configuration");
        simConfigTitle.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));
        panel.add(simConfigTitle, gbc);

        // Row 2: Inbound rate
        gbc.insets = new Insets(5, 10, 2, 0);
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel inboundRateLabel = new JLabel("Inbound Rate (aircraft/hour)");
        inboundRateLabel.setFont(labelFont);
        panel.add(inboundRateLabel, gbc);

        gbc.gridx = 1;
        inboundRateField = new JTextField("eg. 8");
        inboundRateField.setColumns(15);
        inboundRateField.setFont(labelFont);
        panel.add(inboundRateField, gbc);

        // Row 3: Outbound rate
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel outboundRateLabel = new JLabel("Outbound Rate (aircraft/hour)");
        outboundRateLabel.setFont(labelFont);
        panel.add(outboundRateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        outboundRateField = new JTextField("eg. 8");
        outboundRateField.setColumns(15);
        outboundRateField.setFont(labelFont);
        panel.add(outboundRateField, gbc);

        // Row 4: Duration
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel durationLabel = new JLabel("Duration (hour)");
        durationLabel.setFont(labelFont);
        panel.add(durationLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        durationField = new JTextField("eg. 8");
        durationField.setColumns(15);
        durationField.setFont(labelFont);
        panel.add(durationField, gbc);

        return panel;
    }


    // RUNWAY CONFIG PANEL
    private JPanel createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.yellow);
        panel.setPreferredSize(new Dimension(680, 230));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // TITLE PANEL
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setPreferredSize(new Dimension(680, 50));

        JLabel titleLabel = new JLabel("Runway Configuration");
        titleLabel.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 20));

        addRunwayButton = new JButton("+ Add Runways");
        addRunwayButton.setFocusable(false);
        addRunwayButton.addActionListener(e -> {
            addNewRunway();
        });

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(addRunwayButton);

        panel.add(titlePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // RUNWAY PANEL
        runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Dynamically add runways
        runwaysContainer.add(createRunwayPanel(numRunways));

        JScrollPane scrollPaneRunways = new JScrollPane(runwaysContainer);
        scrollPaneRunways.setPreferredSize(new Dimension(680, 225));
        scrollPaneRunways.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(scrollPaneRunways);
        return panel;
    }

    // addNewRunway
    private void addNewRunway() {
        if (numRunways < MAX_RUNWAYS) {
            numRunways++;

            JPanel newRunway = createRunwayPanel(numRunways);
            runwaysContainer.add(newRunway);
            // runwaysContainer.add(Box.createRigidArea(new Dimension(0, 5)));

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

    // Function to create runways
    private JPanel createRunwayPanel(int numRunway) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.lightGray),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setMinimumSize(new Dimension(650, 80));
        panel.setPreferredSize(new Dimension(650, 80));

        // titlePanel : title with delete button
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.pink);
        titlePanel.setPreferredSize(new Dimension(650, 30));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        JLabel titleLabel = new JLabel("Runway " + numRunway);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);

        // Delete button
        if (numRunways > 1) {
            JButton deleteButton = new JButton("x");
            deleteButton.setFocusPainted(false);
            deleteButton.setPreferredSize(new Dimension(25, 25));
            deleteButton.setBackground(Color.red);
            deleteButton.setForeground(Color.white);

            // Add action listener
            deleteButton.addActionListener(e -> {
                Container parent = panel.getParent();
                if (parent != null) {
                    parent.remove(panel);
                    numRunways--;

                    if (!addRunwayButton.isEnabled() && numRunways < MAX_RUNWAYS) {
                        addRunwayButton.setEnabled(true);
                    }

                    // Refresh the UI
                    parent.revalidate();
                    parent.repaint();
                }
            });
            titlePanel.add(Box.createHorizontalGlue());
            titlePanel.add(deleteButton);
        }
        panel.add(titlePanel);


        // optionPanel : status and mode for each runway
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBackground(Color.cyan);
        optionPanel.setMinimumSize(new Dimension(650, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode
        gbc.gridx = 0; gbc.gridy = 0;
        optionPanel.add(new JLabel("Mode"), gbc);
        gbc.gridx = 1;
        JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Landing Only", "Takeoff Only", "Mixed Mode"});
        optionPanel.add(modeCombo, gbc);

        // Status
        gbc.gridx = 2; gbc.gridy = 0;
        optionPanel.add(new JLabel("Status"), gbc);
        gbc.gridx = 3;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Maintenance"});
        optionPanel.add(statusCombo, gbc);

        panel.add(optionPanel);

        return panel;
    }


    // When the Submit Button is clicked
    private void submitClicked() {
        try {
            // Get the text from fields and convert to integers
            int numFlights = Integer.parseInt(flightsField.getText());
            int inboundRate = Integer.parseInt(inboundRateField.getText());
            int outboundRate = Integer.parseInt(outboundRateField.getText());

            // Debugging by printing
            System.out.println("Number of flights: " + numFlights);
            System.out.println("Inbound rate: " + inboundRate);
            System.out.println("Outbound rate: " + outboundRate);

            // If succeeded, move to SimulationPage
            app.showSimulationPage();

        } catch (NumberFormatException ex) {
            // Handle case
            System.out.println("Please enter valid numbers");

            // Display an JOptionPane
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}

