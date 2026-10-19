
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
