
package com.courier.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.courier.controller.UserController;
import com.courier.util.DBConnection;

public class CustomerRegistrationTest {

    public static void main(String[] args) {

        UserController controller = new UserController();

        String email = "customer.test."
                + System.currentTimeMillis() + "@example.com";

        try {
            long userId = controller.registerCustomer(
                    "Test Customer",
                    email,
                    "9876543210",
                    "TestPass123!"
            );

            System.out.println("Customer registration successful!");
            System.out.println("Generated user ID: " + userId);

            String sql = """
                    SELECT u.user_id, u.full_name, u.email,
                           r.role_name, c.phone
                    FROM users u
                    JOIN roles r ON r.role_id = u.role_id
                    JOIN customers c ON c.user_id = u.user_id
                    WHERE u.user_id = ?
                    """;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Name: "
                                + rs.getString("full_name"));
                        System.out.println("Email: "
                                + rs.getString("email"));
                        System.out.println("Role: "
                                + rs.getString("role_name"));
                        System.out.println("Phone: "
                                + rs.getString("phone"));
                    } else {
                        System.out.println(
                                "Registration verification failed.");
                    }
                }
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Registration test failed: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}
