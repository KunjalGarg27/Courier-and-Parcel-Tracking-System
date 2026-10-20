
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.courier.model.Shipment;
import com.courier.util.DBConnection;

public class TrackingDAO {

    public Shipment findShipmentByTrackingNumber(String trackingNumber)
            throws SQLException {

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

            ps.setString(1, trackingNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Shipment shipment = new Shipment();

                shipment.setShipmentId(rs.getLong("shipment_id"));
                shipment.setTrackingNumber(rs.getString("tracking_number"));
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

                if (rs.getTimestamp("created_at") != null) {
                    shipment.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime());
                }

                if (rs.getTimestamp("updated_at") != null) {
                    shipment.setUpdatedAt(
                            rs.getTimestamp("updated_at").toLocalDateTime());
                }

                if (rs.getTimestamp("expected_delivery_at") != null) {
                    shipment.setExpectedDeliveryAt(
                            rs.getTimestamp("expected_delivery_at")
                                    .toLocalDateTime());
                }

                if (rs.getTimestamp("delivered_at") != null) {
                    shipment.setDeliveredAt(
                            rs.getTimestamp("delivered_at").toLocalDateTime());
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
                    String eventTime = rs.getTimestamp("event_time")
                            .toLocalDateTime().toString();

                    String entry = eventTime + " | " + status;

                    if (location != null && !location.isBlank()) {
                        entry += " | " + location;
                    }

                    if (notes != null && !notes.isBlank()) {
                        entry += " | " + notes;
                    }

                    history.add(entry);
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
    // Returns {pickupCity, deliveryCity} for the shipment.
    public String[] findRouteCities(long shipmentId) throws SQLException {

        String sql = """
                SELECT pa.city AS pickup_city,
                       da.city AS delivery_city
                FROM shipments s
                JOIN addresses pa ON pa.address_id = s.pickup_address_id
                JOIN addresses da ON da.address_id = s.delivery_address_id
                WHERE s.shipment_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            rs.getString("pickup_city"),
                            rs.getString("delivery_city")
                    };
                }
            }
        }

        return new String[] {null, null};
    }
}
