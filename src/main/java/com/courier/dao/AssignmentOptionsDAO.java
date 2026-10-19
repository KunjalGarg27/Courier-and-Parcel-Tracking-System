
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.courier.util.DBConnection;

public class AssignmentOptionsDAO {

    public List<String[]> findUnassignedShipments() throws SQLException {
        String sql = """
            SELECT
                s.shipment_id,
                s.tracking_number,
                s.receiver_name,
                COALESCE(dest.city, 'Unknown'),
                COALESCE((
                    SELECT ss.status_name
                    FROM shipment_tracking_events ste
                    JOIN shipment_statuses ss
                      ON ss.status_id = ste.status_id
                    WHERE ste.shipment_id = s.shipment_id
                    ORDER BY ste.event_time DESC, ste.event_id DESC
                    LIMIT 1
                ), 'BOOKED') AS current_status
            FROM shipments s
            LEFT JOIN addresses dest
              ON dest.address_id = s.delivery_address_id
            WHERE NOT EXISTS (
                SELECT 1
                FROM delivery_assignments da
                JOIN assignment_statuses ast
                  ON ast.assignment_status_id = da.assignment_status_id
                WHERE da.shipment_id = s.shipment_id
                  AND ast.status_name IN
                      ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')
            )
            AND COALESCE((
                SELECT ss.status_name
                FROM shipment_tracking_events ste
                JOIN shipment_statuses ss
                  ON ss.status_id = ste.status_id
                WHERE ste.shipment_id = s.shipment_id
                ORDER BY ste.event_time DESC, ste.event_id DESC
                LIMIT 1
            ), 'BOOKED') NOT IN ('DELIVERED', 'CANCELLED')
            ORDER BY s.created_at DESC
            """;

        List<String[]> shipments = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                shipments.add(new String[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }
        }

        return shipments;
    }
}
