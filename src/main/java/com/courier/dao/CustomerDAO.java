package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.courier.util.DBConnection;

public class CustomerDAO {

    public void createCustomer(Connection con, long userId, String phone)
            throws SQLException {
        String sql = "INSERT INTO customers (user_id, phone) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, phone);
            ps.executeUpdate();
        }
    }

    public long findCustomerIdByUserId(Connection con, long userId)
            throws SQLException {
        String sql = "SELECT customer_id FROM customers WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("customer_id");
                }
            }
        }
        throw new SQLException("No customer record found for user ID " + userId + ".");
    }

    /**
     * Returns customer rows for the Admin Customer Management table.
     * Each row: customer ID, name, email, phone, shipment count, account status.
     */
    public List<String[]> findCustomers(String searchText) throws SQLException {
        List<String[]> customers = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT c.customer_id,
                       u.full_name,
                       u.email,
                       COALESCE(c.phone, ''),
                       COUNT(DISTINCT s.shipment_id) AS shipment_count,
                       u.account_status
                FROM customers c
                JOIN users u ON u.user_id = c.user_id
                LEFT JOIN shipments s ON s.customer_id = c.customer_id
                """);

        boolean searching = searchText != null && !searchText.isBlank();
        if (searching) {
            sql.append("""
                     WHERE (
                         u.full_name LIKE ?
                         OR u.email LIKE ?
                         OR COALESCE(c.phone, '') LIKE ?
                         OR CAST(c.customer_id AS CHAR) LIKE ?
                         OR CONCAT('C', LPAD(c.customer_id, 3, '0')) LIKE ?
                     )
                    """);
        }

        sql.append("""
                 GROUP BY c.customer_id, u.full_name, u.email, c.phone, u.account_status
                 ORDER BY c.customer_id
                """);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            if (searching) {
                String pattern = "%" + searchText.trim() + "%";
                for (int i = 1; i <= 5; i++) {
                    ps.setString(i, pattern);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("customer_id");
                    String accountStatus = rs.getString("account_status");
                    String displayStatus = "ACTIVE".equalsIgnoreCase(accountStatus)
                            ? "Active" : "INACTIVE".equalsIgnoreCase(accountStatus)
                            ? "Inactive" : accountStatus;

                    customers.add(new String[] {
                        String.format("C%03d", id),
                        safe(rs.getString("full_name")),
                        safe(rs.getString("email")),
                        safe(rs.getString(4)),
                        String.valueOf(rs.getInt("shipment_count")),
                        safe(displayStatus)
                    });
                }
            }
        }
        return customers;
    }

    public String[] findCustomerDetails(String displayCustomerId) throws SQLException {
        long customerId = parseCustomerId(displayCustomerId);
        String sql = """
                SELECT c.customer_id, u.full_name, u.email,
                       COALESCE(c.phone, '') AS phone,
                       COUNT(DISTINCT s.shipment_id) AS shipment_count,
                       u.account_status
                FROM customers c
                JOIN users u ON u.user_id = c.user_id
                LEFT JOIN shipments s ON s.customer_id = c.customer_id
                WHERE c.customer_id = ?
                GROUP BY c.customer_id, u.full_name, u.email, c.phone, u.account_status
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("account_status");
                    return new String[] {
                        String.format("C%03d", rs.getLong("customer_id")),
                        safe(rs.getString("full_name")),
                        safe(rs.getString("email")),
                        safe(rs.getString("phone")),
                        String.valueOf(rs.getInt("shipment_count")),
                        "ACTIVE".equalsIgnoreCase(status) ? "Active"
                            : "INACTIVE".equalsIgnoreCase(status) ? "Inactive" : safe(status)
                    };
                }
            }
        }
        return null;
    }

    public void updateCustomer(String displayCustomerId, String fullName,
                               String email, String phone) throws SQLException {
        long customerId = parseCustomerId(displayCustomerId);
        String updateUser = """
                UPDATE users u
                JOIN customers c ON c.user_id = u.user_id
                SET u.full_name = ?, u.email = ?, c.phone = ?
                WHERE c.customer_id = ?
                """;

        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(updateUser)) {
                ps.setString(1, fullName.trim());
                ps.setString(2, email.trim());
                ps.setString(3, phone.trim());
                ps.setLong(4, customerId);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Customer " + displayCustomerId + " was not found.");
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public void setCustomerActive(String displayCustomerId, boolean active)
            throws SQLException {
        long customerId = parseCustomerId(displayCustomerId);
        String sql = """
                UPDATE users u
                JOIN customers c ON c.user_id = u.user_id
                SET u.account_status = ?
                WHERE c.customer_id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, active ? "ACTIVE" : "INACTIVE");
            ps.setLong(2, customerId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Customer " + displayCustomerId + " was not found.");
            }
        }
    }

    private long parseCustomerId(String displayCustomerId) throws SQLException {
        if (displayCustomerId == null) {
            throw new SQLException("Customer ID is missing.");
        }
        String digits = displayCustomerId.trim().replaceFirst("(?i)^C", "");
        try {
            long id = Long.parseLong(digits);
            if (id <= 0) {
                throw new NumberFormatException("ID must be positive");
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new SQLException("Invalid customer ID: " + displayCustomerId, ex);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
