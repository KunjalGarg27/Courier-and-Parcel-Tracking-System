package com.courier.view.customer;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.courier.view.common.BaseFrame;
import com.courier.view.common.SidebarPanel;

public class CustomerFrame extends BaseFrame {

    private SidebarPanel sidebar;

    public CustomerFrame(String customerName) {

        super("Customer Dashboard", customerName, "Customer");

        createSidebar();

        // Show Dashboard when CustomerFrame opens
        showCustomerPanel(new CustomerDashboardPanel());
    }

    private void createSidebar() {

        String[] menuItems = {
                "Dashboard",
                "Book Shipment",
                "My Shipments",
                "Tracking",
                "Delivery History",
                "Profile"
        };

        sidebar = new SidebarPanel(menuItems);

        add(sidebar, BorderLayout.WEST);

        // Dashboard
        sidebar.setMenuAction(
                "Dashboard",
                () -> showCustomerPanel(
                        new CustomerDashboardPanel()
                )
        );

        // Book Shipment
        sidebar.setMenuAction(
                "Book Shipment",
                () -> showCustomerPanel(
                        new BookShipmentPanel()
                )
        );

        // My Shipments
        sidebar.setMenuAction(
                "My Shipments",
                () -> showCustomerPanel(
                        new MyShipmentsPanel()
                )
        );

        // Tracking
        sidebar.setMenuAction(
                "Tracking",
                () -> showCustomerPanel(
                        new TrackingPanel()
                )
        );

        // Delivery History
        sidebar.setMenuAction(
                "Delivery History",
                () -> showCustomerPanel(
                        new DeliveryHistoryPanel()
                )
        );

        // Profile
        sidebar.setMenuAction(
                "Profile",
                () -> showCustomerPanel(
                        new CustomerProfilePanel()
                )
        );

        // Settings
        sidebar.setMenuAction(
                "Settings",
                () -> showCustomerPanel(
                        new CustomerSettingsPanel()
                )
        );
    }

    public void showCustomerPanel(JPanel panel) {
        setContent(panel);
        revalidate();
        repaint();
    }
}
