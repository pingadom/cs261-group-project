package sim.view.pages;

import sim.view.App;
import sim.view.components.StyledButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Base page for displaying simulation data in a titled, scrollable table.
 * <p>
 *     This page provides:
 *     <ul>
 *         <li>A title panel at the top of the page</li>
 *         <li>A non-editable, scrollable data table</li>
 *         <li>A "Back" button in the footer that returns to the simulation page</li>
 *     </ul>
 * </p>
 *
 * @see BasicPage
 */
public class BasePanel extends BasicPage {
    protected final App app;
    protected final String mainTitle;
    protected final String[] columnNames;
    protected final String[][] data;
    /**
     * Constructs a new BasePanel page with the specified title, and table data.
     *
     * @param app the main application instance for navigation
     * @param mainTitle the title text to display at the top of the page
     * @param columnNames the column header names for the data table
     * @param data the row data to fill the table, as a two-dimensional array
     */
    public BasePanel(App app, String mainTitle, String[] columnNames, String[][] data) {
        this.app = app;

        this.mainTitle = mainTitle;
        this.columnNames = columnNames;
        this.data = data;

        buildPage(createContentPanel());
        customizeFooter();
    }

    /**
     * Creates the main content panel containing the title and data table.
     * The panel uses a vertical BoxLayout to stack:
     * <ol>
     *     <li>Title panel</li>
     *     <li>Table panel</li>
     * </ol>
     *
     * @return the complete content panel with title  and a scrollable table underneth
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = createTitlePanel(mainTitle);
        JPanel tablePanel = createTablePanel(columnNames, data);

        contentPanel.add(titlePanel);
        contentPanel.add(tablePanel);

        return contentPanel;
    }
    /**
     * Adds a "Back" button to the footer that navigates to the simulation page.
     *
     */
    @Override
    protected void customizeFooter() {
        StyledButton buttonBack = new StyledButton("Back", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBack.addActionListener(e -> app.showSimulationPage());

        footerPanel.add(buttonBack);
    }

    /**
     * Creates the title panel displaying the page heading.
     *
     * @param mainTitle the text to display as the page title
     * @return a JPanel containing the title label
     */
    protected JPanel createTitlePanel(String mainTitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(1280, 60));

        JLabel title = new JLabel(mainTitle);
        title.setFont(new Font("Calibri", Font.BOLD, 30));
        title.setForeground(new Color(0x141E54));
        title.setHorizontalAlignment(JLabel.CENTER);

        panel.add(title, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the table panel containing a scrollable data table.
     *
     * @param columnName the column header names for the table
     * @param data the row data to populate the table
     * @return a JPanel containing the scroll pane with the data table
     */
    private JPanel createTablePanel(String[] columnName, String[][] data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);

        JScrollPane scrollPane = createScrollPanel(columnName, data);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a scrollable, non-editable {@link JTable} wrapped in a {@link JScrollPane}.
     * <p>
     *     The table is configured to:
     *     <ul>
     *         <li>Display cell values as tooltips on hover for content that may be truncated</li>
     *         <li>Prevent column reordering and resizing</li>
     *         <li>Disable all cell, row, and column selection</li>
     *     </ul>
     * </p>
     *
     * @param columnName the column header names for the table
     * @param data the row data to populate the table
     * @return a configured JScrollPane containing the data table
     */
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columnName){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial",Font.PLAIN,15));
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1040, 460));

        return scrollPane;
    }
}
