package sim.view.pages;

import sim.view.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PostProcessingPage extends BasePanel {

    public PostProcessingPage(App app, String mainTitle, String[] columnNames, String[][] data) {
        super(app, mainTitle, columnNames, data);
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

    // Creating the legend panel
    private JPanel createPostFlightLegendPanel() {
        JPanel postFooter = new JPanel();
        postFooter.setBackground(new Color(0xF5F6F8));
        postFooter.setLayout(new FlowLayout(FlowLayout.LEFT));
        postFooter.setBounds(440, 620, 400, 60);

        // Creating the Red box
        JPanel redBox = new JPanel();
        redBox.setBackground(new Color(0xE0470A));
        redBox.setPreferredSize(new Dimension(25, 25));
        redBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Creating the text label that will be next to the red box
        JLabel falseFlight = new JLabel();
        falseFlight.setText("Cancelled");
        falseFlight.setFont(new Font("Calibri", Font.BOLD, 18));

        // Creating the Green box
        JPanel greenBox = new JPanel();
        greenBox.setBackground(new Color(0x0AE04E));
        greenBox.setPreferredSize(new Dimension(25, 25));
        greenBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Creating the text label that will be next to the green box
        JLabel trueFlights = new JLabel();
        trueFlights.setText("Arrived");
        trueFlights.setFont((new Font("Calibri", Font.BOLD, 18)));

        // adding elements to the post footer panel
        postFooter.add(redBox);
        postFooter.add(falseFlight);
        postFooter.add(greenBox);
        postFooter.add(trueFlights);

        //return post footer
        return postFooter;
    }

    @Override
    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        //Creating the model using column and data
        DefaultTableModel model = new DefaultTableModel(data, columnName) {
            public boolean isCellEditable(int row, int column) {
                return false; //to prevent the user from editing a cell
            }
        };
        //Create the table using the model
        JTable table = new JTable(model);
        table.setRowHeight(20);
        table.setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1040, 460));
        //Accessing the cell component
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //getting the last column and casting it to string
                String status = (String) table.getModel().getValueAt(row, columnName.length - 1);
                if (status.equals("true")) {
                    c.setBackground(new Color(0x0AE04E));
                } else {
                    c.setBackground(new Color(0xE0470A));
                }
                return c;


            }
        });
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
        //removing access to change the table from the user
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        //removing the last column that we used to color the rows
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(columnName.length - 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

}
