package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTextField;

public class CustomerProfilePanel extends JPanel {

    public CustomerProfilePanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("My Profile");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Profile Card
        AppCard profileCard = new AppCard();
        profileCard.setLayout(new GridLayout(4, 2, 15, 15));

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField nameField = new AppTextField(20);
        nameField.setText("Charu");

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField emailField = new AppTextField(20);
        emailField.setText("customer@email.com");

        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField phoneField = new AppTextField(20);
        phoneField.setText("9876543210");

        JLabel addressLabel = new JLabel("Address");
        addressLabel.setForeground(AppTheme.TEXT_PRIMARY);

        AppTextField addressField = new AppTextField(20);
        addressField.setText("Delhi, India");

        profileCard.add(nameLabel);
        profileCard.add(nameField);

        profileCard.add(emailLabel);
        profileCard.add(emailField);

        profileCard.add(phoneLabel);
        profileCard.add(phoneField);

        profileCard.add(addressLabel);
        profileCard.add(addressField);

        add(profileCard, BorderLayout.CENTER);
    }
}
