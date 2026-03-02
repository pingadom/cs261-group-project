package sim.view.components;

import sim.model.stores.Runway;

import javax.swing.*;
import java.awt.*;

public class RunwayPanel extends JPanel {
    private Runway runway;
    private int runwayId;

    private JComboBox<String> modeCombo;
    private JComboBox<String> statusCombo;


    public RunwayPanel(Runway runway) {
        this.runway = runway;
        this.runwayId = runway.getID();

        // Build the UI
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
        modeCombo.addActionListener(e -> {
            updateMode();
        });
        optionPanel.add(modeCombo, gbc);

        // Status
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setFont(labelFont);
        optionPanel.add(statusLabel, gbc);
        gbc.gridx = 3;
        statusCombo = new JComboBox<>(new String[]{"None", "Available", "Maintenance"});
        statusCombo.setFont(comboFont);
        statusCombo.setPreferredSize(new Dimension(150, 25));
        statusCombo.addActionListener(e-> {
            updateStatus();
        });
        optionPanel.add(statusCombo, gbc);

        add(titlePanel);
        add(optionPanel);
    }

    private void updateMode() {
        // Get the selected mode
        String selectedMode = (String) modeCombo.getSelectedItem();
        // System.out.println(selectedMode);

        if (selectedMode.equals("Landing Only")) {
            runway.setMode("landing");
        } else if (selectedMode.equals("Takeoff Only")) {
            runway.setMode("takeoff");
        } else if (selectedMode.equals("Mixed Mode")) {
            runway.setMode("mixed");
        }
    }

    private void updateStatus() {
        String selectedStatus = (String) statusCombo.getSelectedItem();
        // System.out.println(selectedStatus);

        if (selectedStatus.equals("Available")) {
            runway.setStatus("available");
        } else if (selectedStatus.equals("Maintenance")) {
            runway.setStatus("maintenance");
        } else if (selectedStatus.equals("None")) {
            runway.setStatus("none");
        }
    }
}
