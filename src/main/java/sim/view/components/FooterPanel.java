package sim.view.components;

import javax.swing.*;
import java.awt.*;

public class FooterPanel extends JPanel {

    public FooterPanel() {
        setBackground(Color.white);
        setPreferredSize(new Dimension(1280, 60));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Setting Layout Manager
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
}
