package com.courier.view.customer;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppTable;

public class MyShipmentsPanel extends JPanel {

    public MyShipmentsPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("My Shipments");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Shipment table
        String[] columns = {
                "Tracking ID",
                "Receiver",
                "Destination",
                "Status"
        };

        Object[][] data = {
                {"TRK001", "Rahul Sharma", "Delhi", "Delivered"},
                {"TRK002", "Priya Singh", "Mumbai", "In Transit"},
                {"TRK003", "Aman Verma", "Bangalore", "Delivered"}
        };

        AppTable table = new AppTable(data, columns);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
    }
}
