package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppButton;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTextField;
import com.courier.view.common.components.StatusBadge;

public class TrackingPanel extends JPanel {

    public TrackingPanel() {

        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel("Track Shipment");

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

        JPanel content = new JPanel(
            new BorderLayout(20, 20)
        );

        content.setOpaque(false);


        // =========================
        // SEARCH BAR
        // =========================

        JPanel searchPanel = new JPanel(
            new FlowLayout(
                FlowLayout.LEFT,
                10,
                0
            )
        );

        searchPanel.setOpaque(false);

        AppTextField searchField =
            new AppTextField(25);

        AppButton searchButton =
            new AppButton("Search");
        
        searchButton.addActionListener(e -> {

            String shipmentId =
                searchField.getText().trim();

            if (shipmentId.isEmpty()) {

                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please enter a Shipment ID.",
                    "Search Shipment",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Searching for shipment: " + shipmentId,
                "Shipment Tracking",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        content.add(
            searchPanel,
            BorderLayout.NORTH
        );


        // =========================
        // SHIPMENT INFORMATION
        // =========================

        AppCard shipmentCard =
            new AppCard();

        shipmentCard.setLayout(
            new BorderLayout(0, 15)
        );

        JLabel shipmentTitle =
            new JLabel("Shipment Information");

        shipmentTitle.setFont(
            FontManager.semiBold(18)
        );

        shipmentTitle.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        shipmentCard.add(
            shipmentTitle,
            BorderLayout.NORTH
        );


        JPanel detailsPanel =
            new JPanel(
                new GridLayout(5, 2, 10, 10)
            );

        detailsPanel.setOpaque(false);

        detailsPanel.add(
            createLabel("Shipment ID")
        );

        detailsPanel.add(
            createLabel("CP10231")
        );

        detailsPanel.add(
            createLabel("Customer")
        );

        detailsPanel.add(
            createLabel("Rahul Sharma")
        );

        detailsPanel.add(
            createLabel("Origin")
        );

        detailsPanel.add(
            createLabel("Delhi")
        );

        detailsPanel.add(
            createLabel("Destination")
        );

        detailsPanel.add(
            createLabel("Mumbai")
        );

        detailsPanel.add(
            createLabel("Status")
        );

        detailsPanel.add(
            new StatusBadge("In Transit")
        );

        shipmentCard.add(
            detailsPanel,
            BorderLayout.CENTER
        );


        // =========================
        // MAP PLACEHOLDER
        // =========================

        AppCard mapCard =
            new AppCard();

        mapCard.setLayout(
            new BorderLayout()
        );

        JLabel mapLabel =
            new JLabel(
                "Delivery Agent Location / Map",
                JLabel.CENTER
            );

        mapLabel.setFont(
            FontManager.medium(16)
        );

        mapLabel.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        mapCard.add(
            mapLabel,
            BorderLayout.CENTER
        );

        mapCard.setPreferredSize(
            new Dimension(400, 300)
        );


        content.add(
            shipmentCard,
            BorderLayout.CENTER
        );

        content.add(
            mapCard,
            BorderLayout.EAST
        );


        add(
            content,
            BorderLayout.CENTER
        );
    }


    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
            FontManager.regular(14)
        );

        label.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        return label;
    }
}