
package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.courier.service.DashboardService;
import com.courier.service.ShipmentManagementService;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.StatCard;

public class DashboardPanel extends JPanel {

    private final DashboardService dashboardService =
            new DashboardService();

    private final ShipmentManagementService shipmentService =
            new ShipmentManagementService();

    private final StatCard totalShipmentsCard =
            new StatCard("Total Shipments", "0");

    private final StatCard inTransitCard =
            new StatCard("In Transit", "0");

    private final StatCard deliveredCard =
            new StatCard("Delivered", "0");

    private final StatCard pendingCard =
            new StatCard("Pending", "0");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Shipment ID", "Tracking Number",
                            "Customer", "Destination", "Status"}, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable recentShipmentsTable = new JTable(tableModel);

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(247, 248, 244));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        buildDashboard();
        loadDashboardData();
    }

    private void buildDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 22));
        mainPanel.setOpaque(false);

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(47, 62, 48));

        JLabel subtitle = new JLabel(
                "Overview of your courier and parcel operations");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 118, 108));

        JPanel headingText = new JPanel(new GridLayout(2, 1, 0, 5));
        headingText.setOpaque(false);
        headingText.add(title);
        headingText.add(subtitle);

        headingPanel.add(headingText, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(totalShipmentsCard);
        statsPanel.add(inTransitCard);
        statsPanel.add(deliveredCard);
        statsPanel.add(pendingCard);

        JPanel topSection = new JPanel(new BorderLayout(0, 20));
        topSection.setOpaque(false);
        topSection.add(headingPanel, BorderLayout.NORTH);
        topSection.add(statsPanel, BorderLayout.CENTER);

        AppCard recentShipmentsCard = new AppCard();
        recentShipmentsCard.setLayout(new BorderLayout(0, 12));
        recentShipmentsCard.setBorder(
                BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel recentTitle = new JLabel("Recent Shipments");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        recentTitle.setForeground(new Color(47, 62, 48));

        recentShipmentsTable.setRowHeight(34);
        recentShipmentsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        recentShipmentsTable.setForeground(new Color(55, 62, 55));
        recentShipmentsTable.setGridColor(new Color(229, 232, 225));
        recentShipmentsTable.setShowVerticalLines(false);
        recentShipmentsTable.setSelectionBackground(new Color(225, 235, 218));
        recentShipmentsTable.setSelectionForeground(new Color(40, 55, 40));
        recentShipmentsTable.setFillsViewportHeight(true);

        JTableHeader header = recentShipmentsTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(235, 239, 230));
        header.setForeground(new Color(47, 62, 48));
        header.setPreferredSize(new Dimension(100, 38));

        JScrollPane scrollPane = new JScrollPane(recentShipmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        recentShipmentsCard.add(recentTitle, BorderLayout.NORTH);
        recentShipmentsCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(recentShipmentsCard, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public final void loadDashboardData() {
        try {
            Map<String, Integer> stats =
                    dashboardService.getShipmentStatistics();

            totalShipmentsCard.setValue(
                    String.valueOf(stats.getOrDefault("total", 0)));

            inTransitCard.setValue(
                    String.valueOf(stats.getOrDefault("inTransit", 0)));

            deliveredCard.setValue(
                    String.valueOf(stats.getOrDefault("delivered", 0)));

            pendingCard.setValue(
                    String.valueOf(stats.getOrDefault("pending", 0)));

            loadRecentShipments();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load dashboard data.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentShipments() throws SQLException {
        tableModel.setRowCount(0);

        List<String[]> shipments = shipmentService.getAllShipments();

        // The existing DAO returns newest shipments first.
        int count = Math.min(5, shipments.size());

        for (int i = 0; i < count; i++) {
            String[] shipment = shipments.get(i);

            tableModel.addRow(new Object[]{
                    shipment[0], // Shipment ID
                    shipment[1], // Tracking number
                    shipment[2], // Customer
                    shipment[4], // Destination
                    shipment[6]  // Latest status
            });
        }
    }
}
