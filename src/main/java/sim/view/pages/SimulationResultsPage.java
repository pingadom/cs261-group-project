package sim.view.pages;

import sim.core.metrics.Metrics;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;
import sim.view.App;
import sim.view.components.StyledButton;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Live results page showing the final summary metrics of a completed simulation.
 *
 * <p>The page displays a single summary row containing key performance measures such as:
 * <ul>
 *   <li>arrivals processed,</li>
 *   <li>departures processed,</li>
 *   <li>maximum delays,</li>
 *   <li>average delays,</li>
 *   <li>diversions,</li>
 *   <li>cancellations.</li>
 * </ul>
 *
 * <p>The page also provides a download button to export the currently displayed summary
 * table as a CSV file.
 */
public class SimulationResultsPage extends BasePanel {

    /** Controller used to access the latest live simulation snapshot. */
    private final SimController simController;

    /** Table used to display simulation summary results. */
    private JTable table;

    /** Non-editable table model backing the results table. */
    private DefaultTableModel model;

    /**
     * Constructs a new live simulation results page.
     *
     * @param app main application instance used for navigation
     * @param simController controller used to read simulation state
     * @param mainTitle title displayed at the top of the page
     * @param detailsCol column headers for the results table
     */
    public SimulationResultsPage(App app, SimController simController, String mainTitle, String[] detailsCol) {
        super(app, mainTitle, detailsCol, new String[0][0]);
        this.simController = simController;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshResults();
            }
        });
    }

    /**
     * Customises the footer with navigation controls.
     *
     * <p>The footer includes:
     * <ul>
     *   <li>a Back button to return to the simulation page,</li>
     *   <li>a New Simulation button to go back to the input page and reset the ended flag.</li>
     * </ul>
     */
    @Override
    protected void customizeFooter() {
        StyledButton buttonBack = new StyledButton("Back", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBack.addActionListener(e -> {
            app.getSimulationPage().stopTimer();
            app.showSimulationPage();
        });

        StyledButton newSimButton = new StyledButton("+ New Simulation", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        newSimButton.setPreferredSize(new Dimension(150, 30));
        newSimButton.setMaximumSize(new Dimension(150, 30));
        newSimButton.setFont(new Font("Arial", Font.BOLD, 14));
        newSimButton.addActionListener(e -> {
            app.getSimulationPage().resetSimulationEndedFlag();
            app.showInputPage();
        });

        footerPanel.add(buttonBack);
        footerPanel.add(Box.createHorizontalGlue());
        footerPanel.add(newSimButton);
    }

    /**
     * Creates a non-editable, colour-coded summary results table.
     *
     * <p>Columns are coloured by category:
     * <ul>
     *   <li>green for arrivals/departures,</li>
     *   <li>orange for delay metrics,</li>
     *   <li>red for diversions/cancellations.</li>
     * </ul>
     *
     * @param columnName result column headers
     * @param data unused placeholder data array required by the base signature
     * @return scroll pane containing the formatted results table
     */
    @Override
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        model = new DefaultTableModel(columnName, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 30));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    Object value = table.getValueAt(row, col);
                    table.setToolTipText(value != null ? value.toString() : null);
                }
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String currentColumnName = table.getColumnName(column);

                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);

                if (currentColumnName.equals("Cancelled") || currentColumnName.equals("Diverted")) {
                    c.setBackground(new Color(0xE60000));
                    c.setForeground(Color.WHITE);
                } else if (
                        currentColumnName.equals("Max Queue Delay")
                                || currentColumnName.equals("Avg Holding Delay")
                                || currentColumnName.equals("Max Holding Delay")
                                || currentColumnName.equals("Avg Queue Delay")
                ) {
                    c.setBackground(new Color(0xFF8C00));
                    c.setForeground(Color.WHITE);
                } else if (currentColumnName.equals("Arrived") || currentColumnName.equals("Departed")) {
                    c.setBackground(new Color(0x12D14A));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1040, 80));

        return scrollPane;
    }

    /**
     * Creates the main content area for the results page.
     *
     * <p>The page contains:
     * <ul>
     *   <li>the title panel,</li>
     *   <li>the summary results table,</li>
     *   <li>the CSV download button panel.</li>
     * </ul>
     *
     * @return results page content panel
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.white);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.white);

        JPanel titlePanel = createTitlePanel(mainTitle);
        JScrollPane scrollPane = createScrollPanel(columnNames, data);
        JPanel buttonPanel = createDownloadButtonPanel();

        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    /**
     * Refreshes the summary results table from the current simulation metrics.
     *
     * <p>Average delays are calculated from total delays divided by the number of
     * successfully processed arrivals/departures.
     */
    private void refreshResults() {
        if (simController == null) return;

        SimState state = simController.getStateSnapshot();
        if (state == null) return;

        Metrics m = state.getMetrics();

        int arrived = m.arrivalsProcessed;
        int departed = m.departuresProcessed;
        int diverted = m.arrivalsDiverted;
        int cancelled = m.departuresCancelled;

        double avgQueueDelay = arrived > 0 ? m.totalArrivalDelaySeconds / arrived : 0.0;
        double avgHoldingDelay = departed > 0 ? m.totalDepartureDelaySeconds / departed : 0.0;

        model.setRowCount(0);
        model.addRow(new Object[]{
            arrived,
            departed,
            round1(m.maxDepartureDelaySeconds),
            round1(m.maxArrivalDelaySeconds),
            round1(avgHoldingDelay),
            round1(avgQueueDelay),
            diverted,
            cancelled
        });
    }

    /**
     * Rounds a numeric value to 1 decimal place for display.
     *
     * @param value raw value
     * @return rounded value to 1 decimal place
     */
    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Exports the currently displayed summary table to a CSV file.
     *
     * @param file destination file
     * @throws IOException if writing fails
     */
    private void exportTableModelToCSV(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.write(model.getColumnName(col));
                if (col < model.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(value == null ? "" : value.toString());
                    if (col < model.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
            }
        }
    }

    /**
     * Creates the panel containing the CSV export button.
     *
     * <p>The user can choose a save location and export the current summary table as a CSV file.
     *
     * @return panel containing the download button
     */
    public JPanel createDownloadButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);

        StyledButton downButton = new StyledButton(
                "Download Simulation Details",
                new Color(0x065F46),
                new Color(0x333333),
                new Color(0x000000),
                Color.WHITE
        );
        downButton.setPreferredSize(new Dimension(300, 50));
        downButton.setFont(new Font("Roboto", Font.BOLD, 15));

        downButton.addActionListener(e -> {
            if (model == null || model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No data to export.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Simulation_Details.csv"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

            int result = fileChooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                String path = file.getAbsolutePath();
                if (!path.endsWith(".csv")) {
                    path += ".csv";
                    file = new File(path);
                }

                try {
                    exportTableModelToCSV(file);
                    JOptionPane.showMessageDialog(null, "File has been created successfully:\n" + file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error in creating CSV file:\n" + ex.getMessage());
                }
            }
        });

        buttonPanel.add(downButton);
        return buttonPanel;
    }
}