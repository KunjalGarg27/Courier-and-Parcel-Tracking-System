package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.courier.model.CustomerStats;
import com.courier.util.DBConnection;

public class CustomerShipmentDAO {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    // Counts only the shipments that belong to this logged-in user's customer record.
    public CustomerStats findStatsByUserId(long userId) throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total_count,
                    COALESCE(SUM(CASE WHEN ss.status_name IN
                        ('BOOKED', 'ASSIGNED') THEN 1 ELSE 0 END), 0)
                        AS booked_count,
                    COALESCE(SUM(CASE WHEN ss.status_name IN
                        ('PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY')
                        THEN 1 ELSE 0 END), 0) AS transit_count,
                    COALESCE(SUM(CASE WHEN ss.status_name = 'DELIVERED'
                        THEN 1 ELSE 0 END), 0) AS delivered_count
                FROM shipments s
                JOIN customers c
                    ON c.customer_id = s.customer_id
                LEFT JOIN shipment_tracking_events ste
                    ON ste.event_id = (
                        SELECT e.event_id
                        FROM shipment_tracking_events e
                        WHERE e.shipment_id = s.shipment_id
                        ORDER BY e.event_time DESC, e.event_id DESC
                        LIMIT 1
                    )
                LEFT JOIN shipment_statuses ss
                    ON ss.status_id = ste.status_id
                WHERE c.user_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerStats(
                            rs.getInt("total_count"),
                            rs.getInt("booked_count"),
                            rs.getInt("transit_count"),
                            rs.getInt("delivered_count"));
                }
            }
        }

        return new CustomerStats(0, 0, 0, 0);
    }

    // Each row: {trackingNumber, receiverName, deliveryCity, status, bookedOn}
    // Only shipments belonging to this logged-in user's customer record.
    public List<String[]> findShipmentsByUserId(long userId)
            throws SQLException {

        List<String[]> rows = new ArrayList<>();

        String sql = """
                SELECT
                    s.tracking_number,
                    s.receiver_name,
                    da.city AS delivery_city,
                    COALESCE(ss.status_name, 'UNKNOWN') AS status_name,
                    s.created_at
                FROM shipments s
                JOIN customers c
                    ON c.customer_id = s.customer_id
                JOIN addresses da
                    ON da.address_id = s.delivery_address_id
                LEFT JOIN shipment_tracking_events ste
                    ON ste.event_id = (
                        SELECT e.event_id
                        FROM shipment_tracking_events e
                        WHERE e.shipment_id = s.shipment_id
                        ORDER BY e.event_time DESC, e.event_id DESC
                        LIMIT 1
                    )
                LEFT JOIN shipment_statuses ss
                    ON ss.status_id = ste.status_id
                WHERE c.user_id = ?
                ORDER BY s.created_at DESC, s.shipment_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Timestamp created = rs.getTimestamp("created_at");

                    rows.add(new String[] {
                            rs.getString("tracking_number"),
                            rs.getString("receiver_name"),
                            rs.getString("delivery_city"),
                            rs.getString("status_name").replace('_', ' '),
                            created == null
                                    ? "-"
                                    : created.toLocalDateTime()
                                    .format(DATE_FORMAT)
                    });
                }
            }
        }

        return rows;
    }

    // Each row: {trackingNumber, receiverName, deliveryCity, deliveryDate, status}
    // Only this customer's shipments whose CURRENT status is DELIVERED.
    public List<String[]> findDeliveredByUserId(long userId)
            throws SQLException {

        List<String[]> rows = new ArrayList<>();

        String sql = """
                SELECT
                    s.tracking_number,
                    s.receiver_name,
                    da.city AS delivery_city,
                    COALESCE(s.delivered_at, ste.event_time) AS delivery_time,
                    ss.status_name
                FROM shipments s
                JOIN customers c
                    ON c.customer_id = s.customer_id
                JOIN addresses da
                    ON da.address_id = s.delivery_address_id
                JOIN shipment_tracking_events ste
                    ON ste.event_id = (
                        SELECT e.event_id
                        FROM shipment_tracking_events e
                        WHERE e.shipment_id = s.shipment_id
                        ORDER BY e.event_time DESC, e.event_id DESC
                        LIMIT 1
                    )
                JOIN shipment_statuses ss
                    ON ss.status_id = ste.status_id
                WHERE c.user_id = ?
                  AND ss.status_name = 'DELIVERED'
                ORDER BY delivery_time DESC, s.shipment_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Timestamp delivered = rs.getTimestamp("delivery_time");

                    rows.add(new String[] {
                            rs.getString("tracking_number"),
                            rs.getString("receiver_name"),
                            rs.getString("delivery_city"),
                            delivered == null
                                    ? "-"
                                    : delivered.toLocalDateTime()
                                    .format(DATE_FORMAT),
                            rs.getString("status_name").replace('_', ' ')
                    });
                }
            }
        }

        return rows;
    }
}