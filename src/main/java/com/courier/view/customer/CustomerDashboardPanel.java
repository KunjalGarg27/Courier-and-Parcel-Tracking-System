package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.courier.controller.ShipmentController;
import com.courier.model.CustomerStats;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;
import com.courier.view.common.components.StatCard;

public class CustomerDashboardPanel extends JPanel {

    private static final String[] COLUMNS = {
            "Tracking ID",
            "Receiver",
            "Destination",
            "Status",
            "Booked On"
    };

    private static final int RECENT_LIMIT = 5;

    private final ShipmentController shipmentController =
            new ShipmentController();

    private final StatCard totalCard = new StatCard("Total Shipments", "-");
    private final StatCard bookedCard = new StatCard("Booked", "-");
    private final StatCard transitCard = new StatCard("In Transit", "-");
    private final StatCard deliveredCard = new StatCard("Delivered", "-");

    private final AppCard recentCard = new AppCard();

    private final JLabel messageLabel = new JLabel(
            "Welcome! Manage your shipments and track your parcels from here.");

    public CustomerDashboardPanel(long userId) {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // Statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(totalCard);
        statsPanel.add(bookedCard);
        statsPanel.add(transitCard);
        statsPanel.add(deliveredCard);

        // Recent shipments card
        JLabel recentTitle = new JLabel("Recent Shipments");
        recentTitle.setFont(FontManager.semiBold(16));
        recentTitle.setForeground(AppTheme.TEXT_PRIMARY);

        recentCard.setLayout(new BorderLayout(0, 12));
        recentCard.add(recentTitle, BorderLayout.NORTH);

        // Stats on top, recent shipments fill the remaining space.
        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(statsPanel, BorderLayout.NORTH);
        center.add(recentCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Welcome / message card
        messageLabel.setFont(FontManager.medium(15));
        messageLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppCard welcomeCard = new AppCard();
        welcomeCard.add(messageLabel);
        add(welcomeCard, BorderLayout.SOUTH);

        loadData(userId);
    }

    private void loadData(long userId) {
        try {
            CustomerStats stats = shipmentController.getDashboardStats(userId);

            totalCard.setValue(String.valueOf(stats.getTotal()));
            bookedCard.setValue(String.valueOf(stats.getBooked()));
            transitCard.setValue(String.valueOf(stats.getInTransit()));
            deliveredCard.setValue(String.valueOf(stats.getDelivered()));

            List<String[]> rows = shipmentController.getMyShipments(userId);

            if (rows.isEmpty()) {
                showRecentMessage("You have not booked any shipments yet.",
                        AppTheme.TEXT_SECONDARY);
            } else {
                showRecentTable(rows.subList(
                        0, Math.min(RECENT_LIMIT, rows.size())));
            }

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Could not load your shipment summary. "
                    + "Check the database connection.");
        }
    }

    private void showRecentTable(List<String[]> rows) {
        Object[][] data = rows.toArray(new Object[0][]);

        // Read-only table.
        AppTable table = new AppTable(data, COLUMNS) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentCard.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void showRecentMessage(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(FontManager.medium(14));
        label.setForeground(color);
        recentCard.add(label, BorderLayout.CENTER);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(AppTheme.DANGER);
        showRecentMessage("Recent shipments are unavailable.",
                AppTheme.TEXT_SECONDARY);
    }
}