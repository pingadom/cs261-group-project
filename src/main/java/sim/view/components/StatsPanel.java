package sim.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * A custom panel for displaying a labelled statistic value.
 * The panel has a coloured background and is designe ot be used in the statistics column
 * of the simulation page. It provides constructors for both integer and double values, with double values
 * automatically suffixed with "s" (for seconds).
 */
public class StatsPanel extends JPanel {
    JLabel statsLabel;
    JLabel statsValue;

    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 18);

    /**
     * Constructs a StatsPanel for displaying a double value
     * The value will be displayed with a trailing "s".
     *
     * @param color the background colour of the panel
     * @param title the statistic name
     * @param value the initial double value to display
     */
    public StatsPanel(Color color, String title, double value) {
        String valueString = Double.toString(value) + "s";
        setupUI(color, title, valueString);
    }

    /**
     * Constructs a StatsPanel for displaying an integer value
     *
     * @param color the background colour of the panel
     * @param title the statistic name
     * @param value the initial integer value to display
     */
    public StatsPanel(Color color, String title, int value) {
        String valueString = Integer.toString(value);
        setupUI(color, title, valueString);
    }

    /**
     * Intialises the user interface components.
     * Users GridBagLayout with two rows to display
     * <ul>
     *     <li>Row 0: The statistic label</li>
     *      <li>Row 1: The current value</li>
     * </ul>
     *
     * Both labels are centered and displayed in white text on the colour background
     *
     * @param color the background colour of the panel
     * @param title the statistic name
     * @param value the initial integer value to display
     */
    private void setupUI(Color color, String title, String value) {
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
        statsLabel.setFont(LABEL_FONT);
        statsLabel.setForeground(Color.white);
        add(statsLabel, gbc);

        gbc.gridy = 1;
        statsValue = new JLabel();
        statsValue.setText(value);
        statsValue.setFont(LABEL_FONT);
        statsValue.setForeground(Color.white);
        add(statsValue, gbc);
    }


    /**
     * Updates the displayed value with a new double value.
     *
     * @param value the new double value to display
     */
    public void setValue(double value) {
        String valueString = Double.toString(value);
        statsValue.setText(valueString + "s");
    }

    /**
     * Updates the displayed value with a new integer value.
     *
     * @param value the new integer value to display
     */
    public void setValue(int value) {
        String valueString = Integer.toString(value);
        statsValue.setText(valueString);
    }
}
