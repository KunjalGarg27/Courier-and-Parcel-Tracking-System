package com.courier.view.customer;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.courier.model.User;
import com.courier.view.common.BaseFrame;
import com.courier.view.common.SidebarPanel;

public class CustomerFrame extends BaseFrame {

    private final User user;
    private SidebarPanel sidebar;

    public CustomerFrame(User user) {

        super("Customer Dashboard", user.getFullName(), "Customer");

        this.user = user;

        createSidebar();

        // Show Dashboard when CustomerFrame opens
        showCustomerPanel(new CustomerDashboardPanel(user.getUserId()));
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

        // Dashboard (reloads the shipment counts from the database)
        sidebar.setMenuAction("Dashboard",
                () -> showCustomerPanel(
                        new CustomerDashboardPanel(user.getUserId())));

        // Book Shipment
        sidebar.setMenuAction("Book Shipment",
                () -> showCustomerPanel(
                        new BookShipmentPanel(user.getUserId())));

        // My Shipments (loads this customer's shipments from the database)
        sidebar.setMenuAction("My Shipments",
                () -> showCustomerPanel(
                        new MyShipmentsPanel(user.getUserId())));

        // Tracking (uses the customer TrackingPanel from this same package)
        sidebar.setMenuAction("Tracking",
                () -> showCustomerPanel(new TrackingPanel()));

        // Delivery History (this customer's DELIVERED shipments)
        sidebar.setMenuAction("Delivery History",
                () -> showCustomerPanel(
                        new DeliveryHistoryPanel(user.getUserId())));

        // Profile (still demo data)
        sidebar.setMenuAction("Profile",
                () -> showCustomerPanel(new CustomerProfilePanel()));

        // Settings
        sidebar.setMenuAction("Settings",
                () -> showCustomerPanel(new CustomerSettingsPanel()));
    }

    public void showCustomerPanel(JPanel panel) {
        setContent(panel);
        revalidate();
        repaint();
    }
}