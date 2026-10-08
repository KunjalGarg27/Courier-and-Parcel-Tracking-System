package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.BaseFrame;
import com.courier.view.common.SidebarPanel;

public class AdminFrame extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private SidebarPanel sidebarPanel;

    public AdminFrame(String user) {

        super(
            "Admin Dashboard",
            user,
            "Administrator"
        );

        setupAdminNavigation();

        // Open Dashboard initially
        setContent(new DashboardPanel());
    }

    private void setupAdminNavigation() {

        sidebarPanel = new SidebarPanel();

        add(sidebarPanel, BorderLayout.WEST);

        // =========================
        // DASHBOARD
        // =========================

        sidebarPanel.setMenuAction("Dashboard", () -> {
            setContent(new DashboardPanel());
        });

        // =========================
        // TRACKING
        // =========================

        sidebarPanel.setMenuAction("Tracking", () -> {
            setContent(new TrackingPanel());
        });

        // =========================
        // SHIPMENTS
        // =========================

        sidebarPanel.setMenuAction("Shipments", () -> {
            setContent(new ShipmentManagementPanel());
        });

        // =========================
        // CUSTOMERS
        // =========================

        sidebarPanel.setMenuAction("Customers", () -> {
            setContent(new CustomerManagementPanel());
        });

        // =========================
        // DELIVERY AGENTS
        // =========================

        sidebarPanel.setMenuAction("Delivery Agents", () -> {
            setContent(new DeliveryAgentManagementPanel());
        });
        
        // =========================
        // REPORTS
        // =========================

        sidebarPanel.setMenuAction("Reports", () -> {
            setContent(new ReportsPanel());
        });

        // =========================
        // SETTINGS
        // =========================

        sidebarPanel.setMenuAction("Settings", () -> {
            setContent(new SettingsPanel());
        });
    }

    private void setPlaceholder(String title) {

        JPanel panel = new JPanel(new GridLayout(1, 1));

        panel.setBackground(AppTheme.BACKGROUND);

        JLabel label = new JLabel(
                title,
                SwingConstants.CENTER
        );

        label.setFont(
                FontManager.semiBold(26)
        );

        label.setForeground(
                AppTheme.TEXT_PRIMARY
        );

        panel.add(label);

        setContent(panel);
    }
}