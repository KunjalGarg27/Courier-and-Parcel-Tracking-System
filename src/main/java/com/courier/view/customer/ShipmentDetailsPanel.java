package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;

public class ShipmentDetailsPanel extends JPanel {

    public ShipmentDetailsPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Shipment Details");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Details Card
        AppCard detailsCard = new AppCard();
        detailsCard.setLayout(new GridLayout(6, 2, 15, 15));

        addDetail(detailsCard, "Tracking ID", "TRK001");
        addDetail(detailsCard, "Receiver", "Rahul Sharma");
        addDetail(detailsCard, "Pickup Address", "Delhi");
        addDetail(detailsCard, "Delivery Address", "Mumbai");
        addDetail(detailsCard, "Shipment Status", "In Transit");
        addDetail(detailsCard, "Expected Delivery", "15 Oct 2026");

        add(detailsCard, BorderLayout.CENTER);
    }

    private void addDetail(
            JPanel panel,
            String labelText,
            String valueText) {

        JLabel label = new JLabel(labelText);
        label.setFont(FontManager.medium(14));
        label.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel value = new JLabel(valueText);
        value.setForeground(AppTheme.TEXT_PRIMARY);

        panel.add(label);
        panel.add(value);
    }
}