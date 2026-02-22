package sim.view.components;

import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {

    Font labelFont = new Font("SansSerif", Font.BOLD, 18);

    public StatsPanel(Color color, String title, String value) {
        setLayout(new GridBagLayout());
        setBackground(color);
        setBorder(BorderFactory.createLineBorder(Color.black, 1));
        setPreferredSize(new Dimension(200, 55));
        setMaximumSize(new Dimension(200, 55));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 5);

        JLabel cancelledLabel = new JLabel(title);
        cancelledLabel.setFont(labelFont);
        cancelledLabel.setForeground(Color.white);
        add(cancelledLabel, gbc);

        gbc.gridy = 1;
        JLabel cancelledValue = new JLabel();
        cancelledValue.setText(value);
        cancelledValue.setFont(labelFont);
        cancelledValue.setForeground(Color.white);
        add(cancelledValue, gbc);
    }
}
