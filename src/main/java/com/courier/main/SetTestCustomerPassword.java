package com.courier.main;

import com.courier.controller.UserController;

import javax.swing.JOptionPane;
import java.sql.SQLException;

public class SetTestCustomerPassword {

    public static void main(String[] args) {

        String email = JOptionPane.showInputDialog(
                "Enter the registered customer email:"
        );

        if (email == null || email.isBlank()) {
            return;
        }

        String password = JOptionPane.showInputDialog(
                "Enter a new test password (minimum 8 characters):"
        );

        if (password == null || password.length() < 8) {
            JOptionPane.showMessageDialog(
                    null,
                    "Password must contain at least 8 characters."
            );
            return;
        }

        try {
            UserController controller = new UserController();

            boolean updated = controller.changePassword(
                    email.trim(),
                    password
            );

            JOptionPane.showMessageDialog(
                    null,
                    updated
                            ? "Password updated successfully."
                            : "No matching user found."
            );

        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Password update failed: " + ex.getMessage()
            );
        }
    }
}

