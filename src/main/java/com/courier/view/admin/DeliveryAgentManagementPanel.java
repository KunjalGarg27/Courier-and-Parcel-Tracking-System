package com.courier.view.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;

public class DeliveryAgentManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private AppTable agentTable;
    private MapPanel mapPanel;

    public DeliveryAgentManagementPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // ==========================================
        // TITLE
        // ==========================================

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 3)
        );

        titlePanel.setOpaque(false);

        JLabel title =
            new JLabel("Delivery Agent Management");

        title.setFont(
            FontManager.semiBold(26)
        );

        title.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        JLabel subtitle =
            new JLabel(
                "Monitor agents, assignments and current locations"
            );

        subtitle.setFont(
            FontManager.regular(13)
        );

        subtitle.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // MAIN AREA
        // ==========================================

        JPanel mainPanel =
            new JPanel(new BorderLayout(15, 15));

        mainPanel.setOpaque(false);

        // ==========================================
        // SEARCH
        // ==========================================

        AppCard searchCard = new AppCard();

        searchCard.setLayout(
            new FlowLayout(
                FlowLayout.LEFT,
                12,
                12
            )
        );

        JLabel searchLabel =
            new JLabel("Search Agent:");

        searchLabel.setFont(
            FontManager.semiBold(13)
        );

        searchLabel.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        searchField =
            new JTextField();

        searchField.setFont(
            FontManager.regular(13)
        );

        searchField.setPreferredSize(
            new Dimension(280, 38)
        );

        JButton searchButton =
            new JButton("Search");

        stylePrimaryButton(searchButton, 110);

        JButton resetButton =
            new JButton("Reset");

        styleSecondaryButton(resetButton, 90);

        searchCard.add(searchLabel);
        searchCard.add(searchField);
        searchCard.add(searchButton);
        searchCard.add(resetButton);

        mainPanel.add(
            searchCard,
            BorderLayout.NORTH
        );

        // ==========================================
        // AGENT TABLE
        // ==========================================

        String[] columns = {
            "Agent ID",
            "Name",
            "Phone",
            "Assigned",
            "Status"
        };

        Object[][] data = {

            {
                "A001",
                "Amit Sharma",
                "9876543210",
                "12",
                "On Delivery"
            },

            {
                "A002",
                "Rohit Kumar",
                "9876501234",
                "8",
                "Available"
            },

            {
                "A003",
                "Neeraj Singh",
                "9812345678",
                "5",
                "On Delivery"
            },

            {
                "A004",
                "Vikas Gupta",
                "9898765432",
                "0",
                "Available"
            },

            {
                "A005",
                "Karan Mehta",
                "9123456789",
                "6",
                "On Delivery"
            }
        };

        agentTable =
            new AppTable(data, columns);

        JScrollPane tableScroll =
            new JScrollPane(agentTable);

        tableScroll.setBorder(
            BorderFactory.createLineBorder(
                AppTheme.BORDER
            )
        );

        tableScroll.getViewport()
                   .setBackground(AppTheme.CARD);

        mainPanel.add(
            tableScroll,
            BorderLayout.CENTER
        );

        // ==========================================
        // MAP
        // ==========================================

        mapPanel = new MapPanel();

        mainPanel.add(
            mapPanel,
            BorderLayout.EAST
        );

        // ==========================================
        // ACTION BUTTONS
        // ==========================================

        AppCard actionCard = new AppCard();

        actionCard.setLayout(
            new FlowLayout(
                FlowLayout.RIGHT,
                12,
                10
            )
        );

        JButton viewButton =
            new JButton("View Details");

        JButton locationButton =
            new JButton("View Location");

        JButton assignButton =
            new JButton("Assign Shipment");

        stylePrimaryButton(viewButton, 120);
        styleSecondaryButton(locationButton, 120);
        styleSecondaryButton(assignButton, 130);

        actionCard.add(locationButton);
        actionCard.add(viewButton);
        actionCard.add(assignButton);

        mainPanel.add(
            actionCard,
            BorderLayout.SOUTH
        );

        add(
            mainPanel,
            BorderLayout.CENTER
        );

        // ==========================================
        // EVENT HANDLING
        // ==========================================

        searchButton.addActionListener(
            e -> searchAgent()
        );

        resetButton.addActionListener(
            e -> resetSearch()
        );

        viewButton.addActionListener(
            e -> viewAgent()
        );

        locationButton.addActionListener(
            e -> viewLocation()
        );

        assignButton.addActionListener(
            e -> assignShipment()
        );

        agentTable.getSelectionModel()
            .addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {

                    int row =
                        agentTable.getSelectedRow();

                    if (row != -1) {

                        String agentId =
                            agentTable
                                .getValueAt(row, 0)
                                .toString();

                        String agentName =
                            agentTable
                                .getValueAt(row, 1)
                                .toString();

                        mapPanel.setAgent(
                            agentId,
                            agentName
                        );
                    }
                }
            });
    }

    // ==========================================
    // SEARCH
    // ==========================================

    private void searchAgent() {

        String text =
            searchField.getText().trim();

        if (text.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter an Agent ID or name.",
                "Search Agent",
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
            this,
            "Searching for agent: " + text,
            "Agent Search",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // RESET
    // ==========================================

    private void resetSearch() {

        searchField.setText("");

        JOptionPane.showMessageDialog(
            this,
            "Search has been reset.",
            "Reset",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // VIEW AGENT
    // ==========================================

    private void viewAgent() {

        int row =
            agentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a delivery agent first.",
                "View Agent",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String id =
            agentTable.getValueAt(row, 0).toString();

        String name =
            agentTable.getValueAt(row, 1).toString();

        String phone =
            agentTable.getValueAt(row, 2).toString();

        String assigned =
            agentTable.getValueAt(row, 3).toString();

        String status =
            agentTable.getValueAt(row, 4).toString();

        JOptionPane.showMessageDialog(
            this,
            "Agent ID: " + id
            + "\nName: " + name
            + "\nPhone: " + phone
            + "\nAssigned Shipments: " + assigned
            + "\nStatus: " + status,
            "Agent Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // VIEW LOCATION
    // ==========================================

    private void viewLocation() {

        int row =
            agentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select an agent first.",
                "Agent Location",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String name =
            agentTable.getValueAt(row, 1).toString();

        mapPanel.setAgent(
            agentTable.getValueAt(row, 0).toString(),
            name
        );

        JOptionPane.showMessageDialog(
            this,
            "Showing current location of "
            + name
            + " on the map.",
            "Agent Location",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // ASSIGN SHIPMENT
    // ==========================================

    private void assignShipment() {

        int row =
            agentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select an agent first.",
                "Assign Shipment",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String name =
            agentTable.getValueAt(row, 1).toString();

        JOptionPane.showMessageDialog(
            this,
            "Shipment assignment screen for "
            + name
            + " will open here.",
            "Assign Shipment",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // BUTTON STYLING
    // ==========================================

    private void stylePrimaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.CARD
        );

        button.setBackground(
            AppTheme.PRIMARY
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }

    private void styleSecondaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        button.setBackground(
            AppTheme.SURFACE
        );

        button.setFocusPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }

    // ==========================================
    // SIMPLE MAP PANEL
    // ==========================================

    private static class MapPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private String agentId = "A001";
        private String agentName = "Amit Sharma";

        public MapPanel() {

            setPreferredSize(
                new Dimension(360, 360)
            );

            setBackground(
                AppTheme.SURFACE
            );

            setBorder(
                BorderFactory.createLineBorder(
                    AppTheme.BORDER
                )
            );
        }

        public void setAgent(
            String id,
            String name
        ) {

            agentId = id;
            agentName = name;

            repaint();
        }

        @Override
        protected void paintComponent(
            Graphics g
        ) {

            super.paintComponent(g);

            Graphics2D g2 =
                (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Map-like background

            g2.setColor(
                AppTheme.CARD
            );

            g2.fillRect(
                0,
                0,
                width,
                height
            );

            // Roads

            g2.setColor(
                AppTheme.BORDER
            );

            g2.setStroke(
                new BasicStroke(18)
            );

            g2.drawLine(
                20,
                height - 60,
                width - 20,
                70
            );

            g2.drawLine(
                40,
                80,
                width - 50,
                height - 40
            );

            g2.setStroke(
                new BasicStroke(8)
            );

            g2.drawLine(
                width / 2,
                20,
                width / 2 + 40,
                height - 20
            );

            // Map title

            g2.setFont(
                FontManager.semiBold(15)
            );

            g2.setColor(
                AppTheme.TEXT_PRIMARY
            );

            g2.drawString(
                "Agent Location",
                18,
                28
            );

            // Agent marker

            int markerX =
                width / 2;

            int markerY =
                height / 2;

            g2.setColor(
                AppTheme.PRIMARY
            );

            g2.fillOval(
                markerX - 10,
                markerY - 10,
                20,
                20
            );

            g2.setColor(
                AppTheme.TEXT_PRIMARY
            );

            g2.drawOval(
                markerX - 10,
                markerY - 10,
                20,
                20
            );

            // Agent label

            g2.setFont(
                FontManager.semiBold(12)
            );

            g2.drawString(
                agentName,
                markerX + 15,
                markerY + 5
            );

            g2.setFont(
                FontManager.regular(11)
            );

            g2.setColor(
                AppTheme.TEXT_SECONDARY
            );

            g2.drawString(
                agentId + " • Current Location",
                18,
                height - 18
            );

            g2.dispose();
        }
    }
}