package sim.view.pages;

import sim.view.App;

import javax.swing.*;
import java.awt.*;

public class InputPage extends JPanel {
    private App app;

    // User input fields
    JTextField flightsField;
    JTextField inboundRateField;
    JTextField outboundRateField;

    // Constructor
    public InputPage(App app) {
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
        JPanel panelContent = new JPanel(new GridBagLayout());
        panelContent.setBackground(Color.yellow);
        panelContent.setPreferredSize(new Dimension(1040, 460));

        // formPanel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.lightGray);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray, 2),
                BorderFactory.createEmptyBorder(30, 50, 20, 50)
        ));

        // Title inside form
        JLabel formTitle = new JLabel("Simulation Configuration");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;   // Set starting position
        gbc.gridwidth = 2;  // specifies number of columns a component should span horizontally
        gbc.insets = new Insets(0, 0, 20, 0);   // External padding (margin) added around a component
        formPanel.add(formTitle, gbc);

        // Form Fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5,5,5,5);

        // Number of flights
        gbc.gridx = 0; gbc.gridy = 1;   // Move one position down
        formPanel.add(new JLabel("Number of Flights: "), gbc);
        gbc.gridx = 1;
        flightsField = new JTextField(15);
        formPanel.add(flightsField, gbc);

        // Inbound rate
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Inbound rate (aircraft/hour): "), gbc);
        gbc.gridx = 1;
        inboundRateField = new JTextField(15);
        formPanel.add(inboundRateField, gbc);

        // Outbound rate
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Outbound rate (aircraft/hour): "), gbc);
        gbc.gridx = 1;
        outboundRateField = new JTextField(15);
        formPanel.add(outboundRateField, gbc);

        // Submit button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton submitBtn = new JButton("Start Simulation");
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> {
            submitClicked();
        });
        formPanel.add(submitBtn, gbc);


        // add formPanel into the panelContent
        panelContent.add(formPanel);





        // FOOTER PANEL ----------------------------------------
        JPanel panelFooter = new JPanel();
        panelFooter.setBackground(Color.red);
        panelFooter.setPreferredSize(new Dimension(1100, 70));

        JLabel label = new JLabel("This is the input page.");
        panelFooter.add(label);


        // Add main panels
        add(panelHeader, BorderLayout.NORTH);
        add(panelLeft, BorderLayout.WEST);
        add(panelContent, BorderLayout.CENTER);
        add(panelRight, BorderLayout.EAST);
        add(panelFooter, BorderLayout.SOUTH);
    }


    // When the Submit Button is clicked
    private void submitClicked() {
        try {
            // Get the text from fields and convert to integers
            int numFlights = Integer.parseInt(flightsField.getText());
            int inboundRate = Integer.parseInt(inboundRateField.getText());
            int outboundRate = Integer.parseInt(outboundRateField.getText());

            // Debugging by printing
            System.out.println("Number of flights: " + numFlights);
            System.out.println("Inbound rate: " + inboundRate);
            System.out.println("Outbound rate: " + outboundRate);

            // If succeeded, move to SimulationPage
            app.showSimulationPage();

        } catch (NumberFormatException ex) {
            // Handle case
            System.out.println("Please enter valid numbers");

            // Display an JOptionPane
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}

