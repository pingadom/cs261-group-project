package frontend.pages;

import frontend.App;
import frontend.components.FooterPanel;
import frontend.components.HeaderPanel;
import frontend.components.RunwayCard;
import frontend.components.SidePanel;

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
        setLayout(new BorderLayout());

        // Creating subpanels inside this page
        JPanel panelHeader = new HeaderPanel();
        JPanel panelLeft = new SidePanel(Color.blue);
        JPanel panelRight = new SidePanel(Color.cyan);


        // CONTENT PANEL ----------------------------------------
        JPanel panelContent = new JPanel();
        panelContent.setBackground(Color.yellow);
        panelContent.setPreferredSize(new Dimension(1100, 460));

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
        scrollPaneRunwaysContainer.setPreferredSize(new Dimension(700, 400));
        scrollPaneRunwaysContainer.getVerticalScrollBar().setUnitIncrement(10);     // Changing sensitivity of scrollbar

        panelContent.add(scrollPaneRunwaysContainer);


        // FOOTER PANEL ----------------------------------------
        JPanel panelFooter = new FooterPanel();

        JLabel label = new JLabel("This is the SIMULATION page.");
        panelFooter.add(label);

        JButton buttonBack = new JButton("Back");
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e -> {
            app.showInputPage();
        });

        panelFooter.add(buttonBack);


        // Add main panels and set positions
        add(panelHeader, BorderLayout.NORTH);
        add(panelLeft, BorderLayout.WEST);
        add(panelContent, BorderLayout.CENTER);
        add(panelRight, BorderLayout.EAST);
        add(panelFooter, BorderLayout.SOUTH);
    }

    // Functions
    public void refreshData() {
        System.out.println("Test");
    }
}
