
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeliveryAssignmentDAO {

    private final ShipmentTrackingDAO trackingDAO =
            new ShipmentTrackingDAO();

    public long assignShipment(
            Connection con,
            long shipmentId,
            long agentId,
            long assignedByUserId
    ) throws SQLException {

        // 1. Resolve the ASSIGNED assignment status.
        int assignedStatusId = findAssignmentStatusId(con, "ASSIGNED");

        // 2. Create the delivery assignment.
        long assignmentId = insertAssignment(
                con,
                shipmentId,
                agentId,
                assignedStatusId,
                assignedByUserId
        );

        // 3. Record the shipment's ASSIGNED status in its tracking history.
        trackingDAO.createTrackingEvent(
                con,
                shipmentId,
                "ASSIGNED",
                null,
                "Shipment assigned to delivery agent. Assignment ID: "
                        + assignmentId,
                assignedByUserId
        );

        // 4. Update the shipment's modification timestamp.
        updateShipmentTimestamp(con, shipmentId);

        // The calling service commits the transaction.
        return assignmentId;
    }

    private int findAssignmentStatusId(
            Connection con,
            String statusName
    ) throws SQLException {

        String sql = """
                SELECT assignment_status_id
                FROM assignment_statuses
                WHERE status_name = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, statusName);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            statusName + " assignment status not found."
                    );
                }

                return rs.getInt("assignment_status_id");
            }
        }
    }

    private long insertAssignment(
            Connection con,
            long shipmentId,
            long agentId,
            int assignmentStatusId,
            long assignedByUserId
    ) throws SQLException {

        String sql = """
                INSERT INTO delivery_assignments (
                    shipment_id,
                    agent_id,
                    assignment_status_id,
                    assigned_by_user_id
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, shipmentId);
            ps.setLong(2, agentId);
            ps.setInt(3, assignmentStatusId);
            ps.setLong(4, assignedByUserId);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Delivery-agent assignment failed."
                );
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException(
                "Assignment created but no assignment ID was returned."
        );
    }

    private void updateShipmentTimestamp(
            Connection con,
            long shipmentId
    ) throws SQLException {

        String sql = """
                UPDATE shipments
                SET updated_at = CURRENT_TIMESTAMP
                WHERE shipment_id = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, shipmentId);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Could not update shipment timestamp."
                );
            }
        }
    }
}
