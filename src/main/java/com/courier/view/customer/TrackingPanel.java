package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppButton;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTextField;

public class TrackingPanel extends JPanel {

    public TrackingPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Track Shipment");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Tracking Card
        AppCard trackingCard = new AppCard();
        trackingCard.setLayout(new GridLayout(2, 2, 15, 15));

        JLabel trackingLabel = new JLabel("Tracking ID");
        trackingLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField trackingField = new AppTextField(20);

        AppButton trackButton = new AppButton("Track Shipment");

        trackingCard.add(trackingLabel);
        trackingCard.add(trackingField);

        trackingCard.add(new JLabel(""));
        trackingCard.add(trackButton);

        add(trackingCard, BorderLayout.CENTER);
    }
}
