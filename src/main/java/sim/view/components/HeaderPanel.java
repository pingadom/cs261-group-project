package sim.view.components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    // Dimension constants
    private static final int APP_HEIGHT = 720;
    private static final int APP_WIDTH = 1280;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(APP_WIDTH, 80));

        JLabel titleLabel = new JLabel("Airport Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        add(titleLabel, BorderLayout.CENTER);
    }
}
