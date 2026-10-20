
package com.courier.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.courier.dao.DeliveryAgentDAO;
import com.courier.util.AppTheme;
import com.courier.view.agent.AgentDashboardPanel;
import com.courier.view.agent.AgentSettingsPanel;
import com.courier.view.common.BaseFrame;
import com.courier.view.common.SidebarPanel;

public class AgentGUITest {

    private static JPanel mainArea;
    private static BaseFrame frame;
    private static long agentId;
    private static String[] agentProfile;

    private static final DeliveryAgentDAO dao = new DeliveryAgentDAO();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AgentGUITest::createGUI);
    }

    private static void createGUI() {
        try {
            agentId = dao.getAgentIdByEmail("agent.test@kahinbhi.com");

            if (agentId == -1) {
                JOptionPane.showMessageDialog(
                    null,
                    "Agent account not found in the database."
                );
                return;
            }

            agentProfile = dao.getAgentProfile(agentId);

            if (agentProfile == null) {
                JOptionPane.showMessageDialog(
                    null,
                    "Could not load the delivery agent profile."
                );
                return;
            }

            String agentName = "Lovedeep Singh";
            frame = new BaseFrame(
                "Delivery Agent Portal",
                agentName,
                "Delivery Agent"
            );

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(AppTheme.BACKGROUND);

            SidebarPanel sidebar = new SidebarPanel(
                new String[] {
                    "Dashboard",
                    "My Deliveries",
                    "Tracking",
                    "Delivery History",
                    "Settings"
                }
            );

            mainArea = new JPanel(new BorderLayout());
            mainArea.setBackground(AppTheme.BACKGROUND);

            root.add(sidebar, BorderLayout.WEST);
            root.add(mainArea, BorderLayout.CENTER);

            frame.setContent(root);

            sidebar.setMenuAction(
                "Dashboard",
                () -> showContent(createDashboard())
            );

            sidebar.setMenuAction(
                "My Deliveries",
                () -> showContent(createMyDeliveries())
            );

            sidebar.setMenuAction(
                "Tracking",
                () -> showContent(createTracking())
            );

            sidebar.setMenuAction(
                "Delivery History",
                () -> showContent(createDeliveryHistory())
            );

            sidebar.setMenuAction(
                "Settings",
                () -> showContent(new AgentSettingsPanel(agentId))
            );

            showContent(createDashboard());
            frame.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Could not connect to the database:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private static void showContent(JPanel panel) {
        mainArea.removeAll();
        mainArea.add(panel, BorderLayout.CENTER);
        mainArea.revalidate();
        mainArea.repaint();
    }

    // DASHBOARD
    private static JPanel createDashboard() {
        return new AgentDashboardPanel(agentId);
    }

    // MY DELIVERIES
    private static JPanel createMyDeliveries() {
        JPanel panel = createPagePanel(
            "My Deliveries",
            "View your assigned parcels and their current status."
        );

        String[] columns = {
            "Parcel ID",
            "Customer",
            "Phone",
            "Address",
            "Shipment Status",
            "Assignment ID",
            "Assignment Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createTable(model);

        try {
            List<String[]> deliveries = dao.getAssignedDeliveries(agentId);

            for (String[] d : deliveries) {
                model.addRow(new Object[] {
                    d[2],
                    d[3],
                    d[4],
                    d[5],
                    d[7],
                    d[0],
                    d[6]
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Could not load deliveries:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        hideColumn(table, "Assignment ID");
        hideColumn(table, "Assignment Status");

        JPanel section = createSection("Assigned Parcels");
        section.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(e ->
            showContent(createMyDeliveries())
        );

        JButton updateButton = createButton("Update Status");

        updateButton.addActionListener(e -> {
            int selectedViewRow = table.getSelectedRow();

            if (selectedViewRow == -1) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please select a parcel first."
                );
                return;
            }

            int selectedModelRow =
                table.convertRowIndexToModel(selectedViewRow);

            String trackingNumber =
                String.valueOf(model.getValueAt(selectedModelRow, 0));

            long selectedAssignmentId;

            try {
                selectedAssignmentId = Long.parseLong(
                    String.valueOf(model.getValueAt(selectedModelRow, 5))
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not identify the selected delivery.",
                    "Selection Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String currentStatus =
                String.valueOf(model.getValueAt(selectedModelRow, 6));

            String nextStatus;

            switch (currentStatus) {
                case "ASSIGNED" ->
                    nextStatus = "ACCEPTED";
                case "ACCEPTED" ->
                    nextStatus = "IN_PROGRESS";
                case "IN_PROGRESS" ->
                    nextStatus = "COMPLETED";
                default -> {
                    JOptionPane.showMessageDialog(
                        frame,
                        "This delivery cannot be updated from status: "
                            + currentStatus
                    );
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Parcel: " + trackingNumber
                    + "\nCurrent assignment status: " + currentStatus
                    + "\nNew assignment status: " + nextStatus
                    + "\n\nContinue with this update?",
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String notes = JOptionPane.showInputDialog(
                frame,
                "Enter an optional delivery note:",
                "Delivery Note",
                JOptionPane.PLAIN_MESSAGE
            );

            if (notes == null) {
                return;
            }

            Double latitude = null;
            Double longitude = null;

            int coordinateChoice = JOptionPane.showConfirmDialog(
                frame,
                "Do you want to record coordinates for this event?\n"
                    + "Choose No to continue without coordinates.",
                "Record Location",
                JOptionPane.YES_NO_OPTION
            );

            if (coordinateChoice == JOptionPane.YES_OPTION) {
                JTextField latitudeField = new JTextField(12);
                JTextField longitudeField = new JTextField(12);

                JPanel coordinatePanel = new JPanel(
                    new GridLayout(0, 2, 10, 10)
                );

                coordinatePanel.add(new JLabel("Latitude (-90 to 90):"));
                coordinatePanel.add(latitudeField);
                coordinatePanel.add(new JLabel("Longitude (-180 to 180):"));
                coordinatePanel.add(longitudeField);

                int coordinateResult = JOptionPane.showConfirmDialog(
                    frame,
                    coordinatePanel,
                    "Enter Delivery Coordinates",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (coordinateResult != JOptionPane.OK_OPTION) {
                    return;
                }

                try {
                    latitude = Double.parseDouble(
                        latitudeField.getText().trim()
                    );

                    longitude = Double.parseDouble(
                        longitudeField.getText().trim()
                    );

                    if (!Double.isFinite(latitude)
                            || !Double.isFinite(longitude)
                            || latitude < -90
                            || latitude > 90
                            || longitude < -180
                            || longitude > 180) {

                        JOptionPane.showMessageDialog(
                            frame,
                            "Invalid coordinates.\n"
                                + "Latitude must be between -90 and 90.\n"
                                + "Longitude must be between -180 and 180.",
                            "Invalid Coordinates",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Please enter valid numeric latitude and longitude.",
                        "Invalid Coordinates",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            try {
                dao.updateDeliveryStatus(
                    agentId,
                    selectedAssignmentId,
                    nextStatus,
                    notes,
                    latitude,
                    longitude
                );

                String successMessage =
                    "Delivery status updated successfully.\n"
                        + "Parcel: " + trackingNumber
                        + "\nNew status: " + nextStatus;

                if (latitude != null && longitude != null) {
                    successMessage += "\nLatitude: " + latitude
                        + "\nLongitude: " + longitude;
                } else {
                    successMessage += "\nCoordinates were not recorded.";
                }

                JOptionPane.showMessageDialog(
                    frame,
                    successMessage,
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                showContent(createMyDeliveries());

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not update delivery status:\n" + ex.getMessage(),
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });

        // REJECT DELIVERY
        JButton rejectButton = createButton("Reject Delivery");

        rejectButton.addActionListener(e -> {
            int selectedViewRow = table.getSelectedRow();

            if (selectedViewRow == -1) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please select a parcel to reject.",
                    "No Parcel Selected",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int selectedModelRow =
                table.convertRowIndexToModel(selectedViewRow);

            String trackingNumber =
                String.valueOf(model.getValueAt(selectedModelRow, 0));

            String currentStatus =
                String.valueOf(model.getValueAt(selectedModelRow, 6));

            long selectedAssignmentId;

            try {
                selectedAssignmentId = Long.parseLong(
                    String.valueOf(model.getValueAt(selectedModelRow, 5))
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not identify the selected delivery.",
                    "Selection Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (!"ASSIGNED".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Only newly assigned deliveries can be rejected.",
                    "Cannot Reject Delivery",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JTextField reasonField = new JTextField(25);

            JPanel reasonPanel = new JPanel(new BorderLayout(8, 8));
            reasonPanel.add(
                new JLabel("Enter the reason for rejecting this delivery:"),
                BorderLayout.NORTH
            );
            reasonPanel.add(reasonField, BorderLayout.CENTER);

            int reasonChoice = JOptionPane.showConfirmDialog(
                frame,
                reasonPanel,
                "Reject Delivery - " + trackingNumber,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (reasonChoice != JOptionPane.OK_OPTION) {
                return;
            }

            String reason = reasonField.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "A rejection reason is required.",
                    "Reason Required",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (reason.length() > 500) {
                JOptionPane.showMessageDialog(
                    frame,
                    "The rejection reason cannot exceed 500 characters.",
                    "Reason Too Long",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to reject this delivery?\n\n"
                    + "Tracking number: " + trackingNumber
                    + "\nReason: " + reason,
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                boolean rejected = dao.rejectDelivery(
                    agentId,
                    selectedAssignmentId,
                    reason
                );

                if (rejected) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Delivery rejected successfully.\n"
                            + "Tracking number: " + trackingNumber
                            + "\nReason: " + reason,
                        "Rejection Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "The delivery was not rejected. It may have "
                            + "already changed or may not belong to you.",
                        "Rejection Failed",
                        JOptionPane.WARNING_MESSAGE
                    );
                }

                showContent(createMyDeliveries());

            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not reject delivery:\n" + ex.getMessage(),
                    "Rejection Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(updateButton);
        buttons.add(rejectButton);

        section.add(buttons, BorderLayout.SOUTH);
        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    private static void hideColumn(JTable table, String columnName) {
        TableColumn column =
            table.getColumnModel().getColumn(
                table.getColumnModel().getColumnIndex(columnName)
            );

        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setResizable(false);
    }

    // TRACKING TIMELINE
    private static JPanel createTracking() {
        JPanel panel = createPagePanel(
            "Tracking",
            "View the complete recorded history of a parcel assigned to you."
        );

        JPanel section = createSection("Parcel Tracking Timeline");

        JPanel searchPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 12, 12)
        );
        searchPanel.setOpaque(false);

        JTextField parcelField = new JTextField(20);
        parcelField.setPreferredSize(new Dimension(230, 35));

        JButton trackButton = createButton("Track Parcel");
        JButton clearButton = createButton("Clear");

        JLabel result = new JLabel(
            "Enter a tracking number to view its tracking history."
        );
        result.setForeground(AppTheme.TEXT_SECONDARY);

        String[] columns = {
            "Event Date / Time",
            "Shipment Status",
            "Location",
            "Notes",
            "Latitude",
            "Longitude"
        };

        DefaultTableModel timelineModel =
            new DefaultTableModel(columns, 0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        JTable timelineTable = createTable(timelineModel);
        timelineTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane timelineScroll = new JScrollPane(timelineTable);
        timelineScroll.setPreferredSize(new Dimension(700, 300));

        trackButton.addActionListener(e -> {
            String trackingNumber = parcelField.getText().trim();

            if (trackingNumber.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a tracking number.",
                    "Tracking Number Required",
                    JOptionPane.WARNING_MESSAGE
                );
                parcelField.requestFocusInWindow();
                return;
            }

            timelineModel.setRowCount(0);
            result.setText("Loading tracking history...");

            try {
                List<String[]> timeline =
                    dao.getTrackingTimeline(agentId, trackingNumber);

                if (timeline.isEmpty()) {
                    result.setText(
                        "No tracking events found for this parcel. "
                            + "The parcel may not be assigned to you, "
                            + "or no tracking events have been recorded."
                    );
                    return;
                }

                for (String[] event : timeline) {
                    timelineModel.addRow(new Object[] {
                        event[0],
                        event[1],
                        event[2],
                        event[3],
                        event[4],
                        event[5]
                    });
                }

                result.setText(
                    "Tracking number: " + trackingNumber
                        + "    |    Recorded events: " + timeline.size()
                );

            } catch (SQLException ex) {
                result.setText("Could not load tracking history.");

                JOptionPane.showMessageDialog(
                    frame,
                    "Could not retrieve tracking history:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        });

        clearButton.addActionListener(e -> {
            parcelField.setText("");
            timelineModel.setRowCount(0);
            result.setText(
                "Enter a tracking number to view its tracking history."
            );
            parcelField.requestFocusInWindow();
        });

        parcelField.addActionListener(e -> trackButton.doClick());

        searchPanel.add(new JLabel("Tracking Number:"));
        searchPanel.add(parcelField);
        searchPanel.add(trackButton);
        searchPanel.add(clearButton);

        JPanel timelinePanel = new JPanel(new BorderLayout(8, 8));
        timelinePanel.setOpaque(false);
        timelinePanel.add(result, BorderLayout.NORTH);
        timelinePanel.add(timelineScroll, BorderLayout.CENTER);

        section.add(searchPanel, BorderLayout.NORTH);
        section.add(timelinePanel, BorderLayout.CENTER);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // DELIVERY HISTORY
    private static JPanel createDeliveryHistory() {
        JPanel panel = createPagePanel(
            "Delivery History",
            "Review completed and rejected deliveries, including rejection reasons."
        );

        String[] columns = {
            "Parcel ID",
            "Customer",
            "Phone",
            "Destination",
            "Assignment Status",
            "Assigned At",
            "Completed At",
            "Rejection Reason"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        try {
            List<String[]> history = dao.getDeliveryHistory(agentId);

            for (String[] d : history) {
                model.addRow(new Object[] {
                    d[1],
                    d[2],
                    d[3],
                    d[4],
                    d[7],
                    d[5],
                    d[6],
                    d[8]
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Could not load delivery history:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        JPanel section = createSection(
            "Completed and Rejected Deliveries"
        );

        if (model.getRowCount() == 0) {
            JLabel empty = new JLabel(
                "No completed or rejected deliveries found.",
                SwingConstants.CENTER
            );
            empty.setForeground(AppTheme.TEXT_SECONDARY);
            section.add(empty, BorderLayout.CENTER);
        } else {
            section.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(
            e -> showContent(createDeliveryHistory())
        );

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(refreshButton);
        section.add(bottom, BorderLayout.SOUTH);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // SETTINGS - LOAD AND SAVE PROFILE
    private static JPanel createSettings() {
        JPanel panel = createPagePanel(
            "Settings",
            "View your profile and update your phone number."
        );

        JPanel section = createSection("Agent Profile");

        JPanel form = new JPanel(new GridLayout(0, 2, 15, 18));
        form.setOpaque(false);
        form.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JTextField nameField = new JTextField();
        JTextField roleField = new JTextField("Delivery Agent");
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        nameField.setEditable(false);
        roleField.setEditable(false);
        emailField.setEditable(false);

        nameField.setBackground(AppTheme.BACKGROUND);
        roleField.setBackground(AppTheme.BACKGROUND);
        emailField.setBackground(AppTheme.BACKGROUND);

        try {
            agentProfile = dao.getAgentProfile(agentId);

            if (agentProfile == null) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not find your profile in the database.",
                    "Profile Not Found",
                    JOptionPane.ERROR_MESSAGE
                );
                return panel;
            }

            nameField.setText(agentProfile[0]);
            emailField.setText(agentProfile[1]);
            phoneField.setText(agentProfile[2]);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                frame,
                "Could not load your profile:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            return panel;
        }

        form.add(new JLabel("Full Name"));
        form.add(nameField);
        form.add(new JLabel("Role"));
        form.add(roleField);
        form.add(new JLabel("Phone Number"));
        form.add(phoneField);
        form.add(new JLabel("Email Address"));
        form.add(emailField);

        JLabel hint = new JLabel(
            "Only your phone number can be edited on this page."
        );
        hint.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel formContainer = new JPanel(new BorderLayout(8, 12));
        formContainer.setOpaque(false);
        formContainer.add(form, BorderLayout.CENTER);
        formContainer.add(hint, BorderLayout.SOUTH);

        JButton saveButton = createButton("Save Settings");
        JButton reloadButton = createButton("Reload Profile");

        saveButton.addActionListener(e -> {
            String phone = phoneField.getText().trim();

            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Phone number is required.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
                );
                phoneField.requestFocusInWindow();
                return;
            }

            if (!phone.matches("[0-9+() -]{7,20}")) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Enter a valid phone number using 7 to 20 characters.\n"
                        + "You may use digits, +, parentheses, spaces, or hyphens.",
                    "Invalid Phone Number",
                    JOptionPane.WARNING_MESSAGE
                );
                phoneField.requestFocusInWindow();
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Save this phone number to your profile?\n\n" + phone,
                "Confirm Profile Update",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                boolean updated = dao.updateAgentPhoneNumber(agentId, phone);

                if (updated) {
                    agentProfile = dao.getAgentProfile(agentId);

                    JOptionPane.showMessageDialog(
                        frame,
                        "Your phone number has been saved successfully.",
                        "Settings Saved",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    showContent(createSettings());

                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "No profile was updated. Please check your agent account.",
                        "Update Not Completed",
                        JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Could not save your phone number:\n" + ex.getMessage(),
                    "Save Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });

        reloadButton.addActionListener(e -> showContent(createSettings()));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(reloadButton);
        bottom.add(saveButton);

        section.add(formContainer, BorderLayout.CENTER);
        section.add(bottom, BorderLayout.SOUTH);

        panel.add(section, BorderLayout.CENTER);

        return panel;
    }

    // REUSABLE PAGE LAYOUT
    private static JPanel createPagePanel(
            String titleText,
            String subtitleText) {

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBackground(AppTheme.BACKGROUND);
        panel.setBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

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
        section.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
            )
        );

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        section.add(title, BorderLayout.NORTH);

        return section;
    }

    private static JTable createTable(DefaultTableModel model) {
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
        table.setAutoCreateRowSorter(true);

        return table;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
