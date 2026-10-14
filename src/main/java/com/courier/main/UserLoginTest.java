
package com.courier.main;

import java.sql.SQLException;

import com.courier.controller.UserController;
import com.courier.model.User;

public class UserLoginTest {

    public static void main(String[] args) {

        UserController controller = new UserController();

        // Test account created by CustomerRegistrationTest
        String email = "customer.test.1792002928640@example.com";
        String correctPassword = "TestPass123!";
        String wrongPassword = "WrongPass123!";

        try {
            // Test 1: Correct credentials
            User user = controller.login(email, correctPassword);

            if (user != null) {
                System.out.println("Correct-password test: PASSED");
                System.out.println("Logged-in user: " + user.getFullName());
                System.out.println("User ID: " + user.getUserId());
                System.out.println("Role ID: " + user.getRoleId());
            } else {
                System.out.println("Correct-password test: FAILED");
            }

            // Test 2: Incorrect password
            User wrongLogin = controller.login(email, wrongPassword);

            if (wrongLogin == null) {
                System.out.println("Wrong-password test: PASSED");
            } else {
                System.out.println("Wrong-password test: FAILED");
            }

        } catch (SQLException e) {
            System.out.println("Login test encountered a database error.");
            e.printStackTrace();
        }
    }
}
