package sim.view.components;

import sim.model.stores.Runway;

import javax.swing.*;
import java.awt.*;

public class RunwayInputPanel extends JPanel {
    private final Runway runway;
    private final int runwayId;

    private JComboBox<String> modeCombo;
    private JComboBox<String> statusCombo;

    public RunwayInputPanel(Runway runway) {
        this.runway = runway;
        this.runwayId = runway.getID();

        setupUI();
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.lightGray, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setMinimumSize(new Dimension(610, 80));
        setPreferredSize(new Dimension(610, 80));

        Font titleFont = new Font("Arial", Font.ITALIC, 16);
        Font labelFont = new Font("Arial", Font.BOLD, 15);
        Font comboFont = new Font("Arial", Font.PLAIN, 14);

        // titlePanel : title with delete button
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.white);
        titlePanel.setPreferredSize(new Dimension(610, 30));
        JLabel titleLabel = new JLabel("Runway " + runwayId);
        titleLabel.setFont(titleFont);
        titlePanel.add(titleLabel);

        // optionPanel : status and mode for each runway
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBackground(Color.white);
        optionPanel.setMinimumSize(new Dimension(610, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mode
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel modeLabel = new JLabel("Mode: ");
        modeLabel.setFont(labelFont);
        optionPanel.add(modeLabel, gbc);
        gbc.gridx = 1;
        modeCombo = new JComboBox<>(new String[]{"None", "Landing Only", "Takeoff Only", "Mixed Mode"});
        modeCombo.setFont(comboFont);
        modeCombo.setPreferredSize(new Dimension(150, 25));
        modeCombo.addActionListener(e -> updateMode());
        optionPanel.add(modeCombo, gbc);

        // Status
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(labelFont);
        optionPanel.add(statusLabel, gbc);
        gbc.gridx = 3;
        statusCombo = new JComboBox<>(new String[]{"None", "Available", "Runway Inspection", "Snow Clearance", "Failure"});
        statusCombo.setFont(comboFont);
        statusCombo.setPreferredSize(new Dimension(150, 25));
        statusCombo.addActionListener(e-> updateStatus());
        optionPanel.add(statusCombo, gbc);

        add(titlePanel);
        add(optionPanel);
    }

    private void updateMode() {
        String selectedMode = (String) modeCombo.getSelectedItem();

        if (selectedMode != null) {
            switch (selectedMode) {
                case "Landing Only" -> runway.setMode(sim.config.SimConfig.RunwayMode.LANDING);
                case "Takeoff Only" -> runway.setMode(sim.config.SimConfig.RunwayMode.TAKEOFF);
                case "Mixed Mode" -> runway.setMode(sim.config.SimConfig.RunwayMode.MIXED);
            }
        }
    }

    private void updateStatus() {
        String selectedStatus = (String) statusCombo.getSelectedItem();

        if (selectedStatus != null) {
            switch (selectedStatus) {
                case "Available" -> runway.setStatus(sim.config.SimConfig.RunwayStatus.AVAILABLE);
                case "Runway Inspection" -> runway.setStatus(sim.config.SimConfig.RunwayStatus.INSPECTION);
                case "Snow Clearance" -> runway.setStatus(sim.config.SimConfig.RunwayStatus.SNOW);
                case "Failure" -> runway.setStatus(sim.config.SimConfig.RunwayStatus.FAILURE);
                case "None" -> runway.setStatus(sim.config.SimConfig.RunwayStatus.UNAVAIALABLE);
            }
        }
    }
}
