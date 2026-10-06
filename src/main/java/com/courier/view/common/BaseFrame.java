package com.courier.view.common;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.admin.DashboardPanel;
import com.courier.view.admin.TrackingPanel;
import com.courier.view.admin.ShipmentManagementPanel;

public class BaseFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    protected JPanel contentPanel;
    private HeaderPanel headerPanel;
    private SidebarPanel sidebarPanel;

    public BaseFrame(String title) {
        this(title, "Kunjal", "Administrator");
    }

    public BaseFrame(String title, String user, String role) {

        super(title);

        initializeFrame(title, user, role);

        // Default screen
        setContent(new DashboardPanel());
    }

    private void initializeFrame(String title, String user, String role) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // =========================
        // HEADER
        // =========================

        headerPanel = new HeaderPanel(title, user, role);
        add(headerPanel, BorderLayout.NORTH);

        // =========================
        // SIDEBAR
        // =========================

        sidebarPanel = new SidebarPanel();

        add(sidebarPanel, BorderLayout.WEST);

        // =========================
        // CONTENT AREA
        // =========================

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(AppTheme.BACKGROUND);

        add(contentPanel, BorderLayout.CENTER);

        // =========================
        // EVENT HANDLING
        // =========================

        setupNavigation();
    }

    private void setupNavigation() {

        // Dashboard
        sidebarPanel.setMenuAction("Dashboard", () -> {
            setContent(new DashboardPanel());
        });

        // Tracking
        sidebarPanel.setMenuAction("Tracking", () -> {
            setContent(new TrackingPanel());
        });

        // These screens will be created next
        sidebarPanel.setMenuAction("Shipments", () -> {
            setContent(new ShipmentManagementPanel());
        });

        sidebarPanel.setMenuAction("Customers", () -> {
            setPlaceholder("Customer Management");
        });

        sidebarPanel.setMenuAction("Delivery Agents", () -> {
            setPlaceholder("Delivery Agent Management");
        });

        sidebarPanel.setMenuAction("Reports", () -> {
            setPlaceholder("Reports");
        });

        sidebarPanel.setMenuAction("Settings", () -> {
            setPlaceholder("Settings");
        });
    }

    // Temporary screen until we create the actual Admin panels
    private void setPlaceholder(String title) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.BACKGROUND);

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(FontManager.semiBold(26));
        label.setForeground(AppTheme.TEXT_PRIMARY);

        panel.add(label, BorderLayout.CENTER);

        setContent(panel);
    }

    public void setContent(JPanel panel) {

        contentPanel.removeAll();

        contentPanel.add(panel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    protected void showPanel(JPanel panel) {
        setContent(panel);
    }
}