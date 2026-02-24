package sim.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RunwayCard extends JPanel {
    private JLabel runwayIDLabel;
    private JLabel statusLabel;
    private JLabel modeLabel;
    private JLabel aircraftLabel;
    private JLabel occupiedLabel;

    // Constructor
    public RunwayCard(String id, String status, String mode, String aircraft, Boolean occupied) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Runway " + id));
        setPreferredSize(new Dimension(500, 100));

        // Add the labels
        statusLabel = new JLabel("Status: " + status);
        modeLabel = new JLabel("Mode: " + mode);
        aircraftLabel = new JLabel("Aircraft: " + aircraft);
        if (occupied) {
            occupiedLabel = new JLabel("Occupied");
        } else {
            occupiedLabel = new JLabel("Free");
        }

        // Define GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 0, 15);
        add(statusLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 0, 15);
        add(modeLabel, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 0, 15);
        add(aircraftLabel, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 0, 15);
        add(occupiedLabel, gbc);

    }
}
