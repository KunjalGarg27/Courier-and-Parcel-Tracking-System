
package com.courier.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.courier.dao.ShipmentTrackingDAO;
import com.courier.util.DBConnection;

public class ShipmentStatusService {

    private final ShipmentTrackingDAO trackingDAO =
            new ShipmentTrackingDAO();

    public void updateStatus(
            long shipmentId,
            String statusName,
            String locationDescription,
            String notes,
            Long recordedByUserId
    ) throws SQLException {

        if (shipmentId <= 0) {
            throw new IllegalArgumentException("Invalid shipment ID.");
        }

        if (statusName == null || statusName.isBlank()) {
            throw new IllegalArgumentException("Status is required.");
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Confirm that the shipment exists.
                String checkSql = """
                        SELECT shipment_id
                        FROM shipments
                        WHERE shipment_id = ?
                        FOR UPDATE
                        """;

                try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                    ps.setLong(1, shipmentId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Shipment not found: " + shipmentId
                            );
                        }
                    }
                }

                // Validate the requested status.
                String statusSql = """
                        SELECT status_id
                        FROM shipment_statuses
                        WHERE status_name = ?
                        """;

                try (PreparedStatement ps = con.prepareStatement(statusSql)) {
                    ps.setString(1, statusName);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Unknown shipment status: " + statusName
                            );
                        }
                    }
                }

                // Add a tracking event instead of overwriting history.
                trackingDAO.createTrackingEvent(
                        con,
                        shipmentId,
                        statusName,
                        locationDescription,
                        notes,
                        recordedByUserId
                );

                // Keep delivered_at consistent with the latest status.
                String deliverySql = """
                        UPDATE shipments
                        SET delivered_at = CASE
                            WHEN ? = 'DELIVERED'
                                THEN COALESCE(delivered_at, CURRENT_TIMESTAMP)
                            ELSE NULL
                        END,
                        updated_at = CURRENT_TIMESTAMP
                        WHERE shipment_id = ?
                        """;

                try (PreparedStatement ps = con.prepareStatement(deliverySql)) {
                    ps.setString(1, statusName);
                    ps.setLong(2, shipmentId);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException | RuntimeException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                throw ex;

            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is closed automatically by try-with-resources.
                }
            }
        }
    }
}
