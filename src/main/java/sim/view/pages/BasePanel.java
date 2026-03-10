package sim.view.pages;

import sim.view.App;
import sim.view.components.StyledButton;
import sim.view.controllers.PageDataController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class BasePanel extends BasicPage {
    // Instance variables
    protected final App app;

    protected final String mainTitle;
    protected final String[] columnNames;
    protected final String[][] data;

    public BasePanel(App app, String mainTitle, String[] columnNames, String[][] data) {
        this.app = app;

        this.mainTitle = mainTitle;
        this.columnNames = columnNames;
        this.data = data;

        buildPage(createContentPanel());
        customizeFooter();
    }

    @Override
    protected JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.white);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel titlePanel = createTitlePanel(mainTitle);
        JPanel tablePanel = createTablePanel(columnNames, data);

        contentPanel.add(titlePanel);
        // contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(tablePanel);

        return contentPanel;
    }

    @Override
    protected void customizeFooter() {
        StyledButton buttonBack = new StyledButton("Back", Color.black, new Color(0x333333), new Color(0x000000), Color.black);
        buttonBack.setPreferredSize(new Dimension(100, 30));
        buttonBack.setMaximumSize(new Dimension(100, 30));
        buttonBack.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBack.addActionListener(e -> app.showSimulationPage());

        footerPanel.add(buttonBack);
    }

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

    private JPanel createTablePanel(String[] columnName, String[][] data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);

        JScrollPane scrollPane = createScrollPanel(columnName, data);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    protected JScrollPane createScrollPanel(String[] columnName, String[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columnName){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Arial",Font.PLAIN,15));
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1040, 460));

        return scrollPane;
    }
}
