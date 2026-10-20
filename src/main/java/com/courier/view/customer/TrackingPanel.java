package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppButton;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTextField;

public class TrackingPanel extends JPanel {

    private final AppTextField trackingField;
    private final JLabel statusLabel;

    public TrackingPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Page title
        JLabel title = new JLabel("Track Shipment");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(AppTheme.BACKGROUND);

        // Tracking form card
        AppCard trackingCard = new AppCard();
        trackingCard.setLayout(new BorderLayout(12, 12));
        trackingCard.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel trackingLabel = new JLabel("Tracking ID");
        trackingLabel.setFont(FontManager.medium(16));
        trackingLabel.setForeground(AppTheme.TEXT_PRIMARY);

        trackingField = new AppTextField(20);

        AppButton trackButton = new AppButton("Track Shipment");

        statusLabel = new JLabel("Enter a tracking ID to begin.");
        statusLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setOpaque(false);
        form.add(trackingLabel, BorderLayout.NORTH);
        form.add(trackingField, BorderLayout.CENTER);

        JPanel formContent = new JPanel(new BorderLayout(15, 15));
        formContent.setOpaque(false);
        formContent.add(form, BorderLayout.NORTH);
        formContent.add(trackButton, BorderLayout.CENTER);
        formContent.add(statusLabel, BorderLayout.SOUTH);

        trackingCard.add(formContent, BorderLayout.NORTH);
        trackingCard.setPreferredSize(new Dimension(320, 350));

        // Demo tracking action
        trackButton.addActionListener(e -> {
            String id = trackingField.getText().trim();

            if (id.isEmpty()) {
                statusLabel.setText("Please enter a Tracking ID.");
            } else {
                statusLabel.setText(
                        "Demo only: " + id
                                + " — live tracking unavailable."
                );
            }
        });

        // Map-style placeholder card
        AppCard mapCard = new AppCard();
        mapCard.setLayout(new BorderLayout(10, 10));
        mapCard.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel mapTitle = new JLabel(
                "Delivery Agent Location",
                SwingConstants.CENTER
        );
        mapTitle.setFont(FontManager.medium(16));
        mapTitle.setForeground(AppTheme.TEXT_PRIMARY);

        DemoMapPanel mapPanel = new DemoMapPanel();

        mapCard.add(mapTitle, BorderLayout.NORTH);
        mapCard.add(mapPanel, BorderLayout.CENTER);
        mapCard.setPreferredSize(new Dimension(450, 350));

        // Arrange the cards
        content.add(trackingCard, BorderLayout.CENTER);
        content.add(mapCard, BorderLayout.EAST);

        add(content, BorderLayout.CENTER);
    }

    // Decorative demo map panel
    private static class DemoMapPanel extends JPanel {

        DemoMapPanel() {
            setPreferredSize(new Dimension(400, 300));
            setBackground(new Color(242, 239, 230));

            setBorder(BorderFactory.createLineBorder(
                    new Color(220, 214, 202)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            // Decorative roads
            g2.setColor(new Color(220, 214, 202));
            g2.setStroke(new BasicStroke(
                    18,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            g2.drawLine(-10, h / 3, w + 10, h / 2);
            g2.drawLine(w / 4, -10, w * 3 / 4, h + 10);
            g2.drawLine(-10, h * 4 / 5, w + 10, h * 2 / 3);

            // Demo delivery agent marker
            int x = w / 2;
            int y = h / 2;

            g2.setColor(new Color(85, 105, 49));
            g2.fillOval(x - 15, y - 15, 30, 30);

            g2.setColor(Color.WHITE);
            g2.fillOval(x - 6, y - 6, 12, 12);

            // Map label
            g2.setColor(new Color(55, 65, 45));
            g2.setFont(new Font(
                    "SansSerif",
                    Font.PLAIN,
                    12
            ));

            g2.drawString(
                    "Demo location — not GPS",
                    12,
                    h - 15
            );

            g2.dispose();
        }
    }
}
