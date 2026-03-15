package sim.view.components;

import sim.config.SimConfig;
import sim.core.viewmodel.RunwaySetup;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that allows users to configure an individual runway's mode and status.
 * This component is used in the Input page to let users set up each runway's
 * operational parameters before starting a simulation.
 * Any changes made to the dropdown selections are immediately reflected in the associated {@link RunwaySetup} object.
 *
 * @see RunwaySetup
 * @see sim.config.SimConfig.RunwayMode
 * @see sim.config.SimConfig.RunwayStatus
 */
public class RunwayInputPanel extends JPanel {
    private final RunwaySetup runwaySetup;
    private final String runwayId;

    private JComboBox<String> modeCombo;
    private JComboBox<String> statusCombo;

    // ===================== CONSTRUCTOR =====================

    /**
     * Constructs a new RunwayInputPanel for the specified runway setup.
     *
     * @param runwaySetup the runway object to display and modify
     */
    public RunwayInputPanel(RunwaySetup runwaySetup) {
        this.runwaySetup = runwaySetup;
        this.runwayId = runwaySetup.getId();

        setupUI();
    }

    // ===================== UI INITIALISATION =====================

    /**
     * Initialises the user interface components.
     * Creates a vertically stacked layout with:
     * <ol>
     *     <li>A title panel showing the runway ID</li>
     *     <li>An options panel with mode and status dropdowns</li>
     * </ol>
     */
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
        JLabel titleLabel = new JLabel(runwayId);
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
        modeCombo = new JComboBox<>(new String[]{"Landing Only", "Takeoff Only", "Mixed Mode"});
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
        statusCombo = new JComboBox<>(new String[]{"Available", "Runway Inspection", "Snow Clearance", "Failure", "Unavailable"});
        statusCombo.setFont(comboFont);
        statusCombo.setPreferredSize(new Dimension(150, 25));
        statusCombo.addActionListener(e-> updateStatus());
        optionPanel.add(statusCombo, gbc);

        add(titlePanel);
        add(optionPanel);
    }

    // ===================== EVENT HANDLERS =====================

    /**
     * Updates the runway mode in the associated {@link RunwaySetup} object
     * based on the current selection in the mode combo box.
     * This method is called whenever the user selects a different mode from the dropdown.
     */
    private void updateMode() {
        String selectedMode = (String) modeCombo.getSelectedItem();

        if (selectedMode != null) {
            switch (selectedMode) {
                case "Landing Only" -> runwaySetup.setMode(SimConfig.RunwayMode.LANDING);
                case "Takeoff Only" -> runwaySetup.setMode(SimConfig.RunwayMode.TAKEOFF);
                case "Mixed Mode" -> runwaySetup.setMode(SimConfig.RunwayMode.MIXED);
            }
        }
    }

    /**
     * Updates the runway status in the associated {@link RunwaySetup} object
     * based on the current selection in the status combo box.
     * This method is called whenever the user selects a different status from the dropdown.
     */
    private void updateStatus() {
        String selectedStatus = (String) statusCombo.getSelectedItem();

        if (selectedStatus != null) {
            switch (selectedStatus) {
                case "Available" -> runwaySetup.setStatus(SimConfig.RunwayStatus.AVAILABLE);
                case "Runway Inspection" -> runwaySetup.setStatus(SimConfig.RunwayStatus.INSPECTION);
                case "Snow Clearance" -> runwaySetup.setStatus(SimConfig.RunwayStatus.SNOW);
                case "Failure" -> runwaySetup.setStatus(SimConfig.RunwayStatus.FAILURE);
                case "Unavailable" -> runwaySetup.setStatus(SimConfig.RunwayStatus.UNAVAILABLE);
            }
        }
    }
}
