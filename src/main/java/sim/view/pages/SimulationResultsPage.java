package sim.view.pages;

import sim.view.App;
import sim.view.components.StyledButton;
import sim.view.controllers.PageDataController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

public class SimulationResultsPage extends BasePanel{

    public SimulationResultsPage(App app, String mainTitle, String[] columnNames, String[][] data) {
        super(app, mainTitle, columnNames, data);
    }

    @Override
    protected void customizeFooter() {
        super.customizeFooter();

        // Adding the New Simulation button
        StyledButton newSimButton = new StyledButton("+ New Simulation", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        newSimButton.setPreferredSize(new Dimension(150, 30));
        newSimButton.setMaximumSize(new Dimension(150, 30));
        newSimButton.setFont(new Font("Arial", Font.BOLD, 14));
        newSimButton.addActionListener(e -> app.showInputPage());

        // Add button to the right side of footer
        footerPanel.add(Box.createHorizontalGlue());
        footerPanel.add(newSimButton);
    }

    @Override
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        //copying the original array then adding the remove column
        String[] columns = Arrays.copyOf(columnName, columnName.length + 1);
        columns[columnName.length] = "Remove";

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return column == getColumnCount() - 1;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(columnName.length).setPreferredWidth(80);
        table.setRowHeight(20);

        // Custom renderer to display a button in the Remove column
        table.getColumnModel().getColumn(columnName.length).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = new JButton("Delete");
                button.setFont(new Font("Arial", Font.PLAIN, 15));
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(200, 50, 50));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return button;
            }
        });

        // Custom editor to handle button clicks in the Remove column
        table.getColumnModel().getColumn(columnName.length).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton button = new JButton("Delete");
            private int currentRow;

            {
                button.setFont(new Font("Arial", Font.PLAIN, 15));
                button.addActionListener(e -> {
                    fireEditingStopped(); //to handle multiple clicks
                    model.removeRow(currentRow);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                currentRow = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "Delete";
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1040, 460));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        table.getTableHeader().setReorderingAllowed(false);     // The dimensions of the result table

        return scrollPane;
    }
}
