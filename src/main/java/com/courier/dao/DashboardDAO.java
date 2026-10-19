
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.courier.util.DBConnection;

public class DashboardDAO {

    public Map<String, Integer> getShipmentStatistics() throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS total_shipments,

                SUM(CASE WHEN COALESCE(
                    (SELECT ss.status_name
                     FROM shipment_tracking_events ste
                     JOIN shipment_statuses ss
                       ON ss.status_id = ste.status_id
                     WHERE ste.shipment_id = s.shipment_id
                     ORDER BY ste.event_time DESC, ste.event_id DESC
                     LIMIT 1),
                    'BOOKED'
                ) = 'IN_TRANSIT' THEN 1 ELSE 0 END) AS in_transit,

                SUM(CASE WHEN COALESCE(
                    (SELECT ss.status_name
                     FROM shipment_tracking_events ste
                     JOIN shipment_statuses ss
                       ON ss.status_id = ste.status_id
                     WHERE ste.shipment_id = s.shipment_id
                     ORDER BY ste.event_time DESC, ste.event_id DESC
                     LIMIT 1),
                    'BOOKED'
                ) = 'DELIVERED' THEN 1 ELSE 0 END) AS delivered,

                SUM(CASE WHEN COALESCE(
                    (SELECT ss.status_name
                     FROM shipment_tracking_events ste
                     JOIN shipment_statuses ss
                       ON ss.status_id = ste.status_id
                     WHERE ste.shipment_id = s.shipment_id
                     ORDER BY ste.event_time DESC, ste.event_id DESC
                     LIMIT 1),
                    'BOOKED'
                ) IN ('BOOKED', 'ASSIGNED')
                THEN 1 ELSE 0 END) AS pending

            FROM shipments s
            """;

        Map<String, Integer> stats = new LinkedHashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("total", rs.getInt("total_shipments"));
                stats.put("inTransit", rs.getInt("in_transit"));
                stats.put("delivered", rs.getInt("delivered"));
                stats.put("pending", rs.getInt("pending"));
            }
        }

        return stats;
    }
}
