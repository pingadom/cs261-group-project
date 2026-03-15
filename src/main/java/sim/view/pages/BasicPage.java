package sim.view.pages;

import sim.view.components.FooterPanel;
import sim.view.components.HeaderPanel;
import sim.view.components.SidePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract base class for all pages in the application.
 * Provides a consistent layout structure with header, footer, and side panels, and
 * defines the template methods that subclasses must implement.
 *
 * @see HeaderPanel
 * @see FooterPanel
 * @see SidePanel
 */
public abstract class BasicPage extends JPanel {
    private static final int SPACER_SIZE_10 = 10;

    protected HeaderPanel headerPanel;
    protected SidePanel leftPanel;
    protected SidePanel rightPanel;
    protected FooterPanel footerPanel;

    /**
     * Constructs a new BasicPage with the standard layout.
     * Initialises the BorderLayout with vertical spacing and creates the common
     * panels used across all pages.
     */
    public BasicPage() {
        // Set up BorderLayout with horizontal gap=0, vertical gap=10
        setLayout(new BorderLayout(0, SPACER_SIZE_10));
        setBackground(Color.white);

        createCommonPanels();
    }

    /**
     * Creates and initialises the common panels shared by all pages.
     * This method is called during construction to instantiate the header, side panels, and footer
     */
    private void createCommonPanels() {
        headerPanel = new HeaderPanel();
        leftPanel = new SidePanel();
        rightPanel = new SidePanel();
        footerPanel = new FooterPanel();
    }

    /**
     * Assembles the complete page by adding all panels to their respective positions.
     * This method should be called by subclasses after creating their specific content panel.
     *
     * @param contentPanel the main content panel provided by the subclass
     */
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

    /**
     * Creates the main content panel for this page.
     * Subclasses must implement this method to provide their specific content.
     *
     * @return a JPanel containing the content
     */
    protected abstract JPanel createContentPanel();

    /**
     * Hook method for subclasses to customise the footer panel.
     * Subclass should override this method to add page-specific buttons to the footer
     */
    protected void customizeFooter() {}
}
