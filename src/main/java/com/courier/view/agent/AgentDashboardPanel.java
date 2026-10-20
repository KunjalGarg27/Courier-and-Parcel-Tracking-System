package com.courier.view.agent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.courier.util.AppTheme;

public class AgentDashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public AgentDashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel welcomePanel = new JPanel(new BorderLayout(0, 8));
        welcomePanel.setOpaque(false);

        JLabel heading = new JLabel("Delivery Agent Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Manage your assigned deliveries and track their progress.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        welcomePanel.add(heading, BorderLayout.NORTH);
        welcomePanel.add(subtitle, BorderLayout.CENTER);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 135));

        cardsPanel.add(createStatCard(
                "My Deliveries", "0", "Assigned parcels"));
        cardsPanel.add(createStatCard(
                "In Transit", "0", "Currently in progress"));
        cardsPanel.add(createStatCard(
                "Delivered", "0", "Successfully completed"));

        JPanel mainContent = new JPanel(new BorderLayout(0, 25));
        mainContent.setOpaque(false);
        mainContent.add(welcomePanel, BorderLayout.NORTH);
        mainContent.add(cardsPanel, BorderLayout.CENTER);

        JPanel activityPanel = new JPanel(new BorderLayout(0, 12));
        activityPanel.setBackground(AppTheme.CARD);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel activityHeading = new JLabel("Delivery Overview");
        activityHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        activityHeading.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel activityText = new JLabel(
                "Your assigned delivery information will appear here.");
        activityText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        activityText.setForeground(AppTheme.TEXT_SECONDARY);

        activityPanel.add(activityHeading, BorderLayout.NORTH);
        activityPanel.add(activityText, BorderLayout.CENTER);

        mainContent.add(activityPanel, BorderLayout.SOUTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createStatCard(
            String title, String value, String description) {

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value, SwingConstants.LEFT);
        valueLabel.setForeground(AppTheme.PRIMARY);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel valuePanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        valuePanel.add(valueLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        card.add(descriptionLabel, BorderLayout.SOUTH);

        return card;
    }
}
