package frontend.components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(1280, 80));

        JLabel titleLabel = new JLabel("Airport Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        add(titleLabel, BorderLayout.CENTER);
    }

}
