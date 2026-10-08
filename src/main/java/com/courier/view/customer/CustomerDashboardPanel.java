package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.StatCard;

public class CustomerDashboardPanel extends JPanel {

    public CustomerDashboardPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Customer Dashboard");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Statistics
        JPanel statsPanel = new JPanel(
                new GridLayout(1, 3, 15, 0)
        );
        statsPanel.setOpaque(false);

        statsPanel.add(
                new StatCard("Total Shipments", "12")
        );

        statsPanel.add(
                new StatCard("In Transit", "3")
        );

        statsPanel.add(
                new StatCard("Delivered", "9")
        );

        add(statsPanel, BorderLayout.CENTER);

        // Welcome card
        AppCard welcomeCard = new AppCard();

        JLabel welcome = new JLabel(
                "Welcome! Manage your shipments and track your parcels from here."
        );

        welcome.setFont(FontManager.medium(15));
        welcome.setForeground(AppTheme.TEXT_PRIMARY);

        welcomeCard.add(welcome);

        add(welcomeCard, BorderLayout.SOUTH);
    }
}
