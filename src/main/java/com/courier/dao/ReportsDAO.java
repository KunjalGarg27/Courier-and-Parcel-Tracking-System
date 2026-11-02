
package com.courier.dao;

import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    public record Summary(long total, long delivered,
                          long inTransit, long pending) {}

    public record MonthCount(String month, long count) {}

    public record ReportRow(String[] values) {}

    public Summary getSummary() throws SQLException {
        String sql = """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(status_name = 'DELIVERED'), 0) AS delivered,
                COALESCE(SUM(status_name IN
                    ('PICKED_UP','IN_TRANSIT','OUT_FOR_DELIVERY')), 0)
                    AS in_transit,
                COALESCE(SUM(status_name IN
                    ('BOOKED','ASSIGNED')), 0) AS pending
            FROM (
                SELECT s.shipment_id,
                       COALESCE(
                           (SELECT ss.status_name
                            FROM shipment_tracking_events ste
                            JOIN shipment_statuses ss
                              ON ss.status_id = ste.status_id
                            WHERE ste.shipment_id = s.shipment_id
                            ORDER BY ste.event_time DESC, ste.event_id DESC
                            LIMIT 1),
                           'BOOKED'
                       ) AS status_name
                FROM shipments s
            ) latest
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Summary(
                    rs.getLong("total"),
                    rs.getLong("delivered"),
                    rs.getLong("in_transit"),
                    rs.getLong("pending")
                );
            }
        }

        return new Summary(0, 0, 0, 0);
    }

    public List<MonthCount> getMonthlyShipments() throws SQLException {
        List<MonthCount> result = new ArrayList<>();

        YearMonth current = YearMonth.now();
        YearMonth start = current.minusMonths(11);

        String sql = """
            SELECT DATE_FORMAT(created_at, '%Y-%m') AS month_key,
                   COUNT(*) AS shipment_count
            FROM shipments
            WHERE created_at >= ?
              AND created_at < ?
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month_key
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(
                start.atDay(1).atStartOfDay()));
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(
                current.plusMonths(1).atDay(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                java.util.Map<String, Long> counts =
                    new java.util.HashMap<>();

                while (rs.next()) {
                    counts.put(rs.getString("month_key"),
                               rs.getLong("shipment_count"));
                }

                for (int i = 0; i < 12; i++) {
                    YearMonth month = start.plusMonths(i);
                    String key = month.toString();

                    result.add(new MonthCount(
                        month.getMonth().name().substring(0, 1)
                            + month.getMonth().name().substring(1, 3)
                                .toLowerCase(),
                        counts.getOrDefault(key, 0L)
                    ));
                }
            }
        }

        return result;
    }

    public List<ReportRow> getReport(String type) throws SQLException {
        List<ReportRow> rows = new ArrayList<>();

        String sql;

        switch (type) {
            case "Delivery Performance" -> sql = """
                SELECT s.tracking_number,
                       COALESCE(ss.status_name, 'BOOKED') AS status,
                       s.created_at,
                       s.delivered_at,
                       CASE
                           WHEN s.delivered_at IS NULL THEN 'Not delivered'
                           WHEN s.expected_delivery_at IS NULL THEN 'No ETA'
                           WHEN s.delivered_at <= s.expected_delivery_at
                               THEN 'On time'
                           ELSE 'Late'
                       END AS performance
                FROM shipments s
                LEFT JOIN (
                    SELECT shipment_id, MAX(event_id) AS event_id
                    FROM shipment_tracking_events
                    GROUP BY shipment_id
                ) latest ON latest.shipment_id = s.shipment_id
                LEFT JOIN shipment_tracking_events ste
                    ON ste.event_id = latest.event_id
                LEFT JOIN shipment_statuses ss
                    ON ss.status_id = ste.status_id
                ORDER BY s.created_at DESC
                """;

            case "Shipment Status Summary" -> sql = """
                SELECT status, COUNT(*) AS shipment_count
                FROM (
                    SELECT s.shipment_id,
                           COALESCE(
                               (SELECT ss.status_name
                                FROM shipment_tracking_events ste
                                JOIN shipment_statuses ss
                                  ON ss.status_id = ste.status_id
                                WHERE ste.shipment_id = s.shipment_id
                                ORDER BY ste.event_time DESC, ste.event_id DESC
                                LIMIT 1),
                               'BOOKED'
                           ) AS status
                    FROM shipments s
                ) x
                GROUP BY status
                ORDER BY shipment_count DESC
                """;

            case "Agent Performance" -> sql = """
                SELECT u.full_name AS agent,
                       COUNT(DISTINCT a.shipment_id) AS assigned_shipments,
                       SUM(ast.status_name = 'COMPLETED') AS completed
                FROM delivery_agents da
                JOIN users u ON u.user_id = da.user_id
                LEFT JOIN delivery_assignments a
                    ON a.agent_id = da.agent_id
                LEFT JOIN assignment_statuses ast
                    ON ast.assignment_status_id = a.assignment_status_id
                GROUP BY da.agent_id, u.full_name
                ORDER BY assigned_shipments DESC
                """;

            default -> sql = """
                SELECT DATE_FORMAT(created_at, '%Y-%m') AS month,
                       COUNT(*) AS shipments
                FROM shipments
                GROUP BY DATE_FORMAT(created_at, '%Y-%m')
                ORDER BY month DESC
                """;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] values = new String[columns];

                for (int i = 1; i <= columns; i++) {
                    Object value = rs.getObject(i);
                    values[i - 1] = value == null
                        ? "" : value.toString();
                }

                rows.add(new ReportRow(values));
            }
        }

        return rows;
    }
}
