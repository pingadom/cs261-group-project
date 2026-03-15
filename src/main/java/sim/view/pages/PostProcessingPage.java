package sim.view.pages;

import sim.view.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * The page displaying post-simulation processed flight data in a colour-coded table.
 * <p>
 *     This page extends {@link BasePanel} to provide:
 *     <ul>
 *         <li>A scrollable table of processed flights with row-level colour coding</li>
 *         <li>A legend panel indicating the meaning of each row colour</li>
 *         <li>Green rows for arrived flights and red rows for cancelled flights</li>
 *     </ul>
 * </p>
 *
 * @see BasePanel
 */
public class PostProcessingPage extends BasePanel {
    /**
     * Constructs a new PostProcessingPage with the specified title, and table data.
     *
     * @param app the main application instance for navigation
     * @param mainTitle the title text to display at the top of the page
     * @param columnNames the column header names for the data table
     * @param data the row data to fill the table, as a two-dimensional array
     */
    public PostProcessingPage(App app, String mainTitle, String[] columnNames, String[][] data) {
        super(app, mainTitle, columnNames, data);
    }

    @Override
    protected void customizeFooter() {
        super.customizeFooter();
    }

    /**
     * Creates the main content panel by extending the original content panel with a flight status legend.
     * Adds a colour legend panel below the table to explain the row colour coding.
     *
     * @return a JPanel containing the original content and the post-flight legend
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = super.createContentPanel();

        JPanel postFlightPanel = createPostFlightLegendPanel();
        contentPanel.add(postFlightPanel);

        return contentPanel;
    }

    /**
     * Creates the legend panel that describes the row colour coding used in the flights table.
     * The legend displays a red box labelled "Cancelled" and a green box labelled "Arrived".
     *
     * @return a JPanel containing the colour legend
     */
    private JPanel createPostFlightLegendPanel() {
        JPanel postFooter = new JPanel();
        postFooter.setBackground(new Color(0xF5F6F8));
        postFooter.setLayout(new FlowLayout(FlowLayout.LEFT));
        postFooter.setBounds(440, 620, 400, 60);


        JPanel redBox = new JPanel();
        redBox.setBackground(new Color(0xE0470A));
        redBox.setPreferredSize(new Dimension(25, 25));
        redBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        JLabel falseFlight = new JLabel();
        falseFlight.setText("Cancelled");
        falseFlight.setFont(new Font("Calibri", Font.BOLD, 18));


        JPanel greenBox = new JPanel();
        greenBox.setBackground(new Color(0x0AE04E));
        greenBox.setPreferredSize(new Dimension(25, 25));
        greenBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        JLabel trueFlights = new JLabel();
        trueFlights.setText("Arrived");
        trueFlights.setFont((new Font("Calibri", Font.BOLD, 18)));


        postFooter.add(redBox);
        postFooter.add(falseFlight);
        postFooter.add(greenBox);
        postFooter.add(trueFlights);


        return postFooter;
    }

    /**
     * Creates a scrollable, colour-coded {@link JTable} wrapped in a {@link JScrollPane}.
     * <p>
     *     This method overrides the base implementation to:
     *     <ul>
     *         <li>Apply row-level background colours based on flight status</li>
     *         <li>Display cell values as tooltips on hover for content that may be truncated</li>
     *         <li>Prevent column reordering and resizing</li>
     *         <li>Disable all cell, row, and column selection</li>
     *         <li>Remove the hidden status column used for colour logic after rendering</li>
     *     </ul>
     * </p>
     *
     * @param columnName the column header names for the table
     * @param data the row data to fill the table
     * @return a configured JScrollPane containing the colour-coded data table
     */
    @Override
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columnName) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1040, 460));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, columnName.length - 1);
                if (status.equals("true")) {
                    c.setBackground(new Color(0x0AE04E));
                } else {
                    c.setBackground(new Color(0xE0470A));
                }
                return c;


            }
        });
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

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(columnName.length - 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

}
