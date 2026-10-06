package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.StatCard;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {

        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // =========================
        // PAGE TITLE
        // =========================

        JLabel title = new JLabel("Dashboard");

        title.setFont(
            FontManager.semiBold(26)
        );

        title.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        add(title, BorderLayout.NORTH);


        // =========================
        // MAIN CONTENT
        // =========================

        JPanel mainPanel = new JPanel(
            new BorderLayout(0, 20)
        );

        mainPanel.setOpaque(false);


        // =========================
        // STAT CARDS
        // =========================

        JPanel statsPanel = new JPanel(
            new GridLayout(1, 4, 16, 0)
        );

        statsPanel.setOpaque(false);

        statsPanel.add(
            new StatCard("Total Shipments", "1,248")
        );

        statsPanel.add(
            new StatCard("In Transit", "342")
        );

        statsPanel.add(
            new StatCard("Delivered", "850")
        );

        statsPanel.add(
            new StatCard("Pending", "56")
        );

        mainPanel.add(
            statsPanel,
            BorderLayout.NORTH
        );


        // =========================
        // RECENT SHIPMENTS
        // =========================

        AppCard recentShipments = new AppCard();

        recentShipments.setLayout(new BorderLayout(0, 15));

        JLabel recentTitle = new JLabel("Recent Shipments");

        recentTitle.setFont(
            FontManager.semiBold(18)
        );

        recentTitle.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        recentShipments.add(
            recentTitle,
            BorderLayout.NORTH
        );


        // =========================
        // SHIPMENT TABLE
        // =========================

        String[] columns = {
            "Shipment ID",
            "Customer",
            "Destination",
            "Status"
        };

        Object[][] data = {
            {"CP10231", "Rahul Sharma", "Delhi", "Delivered"},
            {"CP10232", "Ananya Singh", "Mumbai", "In Transit"},
            {"CP10233", "Rohan Kumar", "Jaipur", "Pending"},
            {"CP10234", "Priya Mehta", "Chandigarh", "Delivered"},
            {"CP10235", "Aman Gupta", "Pune", "In Transit"}
        };

        JTable table = new JTable(data, columns);

        table.setFont(
            FontManager.regular(14)
        );

        table.setRowHeight(38);

        table.setShowVerticalLines(false);

        table.setGridColor(AppTheme.BORDER);

        table.setBackground(Color.WHITE);

        table.setForeground(AppTheme.TEXT_PRIMARY);


        // Header

        JTableHeader tableHeader = table.getTableHeader();

        tableHeader.setFont(
            FontManager.semiBold(14)
        );

        tableHeader.setBackground(
            AppTheme.SURFACE
        );

        tableHeader.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        tableHeader.setPreferredSize(
            new Dimension(0, 40)
        );


        // Remove default selection highlight for now

        table.setSelectionBackground(
            AppTheme.SURFACE
        );

        table.setSelectionForeground(
            AppTheme.TEXT_PRIMARY
        );


        JScrollPane scrollPane =
            new JScrollPane(table);

        scrollPane.setBorder(null);

        scrollPane.getViewport().setBackground(Color.WHITE);

        recentShipments.add(
            scrollPane,
            BorderLayout.CENTER
            

        );
        // Add main content to dashboard
       add(mainPanel, BorderLayout.CENTER);
    }
}