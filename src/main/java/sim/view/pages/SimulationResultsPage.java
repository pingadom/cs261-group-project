package sim.view.pages;

import sim.view.App;
import sim.view.components.StyledButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * The page displaying the final summary results of a completed simulation.
 * <p>
 *     This page extends {@link BasePanel} to provide:
 *     <ul>
 *         <li>A colour-coded summary table of key simulation metrics</li>
 *         <li>A "Download Simulation Details" button to export data as a CSV file</li>
 *         <li>A "Back" button to return to the simulation page</li>
 *         <li>A "New Simulation" button to reset and navigate to the input page</li>
 *     </ul>
 * </p>
 *
 * @see BasePanel
 * @see SimulationPage
 */
public class SimulationResultsPage extends BasePanel{
    private String[] detailsCol;
    private String[][] detailsData;

    /**
     * Constructs a new SimulationResultsPage with the specified , title, and table data.
     *
     * @param app the main application instance for navigation
     * @param mainTitle the title text to display at the top of the page
     * @param detailsCol the column header names for the summary table
     * @param detailsData the row data to fill the summary table, as a two-dimensional array
     */
    public SimulationResultsPage(App app, String mainTitle, String[] detailsCol, String[][] detailsData) {
        super(app, mainTitle, detailsCol, detailsData);
        this.detailsCol = detailsCol;
        this.detailsData = detailsData;
    }

    /**
     * Customises the footer with "Back" and "New Simulation" buttons.
     * <p>
     *     The footer is configured with:
     *     <ul>
     *         <li>A "Back" button on the left returns to the simulation page</li>
     *         <li>A "+ New Simulation" button on the right that resets the simulation state and navigates to the input page</li>
     *     </ul>
     * </p>
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
     * Creates a scrollable, colour-coded {@link JTable} wrapped in a {@link JScrollPane}.
     * <p>
     *     This method overrides the base implementation to:
     *     <ul>
     *         <li>Apply column-level background colours based on the metric category</li>
     *         <li>Display cell values as tooltips on hover for content that may be truncated</li>
     *         <li>Prevent column reordering and resizing</li>
     *         <li>Disable all cell, row, and column selection</li>
     *     </ul>
     * </p>
     *
     * The colour scheme mirrors the statistics panel on the simulation page:
     * red for cancellations and diversions, orange for delay metrics, and green for
     * arrivals and departures.
     *
     * @param columnName the column header names for the table
     * @param data the row data to fill the table
     * @return a configured JScrollPane containing the colour-coded summary table
     */
    @Override
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columnName){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial",Font.PLAIN,30));
        table.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        //to get the value in each cell using the mouse so we can read values with long length
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

                String columnName = table.getColumnName(column);


                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE);


                if (columnName.equals("Cancelled") || columnName.equals("Diverted")) {
                    c.setBackground(new Color(0xE60000));
                    c.setForeground(Color.WHITE);
                }


                else if (columnName.equals("Max Queue Delay") || columnName.equals("Avg Holding Delay")||
                        columnName.equals("Max Holding Delay")||columnName.equals("Avg Queue Delay")) {
                    c.setBackground(new Color(0xFF8C00));
                    c.setForeground(Color.WHITE);
                }


                else if (columnName.equals("Arrived") || columnName.equals("Departed")) {
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
     * Creates the main content panel for the results page.
     * <p>
     *     The content panel uses a BorderLayout to arrange:
     *     <ul>
     *         <li><b>North:</b> Title panel and colour-coded summary table</li>
     *         <li><b>South:</b> Download button panel</li>
     *     </ul>
     * </p>
     *
     * @return a JPanel containing the results layout
     */
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.white);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.white);

        JPanel titlePanel = createTitlePanel(mainTitle);
        JScrollPane scrollPane = createScrollPanel(columnNames, data);
        JPanel ButtonPanel = createDownloadButtonPanel();

        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(ButtonPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    /**
     * Exports the simulation details to a CSV file.
     * Writes the column headers on the first row, followed by one data row per simulation entry.
     *
     * @param detailsCol the column header names to write as the CSV header row
     * @param detailsData the row data to write to the CSV file
     * @param file the destination file to write to
     */
    private void exportTableModelToCSV(String[] detailsCol, String[][] detailsData, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (int col = 0; col < detailsCol.length; col++) {
                writer.write(detailsCol[col]);
                if (col < detailsCol.length - 1) writer.write(",");
            }
            writer.write("\n");

            for (int row = 0; row < detailsData.length; row++) {
                for (int col = 0; col < detailsCol.length; col++) {
                    String value = (col < detailsData[row].length) ? detailsData[row][col] : "";
                    writer.write(value == null ? "" : value);
                    if (col < detailsCol.length - 1) writer.write(",");
                }
                writer.write("\n");
            }
        }
    }

    /**
     * Creates the panel containing the "Download Simulation Details" button.
     * <p>
     *     When clicked, the button:
     *     <ul>
     *         <li>Validates that export data is available</li>
     *         <li>Opens a file chooser pre-configured for CSV output</li>
     *         <li>Appends a {@code .csv} extension if not already present</li>
     *         <li>Writes the simulation details to the chosen file via {@link #exportTableModelToCSV}</li>
     *         <li>Displays a confirmation or error dialog based on the outcome</li>
     *     </ul>
     * </p>
     *
     * @return a JPanel containing the download button
     */
    public JPanel createDownloadButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        StyledButton DownButton = new StyledButton("Download Simulation Details", new Color(0x065F46), new Color(0x333333), new Color(0x000000), Color.WHITE);
        DownButton.setPreferredSize(new Dimension(300, 50));
        DownButton.setFont(new Font("Roboto", Font.BOLD, 15));
        DownButton.addActionListener(e -> {
            if (this.detailsCol == null || this.detailsData == null) {
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
                    exportTableModelToCSV(this.detailsCol, this.detailsData, file);
                    JOptionPane.showMessageDialog(null, "File has been created successfully:\n" + file.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error in creating CSV file:\n" + ex.getMessage());
                }
            }
        });
        buttonPanel.add(DownButton);
        return buttonPanel;
    }
}