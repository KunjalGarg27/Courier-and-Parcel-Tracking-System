package com.courier.view.common.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class StatusBadge extends JLabel {

    private String status;

    public StatusBadge(String status) {

        super(status);

        this.status = status;

        setFont(
            FontManager.medium(12)
        );

        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        setPreferredSize(
            new Dimension(100, 28)
        );

        setOpaque(false);

        // Set text color safely after status is initialized
        setForeground(getTextColor());
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(getBackgroundColor());

        g2.fillRoundRect(
            0,
            0,
            getWidth(),
            getHeight(),
            16,
            16
        );

        g2.dispose();

        super.paintComponent(g);
    }

    private Color getBackgroundColor() {

        switch (status.toLowerCase()) {

            case "delivered":
                return new Color(220, 242, 226);

            case "in transit":
                return new Color(225, 236, 250);

            case "pending":
                return new Color(250, 239, 215);

            case "cancelled":
                return new Color(250, 225, 225);

            default:
                return AppTheme.SURFACE;
        }
    }

    private Color getTextColor() {

        switch (status.toLowerCase()) {

            case "delivered":
                return new Color(40, 120, 70);

            case "in transit":
                return new Color(55, 95, 145);

            case "pending":
                return new Color(155, 105, 35);

            case "cancelled":
                return new Color(175, 60, 60);

            default:
                return AppTheme.TEXT_PRIMARY;
        }
    }
}