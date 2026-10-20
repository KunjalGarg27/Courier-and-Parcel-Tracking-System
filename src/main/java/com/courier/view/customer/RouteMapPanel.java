package com.courier.view.customer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.QuadCurve2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.courier.util.FontManager;

public class RouteMapPanel extends JPanel {

    private String pickupCity;
    private String deliveryCity;
    private String status;

    public RouteMapPanel() {
        setBackground(new Color(242, 239, 230));
        setBorder(BorderFactory.createLineBorder(new Color(220, 214, 202)));
    }

    public void showRoute(String pickupCity, String deliveryCity, String status) {
        this.pickupCity = pickupCity;
        this.deliveryCity = deliveryCity;
        this.status = status;
        repaint();
    }

    public void clear() {
        this.pickupCity = null;
        this.deliveryCity = null;
        this.status = null;
        repaint();
    }

    // Position of the parcel along the route (0 = pickup, 1 = delivery).
    private static double progressFor(String status) {
        return switch (status) {
            case "BOOKED" -> 0.0;
            case "ASSIGNED" -> 0.05;
            case "PICKED_UP" -> 0.15;
            case "IN_TRANSIT" -> 0.5;
            case "OUT_FOR_DELIVERY", "DELIVERY_FAILED" -> 0.85;
            case "DELIVERED" -> 1.0;
            default -> 0.0;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Decorative background roads
        g2.setColor(new Color(220, 214, 202));
        g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawLine(-10, h / 3, w + 10, h / 2);
        g2.drawLine(w / 4, -10, w * 3 / 4, h + 10);

        g2.setFont(FontManager.regular(12));
        FontMetrics fm = g2.getFontMetrics();

        if (status == null) {
            g2.setColor(new Color(107, 114, 128));
            String hint = "Track a shipment to see its route.";
            g2.drawString(hint, (w - fm.stringWidth(hint)) / 2, h / 2);
            g2.dispose();
            return;
        }

        double sx = 60, sy = h * 0.65;
        double ex = w - 60, ey = h * 0.35;
        double cx = w / 2.0, cy = h * 0.12;

        // Route line
        g2.setColor(new Color(85, 105, 49, 170));
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 10f, new float[] {10f, 8f}, 0f));
        g2.draw(new QuadCurve2D.Double(sx, sy, cx, cy, ex, ey));

        // Pickup and delivery points
        Color pointColor = new Color(139, 94, 60);
        g2.setColor(pointColor);
        g2.fillOval((int) sx - 8, (int) sy - 8, 16, 16);
        g2.fillOval((int) ex - 8, (int) ey - 8, 16, 16);

        g2.setColor(new Color(46, 46, 46));
        drawCentered(g2, fm, "From: " + safe(pickupCity), sx, sy + 28);
        drawCentered(g2, fm, "To: " + safe(deliveryCity), ex, ey + 28);

        // Parcel marker (not shown for cancelled shipments)
        if (!"CANCELLED".equals(status)) {
            double t = progressFor(status);
            double px = (1 - t) * (1 - t) * sx + 2 * (1 - t) * t * cx + t * t * ex;
            double py = (1 - t) * (1 - t) * sy + 2 * (1 - t) * t * cy + t * t * ey;

            g2.setColor(new Color(85, 105, 49));
            g2.fillOval((int) px - 15, (int) py - 15, 30, 30);
            g2.setColor(Color.WHITE);
            g2.fillOval((int) px - 6, (int) py - 6, 12, 12);
        }

        // Labels
        g2.setColor(new Color(55, 65, 45));
        g2.setFont(FontManager.medium(13));
        g2.drawString("Status: " + status.replace('_', ' '), 14, 22);

        g2.setFont(FontManager.regular(11));
        g2.setColor(new Color(107, 114, 128));
        g2.drawString("Position is based on shipment status — not live GPS",
                14, h - 12);

        g2.dispose();
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static void drawCentered(Graphics2D g2, FontMetrics fm,
                                     String text, double x, double y) {
        g2.drawString(text, (int) (x - fm.stringWidth(text) / 2.0), (int) y);
    }
}
