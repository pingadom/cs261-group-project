package sim.view.pages;

import sim.view.App;
import sim.view.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SimulationPage extends JPanel {
    private App app;

    int toggleStartPause = 0;
    StyledButton startPauseButton;
    StyledButton resetButton;
    JLabel startPauseLabel;

    int simulationSpeed = 1;

    // Constructor
    public SimulationPage(App app) {
        this.app = app;
        setupUI();
    }

    // Setting up the UI
    private void setupUI() {
        // set LayoutManager
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.white);

        // Creating subpanels inside this page
        JPanel headerPanel = new HeaderPanel();
        JPanel leftPanel = new SidePanel();
        JPanel rightPanel = new SidePanel();
        JPanel footerPanel = new FooterPanel();

        // CONTENT PANEL ----------------------------------------
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBackground(Color.white);
        contentPanel.setPreferredSize(new Dimension());

        // LEFT Column - Stats
        JPanel leftContentColumn = createStatsPanel();

        // CENTER Column - Control + Runways
        JPanel centerContentColumn = createCenterColumnPanel();

        // RIGHT Column - Clock + Buttons
        JPanel rightContentColumn = createRightColumnPanel();

        // Adding leftColumn and rightColumn into contentPanel
        contentPanel.add(leftContentColumn);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 0)));    // Gap in between
        contentPanel.add(centerContentColumn);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        contentPanel.add(rightContentColumn);

        // FOOTER PANEL ----------------------------------------
        StyledButton buttonBack = new StyledButton("Back", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBack.addActionListener(e -> {
            app.showInputPage();
        });

        footerPanel.add(buttonBack);


        // Add main panels and set positions
        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        // leftContentColumn.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.setMinimumSize(new Dimension(200, 520));
        panel.setMaximumSize(new Dimension(200, 520));

        // Panels for all stats
        JPanel cancelledStats = new StatsPanel(new Color(0xE00A0A), "Cancelled", 0);
        JPanel divertedStats = new StatsPanel(new Color(0xE00A0A), "Diverted", 0);
        JPanel avgQueueStats = new StatsPanel(new Color(0xFF8C0A), "Avg Queue", 0);
        JPanel avgHoldingStats = new StatsPanel(new Color(0xFF8C0A), "Avg Holding", 0);
        JPanel maxQueueStats = new StatsPanel(new Color(0xFF8C0A), "Max Queue", 0);
        JPanel maxHoldingStats = new StatsPanel(new Color(0xFF8C0A), "Max Holding", 0);
        JPanel departedStats = new StatsPanel(new Color(0x0AE04E), "Departed", 0);
        JPanel arrivedStats = new StatsPanel(new Color(0x0AE04E), "Arrived", 0);

        panel.add(Box.createVerticalGlue());
        panel.add(cancelledStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(divertedStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(avgQueueStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(avgHoldingStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(maxQueueStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(maxHoldingStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(departedStats);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(arrivedStats);

        return panel;
    }

    private JPanel createCenterColumnPanel() {
        // CENTER Column - Control + Runways
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(670, 520));

        // Control panel
        JPanel controlPanel = createControlPanel();

        // Runway panel
        JPanel runwayPanel = createRunwayPanel();

        // Adding into topCenterColumn
        panel.add(controlPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(runwayPanel);

        return panel;
    }

    private JPanel createControlPanel() {
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.white);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        // controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.setPreferredSize(new Dimension(670, 40));
        controlPanel.setMaximumSize(new Dimension(670, 40));
        controlPanel.setMinimumSize(new Dimension(670, 40));

        // Start button
        startPauseButton = new StyledButton("", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        startPauseButton.setLayout(new BorderLayout());
        startPauseButton.setFocusPainted(false);
        startPauseButton.setBackground(Color.black);
        startPauseButton.setPreferredSize(new Dimension(120, 40));
        startPauseButton.setMaximumSize(new Dimension(120, 40));
        startPauseButton.addActionListener(e -> {
            toggleStartButton();
        });

        // Add an icon
        startPauseLabel = new JLabel("Pause", JLabel.CENTER);
        startPauseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        startPauseLabel.setForeground(Color.white);
        startPauseButton.add(startPauseLabel, BorderLayout.CENTER);

        // Reset button
        resetButton = new StyledButton("Reset", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        resetButton.setPreferredSize(new Dimension(120, 40));
        resetButton.setMaximumSize(new Dimension(120, 40));
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        resetButton.addActionListener(e -> {
            resetSimulation();
        });

        // SPEEDUP PANEL
        JPanel speedupPanel = createSpeedPanel();

        // Adding components into controlPanel
        controlPanel.add(startPauseButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(resetButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(speedupPanel);

        return controlPanel;
    }

    private JPanel createSpeedPanel() {
        // SPEEDUP CONTROL PANEL
        JPanel speedupPanel = new JPanel(new GridBagLayout());
        speedupPanel.setBackground(Color.white);

        JLabel speedupLabel = new JLabel("Speed: ");
        speedupLabel.setForeground(Color.black);
        speedupLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Create radio buttons
        JToggleButton x1Button = new JToggleButton("x1");
        JToggleButton x2Button = new JToggleButton("x2");
        JToggleButton x5Button = new JToggleButton("x5");
        JToggleButton x10Button = new JToggleButton("x10");

        List<JToggleButton> speedButtons = new ArrayList<>();
        speedButtons.add(x1Button);
        speedButtons.add(x2Button);
        speedButtons.add(x5Button);
        speedButtons.add(x10Button);

        ButtonGroup speedGroup = new ButtonGroup();

        // Style the radio button to look like buttons and add into button group
        for (JToggleButton btn : speedButtons) {
            speedGroup.add(btn);

            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(50, 35));
            btn.setBackground(Color.white);
            btn.setForeground(Color.black);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.setFocusPainted(false);

            btn.setContentAreaFilled(false);
            btn.setOpaque(true);

            // Changes appearance when selected
            btn.addItemListener(e -> {
                if (btn.isSelected()) {
                    // System.out.println("Button " + btn.getText() + " selected");
                    btn.setBackground(Color.black);
                    btn.setForeground(Color.white);
                } else {
                    btn.setBackground(Color.white);
                    btn.setForeground(Color.black);
                }
            });
        }

        // Default selection
        x1Button.setSelected(true);

        // Add ActionListeners for each
        x1Button.addActionListener(e -> setSimulationSpeed(1));
        x2Button.addActionListener(e -> setSimulationSpeed(2));
        x5Button.addActionListener(e -> setSimulationSpeed(5));
        x10Button.addActionListener(e -> setSimulationSpeed(10));

        // Add labels and buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        speedupPanel.add(speedupLabel);

        gbc.gridx = 1; speedupPanel.add(Box.createRigidArea(new Dimension(5, 0)), gbc); // Spacer
        gbc.gridx = 2; speedupPanel.add(x1Button);
        gbc.gridx = 3; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 4; speedupPanel.add(x2Button);
        gbc.gridx = 5; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 6; speedupPanel.add(x5Button);
        gbc.gridx = 7; speedupPanel.add(Box.createRigidArea(new Dimension(2, 0)), gbc); // Spacer
        gbc.gridx = 8; speedupPanel.add(x10Button);

        return speedupPanel;
    }

    private void setSimulationSpeed(int speed) {
        simulationSpeed = speed;
        System.out.println("Simulation speed is: " + simulationSpeed);
    }

    private JPanel createRunwayPanel() {
        // Runway panel
        JPanel runwayPanel = new JPanel();
        runwayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        runwayPanel.setPreferredSize(new Dimension(670, 470));

        // Main container for all the runways
        JPanel runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add runway card for each runway
        runwaysContainer.add(new RunwayCard("1", "Available", "Landing", "AA100", true));
        runwaysContainer.add(new RunwayCard("2", "Available", "Take-off", "AA104", true));
        runwaysContainer.add(new RunwayCard("3", "Available", "Landing", "AA140", false));
        runwaysContainer.add(new RunwayCard("4", "Available", "Take-off", "AA141", false));
        runwaysContainer.add(new RunwayCard("5", "Available", "Landing", "AA120", false));
        runwaysContainer.add(new RunwayCard("6", "Available", "Mixed", "BB140", true));

        JScrollPane scrollPaneRunwaysContainer = new JScrollPane(runwaysContainer);
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(660, 460));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        runwayPanel.add(scrollPaneRunwaysContainer);

        return runwayPanel;
    }

    private JPanel createRightColumnPanel() {
        // RIGHT Column - Clock + Buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(200, 520));

        // Clock panel
        JPanel clockPanel = new JPanel();
        // clockPanel.setBackground(Color.yellow);
        clockPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        clockPanel.setPreferredSize(new Dimension(200, 80));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(Color.white);
        // buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        buttonsPanel.setPreferredSize(new Dimension(200, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10);

        JButton listOfFlightsButton = new StyledButton("List of Flights", new Color(0x1B30A6), new Color(0x2A45C9), new Color(0x0F1F73), new Color(0x8799E0));
        buttonsPanel.add(listOfFlightsButton, gbc);

        gbc.gridy = 1;
        JButton holdingPatternButton = new StyledButton("Holding Pattern", new Color(0x4A1073), new Color(0x621A96), new Color(0x320A4F), new Color(0x9B6BCE));
        buttonsPanel.add(holdingPatternButton, gbc);

        gbc.gridy = 2;
        JButton takeoffQueueButton = new StyledButton("Take-off Queue", new Color(0x141E54), new Color(0x1E2D7A), new Color(0x0B1238), new Color(0x5A6AB0));
        buttonsPanel.add(takeoffQueueButton, gbc);

        // Adding control panel and buttons panel into panel
        panel.add(clockPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));    // Gap in between
        panel.add(buttonsPanel);

        return panel;
    }

    // Functions
    private void toggleStartButton() {
        if (toggleStartPause == 0) {
            startPauseLabel.setText("Start");
            System.out.println("System paused!");
            toggleStartPause = 1;
        } else if (toggleStartPause == 1) {
            startPauseLabel.setText("Pause");
            System.out.println("System resumed!");
            toggleStartPause = 0;
        }
    }

    private void resetSimulation() {
        System.out.println("System reset");
    }

}
