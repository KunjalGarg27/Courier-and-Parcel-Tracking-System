
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                agents.add(new String[]{
                    String.valueOf(rs.getLong("agent_id")),
                    rs.getString("full_name"),
                    rs.getString("phone") == null
                        ? "Not provided" : rs.getString("phone"),
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
            SELECT
                latitude,
                longitude,
                accuracy_m,
                recorded_at
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
                    java.sql.Timestamp recordedAt =
                            rs.getTimestamp("recorded_at");

                    java.math.BigDecimal accuracy =
                            rs.getBigDecimal("accuracy_m");

                    return new String[] {
                        rs.getBigDecimal("latitude").toPlainString(),
                        rs.getBigDecimal("longitude").toPlainString(),
                        accuracy == null
                                ? "Not available"
                                : accuracy.toPlainString() + " m",
                        recordedAt == null
                                ? "Not available"
                                : recordedAt.toString()
                    };
                }
            }
        }

        return null;
    }
}
