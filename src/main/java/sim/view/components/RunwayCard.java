package sim.view.components;

import sim.config.SimConfig;
import sim.core.viewmodel.RunwayState;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * A panel that displays the current status of a single runway in the simulation.
 * <p>
 *     This component shows:
 *     <ul>
 *         <li>Runway identifier</li>
 *         <li>Current mode</li>
 *         <li>Current status</li>
 *         <li>Aircraft occupancy info.</li>
 *         <li>A "Configure" button to modify runway settings</li>
 *     </ul>
 * </p>
 *
 * The panel automatically updates its display when the simulation state changes and changes
 * background colour to indicate runway availability.
 *
 * @see RunwayState
 * @see SimController
 * @see StyledButton
 */
public class RunwayCard extends JPanel {
    private static final Color RUNWAY_CLOSED_COLOR = new Color(255, 160, 160);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);

    // ===================== INSTANCE VARIABLES =====================
    JPanel modePanel;
    JPanel statusPanel;
    JPanel aircraftPanel;
    private final JLabel modeLabel;
    private final JLabel statusLabel;
    private final JLabel aircraftLabel;

    private List<RunwayState> runwayStates;
    private final String runwayId;
    private SimConfig.RunwayMode mode;
    private SimConfig.RunwayStatus status;
    private String occupied;

    private final JPanel parentPanel;
    private final RunwayState runwayState;
    private final SimController simController;

    // ===================== CONSTRUCTOR =====================

    /**
     * Constructs a new RunwayCard for the specified runway
     *
     * @param runway the runway state to display
     * @param parent the parent panel
     * @param simController the controller for simulation interaction
     */
    public RunwayCard(RunwayState runway, JPanel parent, SimController simController) {
        this.parentPanel = parent;
        this.runwayState = runway;
        this.runwayId = runway.getCode();
        this.mode = runway.getMode();
        this.status = runway.getStatus();
        this.occupied = runway.getOccupied();
        this.simController = simController;

        // ===================== UI INITIALISATION =====================
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Color.white);

        // Titled border for each card with runway ID
        Border lineBorder = BorderFactory.createLineBorder(Color.black, 1);
        TitledBorder titleBorder = BorderFactory.createTitledBorder(
                lineBorder,
                runwayId,
                TitledBorder.LEFT,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.ITALIC, 16),
                Color.black
        );
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(titleBorder, padding);
        setBorder(compoundBorder);

        setPreferredSize(new Dimension(640, 100));
        setMinimumSize(new Dimension(640, 100));
        setMaximumSize(new Dimension(640, 100));

        // Creates info panels and labels
        modePanel = new JPanel();
        statusPanel = new JPanel();
        aircraftPanel = new JPanel();

        modePanel.setPreferredSize(new Dimension(110, 80));
        modePanel.setBackground(Color.white);
        modePanel.setLayout(new BorderLayout());
        modeLabel = new JLabel();
        updateModeLabel();
        modeLabel.setFont(LABEL_FONT);
        modeLabel.setHorizontalAlignment(JLabel.CENTER);
        modePanel.add(modeLabel, BorderLayout.CENTER);

        statusPanel.setPreferredSize(new Dimension(220, 80));
        statusPanel.setBackground(Color.white);
        statusPanel.setLayout(new BorderLayout());
        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setFont(LABEL_FONT);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        aircraftPanel.setPreferredSize(new Dimension(110, 80));
        aircraftPanel.setBackground(Color.white);
        aircraftPanel.setLayout(new BorderLayout());
        aircraftLabel = new JLabel();
        updateOccupiedLabel();
        aircraftLabel.setFont(LABEL_FONT);
        aircraftLabel.setHorizontalAlignment(JLabel.CENTER);
        aircraftPanel.add(aircraftLabel, BorderLayout.CENTER);

        // Add labels and buttons
        add(modePanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(statusPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(aircraftPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));

        // Create the configure button
        createConfigureButton();
    }

    /**
     * Creates and adds the configure button with its action listener
     */
    private void createConfigureButton() {
        StyledButton runwayConfigButton = new StyledButton(
                "Configure",
                new Color(70, 130, 180),
                new Color(100, 150, 200),
                new Color(70, 130, 180),
                new Color(70, 130, 180)
        );

        runwayConfigButton.setButtonSize(90, 30);
        runwayConfigButton.setFont(LABEL_FONT);
        runwayConfigButton.addActionListener(e -> {
            if (!simController.getStateSnapshot().isPaused()) {
                JOptionPane.showMessageDialog(
                        this,
                        "The system should be paused to configure runways",
                        "Configuration Blocked",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                createRunwayConfigPanel();
            }
        });

        add(runwayConfigButton);
    }

    // ===================== CONFIGURATION DIALOG =====================

    /**
     * Shows a dialog for configuring runway mode and status. Only available when simulation is paused.
     */
    private void createRunwayConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        comboPanel.add(modeLabel, gbc);
        gbc.gridy = 1;
        comboPanel.add(modeCombo, gbc);
        gbc.gridx = 1;
        comboPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        gbc.gridx = 2;
        gbc.gridy = 0;
        comboPanel.add(statusLabel, gbc);
        gbc.gridy = 1;
        comboPanel.add(statusCombo, gbc);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(comboPanel);

        // Show the JOptionPane
        int result = JOptionPane.showConfirmDialog(
                parentPanel,
                panel,
                "Configure Runway " + runwayState.getCode(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // If result is OK
        if (result == JOptionPane.OK_OPTION) {
            // Get the mode selected and update UI
            String modeSelected = (String) modeCombo.getSelectedItem();
            if (modeSelected != null) {
                switch (modeSelected) {
                    case "Landing Only" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.LANDING);
                    case "Takeoff Only" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.TAKEOFF);
                    case "Mixed Mode" -> simController.setRunwayMode(runwayId, SimConfig.RunwayMode.MIXED);
                }
            }
            updateModeLabel();

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
            }
            updateStatusLabel();
        }

        // Update UI
        this.revalidate();
        this.repaint();
    }

    /**
     * Creates the title panel for the dialog panel.
     *
     * @return a JPanel containing the title for the dialog panel.
     */
    private JPanel createTitlePopupPanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        titlePanel.setPreferredSize(new Dimension(380, 50));

        JLabel titleLabel = new JLabel("Configuring for Runway " + runwayState.getCode());
        titleLabel.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        return titlePanel;
    }


    // ===================== UPDATE METHODS =====================

    /**
     * Updates the mode label with the current runway mode from the simulation state.
     */
    private void updateModeLabel() {
        // Get the most recent list of RunwayStates
        this.runwayStates = simController.getStateSnapshot().getRunways();

        // Find the matching runwayState to update its Mode
        for (RunwayState runwayState: runwayStates) {
            if (runwayState.getCode().equals(runwayId)) {
                mode = runwayState.getMode();   // Update the mode
            }
        }

        if (mode == SimConfig.RunwayMode.LANDING) {
            modeLabel.setText("Mode: Landing");
        } else if (mode == SimConfig.RunwayMode.TAKEOFF) {
            modeLabel.setText("Mode: Take-off");
        } else if (mode == SimConfig.RunwayMode.MIXED) {
            modeLabel.setText("Mode: Mixed");
        }
    }

    /**
     * Updates the status label with the current runway status from the simulation state.
     * Also updates the background colour based on availability.
     */
    private void updateStatusLabel() {
        // Get the most recent list of RunwayStates
        SimState simState = simController.getStateSnapshot();
        this.runwayStates = simState.getRunways();

        // Find the matching runwayState to update its Status
        for (RunwayState runwayState: runwayStates) {
            if (runwayState.getCode().equals(runwayId)) {
                status = runwayState.getStatus();   // Update the mode
            }
        }

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

        if (status == SimConfig.RunwayStatus.AVAILABLE) {
            setBackgroundColour(Color.white);
        } else {
            setBackgroundColour(RUNWAY_CLOSED_COLOR);
        }
    }

    /**
     * Sets the background colour of this card and all its info panels.
     *
     * @param color the colour to set
     */
    private void setBackgroundColour(Color color) {
        this.setBackground(color);
        modePanel.setBackground(color);
        statusPanel.setBackground(color);
        aircraftPanel.setBackground(color);
    }

    /**
     * Updates the occupied label with the current aircraft information.
     */
    public void updateOccupiedLabel() {
        // Get the most recent list of RunwayStates
        this.runwayStates = simController.getStateSnapshot().getRunways();

        // Find the matching runwayState to update its Status
        for (RunwayState runwayState: runwayStates) {
            if (runwayState.getCode().equals(runwayId)) {
                occupied = runwayState.getOccupied();   // Update the mode
            }
        }

        if (occupied.isEmpty()) {
            aircraftLabel.setText("Not Occupied");
        } else {
            aircraftLabel.setText("Aircraft: " + occupied);
        }
    }
}
