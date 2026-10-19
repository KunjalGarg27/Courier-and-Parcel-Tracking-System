package com.courier.view.admin;

import java.awt.BorderLayout;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.courier.controller.ShipmentManagementController;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import com.courier.controller.ShipmentStatusController;
import com.courier.controller.TrackingController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;
import com.courier.view.common.components.StatusBadge;

import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

public class ShipmentManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private AppTable shipmentTable;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private final ShipmentManagementController controller =
            new ShipmentManagementController();
    
    private final ShipmentStatusController statusController =
            new ShipmentStatusController();
    private final TrackingController trackingController =
            new TrackingController();

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
            "Tracking Number",
            "Customer",
            "Origin",
            "Destination",
            "Agent",
            "Status"
        };

        DefaultTableModel tableModel = new DefaultTableModel(
                columns, 0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shipmentTable = new AppTable(new Object[0][columns.length], columns);
        shipmentTable.setModel(tableModel);
        
        sorter = new TableRowSorter<>(tableModel);
        shipmentTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER)
        );
        scrollPane.getViewport().setBackground(AppTheme.CARD);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load actual database records
        loadShipments();

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
    

	private void loadShipments() {
	
	    try {
	        List<String[]> shipments = controller.getAllShipments();
	
	        DefaultTableModel model =
	                (DefaultTableModel) shipmentTable.getModel();
	
	        model.setRowCount(0);
	
	        for (String[] shipment : shipments) {
	            model.addRow(shipment);
	        }
	
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Could not load shipments from the database.\n"
	                        + ex.getMessage(),
	                "Database Error",
	                JOptionPane.ERROR_MESSAGE
	        );
	    }
	}


    // =====================================================
    // SEARCH EVENT & FILTER EVENT
    // =====================================================


	private void searchShipment() {
	    applyFilters();
	}
	
	private void filterShipments() {
	    applyFilters();
	}
	
	private void applyFilters() {
	    String searchText = searchField.getText().trim();
	    String selectedStatus = (String) statusFilter.getSelectedItem();
	
	    java.util.List<RowFilter<Object, Object>> filters =
	            new java.util.ArrayList<>();
	
	    if (!searchText.isEmpty()) {
	        filters.add(RowFilter.regexFilter(
	                "(?i)" + Pattern.quote(searchText)
	        ));
	    }
	
	    if (selectedStatus != null && !"All".equals(selectedStatus)) {
	        String databaseStatus = switch (selectedStatus) {
	            case "Pending" -> "BOOKED";
	            case "In Transit" -> "IN_TRANSIT";
	            case "Delivered" -> "DELIVERED";
	            case "Cancelled" -> "CANCELLED";
	            default -> selectedStatus.toUpperCase()
	                    .replace(' ', '_');
	        };
	
	        filters.add(RowFilter.regexFilter(
	                "^" + Pattern.quote(databaseStatus) + "$", 6
	        ));
	    }
	
	    sorter.setRowFilter(
	            filters.isEmpty()
	                    ? null
	                    : RowFilter.andFilter(filters)
	    );
	}

	// Reset Event
	
	private void resetSearch() {
	    searchField.setText("");
	    statusFilter.setSelectedIndex(0);
	    applyFilters();
	    loadShipments();
	}

    // =====================================================
    // VIEW EVENT
    // =====================================================


	private void viewShipment() {
	    int selectedRow = shipmentTable.getSelectedRow();
	
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Please select a shipment first.",
	                "View Shipment",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }
	
	    int modelRow = shipmentTable.convertRowIndexToModel(selectedRow);
	
	    StringBuilder details = new StringBuilder();
	
	    for (int column = 0; column < shipmentTable.getColumnCount(); column++) {
	        String columnName = shipmentTable.getColumnName(column);
	        Object value = shipmentTable.getModel().getValueAt(modelRow, column);
	
	        details.append(columnName)
	               .append(": ")
	               .append(value == null ? "N/A" : value)
	               .append("\n");
	    }
	
	    JOptionPane.showMessageDialog(
	            this,
	            details.toString(),
	            "Shipment Details",
	            JOptionPane.INFORMATION_MESSAGE
	    );
	}
	

    // =====================================================
    // UPDATE STATUS EVENT
    // =====================================================


	private void updateShipmentStatus() {
	
	    int selectedRow = shipmentTable.getSelectedRow();
	
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Please select a shipment first.",
	                "Update Status",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }
	
	    int modelRow = shipmentTable.convertRowIndexToModel(selectedRow);
	
	    String shipmentIdText =
	            shipmentTable.getModel().getValueAt(modelRow, 0).toString();
	
	    String trackingNumber =
	            shipmentTable.getModel().getValueAt(modelRow, 1).toString();
	
	    String currentStatus =
	            shipmentTable.getModel().getValueAt(modelRow, 6).toString();
	
	    String[] statusLabels = {
	        "Booked",
	        "Assigned",
	        "Picked Up",
	        "In Transit",
	        "Out for Delivery",
	        "Delivered",
	        "Delivery Failed",
	        "Cancelled"
	    };
	
	    String newStatusLabel = (String) JOptionPane.showInputDialog(
	            this,
	            "Tracking number: " + trackingNumber
	                    + "\nCurrent status: " + currentStatus
	                    + "\n\nSelect the new status:",
	            "Update Shipment Status",
	            JOptionPane.QUESTION_MESSAGE,
	            null,
	            statusLabels,
	            statusLabels[0]
	    );
	
	    if (newStatusLabel == null) {
	        return;
	    }
	
	    String newStatus = switch (newStatusLabel) {
	        case "Booked" -> "BOOKED";
	        case "Assigned" -> "ASSIGNED";
	        case "Picked Up" -> "PICKED_UP";
	        case "In Transit" -> "IN_TRANSIT";
	        case "Out for Delivery" -> "OUT_FOR_DELIVERY";
	        case "Delivered" -> "DELIVERED";
	        case "Delivery Failed" -> "DELIVERY_FAILED";
	        case "Cancelled" -> "CANCELLED";
	        default -> throw new IllegalArgumentException("Invalid status.");
	    };
	
	    if (newStatus.equals(currentStatus)) {
	        JOptionPane.showMessageDialog(
	                this,
	                "The shipment already has this status.",
	                "No Change",
	                JOptionPane.INFORMATION_MESSAGE
	        );
	        return;
	    }
	
	    String location = JOptionPane.showInputDialog(
	            this,
	            "Enter the current location (optional):",
	            "Shipment Location",
	            JOptionPane.QUESTION_MESSAGE
	    );
	
	    if (location == null) {
	        return;
	    }
	
	    String notes = JOptionPane.showInputDialog(
	            this,
	            "Enter an update note (optional):",
	            "Tracking Note",
	            JOptionPane.QUESTION_MESSAGE
	    );
	
	    if (notes == null) {
	        return;
	    }
	
	    try {
	        long shipmentId = Long.parseLong(shipmentIdText);
	
	        statusController.updateStatus(
	                shipmentId,
	                newStatus,
	                location.isBlank() ? null : location.trim(),
	                notes.isBlank() ? null : notes.trim(),
	                null
	        );
	
	        loadShipments();
	
	        JOptionPane.showMessageDialog(
	                this,
	                "Shipment " + trackingNumber
	                        + " updated successfully to "
	                        + newStatusLabel + ".",
	                "Status Updated",
	                JOptionPane.INFORMATION_MESSAGE
	        );
	
	    } catch (SQLException | IllegalArgumentException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Could not update shipment status.\n" + ex.getMessage(),
	                "Update Failed",
	                JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

    // =====================================================
    // LOCATION EVENT
    // =====================================================


	private void viewShipmentLocation() {
	
	    int selectedRow = shipmentTable.getSelectedRow();
	
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Please select a shipment first.",
	                "Shipment Location",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }
	
	    int modelRow = shipmentTable.convertRowIndexToModel(selectedRow);
	    long shipmentId = Long.parseLong(
	            shipmentTable.getModel().getValueAt(modelRow, 0).toString());
	
	    String trackingNumber =
	            shipmentTable.getModel().getValueAt(modelRow, 1).toString();
	
	    try {
	        String status = trackingController.getCurrentStatus(shipmentId);
	        String location = trackingController.getLatestLocation(shipmentId);
	        List<String> history =
	                trackingController.getTrackingHistory(shipmentId);
	
	        StringBuilder message = new StringBuilder();
	        message.append("Tracking Number: ").append(trackingNumber)
	               .append("\nCurrent Status: ")
	               .append(status == null ? "Not recorded" : status)
	               .append("\n\nLatest Recorded Location:\n")
	               .append(location)
	               .append("\n\nTracking History:\n");
	
	        if (history.isEmpty()) {
	            message.append("No tracking events recorded.");
	        } else {
	            for (String event : history) {
	                message.append("\n• ").append(event);
	            }
	        }
	
	        JTextArea area = new JTextArea(message.toString(), 16, 55);
	        area.setEditable(false);
	        area.setLineWrap(true);
	        area.setWrapStyleWord(true);
	        area.setCaretPosition(0);
	
	        JOptionPane.showMessageDialog(
	                this,
	                new JScrollPane(area),
	                "Shipment Location & Tracking",
	                JOptionPane.INFORMATION_MESSAGE
	        );
	
	    } catch (SQLException | IllegalArgumentException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Could not retrieve shipment location.\n" + ex.getMessage(),
	                "Database Error",
	                JOptionPane.ERROR_MESSAGE
	        );
	    }
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