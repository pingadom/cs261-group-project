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

public class SimulationResultsPage extends BasePanel{
    private String[] detailsCol;
    private String[][] detailsData;

    public SimulationResultsPage(App app, String mainTitle, String[] columnNames, String[][] data, String[] detailsCol, String[][]detailsData) {
        super(app, mainTitle, columnNames, data);
        this.detailsCol = detailsCol;
        this.detailsData=detailsData;
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

                // Reset default
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
        //removing access to change the table from the user
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
