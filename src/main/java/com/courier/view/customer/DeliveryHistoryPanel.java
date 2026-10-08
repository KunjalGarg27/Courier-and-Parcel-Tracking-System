package com.courier.view.customer;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppTable;

public class DeliveryHistoryPanel extends JPanel {

    public DeliveryHistoryPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Delivery History");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Delivery history table
        String[] columns = {
                "Tracking ID",
                "Receiver",
                "Destination",
                "Delivery Date",
                "Status"
        };

        Object[][] data = {
                {"TRK001", "Rahul Sharma", "Delhi", "10 Oct 2026", "Delivered"},
                {"TRK003", "Aman Verma", "Bangalore", "08 Oct 2026", "Delivered"},
                {"TRK005", "Neha Gupta", "Pune", "05 Oct 2026", "Delivered"}
        };

        AppTable table = new AppTable(data, columns);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
    }
}
