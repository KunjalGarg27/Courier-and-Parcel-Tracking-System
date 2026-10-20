
package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.courier.controller.TrackingController;
import com.courier.model.Shipment;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

public class TrackingPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color GREEN = new Color(83, 108, 43);
    private static final Color PAGE = new Color(250, 248, 243);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(112, 119, 130);
    private static final Color BORDER = new Color(225, 219, 208);
    private static final Color BLUE_BG = new Color(226, 237, 251);
    private static final Color BLUE_TEXT = new Color(44, 96, 169);
    private static final Color RED = new Color(176, 55, 55);

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final TrackingController controller = new TrackingController();

    private final JTextField trackingField = new JTextField(25);
    private final JButton searchButton = new JButton("Search");
    private final JButton refreshButton = new JButton("Refresh");

    private final JPanel resultPanel = new JPanel(new BorderLayout(16, 16));
    private final JPanel detailsPanel = new JPanel();
    private final JPanel historyPanel = new JPanel();

    private final JLabel messageLabel = new JLabel(
            "Enter a tracking number to view shipment details."
    );

    private final JLabel statusLabel = new JLabel(
            "Awaiting search", SwingConstants.CENTER
    );

    private final JLabel locationLabel = new JLabel(
            "Search for a shipment to view its latest location.",
            SwingConstants.CENTER
    );

    private String currentTrackingNumber;

    public TrackingPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(PAGE);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        trackingField.addActionListener(e -> searchShipment());
        searchButton.addActionListener(e -> searchShipment());
        refreshButton.addActionListener(e -> refreshShipment());

        showEmptyState();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Track Shipment");
        title.setFont(FontManager.semiBold(27));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Track shipment progress, status and delivery location"
        );
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JPanel searchCard = new JPanel(new BorderLayout(8, 10));
        searchCard.setBackground(WHITE);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);

        trackingField.setFont(FontManager.regular(14));
        trackingField.setPreferredSize(new Dimension(420, 42));
        trackingField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        trackingField.setToolTipText("Enter tracking number");

        styleButton(searchButton);
        styleButton(refreshButton);

        searchButton.setPreferredSize(new Dimension(140, 42));
        refreshButton.setPreferredSize(new Dimension(105, 42));

        searchRow.add(trackingField);
        searchRow.add(searchButton);
        searchRow.add(refreshButton);

        messageLabel.setFont(FontManager.regular(12));
        messageLabel.setForeground(MUTED);

        searchCard.add(searchRow, BorderLayout.CENTER);
        searchCard.add(messageLabel, BorderLayout.SOUTH);

        resultPanel.setOpaque(false);

        content.add(searchCard, BorderLayout.NORTH);
        content.add(resultPanel, BorderLayout.CENTER);

        return content;
    }

    private JPanel createCard(String headingText) {
        JPanel card = new JPanel(new BorderLayout(10, 14));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel heading = new JLabel(headingText);
        heading.setFont(FontManager.semiBold(18));
        heading.setForeground(AppTheme.TEXT_PRIMARY);

        card.add(heading, BorderLayout.NORTH);
        return card;
    }

    private void showEmptyState() {
        resultPanel.removeAll();

        JLabel empty = new JLabel(
                "Shipment information will appear here after searching.",
                SwingConstants.CENTER
        );
        empty.setFont(FontManager.regular(15));
        empty.setForeground(MUTED);

        resultPanel.add(empty, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void searchShipment() {
        String trackingNumber = trackingField.getText()
                .trim().toUpperCase();

        if (trackingNumber.isBlank()) {
            showMessage("Please enter a tracking number.", RED);
            trackingField.requestFocusInWindow();
            return;
        }

        currentTrackingNumber = trackingNumber;
        setBusy(true);
        showMessage("Searching shipment...", MUTED);

        new SwingWorker<TrackingData, Void>() {
            @Override
            protected TrackingData doInBackground() throws Exception {
                Shipment shipment =
                        controller.trackShipment(trackingNumber);

                if (shipment == null) {
                    return null;
                }

                TrackingData data = new TrackingData();
                data.shipment = shipment;
                data.status = controller.getCurrentStatus(
                        shipment.getShipmentId()
                );
                data.history = controller.getTrackingHistory(
                        shipment.getShipmentId()
                );
                data.location = controller.getLatestLocation(
                        shipment.getShipmentId()
                );

                return data;
            }

            @Override
            protected void done() {
                setBusy(false);

                try {
                    TrackingData data = get();

                    if (data == null) {
                        showEmptyState();
                        showMessage(
                                "No shipment found for " + trackingNumber,
                                RED
                        );
                        return;
                    }

                    displayShipment(data);
                    showMessage("Shipment information loaded.", GREEN);

                } catch (Exception ex) {
                    showEmptyState();
                    showMessage("Unable to retrieve shipment information.", RED);

                    Throwable cause = ex.getCause() == null
                            ? ex : ex.getCause();

                    JOptionPane.showMessageDialog(
                            TrackingPanel.this,
                            cause.getMessage() == null
                                    ? "An unexpected error occurred."
                                    : cause.getMessage(),
                            "Tracking Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void refreshShipment() {
        if (currentTrackingNumber == null) {
            searchShipment();
            return;
        }

        trackingField.setText(currentTrackingNumber);
        searchShipment();
    }

    private void setBusy(boolean busy) {
        searchButton.setEnabled(!busy);
        refreshButton.setEnabled(!busy);
        trackingField.setEnabled(!busy);
    }
    
    private Double[] extractCoordinates(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile(
                "(?i)Coordinates:\\s*(-?\\d+(?:\\.\\d+)?)\\s*,\\s*"
                        + "(-?\\d+(?:\\.\\d+)?)"
        );

        Matcher matcher = pattern.matcher(location);

        if (!matcher.find()) {
            return null;
        }

        try {
            double latitude = Double.parseDouble(matcher.group(1));
            double longitude = Double.parseDouble(matcher.group(2));

            if (latitude < -90 || latitude > 90
                    || longitude < -180 || longitude > 180) {
                return null;
            }

            return new Double[] { latitude, longitude };

        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void displayShipment(TrackingData data) {
        resultPanel.removeAll();

        Shipment shipment = data.shipment;

        JPanel leftColumn = new JPanel(new BorderLayout(0, 16));
        leftColumn.setOpaque(false);

        JPanel rightColumn = new JPanel(new BorderLayout(0, 16));
        rightColumn.setOpaque(false);

        JPanel detailsCard = createCard("Shipment Information");

        detailsPanel.removeAll();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        detailsPanel.setAlignmentX(LEFT_ALIGNMENT);
        detailsPanel.setMinimumSize(new Dimension(300, 200));

        addDetail("Tracking Number", shipment.getTrackingNumber());
        addDetail("Shipment ID", String.valueOf(shipment.getShipmentId()));
        addDetail("Customer ID", String.valueOf(shipment.getCustomerId()));
        addDetail("Sender", shipment.getSenderName());
        addDetail("Sender Phone", shipment.getSenderPhone());
        addDetail("Receiver", shipment.getReceiverName());
        addDetail("Receiver Phone", shipment.getReceiverPhone());
        addDetail("Package", shipment.getPackageDescription());

        addDetail("Weight",
                shipment.getWeightKg() == null
                        ? null : shipment.getWeightKg() + " kg");

        addDetail("Booked On", format(shipment.getCreatedAt()));
        addDetail("Expected Delivery",
                format(shipment.getExpectedDeliveryAt()));
        addDetail("Delivered On", format(shipment.getDeliveredAt()));

        JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setPreferredSize(new Dimension(560, 300));
        detailsScroll.setMinimumSize(new Dimension(300, 200));
        detailsScroll.getVerticalScrollBar().setUnitIncrement(16);

        detailsCard.add(detailsScroll, BorderLayout.CENTER);
        
        JPanel statusCard = createCard("Current Shipment Status");

        statusLabel.setText(normalizeStatus(data.status));
        statusLabel.setFont(FontManager.semiBold(15));
        statusLabel.setForeground(BLUE_TEXT);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(BLUE_BG);
        statusLabel.setBorder(new EmptyBorder(14, 14, 14, 14));

        statusCard.add(statusLabel, BorderLayout.CENTER);

        JPanel locationCard = createCard("Delivery Agent Location / Map");

        JPanel mapContainer = new JPanel(new BorderLayout(0, 8));
        mapContainer.setBackground(WHITE);

        JMapViewer mapViewer = new JMapViewer();
        mapViewer.setPreferredSize(new Dimension(400, 300));

        // A neutral initial view; no agent position is fabricated.
        mapViewer.setDisplayPosition(new Coordinate(28.6139, 77.2090), 5);
        Double[] coordinates = extractCoordinates(data.location);

        if (coordinates != null) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            mapViewer.removeAllMapMarkers();
            mapViewer.addMapMarker(
                    new MapMarkerDot(
                            "Latest recorded location",
                            new Coordinate(latitude, longitude)
                    )
            );
            mapViewer.setDisplayPosition(
            	    new Coordinate(latitude, longitude),
            	    14
            	);

            locationLabel.setText(
                    "<html>Latest GPS position:<br>"
                            + latitude + ", " + longitude + "</html>"
            );
        } else {
            locationLabel.setText(
                    "<html><div style='text-align:center'>"
                            + "No GPS coordinates recorded for this shipment yet."
                            + "<br>Map is available; waiting for a location update."
                            + "</div></html>"
            );
        }

        mapContainer.add(mapViewer, BorderLayout.CENTER);
        mapContainer.add(locationLabel, BorderLayout.SOUTH);

        locationCard.add(mapContainer, BorderLayout.CENTER);

        JPanel historyCard = createCard("Tracking History");

        historyPanel.removeAll();
        historyPanel.setOpaque(false);
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

        if (data.history == null || data.history.isEmpty()) {
            JLabel noHistory = new JLabel(
                    "No tracking events have been recorded yet."
            );
            noHistory.setFont(FontManager.regular(13));
            noHistory.setForeground(MUTED);
            historyPanel.add(noHistory);
        } else {
            for (String entry : data.history) {
                JLabel event = new JLabel(
                        "<html>" + escape(entry).replace("\n", "<br>")
                                + "</html>"
                );
                event.setFont(FontManager.regular(13));
                event.setForeground(AppTheme.TEXT_PRIMARY);
                event.setBorder(new EmptyBorder(10, 0, 10, 0));
                event.setAlignmentX(LEFT_ALIGNMENT);

                historyPanel.add(event);
                historyPanel.add(new javax.swing.JSeparator());
            }
        }

        historyCard.add(new JScrollPane(historyPanel), BorderLayout.CENTER);

        leftColumn.add(detailsCard, BorderLayout.CENTER);
        leftColumn.add(historyCard, BorderLayout.SOUTH);

        rightColumn.add(statusCard, BorderLayout.NORTH);
        rightColumn.add(locationCard, BorderLayout.CENTER);

        JPanel columns = new JPanel(new GridLayout(1, 2, 16, 0));
        columns.setOpaque(false);
        columns.add(leftColumn);
        columns.add(rightColumn);

        resultPanel.add(columns, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void addDetail(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout(12, 8));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(9, 0, 9, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(FontManager.regular(13));
        label.setForeground(MUTED);

        JLabel value = new JLabel(safe(valueText));
        value.setFont(FontManager.semiBold(13));
        value.setForeground(AppTheme.TEXT_PRIMARY);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);

        detailsPanel.add(row);
        detailsPanel.add(new javax.swing.JSeparator());
    }

    private void openLocation(String location) {
        if (location == null || location.isBlank()
                || location.toLowerCase().contains("no location")) {
            JOptionPane.showMessageDialog(
                    this,
                    "No recorded location is available for this shipment.",
                    "Location Unavailable",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        try {
            if (!Desktop.isDesktopSupported()) {
                throw new IllegalStateException(
                        "Opening a browser is not supported on this system."
                );
            }

            String url = "https://www.openstreetmap.org/search?query="
                    + java.net.URLEncoder.encode(
                            location,
                            java.nio.charset.StandardCharsets.UTF_8
                    );

            Desktop.getDesktop().browse(URI.create(url));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open the map: " + ex.getMessage(),
                    "Map Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleButton(JButton button) {
        button.setFont(FontManager.semiBold(13));
        button.setBackground(GREEN);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    private static String safe(String value) {
        return value == null || value.isBlank()
                ? "Not available" : value;
    }

    private static String normalizeStatus(String status) {
        return status == null || status.isBlank()
                ? "Status not recorded"
                : status.replace('_', ' ');
    }

    private static String format(LocalDateTime value) {
        return value == null ? "Not available" : value.format(DATE_TIME);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static class TrackingData {
        Shipment shipment;
        String status;
        List<String> history;
        String location;
    }
}
