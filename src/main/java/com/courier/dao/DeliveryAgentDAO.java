package com.courier.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Timestamp;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import com.courier.util.DBConnection;

public class DeliveryAgentDAO {

    public List<String[]> findAllAgents() throws SQLException {

        String sql = """

            SELECT

                da.agent_id,

                u.full_name,

                da.phone,

                da.availability_status,

                u.account_status,

                (

                    SELECT COUNT(*)

                    FROM delivery_assignments d

                    JOIN assignment_statuses ast

                      ON ast.assignment_status_id = d.assignment_status_id

                    WHERE d.agent_id = da.agent_id

                      AND ast.status_name IN

                          ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')

                ) AS active_assignments

            FROM delivery_agents da

            JOIN users u ON u.user_id = da.user_id

            ORDER BY da.agent_id DESC

            """;

        List<String[]> agents = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String phone = rs.getString("phone");

                agents.add(new String[] {

                    String.valueOf(rs.getLong("agent_id")),

                    rs.getString("full_name"),

                    phone == null ? "Not provided" : phone,

                    String.valueOf(rs.getInt("active_assignments")),

                    rs.getString("account_status") + " / "

                        + rs.getString("availability_status")

                });

            }

        }

        return agents;

    }

    public String[] findLatestLocation(long agentId) throws SQLException {

        String sql = """

            SELECT latitude, longitude, accuracy_m, recorded_at

            FROM agent_location_history

            WHERE agent_id = ?

            ORDER BY recorded_at DESC, location_id DESC

            LIMIT 1

            """;

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    java.math.BigDecimal latitude =

                        rs.getBigDecimal("latitude");

                    java.math.BigDecimal longitude =

                        rs.getBigDecimal("longitude");

                    java.math.BigDecimal accuracy =

                        rs.getBigDecimal("accuracy_m");

                    Timestamp recordedAt = rs.getTimestamp("recorded_at");

                    return new String[] {

                        latitude == null ? "Not available"

                            : latitude.toPlainString(),

                        longitude == null ? "Not available"

                            : longitude.toPlainString(),

                        accuracy == null ? "Not available"

                            : accuracy.toPlainString() + " m",

                        recordedAt == null ? "Not available"

                            : recordedAt.toString()

                    };

                }

            }

        }

        return null;

    }

    public long getAgentIdByEmail(String email) throws SQLException {

        String sql = """

            SELECT da.agent_id

            FROM delivery_agents da

            JOIN users u ON u.user_id = da.user_id

            WHERE u.email = ?

              AND u.account_status = 'ACTIVE'

            """;

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getLong("agent_id");

                }

            }

        }

        return -1;

    }

    public Map<String, Integer> getDashboardStats(long agentId)

            throws SQLException {

        String sql = """

            SELECT ast.status_name, COUNT(*) AS total

            FROM delivery_assignments da

            JOIN assignment_statuses ast

              ON ast.assignment_status_id = da.assignment_status_id

            WHERE da.agent_id = ?

            GROUP BY ast.status_name

            """;

        Map<String, Integer> stats = new HashMap<>();

        stats.put("assigned", 0);

        stats.put("accepted", 0);

        stats.put("inProgress", 0);

        stats.put("completed", 0);

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String status = rs.getString("status_name");

                    int total = rs.getInt("total");

                    switch (status) {

                        case "ASSIGNED" -> stats.put("assigned", total);

                        case "ACCEPTED" -> stats.put("accepted", total);

                        case "IN_PROGRESS" -> stats.put("inProgress", total);

                        case "COMPLETED" -> stats.put("completed", total);

                        default -> {

                        }

                    }

                }

            }

        }

        return stats;

    }

    public List<String[]> getAssignedDeliveries(long agentId)

            throws SQLException {

        String sql = """

            SELECT

                da.assignment_id,

                s.shipment_id,

                s.tracking_number,

                s.receiver_name,

                s.receiver_phone,

                CONCAT(

                    adr.address_line1, ', ',

                    COALESCE(CONCAT(adr.address_line2, ', '), ''),

                    adr.city, ', ', adr.state, ' ',

                    adr.postal_code

                ) AS delivery_address,

                ast.status_name AS assignment_status,

                ss.status_name AS shipment_status,

                s.expected_delivery_at

            FROM delivery_assignments da

            JOIN assignment_statuses ast

              ON ast.assignment_status_id = da.assignment_status_id

            JOIN shipments s ON s.shipment_id = da.shipment_id

            JOIN addresses adr

              ON adr.address_id = s.delivery_address_id

            LEFT JOIN shipment_tracking_events ste

              ON ste.event_id = (

                  SELECT ste2.event_id

                  FROM shipment_tracking_events ste2

                  WHERE ste2.shipment_id = s.shipment_id

                  ORDER BY ste2.event_time DESC, ste2.event_id DESC

                  LIMIT 1

              )

            LEFT JOIN shipment_statuses ss

              ON ss.status_id = ste.status_id

            WHERE da.agent_id = ?

              AND ast.status_name IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')

            ORDER BY da.assigned_at DESC

            """;

        List<String[]> deliveries = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp expected =

                        rs.getTimestamp("expected_delivery_at");

                    String receiverPhone = rs.getString("receiver_phone");

                    String shipmentStatus = rs.getString("shipment_status");

                    deliveries.add(new String[] {

                        String.valueOf(rs.getLong("assignment_id")),

                        String.valueOf(rs.getLong("shipment_id")),

                        rs.getString("tracking_number"),

                        rs.getString("receiver_name"),

                        receiverPhone == null ? "Not provided" : receiverPhone,

                        rs.getString("delivery_address"),

                        rs.getString("assignment_status"),

                        shipmentStatus == null ? "Not recorded" : shipmentStatus,

                        expected == null ? "Not specified" : expected.toString()

                    });

                }

            }

        }

        return deliveries;

    }

    /**

     * Returns completed and rejected deliveries for the selected agent.

     *

     * Returned array indexes:

     * 0 = Assignment ID

     * 1 = Tracking Number

     * 2 = Receiver Name

     * 3 = Receiver Phone

     * 4 = Delivery Address

     * 5 = Assigned At

     * 6 = Completed At

     * 7 = Assignment Status

     * 8 = Rejection Reason

     */

    public List<String[]> getDeliveryHistory(long agentId)

            throws SQLException {

        String sql = """

            SELECT

                da.assignment_id,

                s.tracking_number,

                s.receiver_name,

                s.receiver_phone,

                CONCAT(

                    adr.address_line1, ', ',

                    COALESCE(CONCAT(adr.address_line2, ', '), ''),

                    adr.city, ', ', adr.state, ' ',

                    adr.postal_code

                ) AS delivery_address,

                da.assigned_at,

                da.completed_at,

                ast.status_name AS assignment_status,

                da.rejection_reason,

                da.ended_at

            FROM delivery_assignments da

            JOIN assignment_statuses ast

              ON ast.assignment_status_id = da.assignment_status_id

            JOIN shipments s

              ON s.shipment_id = da.shipment_id

            JOIN addresses adr

              ON adr.address_id = s.delivery_address_id

            WHERE da.agent_id = ?

              AND ast.status_name IN ('COMPLETED', 'REJECTED')

            ORDER BY

                COALESCE(da.completed_at, da.ended_at, da.assigned_at) DESC,

                da.assignment_id DESC

            """;

        List<String[]> history = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();

             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp assigned = rs.getTimestamp("assigned_at");

                    Timestamp completed = rs.getTimestamp("completed_at");

                    String phone = rs.getString("receiver_phone");

                    String status = rs.getString("assignment_status");

                    String reason = rs.getString("rejection_reason");

                    history.add(new String[] {

                        String.valueOf(rs.getLong("assignment_id")),

                        rs.getString("tracking_number"),

                        rs.getString("receiver_name"),

                        phone == null ? "Not provided" : phone,

                        rs.getString("delivery_address"),

                        assigned == null

                            ? "Not available" : assigned.toString(),

                        completed == null

                            ? "Not completed" : completed.toString(),

                        status == null ? "Unknown" : status,

                        reason == null || reason.isBlank()

                            ? "-" : reason

                    });

                }

            }

        }

        return history;

    }

    /**

     * Backward-compatible method for existing GUI calls.

     * Coordinates remain NULL when they are not supplied.

     */

    public void updateDeliveryStatus(

            long agentId,

            long assignmentId,

            String newStatus,

            String notes) throws SQLException {

        updateDeliveryStatus(

            agentId, assignmentId, newStatus, notes, null, null

        );

    }

    /**

     * Updates a delivery assignment and records its tracking event,

     * including coordinates when provided.

     *

     * Allowed transitions:

     * ASSIGNED -> ACCEPTED

     * ACCEPTED -> IN_PROGRESS

     * IN_PROGRESS -> COMPLETED

     */

    public void updateDeliveryStatus(

            long agentId,

            long assignmentId,

            String newStatus,

            String notes,

            Double latitude,

            Double longitude) throws SQLException {

        if (newStatus == null) {

            throw new SQLException("A new delivery status is required.");

        }

        if ((latitude == null) != (longitude == null)) {

            throw new SQLException(

                "Latitude and longitude must both be provided or both be null."

            );

        }

        if (latitude != null

                && (latitude < -90 || latitude > 90

                || longitude < -180 || longitude > 180)) {

            throw new SQLException("Invalid latitude or longitude values.");

        }

        newStatus = newStatus.trim().toUpperCase();

        String shipmentStatus;

        switch (newStatus) {

            case "ACCEPTED" -> shipmentStatus = "PICKED_UP";

            case "IN_PROGRESS" -> shipmentStatus = "OUT_FOR_DELIVERY";

            case "COMPLETED" -> shipmentStatus = "DELIVERED";

            default -> throw new SQLException(

                "Unsupported status. Choose ACCEPTED, IN_PROGRESS, or COMPLETED."

            );

        }

        try (Connection con = DBConnection.getConnection()) {

            boolean previousAutoCommit = con.getAutoCommit();

            con.setAutoCommit(false);

            try {

                long shipmentId;

                long userId;

                String currentStatus;

                String checkSql = """

                    SELECT da.shipment_id,

                           ast.status_name,

                           agent.user_id

                    FROM delivery_assignments da

                    JOIN assignment_statuses ast

                      ON ast.assignment_status_id = da.assignment_status_id

                    JOIN delivery_agents agent

                      ON agent.agent_id = da.agent_id

                    JOIN users u ON u.user_id = agent.user_id

                    WHERE da.assignment_id = ?

                      AND da.agent_id = ?

                      AND u.account_status = 'ACTIVE'

                    FOR UPDATE

                    """;

                try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                    ps.setLong(1, assignmentId);

                    ps.setLong(2, agentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            throw new SQLException(

                                "Delivery not found for this agent."

                            );

                        }

                        shipmentId = rs.getLong("shipment_id");

                        currentStatus = rs.getString("status_name");

                        userId = rs.getLong("user_id");

                    }

                }

                String expectedNext;

                switch (currentStatus) {

                    case "ASSIGNED" -> expectedNext = "ACCEPTED";

                    case "ACCEPTED" -> expectedNext = "IN_PROGRESS";

                    case "IN_PROGRESS" -> expectedNext = "COMPLETED";

                    default -> throw new SQLException(

                        "This delivery cannot be updated from status "

                            + currentStatus + "."

                    );

                }

                if (!expectedNext.equals(newStatus)) {

                    throw new SQLException(

                        "Invalid transition. Current status: " + currentStatus

                            + ". Next allowed status: " + expectedNext + "."

                    );

                }

                int assignmentStatusId = getStatusId(

                    con, "assignment_statuses",

                    "assignment_status_id", "status_name", newStatus

                );

                int shipmentStatusId = getStatusId(

                    con, "shipment_statuses",

                    "status_id", "status_name", shipmentStatus

                );

                String updateSql;

                if ("ACCEPTED".equals(newStatus)) {

                    updateSql = """

                        UPDATE delivery_assignments

                        SET assignment_status_id = ?,

                            accepted_at = CURRENT_TIMESTAMP

                        WHERE assignment_id = ? AND agent_id = ?

                        """;

                } else if ("COMPLETED".equals(newStatus)) {

                    updateSql = """

                        UPDATE delivery_assignments

                        SET assignment_status_id = ?,

                            completed_at = CURRENT_TIMESTAMP

                        WHERE assignment_id = ? AND agent_id = ?

                        """;

                } else {

                    updateSql = """

                        UPDATE delivery_assignments

                        SET assignment_status_id = ?

                        WHERE assignment_id = ? AND agent_id = ?

                        """;

                }

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {

                    ps.setInt(1, assignmentStatusId);

                    ps.setLong(2, assignmentId);

                    ps.setLong(3, agentId);

                    if (ps.executeUpdate() != 1) {

                        throw new SQLException(

                            "The assignment could not be updated."

                        );

                    }

                }

                String description;

                switch (newStatus) {

                    case "ACCEPTED" ->

                        description = "Delivery accepted by agent";

                    case "IN_PROGRESS" ->

                        description = "Out for delivery";

                    case "COMPLETED" ->

                        description = "Delivery completed";

                    default ->

                        description = "Delivery status updated";

                }

                String eventSql = """

                    INSERT INTO shipment_tracking_events

                        (shipment_id, status_id, latitude, longitude,

                         location_description, notes, recorded_by_user_id)

                    VALUES (?, ?, ?, ?, ?, ?, ?)

                    """;

                try (PreparedStatement ps = con.prepareStatement(eventSql)) {

                    ps.setLong(1, shipmentId);

                    ps.setInt(2, shipmentStatusId);

                    if (latitude == null) {

                        ps.setNull(3, java.sql.Types.DECIMAL);

                        ps.setNull(4, java.sql.Types.DECIMAL);

                    } else {

                        ps.setDouble(3, latitude);

                        ps.setDouble(4, longitude);

                    }

                    ps.setString(5, description);

                    if (notes == null || notes.isBlank()) {

                        ps.setNull(6, java.sql.Types.VARCHAR);

                    } else {

                        ps.setString(6, notes.trim());

                    }

                    ps.setLong(7, userId);

                    ps.executeUpdate();

                }

                con.commit();

            } catch (SQLException | RuntimeException ex) {

                try {

                    con.rollback();

                } catch (SQLException rollbackException) {

                    ex.addSuppressed(rollbackException);

                }

                throw ex;

            } finally {

                try {

                    con.setAutoCommit(previousAutoCommit);

                } catch (SQLException ignored) {

                    // The connection closes after this method.

                }

            }

        }

    }

    private int getStatusId(

            Connection con,

            String tableName,

            String idColumn,

            String nameColumn,

            String statusName) throws SQLException {

        String sql = "SELECT " + idColumn + " FROM " + tableName

            + " WHERE " + nameColumn + " = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, statusName);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);

                }

            }

        }

        throw new SQLException(

            "Status '" + statusName + "' was not found in " + tableName + "."

        );

    }

    public List<String[]> getTrackingTimeline(

            long agentId, String trackingNumber) throws SQLException {

        List<String[]> timeline = new ArrayList<>();

        String sql = """

            SELECT

                e.event_time,

                ss.status_name,

                COALESCE(e.location_description, 'Not provided'),

                COALESCE(e.notes, ''),

                COALESCE(CAST(e.latitude AS CHAR), ''),

                COALESCE(CAST(e.longitude AS CHAR), '')

            FROM shipment_tracking_events e

            JOIN shipment_statuses ss ON e.status_id = ss.status_id

            JOIN shipments s ON e.shipment_id = s.shipment_id

            WHERE s.tracking_number = ?

              AND EXISTS (

                  SELECT 1

                  FROM delivery_assignments da

                  WHERE da.shipment_id = s.shipment_id

                    AND da.agent_id = ?

              )

            ORDER BY e.event_time ASC, e.event_id ASC

            """;

        try (Connection conn = DBConnection.getConnection();

             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trackingNumber.trim());

            ps.setLong(2, agentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    timeline.add(new String[] {

                        rs.getString(1),

                        rs.getString(2),

                        rs.getString(3),

                        rs.getString(4),

                        rs.getString(5),

                        rs.getString(6)

                    });

                }

            }

        }

        return timeline;

    }

    public boolean rejectDelivery(

            long agentId, long assignmentId, String reason) {

        if (reason == null || reason.trim().isEmpty()) {

            throw new IllegalArgumentException(

                "Please provide a reason for rejecting the delivery."

            );

        }

        String rejectionReason = reason.trim();

        if (rejectionReason.length() > 500) {

            throw new IllegalArgumentException(

                "Rejection reason cannot exceed 500 characters."

            );

        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                String currentStatus = null;

                String checkSql = """

                    SELECT ast.status_name

                    FROM delivery_assignments da

                    JOIN assignment_statuses ast

                      ON ast.assignment_status_id = da.assignment_status_id

                    WHERE da.assignment_id = ?

                      AND da.agent_id = ?

                    FOR UPDATE

                    """;

                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {

                    ps.setLong(1, assignmentId);

                    ps.setLong(2, agentId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            currentStatus = rs.getString("status_name");

                        }

                    }

                }

                if (currentStatus == null) {

                    conn.rollback();

                    return false;

                }

                if (!"ASSIGNED".equalsIgnoreCase(currentStatus)) {

                    conn.rollback();

                    throw new IllegalStateException(

                        "Only newly assigned deliveries can be rejected."

                    );

                }

                String updateSql = """

                    UPDATE delivery_assignments

                    SET assignment_status_id = (

                        SELECT assignment_status_id

                        FROM assignment_statuses

                        WHERE status_name = 'REJECTED'

                    ),

                    rejection_reason = ?,

                    ended_at = CURRENT_TIMESTAMP

                    WHERE assignment_id = ?

                      AND agent_id = ?

                    """;

                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {

                    ps.setString(1, rejectionReason);

                    ps.setLong(2, assignmentId);

                    ps.setLong(3, agentId);

                    if (ps.executeUpdate() != 1) {

                        conn.rollback();

                        return false;

                    }

                }

                conn.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                try {

                    conn.rollback();

                } catch (SQLException rollbackError) {

                    e.addSuppressed(rollbackError);

                }

                throw e;

            } finally {

                try {

                    conn.setAutoCommit(true);

                } catch (SQLException ignored) {

                    // Connection closes after this method.

                }

            }

        } catch (SQLException e) {

            throw new RuntimeException(

                "Failed to reject delivery: " + e.getMessage(), e

            );

        }

    }


    /**
     * Loads the profile details for a delivery agent.
     * Returned array indexes: 0 = full name, 1 = email, 2 = phone.
     */
    public String[] getAgentProfile(long agentId) throws SQLException {
        String sql = """
            SELECT u.full_name, u.email, COALESCE(da.phone, '') AS phone
            FROM delivery_agents da
            JOIN users u ON da.user_id = u.user_id
            WHERE da.agent_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, agentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                    };
                }
            }
        }

        return null;
    }

    /**
     * Updates the phone number saved for a delivery agent.
     */
    public boolean updateAgentPhoneNumber(long agentId, String phone)
            throws SQLException {
        String sql = """
            UPDATE delivery_agents
            SET phone = ?
            WHERE agent_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);
            ps.setLong(2, agentId);

            return ps.executeUpdate() > 0;
        }
    }
    public boolean updateAgentEmail(long agentId, String email)
            throws SQLException {

        String sql = """
            UPDATE users
            SET email = ?
            WHERE user_id = (
                SELECT user_id
                FROM delivery_agents
                WHERE agent_id = ?
            )
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setLong(2, agentId);

            return ps.executeUpdate() > 0;
        }
    }
}