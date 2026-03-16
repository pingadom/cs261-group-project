package sim.view.pages;

import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;
import sim.view.App;
import sim.view.components.StyledButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Reusable live-updating table page used for pages such as:
 * <ul>
 *   <li>Flights Soon Arriving,</li>
 *   <li>Flights Soon Departing,</li>
 *   <li>Holding Pattern,</li>
 *   <li>Take-off Queue.</li>
 * </ul>
 *
 * <p>This page owns its own:
 * <ul>
 *   <li>table model,</li>
 *   <li>refresh timer,</li>
 *   <li>row provider callback that converts a {@link SimState} snapshot into table rows.</li>
 * </ul>
 *
 * <p>When the page becomes visible it starts refreshing automatically, and when hidden it stops.
 */
public class LiveTablePage extends BasicPage {

    /**
     * Functional interface used to convert a simulation snapshot into table rows.
     */
    @FunctionalInterface
    public interface RowProvider {
        /**
         * Produces the rows to be displayed for a given simulation snapshot.
         *
         * @param state latest simulation snapshot
         * @return list of table rows, each row represented as a string array
         */
        List<String[]> getRows(SimState state);
    }

    /** Timer refresh interval in milliseconds. */
    private static final int REFRESH_INTERVAL_MS = 500;

    /** Main application instance used for navigation. */
    protected final App app;

    /** Controller used to read live simulation state. */
    protected final SimController simController;

    /** Title displayed at the top of the page. */
    protected final String mainTitle;

    /** Column names for the live table. */
    protected final String[] columnNames;

    /** Callback used to produce table rows from the current simulation state. */
    protected final RowProvider rowProvider;

    /** Table used to display live data. */
    protected JTable table;

    /** Non-editable model backing the live data table. */
    protected DefaultTableModel tableModel;

    /** Timer that refreshes the table while the page is visible. */
    protected Timer updateTimer;

    /**
     * Constructs a reusable live-updating table page.
     *
     * @param app main application instance used for navigation
     * @param simController controller used to access the current simulation snapshot
     * @param mainTitle title displayed at the top of the page
     * @param columnNames column headers for the table
     * @param rowProvider callback used to generate rows from the latest simulation state
     */
    public LiveTablePage(
            App app,
            SimController simController,
            String mainTitle,
            String[] columnNames,
            RowProvider rowProvider
    ) {
        this.app = app;
        this.simController = simController;
        this.mainTitle = mainTitle;
        this.columnNames = columnNames;
        this.rowProvider = rowProvider;

        buildPage(createContentPanel());
        customizeFooter();
        initialiseTimer();
        initialiseVisibilityHandling();
    }

    /**
     * Creates the Swing timer used for periodic live refreshes.
     */
    private void initialiseTimer() {
        updateTimer = new Timer(REFRESH_INTERVAL_MS, e -> refreshTable());
    }

    /**
     * Adds component visibility listeners so the refresh timer starts when the page is shown
     * and stops when the page is hidden.
     */
    private void initialiseVisibilityHandling() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshTable();
                startTimer();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                stopTimer();
            }
        });
    }

    /**
     * Starts the live refresh timer if it is not already running.
     */
    public void startTimer() {
        if (updateTimer != null && !updateTimer.isRunning()) {
            updateTimer.start();
        }
    }

    /**
     * Stops the live refresh timer if it is currently running.
     */
    public void stopTimer() {
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

    /**
     * Refreshes the table contents using the current simulation snapshot.
     *
     * <p>Existing rows are cleared and replaced with the latest rows produced by the row provider.
     */
    public void refreshTable() {
        if (simController == null) return;

        SimState state = simController.getStateSnapshot();
        if (state == null) return;

        List<String[]> rows = rowProvider.getRows(state);

        tableModel.setRowCount(0);
        for (String[] row : rows) {
            tableModel.addRow(row);
        }
    }

    /**
     * Creates the main content panel for the page, consisting of a title and a scrollable table.
     *
     * @return content panel for the live table page
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = createTitlePanel(mainTitle);
        JPanel tablePanel = createTablePanel();

        contentPanel.add(titlePanel);
        contentPanel.add(tablePanel);

        return contentPanel;
    }

    /**
     * Creates the footer containing a Back button that returns to the main simulation page.
     */
    @Override
    protected void customizeFooter() {
        StyledButton buttonBack = new StyledButton(
                "Back",
                Color.black,
                new Color(0x333333),
                new Color(0x000000),
                Color.black
        );
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBack.addActionListener(e -> app.showSimulationPage());

        footerPanel.add(buttonBack);
    }

    /**
     * Creates the title panel shown at the top of the page.
     *
     * @param titleText text to display as the page title
     * @return title panel
     */
    protected JPanel createTitlePanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(1280, 60));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Calibri", Font.BOLD, 30));
        title.setForeground(new Color(0x141E54));
        title.setHorizontalAlignment(JLabel.CENTER);

        panel.add(title, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the scrollable table panel used to display live data rows.
     *
     * @return table container panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1040, 460));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}