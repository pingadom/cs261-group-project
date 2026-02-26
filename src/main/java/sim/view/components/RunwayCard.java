package sim.view.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class RunwayCard extends JPanel {
    private int runwayId;
    private JPanel parentPanel;

    private JLabel statusLabel;
    private JLabel modeLabel;
    private JLabel aircraftLabel;
    private JLabel occupiedLabel;

    // Constructor
    public RunwayCard(int id, String status, String mode, String aircraft, Boolean occupied, JPanel parent) {
        this.runwayId = id;
        this.parentPanel = parent;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        TitledBorder titleBorder = BorderFactory.createTitledBorder("Runway " + runwayId);
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
        //modePanel.setBackground(Color.cyan);
        modePanel.setPreferredSize(new Dimension(110, 80));
        modePanel.setLayout(new BorderLayout());
        modeLabel = new JLabel("Mode: " + mode);
        modeLabel.setFont(labelFont);
        modeLabel.setHorizontalAlignment(JLabel.CENTER);
        modePanel.add(modeLabel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        //statusPanel.setBackground(Color.pink);
        statusPanel.setPreferredSize(new Dimension(140, 80));
        statusPanel.setLayout(new BorderLayout());
        statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(labelFont);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        JPanel aircraftPanel = new JPanel();
        //aircraftPanel.setBackground(Color.orange);
        aircraftPanel.setPreferredSize(new Dimension(110, 80));
        aircraftPanel.setLayout(new BorderLayout());
        aircraftLabel = new JLabel("Aircraft: " + aircraft);
        aircraftLabel.setFont(labelFont);
        aircraftLabel.setHorizontalAlignment(JLabel.CENTER);
        aircraftPanel.add(aircraftLabel, BorderLayout.CENTER);

        JPanel occupiedPanel = new JPanel();
        //occupiedPanel.setBackground(Color.red);
        occupiedPanel.setPreferredSize(new Dimension(80, 80));
        occupiedPanel.setLayout(new BorderLayout());
        if (occupied) {
            occupiedLabel = new JLabel("Occupied");
        } else {
            occupiedLabel = new JLabel("Free");
        }
        occupiedLabel.setFont(labelFont);
        occupiedLabel.setHorizontalAlignment(JLabel.CENTER);
        occupiedPanel.add(occupiedLabel, BorderLayout.CENTER);

        // Button to change the runway's configuration
        StyledButton runwayConfigButton = new StyledButton("Configure", new Color(70, 130, 180), new Color(100, 150, 200), new Color(70, 130, 180), new Color(70, 130, 180));
        runwayConfigButton.setButtonsize(90, 30);
        runwayConfigButton.setFont(new Font("Arial", Font.BOLD, 14));
        runwayConfigButton.addActionListener(e -> {
            createRunwayConfigPanel();
        });


        // Add labels and buttons
        add(modePanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(statusPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(aircraftPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(occupiedPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(runwayConfigButton);
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
        panel.setPreferredSize(new Dimension(400, 180));

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
        comboPanel.setPreferredSize(new Dimension(380, 130));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode Selection
        Font labelFontBold = new Font("Arial", Font.BOLD, 16);
        Font labelFontPlain = new Font("Arial", Font.PLAIN, 16);

        JLabel modeLabel = new JLabel("Mode: ");
        modeLabel.setFont(labelFontBold);
        JComboBox<String> modeCombo = new JComboBox<>(
          new String[]{"Landing Only", "Takeoff Only", "Mixed Mode"}
        );
        modeCombo.setFont(labelFontPlain);

        // Status Selection
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(labelFontBold);
        JComboBox<String> statusCombo = new JComboBox<>(
          new String[]{"Available", "Maintenance"}
        );
        statusCombo.setFont(labelFontPlain);

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
