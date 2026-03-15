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
 * Reusable live-updating table page.
 *
 * The page owns:
 * - a JTable + DefaultTableModel
 * - a refresh timer
 * - a row supplier that converts SimState into table rows
 *
 * This lets different pages reuse the same UI and only change the row-building logic.
 */
public class LiveTablePage extends BasicPage {

    @FunctionalInterface
    public interface RowProvider {
        List<String[]> getRows(SimState state);
    }

    private static final int REFRESH_INTERVAL_MS = 500;

    protected final App app;
    protected final SimController simController;
    protected final String mainTitle;
    protected final String[] columnNames;
    protected final RowProvider rowProvider;

    protected JTable table;
    protected DefaultTableModel tableModel;
    protected Timer updateTimer;

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

    private void initialiseTimer() {
        updateTimer = new Timer(REFRESH_INTERVAL_MS, e -> refreshTable());
    }

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

    public void startTimer() {
        if (updateTimer != null && !updateTimer.isRunning()) {
            updateTimer.start();
        }
    }

    public void stopTimer() {
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

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