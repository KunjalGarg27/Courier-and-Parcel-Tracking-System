package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    public void createCustomer(
            Connection con,
            long userId,
            String phone) throws SQLException {

        String sql = """
                INSERT INTO customers (user_id, phone)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, phone);
            ps.executeUpdate();
        }
    }

    // shipments.customer_id points to customers.customer_id (NOT users.user_id).
    public long findCustomerIdByUserId(
            Connection con,
            long userId) throws SQLException {

        String sql = "SELECT customer_id FROM customers WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("customer_id");
                }
            }
        }

        throw new SQLException(
                "No customer record found for user ID " + userId + ".");
    }
}