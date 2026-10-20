
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.courier.model.Shipment;
import com.courier.util.DBConnection;

public class TrackingDAO {

    public Shipment findShipmentByTrackingNumber(String trackingNumber)
            throws SQLException {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            return null;
        }

        String sql = """
                SELECT
                    s.shipment_id,
                    s.tracking_number,
                    s.customer_id,
                    s.sender_name,
                    s.sender_phone,
                    s.receiver_name,
                    s.receiver_phone,
                    s.pickup_address_id,
                    s.delivery_address_id,
                    s.service_type_id,
                    s.package_description,
                    s.weight_kg,
                    s.created_at,
                    s.updated_at,
                    s.expected_delivery_at,
                    s.delivered_at
                FROM shipments s
                WHERE s.tracking_number = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trackingNumber.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Shipment shipment = new Shipment();

                shipment.setShipmentId(rs.getLong("shipment_id"));
                shipment.setTrackingNumber(
                        rs.getString("tracking_number"));
                shipment.setCustomerId(rs.getLong("customer_id"));
                shipment.setSenderName(rs.getString("sender_name"));
                shipment.setSenderPhone(rs.getString("sender_phone"));
                shipment.setReceiverName(rs.getString("receiver_name"));
                shipment.setReceiverPhone(rs.getString("receiver_phone"));

                shipment.setPickupAddressId(
                        rs.getLong("pickup_address_id"));
                shipment.setDeliveryAddressId(
                        rs.getLong("delivery_address_id"));
                shipment.setServiceTypeId(rs.getInt("service_type_id"));
                shipment.setPackageDescription(
                        rs.getString("package_description"));
                shipment.setWeightKg(rs.getBigDecimal("weight_kg"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    shipment.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    shipment.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                Timestamp expectedAt =
                        rs.getTimestamp("expected_delivery_at");
                if (expectedAt != null) {
                    shipment.setExpectedDeliveryAt(
                            expectedAt.toLocalDateTime());
                }

                Timestamp deliveredAt = rs.getTimestamp("delivered_at");
                if (deliveredAt != null) {
                    shipment.setDeliveredAt(
                            deliveredAt.toLocalDateTime());
                }

                return shipment;
            }
        }
    }

    public List<String> findTrackingHistory(long shipmentId)
            throws SQLException {

        List<String> history = new ArrayList<>();

        String sql = """
                SELECT
                    sts.status_name,
                    ste.location_description,
                    ste.notes,
                    ste.event_time
                FROM shipment_tracking_events ste
                JOIN shipment_statuses sts
                    ON ste.status_id = sts.status_id
                WHERE ste.shipment_id = ?
                ORDER BY ste.event_time DESC, ste.event_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status_name");
                    String location =
                            rs.getString("location_description");
                    String notes = rs.getString("notes");
                    Timestamp timestamp = rs.getTimestamp("event_time");

                    StringBuilder entry = new StringBuilder();

                    entry.append(timestamp == null
                            ? "Unknown time"
                            : timestamp.toLocalDateTime());

                    entry.append(" | ")
                         .append(status == null ? "UNKNOWN" : status);

                    if (location != null && !location.isBlank()) {
                        entry.append(" | ").append(location);
                    }

                    if (notes != null && !notes.isBlank()) {
                        entry.append(" | ").append(notes);
                    }

                    history.add(entry.toString());
                }
            }
        }

        return history;
    }

    public String findCurrentStatus(long shipmentId) throws SQLException {

        String sql = """
                SELECT sts.status_name
                FROM shipment_tracking_events ste
                JOIN shipment_statuses sts
                    ON ste.status_id = sts.status_id
                WHERE ste.shipment_id = ?
                ORDER BY ste.event_time DESC, ste.event_id DESC
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("status_name") : null;
            }
        }
    }

    public String findLatestLocation(long shipmentId) throws SQLException {

        String sql = """
                SELECT
                    location_description,
                    latitude,
                    longitude,
                    event_time
                FROM shipment_tracking_events
                WHERE shipment_id = ?
                  AND (
                      location_description IS NOT NULL
                      OR (latitude IS NOT NULL AND longitude IS NOT NULL)
                  )
                ORDER BY event_time DESC, event_id DESC
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return "No location has been recorded for this shipment yet.";
                }

                String description =
                        rs.getString("location_description");
                java.math.BigDecimal latitude =
                        rs.getBigDecimal("latitude");
                java.math.BigDecimal longitude =
                        rs.getBigDecimal("longitude");
                Timestamp timestamp = rs.getTimestamp("event_time");

                StringBuilder result = new StringBuilder();

                if (description != null && !description.isBlank()) {
                    result.append(description);
                }

                if (latitude != null && longitude != null) {
                    if (!result.isEmpty()) {
                        result.append("\n");
                    }

                    result.append("Coordinates: ")
                          .append(latitude)
                          .append(", ")
                          .append(longitude);
                }

                if (timestamp != null) {
                    if (!result.isEmpty()) {
                        result.append("\n");
                    }

                    result.append("Recorded at: ")
                          .append(timestamp.toLocalDateTime());
                }

                return result.isEmpty()
                        ? "No location details available."
                        : result.toString();
            }
        }
    }
}
