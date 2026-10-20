
package com.courier.view.agent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.courier.dao.DeliveryAgentDAO;
import com.courier.util.AppTheme;

public class AgentDashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DeliveryAgentDAO dao = new DeliveryAgentDAO();

    private JLabel deliveriesValue;
    private JLabel inProgressValue;
    private JLabel deliveredValue;
    private JLabel overviewText;

    /**
     * Kept for compatibility with existing code that calls
     * new AgentDashboardPanel().
     *
     * TODO: Replace the fallback ID with the authenticated agent's ID
     * when integrating the delivery-agent login.
     */
    public AgentDashboardPanel() {
        this(1L);
    }

    public AgentDashboardPanel(long agentId) {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Welcome section
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

        // Statistics cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 135));

        JPanel deliveriesCard = createStatCard(
                "My Deliveries", "0", "Active assigned parcels");
        deliveriesValue = (JLabel) deliveriesCard.getClientProperty("valueLabel");
        cardsPanel.add(deliveriesCard);

        JPanel progressCard = createStatCard(
                "In Progress", "0", "Currently being delivered");
        inProgressValue = (JLabel) progressCard.getClientProperty("valueLabel");
        cardsPanel.add(progressCard);

        JPanel deliveredCard = createStatCard(
                "Delivered", "0", "Successfully completed");
        deliveredValue = (JLabel) deliveredCard.getClientProperty("valueLabel");
        cardsPanel.add(deliveredCard);

        JPanel mainContent = new JPanel(new BorderLayout(0, 25));
        mainContent.setOpaque(false);
        mainContent.add(welcomePanel, BorderLayout.NORTH);
        mainContent.add(cardsPanel, BorderLayout.CENTER);

        // Delivery overview
        JPanel activityPanel = new JPanel(new BorderLayout(0, 12));
        activityPanel.setBackground(AppTheme.CARD);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel activityHeading = new JLabel("Delivery Overview");
        activityHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
        activityHeading.setForeground(AppTheme.TEXT_PRIMARY);

        overviewText = new JLabel("Loading delivery information...");
        overviewText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        overviewText.setForeground(AppTheme.TEXT_SECONDARY);

        activityPanel.add(activityHeading, BorderLayout.NORTH);
        activityPanel.add(overviewText, BorderLayout.CENTER);

        mainContent.add(activityPanel, BorderLayout.SOUTH);
        add(mainContent, BorderLayout.CENTER);

        loadDashboard(agentId);
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

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        valuePanel.add(valueLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        card.add(descriptionLabel, BorderLayout.SOUTH);

        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    public void loadDashboard(long agentId) {
        if (agentId <= 0) {
            showError("Invalid delivery agent ID.");
            return;
        }

        try {
            Map<String, Integer> stats = dao.getDashboardStats(agentId);

            int assigned = stats.getOrDefault("assigned", 0);
            int accepted = stats.getOrDefault("accepted", 0);
            int inProgress = stats.getOrDefault("inProgress", 0);
            int completed = stats.getOrDefault("completed", 0);

            deliveriesValue.setText(
                    String.valueOf(assigned + accepted + inProgress));
            inProgressValue.setText(String.valueOf(inProgress));
            deliveredValue.setText(String.valueOf(completed));

            overviewText.setText(
                    "Assigned: " + assigned
                    + "   |   Accepted: " + accepted
                    + "   |   In progress: " + inProgress
                    + "   |   Completed: " + completed);

        } catch (SQLException e) {
            showError("Unable to load dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Runnable update = () -> {
            overviewText.setText(message);
            deliveriesValue.setText("--");
            inProgressValue.setText("--");
            deliveredValue.setText("--");
        };

        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }
}
