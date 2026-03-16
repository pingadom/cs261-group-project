package sim.view.pages;

import sim.core.events.ArrivalEvent;
import sim.core.events.DepartureEvent;
import sim.core.viewmodel.SimController;
import sim.core.viewmodel.SimState;
import sim.model.stores.Aircraft;
import sim.view.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

/**
 * Live page displaying flights that have finished processing in the simulation.
 *
 * <p>This includes aircraft that have:
 * <ul>
 *   <li>arrived successfully,</li>
 *   <li>departed successfully,</li>
 *   <li>been diverted, or</li>
 *   <li>been cancelled.</li>
 * </ul>
 *
 * <p>The table is populated from the latest simulation snapshot when the page is shown.
 * Rows are colour-coded using a hidden status flag column:
 * <ul>
 *   <li>green for normally processed flights,</li>
 *   <li>red/orange for cancelled or diverted flights.</li>
 * </ul>
 */
public class PostProcessingPage extends BasePanel {

    /** Controller used to access the latest live simulation snapshot. */
    private final SimController simController;

    /** Table used to display processed flight data. */
    private JTable table;

    /** Non-editable table model backing the processed flights table. */
    private DefaultTableModel model;

    /**
     * Constructs a new live post-processing page.
     *
     * @param app main application instance used for navigation
     * @param simController controller used to read live simulation state
     * @param mainTitle title displayed at the top of the page
     * @param columnNames column names for the processed flights table
     */
    public PostProcessingPage(App app, SimController simController, String mainTitle, String[] columnNames) {
        super(app, mainTitle, columnNames, new String[0][0]);
        this.simController = simController;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshProcessedFlights();
            }
        });
    }

    /**
     * Uses the default footer customisation from {@link BasePanel}.
     */
    @Override
    protected void customizeFooter() {
        super.customizeFooter();
    }

    /**
     * Creates the page content by combining the standard titled table content
     * from {@link BasePanel} with an additional legend explaining row colours.
     *
     * @return panel containing the processed flights table and legend
     */
    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = super.createContentPanel();

        JPanel postFlightPanel = createPostFlightLegendPanel();
        contentPanel.add(postFlightPanel);

        return contentPanel;
    }

    /**
     * Creates the legend panel shown below the table.
     *
     * <p>The legend explains the row colour coding:
     * green for normally processed flights and red/orange for cancelled or diverted flights.
     *
     * @return legend panel for processed flight row colours
     */
    private JPanel createPostFlightLegendPanel() {
        JPanel postFooter = new JPanel();
        postFooter.setBackground(new Color(0xF5F6F8));
        postFooter.setLayout(new FlowLayout(FlowLayout.LEFT));
        postFooter.setBounds(440, 620, 500, 60);

        JPanel redBox = new JPanel();
        redBox.setBackground(new Color(0xE0470A));
        redBox.setPreferredSize(new Dimension(25, 25));
        redBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel falseFlight = new JLabel("Cancelled / Diverted");
        falseFlight.setFont(new Font("Calibri", Font.BOLD, 18));

        JPanel greenBox = new JPanel();
        greenBox.setBackground(new Color(0x0AE04E));
        greenBox.setPreferredSize(new Dimension(25, 25));
        greenBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel trueFlights = new JLabel("Processed Normally");
        trueFlights.setFont(new Font("Calibri", Font.BOLD, 18));

        postFooter.add(redBox);
        postFooter.add(falseFlight);
        postFooter.add(Box.createRigidArea(new Dimension(20, 0)));
        postFooter.add(greenBox);
        postFooter.add(trueFlights);

        return postFooter;
    }

    /**
     * Creates a non-editable scrollable table for processed flights.
     *
     * <p>The final column is a hidden status flag used only for colouring rows.
     * Tooltips are enabled so the full content of a cell can be read on hover.
     *
     * @param columnName column headers for the table
     * @param data unused placeholder data array required by the base signature
     * @return scroll pane containing the processed flights table
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
        table.setFont(new Font("Arial", Font.PLAIN, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1040, 460));

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
            ) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String statusFlag = (String) table.getModel().getValueAt(row, columnName.length - 1);
                if ("true".equals(statusFlag)) {
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

    /**
     * Refreshes the processed flights table from the latest simulation snapshot.
     *
     * <p>For each aircraft in the post-processing list, this method determines whether it
     * was an arrival or departure, checks whether it completed successfully, was cancelled,
     * or was diverted, and then adds an appropriately formatted row to the table.
     */
    private void refreshProcessedFlights() {
        if (simController == null) return;

        SimState state = simController.getStateSnapshot();
        if (state == null) return;

        Map<String, ArrivalEvent> arrivalMap = state.getArrivalEventByCallsign();
        Map<String, DepartureEvent> departureMap = state.getDepartureEventByCallsign();

        model.setRowCount(0);

        for (Aircraft ac : state.getPostProcessing()) {
            if (ac == null) continue;

            String callsign = safe(ac.getCallsign());
            ArrivalEvent arr = arrivalMap.get(callsign);
            DepartureEvent dep = departureMap.get(callsign);

            boolean success = true;

            if (arr != null) {
                if (arr.diverted) {
                    success = false;
                } else if (arr.completed) {
                    success = true;
                }
            } else if (dep != null) {
                if (dep.cancelled) {
                    success = false;
                } else if (dep.completed) {
                    success = true;
                }
            }

            model.addRow(buildPostProcessingRow(ac, arr, dep, success));
        }
    }

    /**
     * Builds one processed-flight table row from an aircraft and its matching event.
     *
     * <p>Arrival rows are displayed with:
     * <ul>
     *   <li>origin as {@code N/A},</li>
     *   <li>destination taken from the aircraft origin field,</li>
     *   <li>arrival time filled,</li>
     *   <li>departure time blank.</li>
     * </ul>
     *
     * <p>Departure rows are displayed with:
     * <ul>
     *   <li>origin as {@code HOME},</li>
     *   <li>destination as {@code N/A},</li>
     *   <li>departure time filled,</li>
     *   <li>arrival time blank.</li>
     * </ul>
     *
     * @param ac aircraft being displayed
     * @param arr matching arrival event, or {@code null} if not an arrival
     * @param dep matching departure event, or {@code null} if not a departure
     * @param success whether the aircraft completed normally
     * @return row data for insertion into the processed flights table
     */
    private Object[] buildPostProcessingRow(
        Aircraft ac,
        ArrivalEvent arr,
        DepartureEvent dep,
        boolean success
    ) {
        String origin;
        String destination;
        String departureTime;
        String arrivalTime;

        if (arr != null) {
            origin = "N/A";
            destination = safe(ac.getOrigin());
            departureTime = "";
            arrivalTime = ac.getTime() != null ? ac.getTime().toString() : "";
        } else if (dep != null) {
            origin = "HOME";
            destination = "N/A";
            departureTime = ac.getTime() != null ? ac.getTime().toString() : "";
            arrivalTime = "";
        } else {
            origin = "";
            destination = "";
            departureTime = "";
            arrivalTime = "";
        }

        return new Object[] {
                safe(ac.getCallsign()),
                safe(ac.getOperator()),
                origin,
                destination,
                departureTime,
                arrivalTime,
                Integer.toString(ac.getFuel()),
                Boolean.toString(success)
        };
    }

    /**
     * Safely converts a possibly null string into a non-null display value.
     *
     * @param value input string that may be null
     * @return the original string, or an empty string if null
     */
    private String safe(String value) {
        return value == null ? "" : value;
    }
}