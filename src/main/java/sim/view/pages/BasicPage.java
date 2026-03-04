package sim.view.pages;

import sim.view.components.FooterPanel;
import sim.view.components.HeaderPanel;
import sim.view.components.SidePanel;

import javax.swing.*;
import java.awt.*;

public abstract class BasicPage extends JPanel {
    private static final int SPACER_SIZE_10 = 10;

    protected HeaderPanel headerPanel;
    protected SidePanel leftPanel;
    protected SidePanel rightPanel;
    protected FooterPanel footerPanel;

    public BasicPage() {
        setLayout(new BorderLayout(0, SPACER_SIZE_10));
        setBackground(Color.white);

        createCommonPanels();
    }

    public void createCommonPanels() {
        headerPanel = new HeaderPanel();
        leftPanel = new SidePanel();
        rightPanel = new SidePanel();
        footerPanel = new FooterPanel();
    }

    protected void buildPage(JPanel contentPanel) {
        removeAll();

        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // Abstract method
    protected abstract JPanel createContentPanel();

    protected void customizeFooter() {}
}
