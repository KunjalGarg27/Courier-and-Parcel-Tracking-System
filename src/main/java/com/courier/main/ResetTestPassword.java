package com.courier.main;

import com.courier.util.DBConnection;
import com.courier.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ResetTestPassword {

    public static void main(String[] args) throws Exception {

        String email = "customer.test.1792002928640@example.com";
        String newPassword = "CustomerTest2026!";

        UserService userService = new UserService();
        String hashedPassword = userService.hashPassword(newPassword);

        String sql =
                "UPDATE users SET password_hash = ? " +
                        "WHERE email = ? AND role_id = 2 AND account_status = 'ACTIVE'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashedPassword);
            statement.setString(2, email);

            int updated = statement.executeUpdate();

            if (updated == 1) {
                System.out.println("Test customer password updated successfully.");
                System.out.println("Email: " + email);
                System.out.println("New password: " + newPassword);
            } else {
                System.out.println("No matching active customer found. Password not changed.");
            }
        }
    }
}

