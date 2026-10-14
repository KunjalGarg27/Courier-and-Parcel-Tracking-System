
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.courier.util.DBConnection;

public class ShipmentManagementDAO {

    public List<String[]> findAllShipments() throws SQLException {

        List<String[]> shipments = new ArrayList<>();

        String sql = """
                SELECT
                    s.shipment_id,
                    s.tracking_number,
                    s.sender_name,
                    s.receiver_name,
                    ss.status_name,
                    u.full_name AS agent_name,
                    d.assignment_id
                FROM shipments s

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

                LEFT JOIN delivery_assignments d
                    ON d.assignment_id = (
                        SELECT d2.assignment_id
                        FROM delivery_assignments d2
                        JOIN assignment_statuses ast2
                            ON ast2.assignment_status_id =
                               d2.assignment_status_id
                        WHERE d2.shipment_id = s.shipment_id
                          AND ast2.status_name IN (
                              'ASSIGNED', 'ACCEPTED', 'IN_PROGRESS'
                          )
                        ORDER BY d2.assigned_at DESC, d2.assignment_id DESC
                        LIMIT 1
                    )

                LEFT JOIN delivery_agents agent
                    ON agent.agent_id = d.agent_id

                LEFT JOIN users u
                    ON u.user_id = agent.user_id

                ORDER BY s.created_at DESC, s.shipment_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                    String.valueOf(rs.getLong("shipment_id")),
                    rs.getString("tracking_number"),
                    rs.getString("sender_name"),
                    rs.getString("receiver_name"),
                    rs.getString("status_name") == null
                            ? "UNKNOWN"
                            : rs.getString("status_name"),
                    rs.getString("agent_name") == null
                            ? "Unassigned"
                            : rs.getString("agent_name"),
                    rs.getString("assignment_id") == null
                            ? ""
                            : rs.getString("assignment_id")
                };

                shipments.add(row);
            }
        }

        return shipments;
    }
}
