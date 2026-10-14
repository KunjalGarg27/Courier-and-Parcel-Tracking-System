
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShipmentTrackingDAO {

    public long createTrackingEvent(
            Connection con,
            long shipmentId,
            String statusName,
            String locationDescription,
            String notes,
            Long recordedByUserId
    ) throws SQLException {

        // Find the status ID using its name.
        String statusSql = """
                SELECT status_id
                FROM shipment_statuses
                WHERE status_name = ?
                """;

        int statusId;

        try (PreparedStatement ps = con.prepareStatement(statusSql)) {
            ps.setString(1, statusName);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "Unknown shipment status: " + statusName
                    );
                }

                statusId = rs.getInt("status_id");
            }
        }

        // Insert the tracking event.
        String eventSql = """
                INSERT INTO shipment_tracking_events (
                    shipment_id,
                    status_id,
                    location_description,
                    notes,
                    recorded_by_user_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(
                eventSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, shipmentId);
            ps.setInt(2, statusId);
            ps.setString(3, locationDescription);
            ps.setString(4, notes);

            if (recordedByUserId != null) {
                ps.setLong(5, recordedByUserId);
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Tracking event creation failed.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new SQLException(
                    "Tracking event created but no event ID was returned."
            );
        }
    }
}
