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
 * Live page displaying processed flights.
 */
public class PostProcessingPage extends BasePanel {

    private final SimController simController;

    private JTable table;
    private DefaultTableModel model;

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

    @Override
    protected void customizeFooter() {
        super.customizeFooter();
    }

    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = super.createContentPanel();

        JPanel postFlightPanel = createPostFlightLegendPanel();
        contentPanel.add(postFlightPanel);

        return contentPanel;
    }

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
            // Arrival flight
            origin = "N/A";
            destination = safe(ac.getOrigin());
            departureTime = "";
            arrivalTime = ac.getTime() != null ? ac.getTime().toString() : "";
        } else if (dep != null) {
            // Departure flight
            origin = "HOME";
            destination = "N/A";
            departureTime = ac.getTime() != null ? ac.getTime().toString() : "";
            arrivalTime = "";
        } else {
            // Fallback
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

    private String safe(String value) {
        return value == null ? "" : value;
    }
}