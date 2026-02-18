package frontend.pages;

import frontend.App;
import frontend.components.FooterPanel;
import frontend.components.HeaderPanel;
import frontend.components.RunwayCard;
import frontend.components.SidePanel;

import javax.naming.ldap.Control;
import javax.swing.*;
import java.awt.*;

public class SimulationPage extends JPanel {
    private App app;

    // Sample data - map


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
        // leftContentColumn.setBackground(Color.pink);
        leftContentColumn.setBorder(BorderFactory.createLineBorder(Color.black));
        leftContentColumn.setPreferredSize(new Dimension(200, 520));

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
        // controlPanel.setBackground(Color.orange);
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        controlPanel.setPreferredSize(new Dimension(650, 70));

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
        JPanel buttonsPanel = new JPanel();
        // buttonsPanel.setBackground(Color.blue);
        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        buttonsPanel.setPreferredSize(new Dimension(200, 440));


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
}
