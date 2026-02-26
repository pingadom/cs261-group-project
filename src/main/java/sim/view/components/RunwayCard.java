package sim.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RunwayCard extends JPanel {
    private int runwayId;

    private JLabel statusLabel;
    private JLabel modeLabel;
    private JLabel aircraftLabel;
    private JLabel occupiedLabel;

    // Constructor
    public RunwayCard(int id, String status, String mode, String aircraft, Boolean occupied) {
        this.runwayId = id;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Runway " + id));
        setPreferredSize(new Dimension(500, 100));

        // Add the labels
        modeLabel = new JLabel("Mode: " + mode);
        statusLabel = new JLabel("Status: " + status);
        aircraftLabel = new JLabel("Aircraft: " + aircraft);
        if (occupied) {
            occupiedLabel = new JLabel("Occupied");
        } else {
            occupiedLabel = new JLabel("Free");
        }

        // Define GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 15, 0, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        add(modeLabel, gbc);
        gbc.gridx = 1; add(statusLabel, gbc);
        gbc.gridx = 2; add(aircraftLabel, gbc);
        gbc.gridx = 3; add(occupiedLabel, gbc);

        // Button to change the runway's configuration
        JButton runwayConfigButton = new JButton("Configure Runway");
        runwayConfigButton.addActionListener(e -> {
            createRunwayConfigPanel();
        });

        gbc.gridx = 4; add(runwayConfigButton, gbc);
    }

    // Setter for runway attributes
    private void setModeLabel(String mode) {
        modeLabel.setText("Mode: " + mode);
    }

    private void setStatusLabel(String status) {
        statusLabel.setText("Status: " + status);
    }

    private void setOccupiedLabel(Boolean occupied) {

    }


    // Panel to configure the runways
    private void createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10 ,20, 10));
        panel.setPreferredSize(new Dimension(400, 200));

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        titlePanel.setPreferredSize(new Dimension(380, 50));

        JLabel titleLabel = new JLabel("Configuring for Runway " + runwayId);
        titleLabel.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(titlePanel);

        // Mode and Status
        JPanel comboPanel = new JPanel(new GridBagLayout());
        comboPanel.setPreferredSize(new Dimension(380, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode Selection
        Font labelFont = new Font("Arial", Font.BOLD, 16);

        JLabel modeLabel = new JLabel("Mode: ");
        modeLabel.setFont(labelFont);
        JComboBox<String> modeCombo = new JComboBox<>(
          new String[]{"Landing Only", "Takeoff Only", "Mixed Mode"}
        );
        modeCombo.setFont(labelFont);

        // Status Selection
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(labelFont);
        JComboBox<String> statusCombo = new JComboBox<>(
          new String[]{"Available", "Maintenance"}
        );
        statusCombo.setFont(labelFont);

        gbc.gridx = 0; gbc.gridy = 0;
        comboPanel.add(modeLabel, gbc);
        gbc.gridy = 1; comboPanel.add(modeCombo, gbc);
        gbc.gridx = 1; comboPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        gbc.gridx = 2; gbc.gridy = 0;
        comboPanel.add(statusLabel, gbc);
        gbc.gridy = 1; comboPanel.add(statusCombo, gbc);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboPanel);

        // Show the JOptionPane
        int result = JOptionPane.showConfirmDialog(
          this,
          panel,
          "Configure Runway " + runwayId,
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE
        );

        // If result is OK
        if (result == JOptionPane.OK_OPTION) {

            // Get the mode selected and update UI
            String modeSelected = (String) modeCombo.getSelectedItem();
            if (modeSelected != null) {
                if (modeSelected.equals("Landing Only")) {
                    setModeLabel("Landing");
                } else if (modeSelected.equals("Takeoff Only")) {
                    setModeLabel("Take-off");
                } else {
                    setModeLabel("Mixed");
                }
            }

            // Get the status selected and update UI
            String statusSelected = (String) statusCombo.getSelectedItem();
            if (statusSelected != null) {
                if (statusSelected.equals("Available")) {
                    setStatusLabel("Available");
                } else if (statusSelected.equals("Maintenance")) {
                    setStatusLabel("Maintenance");
                }
            }
        }

        // Update UI
        this.revalidate();
        this.repaint();
    }

}
