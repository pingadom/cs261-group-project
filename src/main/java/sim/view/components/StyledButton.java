package sim.view.components;

import com.fasterxml.jackson.databind.cfg.CacheProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressColor;
    private Color borderColor;

    public StyledButton(String text, Color normalColor, Color hoverColor, Color pressColor, Color borderColor) {
        super(text);

        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressColor = pressColor;
        this.borderColor = borderColor;

        // Basic setup
        setFont(new Font("SansSerif", Font.BOLD, 20));
        setBackground(normalColor);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createLineBorder(borderColor, 2));
        setContentAreaFilled(false);
        setOpaque(true);

        // Size
        setPreferredSize(new Dimension(200, 90));
        setMinimumSize(new Dimension(200, 90));
        // setMargin(new Insets(20, 20, 20, 20));  - no change

        // Hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressColor);
                // setMargin(new Insets(22, 20, 18, 20));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
                // setMargin(new Insets(20, 20, 20, 20));
            }
        });

    }
}
