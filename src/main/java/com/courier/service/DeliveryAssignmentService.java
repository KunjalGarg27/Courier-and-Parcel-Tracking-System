
package com.courier.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.courier.dao.DeliveryAssignmentDAO;
import com.courier.util.DBConnection;

public class DeliveryAssignmentService {

    private final DeliveryAssignmentDAO assignmentDAO =
            new DeliveryAssignmentDAO();

    public long assignShipment(
            long shipmentId,
            long agentId,
            long assignedByUserId
    ) throws SQLException {

        if (shipmentId <= 0 || agentId <= 0 || assignedByUserId <= 0) {
            throw new IllegalArgumentException(
                    "Shipment, agent, and Admin IDs must be valid."
            );
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                validateShipment(con, shipmentId);
                validateAgent(con, agentId);
                validateAdmin(con, assignedByUserId);
                ensureNoActiveAssignment(con, shipmentId);

                long assignmentId = assignmentDAO.assignShipment(
                        con,
                        shipmentId,
                        agentId,
                        assignedByUserId
                );

                con.commit();
                return assignmentId;

            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private void validateShipment(Connection con, long shipmentId)
            throws SQLException {

        String sql = """
                SELECT shipment_id
                FROM shipments
                WHERE shipment_id = ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Shipment does not exist."
                    );
                }
            }
        }
    }

    private void validateAgent(Connection con, long agentId)
            throws SQLException {

        String sql = """
                SELECT da.agent_id
                FROM delivery_agents da
                JOIN users u ON u.user_id = da.user_id
                WHERE da.agent_id = ?
                  AND da.availability_status = 'AVAILABLE'
                  AND u.account_status = 'ACTIVE'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Agent does not exist, is inactive, "
                            + "or is not available."
                    );
                }
            }
        }
    }

    private void validateAdmin(Connection con, long adminUserId)
            throws SQLException {

        String sql = """
                SELECT u.user_id
                FROM users u
                JOIN roles r ON r.role_id = u.role_id
                WHERE u.user_id = ?
                  AND r.role_name = 'ADMIN'
                  AND u.account_status = 'ACTIVE'
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, adminUserId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "The assigning user is not an active Admin."
                    );
                }
            }
        }
    }

    private void ensureNoActiveAssignment(
            Connection con,
            long shipmentId
    ) throws SQLException {

        String sql = """
                SELECT da.assignment_id
                FROM delivery_assignments da
                JOIN assignment_statuses ast
                    ON ast.assignment_status_id =
                       da.assignment_status_id
                WHERE da.shipment_id = ?
                  AND ast.status_name IN (
                      'ASSIGNED', 'ACCEPTED', 'IN_PROGRESS'
                  )
                LIMIT 1
                FOR UPDATE
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException(
                            "Shipment already has an active assignment."
                    );
                }
            }
        }
    }
}
