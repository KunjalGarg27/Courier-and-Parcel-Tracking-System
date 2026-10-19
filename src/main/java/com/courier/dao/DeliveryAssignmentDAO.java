
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeliveryAssignmentDAO {

    public long assignShipment(
            Connection con,
            long shipmentId,
            long agentId,
            long assignedByUserId
    ) throws SQLException {

        // Find the ASSIGNED status ID.
        String statusSql = """
                SELECT assignment_status_id
                FROM assignment_statuses
                WHERE status_name = ?
                """;

        int assignedStatusId;

        try (PreparedStatement ps = con.prepareStatement(statusSql)) {
            ps.setString(1, "ASSIGNED");

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "ASSIGNED assignment status not found."
                    );
                }

                assignedStatusId = rs.getInt("assignment_status_id");
            }
        }

        // Insert the assignment.
        String insertSql = """
                INSERT INTO delivery_assignments (
                    shipment_id,
                    agent_id,
                    assignment_status_id,
                    assigned_by_user_id
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(
                insertSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, shipmentId);
            ps.setLong(2, agentId);
            ps.setInt(3, assignedStatusId);
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

            throw new SQLException(
                    "Assignment created but no assignment ID was returned."
            );
        }
    }
}
