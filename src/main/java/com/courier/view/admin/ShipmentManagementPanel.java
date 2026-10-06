package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;
import com.courier.view.common.components.StatusBadge;

public class ShipmentManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private AppTable shipmentTable;

    public ShipmentManagementPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // =========================
        // PAGE TITLE
        // =========================

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Shipment Management");
        title.setFont(FontManager.semiBold(26));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "View, search and manage all shipments"
        );
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel titleText = new JPanel();
        titleText.setLayout(new GridLayout(2, 1, 0, 3));
        titleText.setOpaque(false);

        titleText.add(title);
        titleText.add(subtitle);

        titlePanel.add(titleText, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // =========================
        // MAIN CONTENT
        // =========================

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        // =========================
        // SEARCH / FILTER
        // =========================

        AppCard filterCard = new AppCard();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(FontManager.semiBold(13));
        searchLabel.setForeground(AppTheme.TEXT_PRIMARY);

        searchField = new JTextField(22);
        searchField.setFont(FontManager.regular(13));
        searchField.setPreferredSize(new java.awt.Dimension(220, 38));

        JButton searchButton = new JButton("Search");
        styleButton(searchButton);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(FontManager.semiBold(13));
        filterLabel.setForeground(AppTheme.TEXT_PRIMARY);

        statusFilter = new JComboBox<>(new String[] {
                "All",
                "Pending",
                "In Transit",
                "Delivered",
                "Cancelled"
        });

        statusFilter.setFont(FontManager.regular(13));
        statusFilter.setPreferredSize(new java.awt.Dimension(150, 38));

        JButton resetButton = new JButton("Reset");
        styleSecondaryButton(resetButton);

        filterCard.add(searchLabel);
        filterCard.add(searchField);
        filterCard.add(searchButton);
        filterCard.add(filterLabel);
        filterCard.add(statusFilter);
        filterCard.add(resetButton);

        mainPanel.add(filterCard, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================

        String[] columns = {
                "Shipment ID",
                "Customer",
                "Origin",
                "Destination",
                "Agent",
                "Status"
        };

        Object[][] data = {
                {"CP10231", "Rahul Sharma", "Delhi", "Mumbai",
                        "Amit Sharma", "In Transit"},

                {"CP10232", "Priya Mehta", "Jaipur", "Delhi",
                        "Rohit Kumar", "Delivered"},

                {"CP10233", "Aman Verma", "Delhi", "Pune",
                        "Neeraj Singh", "Pending"},

                {"CP10234", "Simran Kaur", "Noida", "Bangalore",
                        "Amit Sharma", "In Transit"},

                {"CP10235", "Arjun Gupta", "Gurgaon", "Chandigarh",
                        "Rohit Kumar", "Delivered"}
        };

        shipmentTable = new AppTable(data, columns);

        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getViewport().setBackground(AppTheme.CARD);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // =========================
        // ACTION BUTTONS
        // =========================

        AppCard actionCard = new AppCard();
        actionCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        JButton viewButton = new JButton("View Details");
        styleButton(viewButton);

        JButton updateButton = new JButton("Update Status");
        styleButton(updateButton);

        JButton locationButton = new JButton("View Location");
        styleSecondaryButton(locationButton);

        actionCard.add(locationButton);
        actionCard.add(viewButton);
        actionCard.add(updateButton);

        mainPanel.add(actionCard, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // =========================
        // EVENT HANDLING
        // =========================

        searchButton.addActionListener(e -> searchShipment());

        resetButton.addActionListener(e -> resetSearch());

        statusFilter.addActionListener(e -> filterShipments());

        viewButton.addActionListener(e -> viewShipment());

        updateButton.addActionListener(e -> updateShipmentStatus());

        locationButton.addActionListener(e -> viewShipmentLocation());
    }

    // =====================================================
    // SEARCH EVENT
    // =====================================================

    private void searchShipment() {

        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Shipment ID.",
                    "Search Shipment",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Searching for shipment: " + searchText,
                "Shipment Search",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // RESET EVENT
    // =====================================================

    private void resetSearch() {

        searchField.setText("");
        statusFilter.setSelectedIndex(0);

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Search filters have been reset.",
                "Reset",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // FILTER EVENT
    // =====================================================

    private void filterShipments() {

        String selectedStatus =
                (String) statusFilter.getSelectedItem();

        if ("All".equals(selectedStatus)) {
            return;
        }

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Filtering shipments by: " + selectedStatus,
                "Shipment Filter",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // VIEW EVENT
    // =====================================================

    private void viewShipment() {

        int selectedRow = shipmentTable.getSelectedRow();

        if (selectedRow == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please select a shipment first.",
                    "View Shipment",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String shipmentId =
                shipmentTable.getValueAt(selectedRow, 0).toString();

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Shipment ID: " + shipmentId
                        + "\n\nDetailed shipment information will be displayed here.",
                "Shipment Details",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // UPDATE STATUS EVENT
    // =====================================================

    private void updateShipmentStatus() {

        int selectedRow = shipmentTable.getSelectedRow();

        if (selectedRow == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please select a shipment first.",
                    "Update Status",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String shipmentId =
                shipmentTable.getValueAt(selectedRow, 0).toString();

        String[] statuses = {
                "Pending",
                "In Transit",
                "Delivered",
                "Cancelled"
        };

        String newStatus = (String) javax.swing.JOptionPane.showInputDialog(
                this,
                "Select new status:",
                "Update Shipment Status",
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                statuses[0]
        );

        if (newStatus != null) {

            shipmentTable.setValueAt(
                    newStatus,
                    selectedRow,
                    5
            );

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Shipment " + shipmentId
                            + " updated to: " + newStatus,
                    "Status Updated",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // =====================================================
    // LOCATION EVENT
    // =====================================================

    private void viewShipmentLocation() {

        int selectedRow = shipmentTable.getSelectedRow();

        if (selectedRow == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please select a shipment first.",
                    "Shipment Location",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String shipmentId =
                shipmentTable.getValueAt(selectedRow, 0).toString();

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Map view for " + shipmentId
                        + " will show the shipment's current location.",
                "Shipment Location",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // BUTTON STYLING
    // =====================================================

    private void styleButton(JButton button) {

        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.CARD);
        button.setBackground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new java.awt.Dimension(125, 38));
    }

    private void styleSecondaryButton(JButton button) {

        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(AppTheme.SURFACE);
        button.setFocusPainted(false);
        button.setPreferredSize(new java.awt.Dimension(100, 38));
    }
}