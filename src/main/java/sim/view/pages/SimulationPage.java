package sim.view.pages;

import sim.view.App;
import sim.view.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationPage extends JPanel implements ActionListener {
    private App app;

    int toggleStartPause = 0;
    JButton startPauseButton;
    JLabel startPauseLabel;

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
        JPanel leftContentColumn = new JPanel();
        leftContentColumn.setLayout(new BoxLayout(leftContentColumn, BoxLayout.Y_AXIS));
        leftContentColumn.setBackground(Color.white);
        // leftContentColumn.setBorder(BorderFactory.createLineBorder(Color.black));
        leftContentColumn.setMinimumSize(new Dimension(200, 520));
        leftContentColumn.setMaximumSize(new Dimension(200, 520));

        // Panels for all stats
        JPanel cancelledStats = new StatsPanel(new Color(0xE00A0A), "Cancelled", "0");
        JPanel divertedStats = new StatsPanel(new Color(0xE00A0A), "Diverted", "0");
        JPanel avgQueueStats = new StatsPanel(new Color(0xFF8C0A), "Avg Queue", "0");
        JPanel avgHoldingStats = new StatsPanel(new Color(0xFF8C0A), "Avg Holding", "0");
        JPanel maxQueueStats = new StatsPanel(new Color(0xFF8C0A), "Max Queue", "0");
        JPanel maxHoldingStats = new StatsPanel(new Color(0xFF8C0A), "Max Holding", "0");
        JPanel departedStats = new StatsPanel(new Color(0x0AE04E), "Departed", "0");
        JPanel arrivedStats = new StatsPanel(new Color(0x0AE04E), "Arrived", "0");

        leftContentColumn.add(Box.createVerticalGlue());
        leftContentColumn.add(cancelledStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(divertedStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(avgQueueStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(avgHoldingStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(maxQueueStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(maxHoldingStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(departedStats);
        leftContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentColumn.add(arrivedStats);


        // CENTER Column - Control + Runways
        JPanel centerContentColumn = new JPanel();
        centerContentColumn.setLayout(new BoxLayout(centerContentColumn, BoxLayout.Y_AXIS));
        centerContentColumn.setBackground(Color.white);
        centerContentColumn.setPreferredSize(new Dimension(670, 520));

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.setPreferredSize(new Dimension(670, 50));
        controlPanel.setMaximumSize(new Dimension(670, 50));
        controlPanel.setMinimumSize(new Dimension(670, 50));

        // Start button
        startPauseButton = new JButton();
        startPauseButton.setLayout(new BorderLayout());
        startPauseButton.setFocusPainted(false);
        startPauseButton.setBackground(Color.black);
        startPauseButton.addActionListener(this);
        startPauseButton.setPreferredSize(new Dimension(100, 40));
        startPauseButton.setMaximumSize(new Dimension(100, 40));
        startPauseLabel = new JLabel("Pause", JLabel.CENTER);
        startPauseLabel.setFont(new Font("Arial", Font.BOLD, 14));
        startPauseLabel.setForeground(Color.white);
        startPauseButton.add(startPauseLabel, BorderLayout.CENTER);

        // Reset button
        StyledButton resetButton = new StyledButton("Reset", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        resetButton.setMaximumSize(new Dimension(100, 40));
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Adding components into controlPanel
        controlPanel.add(startPauseButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(resetButton);

        // Runway panel
        JPanel runwayPanel = new JPanel();
        runwayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        runwayPanel.setPreferredSize(new Dimension(670, 460));

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
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(660, 440));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        runwayPanel.add(scrollPaneRunwaysContainer);

        // Adding into topCenterColumn
        centerContentColumn.add(controlPanel);
        centerContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));
        centerContentColumn.add(runwayPanel);


        // RIGHT Column - Clock + Buttons
        JPanel rightContentColumn = new JPanel();
        rightContentColumn.setLayout(new BoxLayout(rightContentColumn, BoxLayout.Y_AXIS));
        rightContentColumn.setBackground(Color.white);
        rightContentColumn.setPreferredSize(new Dimension(200, 520));

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

        // Adding control panel and buttons panel into rightContentColumn
        rightContentColumn.add(clockPanel);
        rightContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));    // Gap in between
        rightContentColumn.add(buttonsPanel);

        // Adding leftColumn and rightColumn into contentPanel
        contentPanel.add(leftContentColumn);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 0)));    // Gap in between
        contentPanel.add(centerContentColumn);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        contentPanel.add(rightContentColumn);

        // FOOTER PANEL ----------------------------------------
        JButton buttonBack = new JButton("Back");
        buttonBack.setFocusPainted(false);
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

    // Functions
    @Override
    public void actionPerformed(ActionEvent e) {

        // When the pause button clicked
        if (e.getSource() == startPauseButton) {
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


    }
}
