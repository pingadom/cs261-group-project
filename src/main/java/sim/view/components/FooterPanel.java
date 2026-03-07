package sim.view.components;

import javax.swing.*;
import java.awt.*;

public class FooterPanel extends JPanel {

    // Dimension constants
    private static final int APP_HEIGHT = 720;
    private static final int APP_WIDTH = 1280;

    public FooterPanel() {
        setBackground(Color.white);
        setPreferredSize(new Dimension(APP_WIDTH, 60));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Setting Layout Manager
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
}
