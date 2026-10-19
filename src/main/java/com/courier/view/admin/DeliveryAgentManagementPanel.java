
package com.courier.view.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.courier.service.DeliveryAgentService;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;

import java.awt.Color;
import java.math.BigDecimal;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import com.courier.controller.DeliveryAssignmentController;
import com.courier.service.AssignmentOptionsService;
import javax.swing.JLabel;
import java.awt.GridLayout;

public class DeliveryAgentManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private JTable agentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private MapPanel mapPanel;

    private final DeliveryAgentService agentService =
            new DeliveryAgentService();
    

	private final DeliveryAssignmentController assignmentController =
	        new DeliveryAssignmentController();
	
	private final AssignmentOptionsService assignmentOptionsService =
	        new AssignmentOptionsService();


    public DeliveryAgentManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
        loadAgents();
    }

    private void buildUI() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Delivery Agent Management");
        title.setFont(FontManager.semiBold(26));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Monitor agents, assignments and availability");
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        // Search controls
        AppCard searchCard = new AppCard();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel searchLabel = new JLabel("Search Agent:");
        searchLabel.setFont(FontManager.semiBold(13));
        searchLabel.setForeground(AppTheme.TEXT_PRIMARY);

        searchField = new JTextField();
        searchField.setFont(FontManager.regular(13));
        searchField.setPreferredSize(new Dimension(280, 38));

        JButton searchButton = new JButton("Search");
        stylePrimaryButton(searchButton, 110);

        JButton resetButton = new JButton("Reset");
        styleSecondaryButton(resetButton, 90);

        searchCard.add(searchLabel);
        searchCard.add(searchField);
        searchCard.add(searchButton);
        searchCard.add(resetButton);

        mainPanel.add(searchCard, BorderLayout.NORTH);

        // Database-driven agent table
        String[] columns = {
            "Agent ID", "Name", "Phone", "Assigned", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        agentTable = new JTable(tableModel);
        agentTable.setRowHeight(34);
        agentTable.setFont(FontManager.regular(12));
        agentTable.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION);
        agentTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(tableModel);
        agentTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(agentTable);
        tableScroll.setBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER));
        tableScroll.getViewport().setBackground(AppTheme.CARD);

        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Clearly labelled illustrative map
        mapPanel = new MapPanel();
        mainPanel.add(mapPanel, BorderLayout.EAST);

        // Actions
        AppCard actionCard = new AppCard();
        actionCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        JButton viewButton = new JButton("View Details");
        JButton locationButton = new JButton("View Location");
        JButton assignButton = new JButton("Assign Shipment");
        JButton refreshButton = new JButton("Refresh");

        stylePrimaryButton(viewButton, 120);
        styleSecondaryButton(locationButton, 120);
        styleSecondaryButton(assignButton, 130);
        styleSecondaryButton(refreshButton, 100);

        actionCard.add(locationButton);
        actionCard.add(viewButton);
        actionCard.add(assignButton);
        actionCard.add(refreshButton);

        mainPanel.add(actionCard, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Event handling
        searchButton.addActionListener(e -> searchAgent());
        searchField.addActionListener(e -> searchAgent());
        resetButton.addActionListener(e -> resetSearch());
        viewButton.addActionListener(e -> viewAgent());
        locationButton.addActionListener(e -> viewLocation());
        assignButton.addActionListener(e -> assignShipment());
        refreshButton.addActionListener(e -> loadAgents());

        agentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = agentTable.getSelectedRow();

                if (row >= 0) {
                    int modelRow = agentTable.convertRowIndexToModel(row);

                    long agentId = Long.parseLong(valueAt(modelRow, 0));
                    String name = valueAt(modelRow, 1);

                    mapPanel.setAgent(agentId, name);
                } else {
                    mapPanel.clearAgent();
                }
            }
        });
    }

    private void loadAgents() {
        try {
            List<String[]> agents = agentService.getAllAgents();
            tableModel.setRowCount(0);

            for (String[] agent : agents) {
                tableModel.addRow(agent);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load delivery agents.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAgent() {
        String text = searchField.getText().trim();

        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter(
                "(?i)" + Pattern.quote(text)));

        if (agentTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No matching delivery agents found.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetSearch() {
        searchField.setText("");
        sorter.setRowFilter(null);
        agentTable.clearSelection();
    }

    private void viewAgent() {
        int row = agentTable.getSelectedRow();

        if (row < 0) {
            showSelectAgentMessage();
            return;
        }

        int modelRow = agentTable.convertRowIndexToModel(row);

        String id = valueAt(modelRow, 0);
        String name = valueAt(modelRow, 1);
        String phone = valueAt(modelRow, 2);
        String assigned = valueAt(modelRow, 3);
        String status = valueAt(modelRow, 4);

        JOptionPane.showMessageDialog(
                this,
                "Agent ID: " + id
                + "\nName: " + name
                + "\nPhone: " + phone
                + "\nActive Assignments: " + assigned
                + "\nAccount / Availability: " + status,
                "Agent Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewLocation() {
        int row = agentTable.getSelectedRow();

        if (row < 0) {
            showSelectAgentMessage();
            return;
        }

        int modelRow = agentTable.convertRowIndexToModel(row);

        try {
            long agentId = Long.parseLong(valueAt(modelRow, 0));
            String agentName = valueAt(modelRow, 1);

            String[] location = agentService.getLatestLocation(agentId);

            if (location == null) {
                mapPanel.clearLocation();

                JOptionPane.showMessageDialog(
                        this,
                        "No location has been recorded for "
                                + agentName + ".\n\n"
                                + "The map cannot show an agent marker "
                                + "until coordinates are available.",
                        "Location Unavailable",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            double latitude = Double.parseDouble(location[0]);
            double longitude = Double.parseDouble(location[1]);

            if (!Double.isFinite(latitude)
                    || !Double.isFinite(longitude)
                    || latitude < -90 || latitude > 90
                    || longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException(
                        "Stored GPS coordinates are invalid."
                );
            }

            mapPanel.showLocation(
                    agentId, agentName, latitude, longitude
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Agent: " + agentName + "\n"
                            + "Latitude: " + location[0] + "\n"
                            + "Longitude: " + location[1] + "\n"
                            + "Accuracy: " + location[2] + "\n"
                            + "Recorded at: " + location[3] + "\n\n"
                            + "This is the latest stored location, "
                            + "not live GPS tracking.",
                    "Agent Location",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "The stored location data is invalid.\n"
                            + ex.getMessage(),
                    "Location Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not retrieve the agent's location.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


	private void assignShipment() {
	    int selectedRow = agentTable.getSelectedRow();
	
	    if (selectedRow < 0) {
	        showSelectAgentMessage();
	        return;
	    }
	
	    int modelRow = agentTable.convertRowIndexToModel(selectedRow);
	
	    long agentId;
	    String agentName;
	    String agentStatus;
	
	    try {
	        agentId = Long.parseLong(valueAt(modelRow, 0));
	        agentName = valueAt(modelRow, 1);
	        agentStatus = valueAt(modelRow, 4);
	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(
	                this, "Invalid agent ID.", "Assignment Error",
	                JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	
	    if (!agentStatus.contains("ACTIVE")
	            || !agentStatus.contains("AVAILABLE")) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Only active and available agents can receive shipments.",
	                "Agent Unavailable",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	
	    try {
	        List<String[]> shipments =
	                assignmentOptionsService.getUnassignedShipments();
	
	        if (shipments.isEmpty()) {
	            JOptionPane.showMessageDialog(
	                    this,
	                    "There are no eligible unassigned shipments.",
	                    "No Shipments Available",
	                    JOptionPane.INFORMATION_MESSAGE);
	            return;
	        }
	
	        List<String[]> eligibleShipments = new ArrayList<>();
	        JComboBox<String> shipmentCombo = new JComboBox<>();
	
	        for (String[] shipment : shipments) {
	            String label = shipment[1] + " | "
	                    + shipment[2] + " | "
	                    + shipment[3] + " | "
	                    + shipment[4];
	
	            shipmentCombo.addItem(label);
	            eligibleShipments.add(shipment);
	        }
	
	        JTextField adminIdField = new JTextField();
	
	        JPanel dialogPanel = new JPanel(new GridLayout(0, 1, 5, 8));
	        dialogPanel.add(new JLabel("Selected Agent: " + agentName
	                + " (ID: " + agentId + ")"));
	        dialogPanel.add(new JLabel("Select Shipment:"));
	        dialogPanel.add(shipmentCombo);
	        dialogPanel.add(new JLabel("Authenticated Admin User ID:"));
	        dialogPanel.add(adminIdField);
	
	        int result = JOptionPane.showConfirmDialog(
	                this,
	                dialogPanel,
	                "Assign Shipment",
	                JOptionPane.OK_CANCEL_OPTION,
	                JOptionPane.PLAIN_MESSAGE);
	
	        if (result != JOptionPane.OK_OPTION) {
	            return;
	        }
	
	        String adminInput = adminIdField.getText().trim();
	
	        if (adminInput.isEmpty()) {
	            JOptionPane.showMessageDialog(
	                    this,
	                    "Enter a valid Admin user ID.",
	                    "Missing Admin ID",
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	
	        long adminUserId;
	
	        try {
	            adminUserId = Long.parseLong(adminInput);
	
	            if (adminUserId <= 0) {
	                throw new NumberFormatException();
	            }
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(
	                    this,
	                    "Admin user ID must be a positive number.",
	                    "Invalid Admin ID",
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	
	        int selectedShipmentIndex = shipmentCombo.getSelectedIndex();
	
	        if (selectedShipmentIndex < 0) {
	            return;
	        }
	
	        String[] shipment = eligibleShipments.get(selectedShipmentIndex);
	        long shipmentId = Long.parseLong(shipment[0]);
	
	        int confirm = JOptionPane.showConfirmDialog(
	                this,
	                "Assign tracking number " + shipment[1]
	                + " to " + agentName + "?\n\n"
	                + "The assignment will be saved to the database.",
	                "Confirm Assignment",
	                JOptionPane.YES_NO_OPTION);
	
	        if (confirm != JOptionPane.YES_OPTION) {
	            return;
	        }
	
	        long assignmentId = assignmentController.assignShipment(
	                shipmentId, agentId, adminUserId);
	
	        loadAgents();
	
	        JOptionPane.showMessageDialog(
	                this,
	                "Shipment assigned successfully!\n"
	                + "Tracking Number: " + shipment[1] + "\n"
	                + "Agent: " + agentName + "\n"
	                + "Assignment ID: " + assignmentId,
	                "Assignment Successful",
	                JOptionPane.INFORMATION_MESSAGE);
	
	    } catch (IllegalArgumentException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                ex.getMessage(),
	                "Assignment Rejected",
	                JOptionPane.WARNING_MESSAGE);
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Could not assign shipment.\n" + ex.getMessage(),
	                "Database Error",
	                JOptionPane.ERROR_MESSAGE);
	    }
	}


    private String valueAt(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void showSelectAgentMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Please select a delivery agent first.",
                "No Agent Selected",
                JOptionPane.WARNING_MESSAGE);
    }

    private void stylePrimaryButton(JButton button, int width) {
        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.CARD);
        button.setBackground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(width, 38));
    }

    private void styleSecondaryButton(JButton button, int width) {
        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(AppTheme.SURFACE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, 38));
    }


	private static class MapPanel extends JPanel {
	
	    private static final long serialVersionUID = 1L;
	
	    private final JMapViewer mapViewer = new JMapViewer();
	
	    private JLabel infoLabel;
	
	    private long selectedAgentId = -1;
	    private String selectedAgentName = "Select an agent";
	
	    MapPanel() {
	        setLayout(new BorderLayout(0, 8));
	        setPreferredSize(new Dimension(340, 380));
	        setBackground(AppTheme.SURFACE);
	        setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
	
	        infoLabel = new JLabel(
	                "<html><b>Delivery Agent Map</b><br>"
	                + "Select an agent to view their last location.</html>"
	        );
	        infoLabel.setBorder(
	                BorderFactory.createEmptyBorder(8, 10, 8, 10)
	        );
	        infoLabel.setFont(FontManager.regular(12));
	        infoLabel.setForeground(AppTheme.TEXT_PRIMARY);
	
	        mapViewer.setTileSource(new OsmTileSource.Mapnik());
	        mapViewer.setZoomControlsVisible(true);
	        mapViewer.setScrollWrapEnabled(false);
	
	        // Initial map view: Faridabad, Haryana.
	        // This is only a starting view, not an agent's location.
	        mapViewer.setDisplayPosition(
	                new Coordinate(28.4089, 77.3178), 12
	        );
	
	        add(infoLabel, BorderLayout.NORTH);
	        add(mapViewer, BorderLayout.CENTER);
	    }
	
	    void setAgent(long agentId, String name) {
	        selectedAgentId = agentId;
	        selectedAgentName = name;
	
	        clearLocation();
	
	        infoLabel.setText(
	                "<html><b>" + escapeHtml(name) + "</b><br>"
	                + "Agent ID: " + agentId + "<br>"
	                + "Click View Location to load recorded coordinates."
	                + "</html>"
	        );
	    }
	
	    void showLocation(
	            long agentId,
	            String name,
	            double latitude,
	            double longitude
	    ) {
	        selectedAgentId = agentId;
	        selectedAgentName = name;
	
	        mapViewer.removeAllMapMarkers();
	
	        Coordinate coordinate = new Coordinate(latitude, longitude);
	        MapMarkerDot marker = new MapMarkerDot(
	                name, coordinate
	        );
	
	        marker.setBackColor(new Color(46, 125, 50));
	        marker.setColor(Color.WHITE);
	
	        mapViewer.addMapMarker(marker);
	        mapViewer.setDisplayPosition(coordinate, 16);
	
	        infoLabel.setText(
	                "<html><b>" + escapeHtml(name) + "</b><br>"
	                + "Agent ID: " + agentId + "<br>"
	                + "Latitude: " + latitude + "<br>"
	                + "Longitude: " + longitude + "</html>"
	        );
	    }
	
	    void clearLocation() {
	        mapViewer.removeAllMapMarkers();
	
	        if (selectedAgentId > 0) {
	            infoLabel.setText(
	                    "<html><b>" + escapeHtml(selectedAgentName)
	                    + "</b><br>Agent ID: " + selectedAgentId
	                    + "<br>No location loaded.</html>"
	            );
	        }
	
	        mapViewer.repaint();
	    }
	
	    void clearAgent() {
	        selectedAgentId = -1;
	        selectedAgentName = "Select an agent";
	
	        mapViewer.removeAllMapMarkers();
	
	        infoLabel.setText(
	                "<html><b>Delivery Agent Map</b><br>"
	                + "Select an agent to view their last location."
	                + "</html>"
	        );
	
	        mapViewer.repaint();
	    }
	
	    private static String escapeHtml(String text) {
	        if (text == null) {
	            return "";
	        }
	
	        return text.replace("&", "&amp;")
	                   .replace("<", "&lt;")
	                   .replace(">", "&gt;")
	                   .replace("\"", "&quot;")
	                   .replace("'", "&#39;");
	    }
	}

}
