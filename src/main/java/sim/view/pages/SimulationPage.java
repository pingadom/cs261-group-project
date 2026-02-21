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
        JPanel cancelledStats = new StatsPanel(Color.red, "Cancelled", "0");
        JPanel divertedStats = new StatsPanel(Color.red, "Diverted", "0");
        JPanel avgQueueStats = new StatsPanel(Color.orange, "Avg Queue", "0");
        JPanel avgHoldingStats = new StatsPanel(Color.orange, "Avg Holding", "0");
        JPanel maxQueueStats = new StatsPanel(Color.orange, "Max Queue", "0");
        JPanel maxHoldingStats = new StatsPanel(Color.orange, "Max Holding", "0");
        JPanel departedStats = new StatsPanel(Color.green, "Departed", "0");
        JPanel arrivedStats = new StatsPanel(Color.green, "Arrived", "0");

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


        // RIGHT Column - Control + Clock + Runways + Buttons
        JPanel rightContentColumn = new JPanel();
        rightContentColumn.setLayout(new BoxLayout(rightContentColumn, BoxLayout.Y_AXIS));
        rightContentColumn.setBackground(Color.white);
        rightContentColumn.setPreferredSize(new Dimension(910, 520));

        // top - Control + Clock
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setBackground(Color.white);
        topRow.setPreferredSize(new Dimension(910, 70));

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.setPreferredSize(new Dimension(650, 70));
        controlPanel.setMaximumSize(new Dimension(650, 70));
        controlPanel.setMinimumSize(new Dimension(650, 70));

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
        JButton resetButton = new JButton("Reset");
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setForeground(Color.white);
        resetButton.setBackground(Color.black);
        resetButton.setMaximumSize(new Dimension(100, 40));




        // Adding components into controlPanel
        controlPanel.add(startPauseButton);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(resetButton);


        // Clock panel
        JPanel clockPanel = new JPanel();
        // clockPanel.setBackground(Color.yellow);
        clockPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        clockPanel.setPreferredSize(new Dimension(250, 70));

        // bottom - Runways + Buttons
        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setBackground(Color.white);
        bottomRow.setPreferredSize(new Dimension(910, 440));

        // Runway panel
        JPanel runwayPanel = new JPanel();
        // runwayPanel.setBackground(Color.red);
        runwayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        runwayPanel.setPreferredSize(new Dimension(700, 440));

        // Main container for all the runways
        JPanel runwaysContainer = new JPanel();
        runwaysContainer.setLayout(new BoxLayout(runwaysContainer, BoxLayout.Y_AXIS));

        // Add runway card for each runway
        runwaysContainer.add(new RunwayCard("1", "Available", "Landing", "AA100"));
        runwaysContainer.add(new RunwayCard("2", "Available", "Take-off", "AA104"));
        runwaysContainer.add(new RunwayCard("3", "Available", "Landing", "AA140"));
        runwaysContainer.add(new RunwayCard("4", "Available", "Take-off", "AA141"));
        runwaysContainer.add(new RunwayCard("5", "Available", "Landing", "AA120"));
        runwaysContainer.add(new RunwayCard("6", "Available", "Mixed", "BB140"));

        JScrollPane scrollPaneRunwaysContainer = new JScrollPane(runwaysContainer);
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(680, 420));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        runwayPanel.add(scrollPaneRunwaysContainer);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(Color.white);
        // buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        buttonsPanel.setPreferredSize(new Dimension(200, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10);

        JButton listOfFlightsButton = new StyledButton("List of Flights", Color.blue, Color.gray, Color.lightGray);
        buttonsPanel.add(listOfFlightsButton, gbc);

        gbc.gridy = 1;
        JButton holdingPatternButton = new StyledButton("Holding Pattern", Color.MAGENTA, Color.gray, Color.lightGray);
        buttonsPanel.add(holdingPatternButton, gbc);

        gbc.gridy = 2;
        JButton takeoffQueueButton = new StyledButton("Take-off Queue", Color.green, Color.gray, Color.lightGray);
        buttonsPanel.add(takeoffQueueButton, gbc);


        // Add in controlPanel and clockPanel inside topRow panel
        topRow.add(controlPanel);
        topRow.add(Box.createRigidArea(new Dimension(10, 0)));
        topRow.add(clockPanel);

        // Adding runwayPanel and buttonsPanel inside bottomRow panel
        bottomRow.add(runwayPanel);
        bottomRow.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomRow.add(buttonsPanel);

        // Adding topRow and bottomRow into rightColumn
        rightContentColumn.add(topRow);
        rightContentColumn.add(Box.createRigidArea(new Dimension(0, 10)));    // Gap in between
        rightContentColumn.add(bottomRow);

        // Adding leftColumn and rightColumn into contentPanel
        contentPanel.add(leftContentColumn);
        contentPanel.add(Box.createRigidArea(new Dimension(10, 0)));    // Gap in between
        contentPanel.add(rightContentColumn);

        // FOOTER PANEL ----------------------------------------
        JLabel label = new JLabel("This is the SIMULATION page.");
        footerPanel.add(label);

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
    public void refreshData() {
        System.out.println("Test");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // When the pause button clicked
        if (e.getSource() == startPauseButton) {
            if (toggleStartPause == 0) {
                startPauseLabel.setText("Start");
                toggleStartPause = 1;
            } else if (toggleStartPause == 1) {
                startPauseLabel.setText("Pause");
                toggleStartPause = 0;
            }
        }


    }
}
