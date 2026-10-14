
package com.courier.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.courier.util.AppTheme;
import com.courier.view.common.BaseFrame;
import com.courier.view.common.SidebarPanel;

public class AgentGUITest {

    private static JPanel mainArea;
    private static BaseFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createGUI());
    }

    private static void createGUI() {

        frame = new BaseFrame(
                "Delivery Agent Portal",
                "Lovedeep",
                "Delivery Agent"
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND);

        SidebarPanel sidebar = new SidebarPanel(
                new String[] {
                        "Dashboard",
                        "My Deliveries",
                        "Tracking",
                        "Delivery History"
                }
        );

        mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(AppTheme.BACKGROUND);

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainArea, BorderLayout.CENTER);

        frame.setContent(root);

        // Dashboard button
        sidebar.setMenuAction("Dashboard", () ->
                showContent(createDashboard()));

        // My Deliveries button
        sidebar.setMenuAction("My Deliveries", () ->
                showContent(createMyDeliveries()));

        // Tracking button
        sidebar.setMenuAction("Tracking", () ->
                showContent(createTracking()));

        // Delivery History button
        sidebar.setMenuAction("Delivery History", () ->
                showContent(createDeliveryHistory()));

        // Settings button
        sidebar.setMenuAction("Settings", () ->
                showContent(createSettings()));

        showContent(createDashboard());

        frame.setVisible(true);
    }

    private static void showContent(JPanel panel) {
        mainArea.removeAll();
        mainArea.add(panel, BorderLayout.CENTER);
        mainArea.revalidate();
        mainArea.repaint();
    }

    // DASHBOARD
    private static JPanel createDashboard() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(AppTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Welcome back! Here is your delivery overview."
        );
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel heading = new JPanel(new BorderLayout(5, 8));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 15));
        stats.setOpaque(false);

        stats.add(createStatCard("Assigned", "12"));
        stats.add(createStatCard("Pending", "5"));
        stats.add(createStatCard("Out for Delivery", "3"));
        stats.add(createStatCard("Completed Today", "4"));

        String[] columns = {
                "Parcel ID", "Customer", "Destination", "Status"
        };

        Object[][] data = {
                {"KB1001", "Rahul Sharma", "Faridabad", "Pending"},
                {"KB1002", "Priya Verma", "Delhi", "Out for Delivery"},
                {"KB1003", "Aman Singh", "Gurugram", "Delivered"},
                {"KB1004", "Neha Gupta", "Noida", "Pending"}
        };

        JTable table = createTable(columns, data);

        JPanel recent = createSection("Recent Deliveries");
        recent.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(20, 20));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(recent, BorderLayout.CENTER);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createStatCard(String label, String value) {

        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel name = new JLabel(label);
        name.setForeground(AppTheme.TEXT_SECONDARY);
        name.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel number = new JLabel(value);
        number.setForeground(AppTheme.PRIMARY);
        number.setFont(new Font("Arial", Font.BOLD, 28));

        card.add(name, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);

        return card;
    }

    // MY DELIVERIES
    private static JPanel createMyDeliveries() {

        JPanel panel = createPagePanel(
                "My Deliveries",
                "View and manage your assigned parcels."
        );

        String[] columns = {
                "Parcel ID", "Customer", "Phone",
                "Address", "Status"
        };

        Object[][] data = {
                {"KB1001", "Rahul Sharma", "98XXXXXX10",
                        "Sector 15, Faridabad", "Pending"},
                {"KB1002", "Priya Verma", "97XXXXXX21",
                        "South Delhi", "Out for Delivery"},
                {"KB1003", "Aman Singh", "99XXXXXX32",
                        "Gurugram", "Delivered"},
                {"KB1004", "Neha Gupta", "96XXXXXX43",
                        "Noida Sector 62", "Pending"}
        };

        JTable table = createTable(columns, data);
        JPanel section = createSection("Assigned Parcels");
        section.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton updateButton = createButton("Update Status");

        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        frame, "Please select a parcel first."
                );
                return;
            }

            String[] statuses = {
                    "Pending",
                    "Out for Delivery",
                    "Delivered",
                    "Failed"
            };

            String status = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select the new delivery status:",
                    "Update Delivery Status",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    statuses,
                    table.getValueAt(row, 4)
            );

            if (status != null) {
                table.setValueAt(status, row, 4);
                JOptionPane.showMessageDialog(
                        frame, "Status updated in this test view."
                );
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(updateButton);

        section.add(bottom, BorderLayout.SOUTH);
        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // TRACKING
    private static JPanel createTracking() {

        JPanel panel = createPagePanel(
                "Tracking",
                "Check the current status of a parcel."
        );

        JPanel section = createSection("Track a Parcel");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        searchPanel.setOpaque(false);

        javax.swing.JTextField parcelField =
                new javax.swing.JTextField(20);
        parcelField.setPreferredSize(new Dimension(230, 35));

        JButton trackButton = createButton("Track Parcel");

        JLabel result = new JLabel("Enter a parcel ID to begin tracking.");
        result.setForeground(AppTheme.TEXT_SECONDARY);

        trackButton.addActionListener(e -> {
            String id = parcelField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame, "Please enter a parcel ID."
                );
                return;
            }

            if (id.equalsIgnoreCase("KB1001")) {
                result.setText("KB1001 — Pending");
            } else if (id.equalsIgnoreCase("KB1002")) {
                result.setText("KB1002 — Out for Delivery");
            } else if (id.equalsIgnoreCase("KB1003")) {
                result.setText("KB1003 — Delivered");
            } else if (id.equalsIgnoreCase("KB1004")) {
                result.setText("KB1004 — Pending");
            } else {
                result.setText("No parcel found in the test data.");
            }
        });

        searchPanel.add(new JLabel("Parcel ID:"));
        searchPanel.add(parcelField);
        searchPanel.add(trackButton);

        section.add(searchPanel, BorderLayout.NORTH);
        section.add(result, BorderLayout.CENTER);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // DELIVERY HISTORY
    private static JPanel createDeliveryHistory() {

        JPanel panel = createPagePanel(
                "Delivery History",
                "Review completed and previous delivery records."
        );

        String[] columns = {
                "Parcel ID", "Customer", "Destination",
                "Delivery Date", "Status"
        };

        Object[][] data = {
                {"KB1003", "Aman Singh", "Gurugram",
                        "12 Oct 2026", "Delivered"},
                {"KB0998", "Karan Mehta", "Delhi",
                        "11 Oct 2026", "Delivered"},
                {"KB0992", "Simran Kaur", "Noida",
                        "10 Oct 2026", "Delivered"}
        };

        JTable table = createTable(columns, data);

        JPanel section = createSection("Previous Deliveries");
        section.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // SETTINGS
    private static JPanel createSettings() {

        JPanel panel = createPagePanel(
                "Settings",
                "Manage your delivery agent profile."
        );

        JPanel section = createSection("Agent Profile");

        JPanel form = new JPanel(new GridLayout(0, 2, 15, 18));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        javax.swing.JTextField nameField =
                new javax.swing.JTextField("Lovedeep");
        javax.swing.JTextField roleField =
                new javax.swing.JTextField("Delivery Agent");
        javax.swing.JTextField phoneField =
                new javax.swing.JTextField("");
        javax.swing.JTextField emailField =
                new javax.swing.JTextField("");

        form.add(new JLabel("Full Name"));
        form.add(nameField);
        form.add(new JLabel("Role"));
        form.add(roleField);
        form.add(new JLabel("Phone Number"));
        form.add(phoneField);
        form.add(new JLabel("Email Address"));
        form.add(emailField);

        JButton saveButton = createButton("Save Settings");

        saveButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Settings saved in this test view."
                )
        );

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(saveButton);

        section.add(form, BorderLayout.CENTER);
        section.add(bottom, BorderLayout.SOUTH);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // REUSABLE PAGE LAYOUT
    private static JPanel createPagePanel(
            String titleText, String subtitleText) {

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBackground(AppTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel heading = new JPanel(new BorderLayout(5, 8));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.CENTER);

        panel.add(heading, BorderLayout.NORTH);

        return panel;
    }

    private static JPanel createSection(String titleText) {

        JPanel section = new JPanel(new BorderLayout(12, 12));
        section.setBackground(AppTheme.CARD);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        section.add(title, BorderLayout.NORTH);

        return section;
    }

    private static JTable createTable(
            String[] columns, Object[][] data) {

        DefaultTableModel model =
                new DefaultTableModel(data, columns) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setBackground(AppTheme.CARD);
        table.setSelectionBackground(AppTheme.ACCENT);
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(AppTheme.SIDEBAR);
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);

        return table;
    }

    private static JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );

        return button;
    }
}
