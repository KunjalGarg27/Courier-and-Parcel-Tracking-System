package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.courier.controller.TrackingController;
import com.courier.model.Shipment;
import com.courier.model.TrackingResult;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppButton;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTextField;

public class TrackingPanel extends JPanel {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final TrackingController trackingController =
            new TrackingController();

    private final AppTextField trackingField = new AppTextField(20);
    private final JLabel messageLabel =
            new JLabel("Enter your tracking number to begin.");
    private final JTextArea detailsArea = readOnlyArea();
    private final JTextArea historyArea = readOnlyArea();

    public TrackingPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Track Shipment");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);
        content.add(buildSearchCard());
        content.add(buildHistoryCard());
        add(content, BorderLayout.CENTER);
    }

    private AppCard buildSearchCard() {

        JLabel label = new JLabel("Tracking number");
        label.setFont(FontManager.medium(16));
        label.setForeground(AppTheme.TEXT_PRIMARY);

        AppButton trackButton = new AppButton("Track Shipment");
        trackButton.setForeground(Color.WHITE);
        trackButton.addActionListener(e -> search());
        trackingField.addActionListener(e -> search());

        messageLabel.setFont(FontManager.regular(13));
        messageLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setOpaque(false);
        form.add(label);
        form.add(trackingField);
        form.add(trackButton);
        form.add(messageLabel);

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 12));
        card.add(form, BorderLayout.NORTH);
        card.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        return card;
    }

    private AppCard buildHistoryCard() {

        JLabel label = new JLabel("Tracking history");
        label.setFont(FontManager.medium(16));
        label.setForeground(AppTheme.TEXT_PRIMARY);

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 12));
        card.add(label, BorderLayout.NORTH);
        card.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        return card;
    }

    private void search() {

        detailsArea.setText("");
        historyArea.setText("");

        try {
            TrackingResult result =
                    trackingController.track(trackingField.getText());

            if (result == null) {
                showMessage("No shipment found for that tracking number.",
                        AppTheme.DANGER);
                return;
            }

            showResult(result);
            showMessage("Shipment found.", AppTheme.SUCCESS);

        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), AppTheme.DANGER);

        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Could not load tracking details. "
                    + "Check the database connection.", AppTheme.DANGER);
        }
    }

    private void showResult(TrackingResult result) {

        Shipment s = result.getShipment();

        StringBuilder details = new StringBuilder();
        details.append("Tracking number: ").append(s.getTrackingNumber()).append("\n");
        details.append("Current status: ")
                .append(result.getCurrentStatus().replace('_', ' ')).append("\n\n");
        details.append("Sender: ").append(s.getSenderName());
        if (s.getSenderPhone() != null) {
            details.append(" (").append(s.getSenderPhone()).append(")");
        }
        details.append("\n");
        details.append("Receiver: ").append(s.getReceiverName());
        if (s.getReceiverPhone() != null) {
            details.append(" (").append(s.getReceiverPhone()).append(")");
        }
        details.append("\n\n");
        details.append("Package: ").append(
                s.getPackageDescription() == null
                        ? "-" : s.getPackageDescription()).append("\n");
        details.append("Weight: ").append(
                s.getWeightKg() == null
                        ? "-" : s.getWeightKg().toPlainString() + " kg").append("\n");
        details.append("Booked on: ").append(format(s.getCreatedAt())).append("\n");
        details.append("Expected delivery: ").append(
                s.getExpectedDeliveryAt() == null
                        ? "Not available yet"
                        : format(s.getExpectedDeliveryAt())).append("\n");

        if (s.getDeliveredAt() != null) {
            details.append("Delivered on: ")
                    .append(format(s.getDeliveredAt())).append("\n");
        }

        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);

        List<String> history = result.getHistory();

        if (history.isEmpty()) {
            historyArea.setText("No tracking events recorded yet.");
        } else {
            historyArea.setText(String.join("\n\n", history));
        }
        historyArea.setCaretPosition(0);
    }

    private String format(LocalDateTime time) {
        return time == null ? "-" : time.format(DATE_FORMAT);
    }

    private void showMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    private static JTextArea readOnlyArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FontManager.regular(14));
        area.setForeground(AppTheme.TEXT_PRIMARY);
        area.setBackground(AppTheme.SURFACE);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return area;
    }
}