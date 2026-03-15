package sim.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom-styled JButton with hover and press effects.
 * This button extends {@link JButton} to provide:
 * <ul>
 *     <li>Custom colours for normal, hover, and pressed states</li>
 *     <li>Configurable button sizes</li>
 * </ul>
 *
 * Button uses {@link MouseAdapter} to handle mouse events
 *
 * @see MouseAdapter
 */
public class StyledButton extends JButton {

    /**
     * Constructs a new StyledButton with the specified text and colours.
     *
     * @param text the text to display on the button
     * @param normalColor the background colour in normal state
     * @param hoverColor the background colour when mouse hovers
     * @param pressColor the background colour when button is pressed
     * @param borderColor the colour of button's border
     */
    public StyledButton(String text, Color normalColor, Color hoverColor, Color pressColor, Color borderColor) {
        super(text);

        // Basic setup
        setFont(new Font("SansSerif", Font.BOLD, 17));
        setBackground(normalColor);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createLineBorder(borderColor, 2));
        setContentAreaFilled(false);
        setOpaque(true);

        setPreferredSize(new Dimension(210, 70));
        setMinimumSize(new Dimension(210, 70));

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
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(hoverColor);
            }
        });
    }

    /**
     * Sets a custom size for the button.
     *
     * @param width the desired width in pixels
     * @param height the desired height in pixels
     */
    public void setButtonSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }
}
