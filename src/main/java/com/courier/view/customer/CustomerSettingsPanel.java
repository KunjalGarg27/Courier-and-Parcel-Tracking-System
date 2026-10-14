package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;

public class CustomerSettingsPanel extends JPanel {

    public CustomerSettingsPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25
        ));

        // Title
        JLabel title = new JLabel("Settings");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        add(title, BorderLayout.NORTH);

        // Settings Card
        AppCard settingsCard = new AppCard();
        settingsCard.setLayout(new GridLayout(3, 1, 10, 10));

        JCheckBox notifications =
                new JCheckBox("Enable shipment notifications");

        JCheckBox deliveryUpdates =
                new JCheckBox("Receive delivery updates");

        JCheckBox emailUpdates =
                new JCheckBox("Receive email notifications");

        notifications.setOpaque(false);
        deliveryUpdates.setOpaque(false);
        emailUpdates.setOpaque(false);

        notifications.setForeground(AppTheme.TEXT_PRIMARY);
        deliveryUpdates.setForeground(AppTheme.TEXT_PRIMARY);
        emailUpdates.setForeground(AppTheme.TEXT_PRIMARY);

        notifications.setFont(FontManager.medium(14));
        deliveryUpdates.setFont(FontManager.medium(14));
        emailUpdates.setFont(FontManager.medium(14));

        notifications.setSelected(true);
        deliveryUpdates.setSelected(true);
        emailUpdates.setSelected(false);

        settingsCard.add(notifications);
        settingsCard.add(deliveryUpdates);
        settingsCard.add(emailUpdates);

        add(settingsCard, BorderLayout.CENTER);
    }
}
