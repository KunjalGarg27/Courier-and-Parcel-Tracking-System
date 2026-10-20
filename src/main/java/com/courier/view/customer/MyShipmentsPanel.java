package com.courier.view.customer;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.courier.controller.ShipmentController;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppTable;

public class MyShipmentsPanel extends JPanel {

    private static final String[] COLUMNS = {
            "Tracking ID",
            "Receiver",
            "Destination",
            "Status",
            "Booked On"
    };

    private final ShipmentController shipmentController =
            new ShipmentController();

    public MyShipmentsPanel(long userId) {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel title = new JLabel("My Shipments");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        loadShipments(userId);
    }

    private void loadShipments(long userId) {
        try {
            List<String[]> rows = shipmentController.getMyShipments(userId);

            if (rows.isEmpty()) {
                showMessage("You have not booked any shipments yet.",
                        AppTheme.TEXT_SECONDARY);
                return;
            }

            Object[][] data = rows.toArray(new Object[0][]);

            // Read-only: customers must not be able to edit the cells.
            AppTable table = new AppTable(data, COLUMNS) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            add(new JScrollPane(table), BorderLayout.CENTER);

        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), AppTheme.DANGER);

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Could not load your shipments. "
                    + "Check the database connection.", AppTheme.DANGER);
        }
    }

    private void showMessage(String text, java.awt.Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(FontManager.medium(15));
        label.setForeground(color);
        add(label, BorderLayout.CENTER);
    }
}