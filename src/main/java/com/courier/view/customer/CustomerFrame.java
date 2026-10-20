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

        sidebar.setMenuAction("Dashboard",
                () -> showCustomerPanel(new CustomerDashboardPanel()));

        sidebar.setMenuAction("Book Shipment",
                () -> showCustomerPanel(
                        new BookShipmentPanel(user.getUserId())));

        sidebar.setMenuAction("My Shipments",
                () -> showCustomerPanel(new MyShipmentsPanel()));

        // Uses the customer TrackingPanel from this same package.
        sidebar.setMenuAction("Tracking",
                () -> showCustomerPanel(new TrackingPanel()));

        sidebar.setMenuAction("Delivery History",
                () -> showCustomerPanel(new DeliveryHistoryPanel()));

        sidebar.setMenuAction("Profile",
                () -> showCustomerPanel(new CustomerProfilePanel()));

        sidebar.setMenuAction("Settings",
                () -> showCustomerPanel(new CustomerSettingsPanel()));
    }

    public void showCustomerPanel(JPanel panel) {
        setContent(panel);
        revalidate();
        repaint();
    }
}