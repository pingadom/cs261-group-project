package sim.view.components;

import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    JLabel statsLabel;
    JLabel statsValue;

    Font labelFont = new Font("SansSerif", Font.BOLD, 18);

    public StatsPanel(Color color, String title, int value) {
        String valueString = Integer.toString(value);

        setLayout(new GridBagLayout());
        setBackground(color);
        setBorder(BorderFactory.createLineBorder(Color.black, 1));
        setPreferredSize(new Dimension(200, 55));
        setMaximumSize(new Dimension(200, 55));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 5);

        statsLabel = new JLabel(title);
        statsLabel.setFont(labelFont);
        statsLabel.setForeground(Color.white);
        add(statsLabel, gbc);

        gbc.gridy = 1;
        statsValue = new JLabel();
        statsValue.setText(valueString);
        statsValue.setFont(labelFont);
        statsValue.setForeground(Color.white);
        add(statsValue, gbc);
    }

    // Setter
    public void setValue(int value) {
        String valueString = Integer.toString(value);
        statsValue.setText(valueString);
    }
}
