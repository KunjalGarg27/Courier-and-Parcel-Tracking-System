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

public class BookShipmentPanel extends JPanel {

    public BookShipmentPanel() {

        setLayout(new BorderLayout(20, 20));

        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Book a Shipment");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Form Card
        AppCard formCard = new AppCard();
        formCard.setLayout(new GridLayout(5, 2, 15, 15));

        // Sender Name
        JLabel senderLabel = new JLabel("Sender Name");
        senderLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField senderField = new AppTextField(20);

        // Receiver Name
        JLabel receiverLabel = new JLabel("Receiver Name");
        receiverLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField receiverField = new AppTextField(20);

        // Pickup Address
        JLabel pickupLabel = new JLabel("Pickup Address");
        pickupLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField pickupField = new AppTextField(20);

        // Delivery Address
        JLabel deliveryLabel = new JLabel("Delivery Address");
        deliveryLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField deliveryField = new AppTextField(20);

        // Book Button
        AppButton bookButton = new AppButton("Book Shipment");

        // Add components to form
        formCard.add(senderLabel);
        formCard.add(senderField);

        formCard.add(receiverLabel);
        formCard.add(receiverField);

        formCard.add(pickupLabel);
        formCard.add(pickupField);

        formCard.add(deliveryLabel);
        formCard.add(deliveryField);

        formCard.add(new JLabel(""));
        formCard.add(bookButton);

        add(formCard, BorderLayout.CENTER);
    }
}


