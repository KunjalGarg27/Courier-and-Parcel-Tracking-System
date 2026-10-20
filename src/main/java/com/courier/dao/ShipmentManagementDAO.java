
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
                    COALESCE(customer_user.full_name, 'Unknown')
                        AS customer_name,
                    COALESCE(pickup.city, 'Unknown') AS origin,
                    COALESCE(delivery.city, 'Unknown') AS destination,
                    COALESCE(agent_user.full_name, 'Unassigned')
                        AS agent_name,
                    COALESCE(current_status.status_name, 'BOOKED')
                        AS status
                FROM shipments s

                LEFT JOIN customers c
                    ON c.customer_id = s.customer_id
                LEFT JOIN users customer_user
                    ON customer_user.user_id = c.user_id

                LEFT JOIN addresses pickup
                    ON pickup.address_id = s.pickup_address_id
                LEFT JOIN addresses delivery
                    ON delivery.address_id = s.delivery_address_id

                LEFT JOIN shipment_tracking_events latest_event
                    ON latest_event.event_id = (
                        SELECT e.event_id
                        FROM shipment_tracking_events e
                        WHERE e.shipment_id = s.shipment_id
                        ORDER BY e.event_time DESC, e.event_id DESC
                        LIMIT 1
                    )
                LEFT JOIN shipment_statuses current_status
                    ON current_status.status_id = latest_event.status_id

                LEFT JOIN delivery_assignments active_assignment
                    ON active_assignment.assignment_id = (
                        SELECT da.assignment_id
                        FROM delivery_assignments da
                        JOIN assignment_statuses ast
                            ON ast.assignment_status_id =
                               da.assignment_status_id
                        WHERE da.shipment_id = s.shipment_id
                          AND ast.status_name IN (
                              'ASSIGNED', 'ACCEPTED', 'IN_PROGRESS'
                          )
                        ORDER BY da.assigned_at DESC, da.assignment_id DESC
                        LIMIT 1
                    )
                LEFT JOIN delivery_agents agent
                    ON agent.agent_id = active_assignment.agent_id
                LEFT JOIN users agent_user
                    ON agent_user.user_id = agent.user_id

                ORDER BY s.created_at DESC, s.shipment_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                shipments.add(new String[] {
                    rs.getString("shipment_id"),
                    rs.getString("tracking_number"),
                    rs.getString("customer_name"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("agent_name"),
                    rs.getString("status")
                });
            }
        }

        return shipments;
    }
}
