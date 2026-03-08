package sim.view.components;

import sim.config.SimConfig;
import sim.core.viewmodel.RunwaySetup;
import sim.core.viewmodel.SimController;
import sim.model.stores.Runway;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RunwayCard extends JPanel {
    private final JPanel parentPanel;

    private final JLabel statusLabel;
    private final JLabel modeLabel;
    private final JLabel aircraftLabel;

    private final Runway runway;
    private final RunwaySetup runwaySetup;
    private final String runwayId;
    private SimConfig.RunwayMode mode;
    private SimConfig.RunwayStatus status;

    private final SimController simController;

    // Constructor
    public RunwayCard(Runway runway, RunwaySetup runwaySetup, JPanel parent, SimController simController) {
        this.runway = runway;
        this.runwaySetup = runwaySetup;

        this.parentPanel = parent;
//        this.mode = runway.getMode();
//        this.status = runway.getStatus();
        this.runwayId = runwaySetup.getId();
        this.mode = runwaySetup.getMode();
        this.status = runwaySetup.getStatus();

        this.simController = simController;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        TitledBorder titleBorder = BorderFactory.createTitledBorder(runwayId);
        titleBorder.setTitleFont(new Font("Arial", Font.ITALIC, 16));
        titleBorder.setTitleColor(Color.black);
        titleBorder.setTitleJustification(TitledBorder.LEFT);

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(titleBorder, padding);

        setBorder(compoundBorder);
        setPreferredSize(new Dimension(640, 100));
        setMinimumSize(new Dimension(640, 100));
        setMaximumSize(new Dimension(640, 100));

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JPanel modePanel = new JPanel();
        modePanel.setPreferredSize(new Dimension(110, 80));
        modePanel.setLayout(new BorderLayout());
        modeLabel = new JLabel();
        updateModeLabel();
        modeLabel.setFont(labelFont);
        modeLabel.setHorizontalAlignment(JLabel.CENTER);
        modePanel.add(modeLabel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(220, 80));
        statusPanel.setLayout(new BorderLayout());
        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setFont(labelFont);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        JPanel aircraftPanel = new JPanel();
        aircraftPanel.setPreferredSize(new Dimension(110, 80));
        aircraftPanel.setLayout(new BorderLayout());
        aircraftLabel = new JLabel();
        updateOccupiedLabel();
        aircraftLabel.setFont(labelFont);
        aircraftLabel.setHorizontalAlignment(JLabel.CENTER);
        aircraftPanel.add(aircraftLabel, BorderLayout.CENTER);

        // Button to change the runway's configuration
        StyledButton runwayConfigButton = new StyledButton("Configure", new Color(70, 130, 180), new Color(100, 150, 200), new Color(70, 130, 180), new Color(70, 130, 180));
        runwayConfigButton.setButtonSize(90, 30);
        runwayConfigButton.setFont(new Font("Arial", Font.BOLD, 14));
        runwayConfigButton.addActionListener(e -> createRunwayConfigPanel());

        // Add labels and buttons
        add(modePanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(statusPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(aircraftPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(runwayConfigButton);
    }


    // Panel to configure the runways
    private void createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10 ,20, 10));
        panel.setPreferredSize(new Dimension(400, 180));

        // Title
        JPanel titlePanel = createTitlePopupPanel();
        panel.add(titlePanel);

        // Mode and Status
        JPanel comboPanel = new JPanel(new GridBagLayout());
        comboPanel.setPreferredSize(new Dimension(380, 130));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode Selection
        Font labelFontBold = new Font("Arial", Font.BOLD, 16);
        Font labelFontPlain = new Font("Arial", Font.PLAIN, 16);

        JLabel modeLabel = new JLabel("Mode: ");
        modeLabel.setFont(labelFontBold);

        String[] modesList = {"Landing Only", "Takeoff Only", "Mixed Mode"};
        JComboBox<String> modeCombo = new JComboBox<>(modesList);
        modeCombo.setFont(labelFontPlain);

        if (mode == SimConfig.RunwayMode.LANDING) {
            modeCombo.setSelectedIndex(0);
        } else if (mode == SimConfig.RunwayMode.TAKEOFF) {
            modeCombo.setSelectedIndex(1);
        } else if (mode == SimConfig.RunwayMode.MIXED) {
            modeCombo.setSelectedIndex(2);
        }

        // Status Selection
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(labelFontBold);

        String[] statusList = {"Available", "Runway Inspection", "Snow Clearance", "Failure", "Unavailable"};
        JComboBox<String> statusCombo = new JComboBox<>(statusList);
        statusCombo.setFont(labelFontPlain);

        if (status == SimConfig.RunwayStatus.AVAILABLE) {
            statusCombo.setSelectedIndex(0);
        } else if (status == SimConfig.RunwayStatus.INSPECTION) {
            statusCombo.setSelectedIndex(1);
        } else if (status == SimConfig.RunwayStatus.SNOW) {
            statusCombo.setSelectedIndex(2);
        } else if (status == SimConfig.RunwayStatus.FAILURE) {
            statusCombo.setSelectedIndex(3);
        } else if (status == SimConfig.RunwayStatus.UNAVAIALABLE) {
            statusCombo.setSelectedIndex(4);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        comboPanel.add(modeLabel, gbc);
        gbc.gridy = 1; comboPanel.add(modeCombo, gbc);
        gbc.gridx = 1; comboPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        gbc.gridx = 2; gbc.gridy = 0;
        comboPanel.add(statusLabel, gbc);
        gbc.gridy = 1; comboPanel.add(statusCombo, gbc);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboPanel);

        // Show the JOptionPane
        int result = JOptionPane.showConfirmDialog(
          parentPanel,
          panel,
          "Configure Runway " + runway.getID(),
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE
        );

        // If result is OK
        if (result == JOptionPane.OK_OPTION) {

            // Get the mode selected and update UI
            String modeSelected = (String) modeCombo.getSelectedItem();
//            if (modeSelected != null) {
//                switch (modeSelected) {
//                    case "Landing Only" -> runway.setMode(SimConfig.RunwayMode.LANDING);
//                    case "Takeoff Only" -> runway.setMode(SimConfig.RunwayMode.TAKEOFF);
//                    case "Mixed Mode" -> runway.setMode(SimConfig.RunwayMode.MIXED);
//                }
//                mode = runway.getMode();
//                updateModeLabel();
//            }

            if (modeSelected != null) {
                switch (modeSelected) {
                    case "Landing Only" -> runway.setMode(SimConfig.RunwayMode.LANDING);
                    case "Takeoff Only" -> runway.setMode(SimConfig.RunwayMode.TAKEOFF);
                    case "Mixed Mode" -> runway.setMode(SimConfig.RunwayMode.MIXED);
                }
                mode = runway.getMode();
                updateModeLabel();
            }

            // Set the mode using SimController method
            if (modeSelected != null) {
                switch (modeSelected) {
                    case "Landing Only" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.LANDING);
                    case "Takeoff Only" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.TAKEOFF);
                    case "Mixed Mode" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.MIXED);
                }
                mode = runway.getMode();
                updateModeLabel();
            }

            // Get the status selected and update UI
            String statusSelected = (String) statusCombo.getSelectedItem();
            if (statusSelected != null) {
                switch (statusSelected) {
                    case "Available" -> simController.setRunwayStatus(runwayId, SimConfig.RunwayStatus.AVAILABLE);
                    case "Runway Inspection" -> simController.setRunwayStatus(runwayId, SimConfig.RunwayStatus.INSPECTION);
                    case "Snow Clearance" -> simController.setRunwayStatus(runwayId, SimConfig.RunwayStatus.SNOW);
                    case "Failure" -> simController.setRunwayStatus(runwayId, SimConfig.RunwayStatus.FAILURE);
                    case "Unavailable" -> simController.setRunwayStatus(runwayId, SimConfig.RunwayStatus.UNAVAIALABLE);
                }
                status = runway.getStatus();
                updateStatusLabel();
            }
        }

        // Update UI
        this.revalidate();
        this.repaint();
    }

    private JPanel createTitlePopupPanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        titlePanel.setPreferredSize(new Dimension(380, 50));

        JLabel titleLabel = new JLabel("Configuring for Runway " + runway.getID());
        titleLabel.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }


    // Setter for runway attributes
    private void updateModeLabel() {
        if (mode == SimConfig.RunwayMode.LANDING) {
            modeLabel.setText("Mode: Landing");
        } else if (mode == SimConfig.RunwayMode.TAKEOFF) {
            modeLabel.setText("Mode: Take-off");
        } else if (mode == SimConfig.RunwayMode.MIXED) {
            modeLabel.setText("Mode: Mixed");
        }
    }

    private void updateStatusLabel() {
        if (status == SimConfig.RunwayStatus.AVAILABLE) {
            statusLabel.setText("Status: Available");
        } else if (status == SimConfig.RunwayStatus.INSPECTION) {
            statusLabel.setText("Status: Runway Inspection");
        } else if (status == SimConfig.RunwayStatus.SNOW) {
            statusLabel.setText("Status: Snow Clearance");
        } else if (status == SimConfig.RunwayStatus.FAILURE) {
            statusLabel.setText("Status: Failure");
        } else if (status == SimConfig.RunwayStatus.UNAVAIALABLE) {
            statusLabel.setText("Status: Unavailable");
        }
    }

    private void updateOccupiedLabel() {
        String aircraft = runway.getOccupied();
        if (aircraft.isEmpty()) {
            aircraftLabel.setText("Not Occupied");
        } else {
            aircraftLabel.setText("Aircraft: " + aircraft);
        }
    }
}
