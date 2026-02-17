package frontend.pages;

import frontend.App;

import javax.swing.*;
import java.awt.*;

public class SimulationPage extends JPanel {
    private App app;

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
        // HEADER PANEL ----------------------------------------
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.green);
        panelHeader.setPreferredSize(new Dimension(1100, 70));

        JLabel titleLabel = new JLabel("Airport Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        panelHeader.add(titleLabel, BorderLayout.CENTER);

        // LEFTMOST PANEL ----------------------------------------
        JPanel panelLeft = new JPanel();
        panelLeft.setBackground(Color.blue);
        panelLeft.setPreferredSize(new Dimension(80, 460));

        // RIGHTMOST PANEL ----------------------------------------
        JPanel panelRight = new JPanel();
        panelRight.setBackground(Color.cyan);
        panelRight.setPreferredSize(new Dimension(80, 460));

        // CONTENT PANEL ----------------------------------------
        JPanel panelContent = new JPanel();
        panelContent.setBackground(Color.yellow);
        panelContent.setPreferredSize(new Dimension(1100, 460));



        // FOOTER PANEL ----------------------------------------
        JPanel panelFooter = new JPanel();
        panelFooter.setBackground(Color.red);
        panelFooter.setPreferredSize(new Dimension(1100, 70));

        JLabel label = new JLabel("This is the SIMULATION page.");
        panelFooter.add(label);

        JButton buttonBack = new JButton("Back");
        buttonBack.setFocusPainted(false);
        buttonBack.addActionListener(e -> {
            app.showInputPage();
        });

        panelFooter.add(buttonBack);


        // Add main panels
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
