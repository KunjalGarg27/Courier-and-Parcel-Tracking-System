
package com.courier.main;

import java.sql.SQLException;

import com.courier.controller.UserController;

public class DatabaseTest {

    public static void main(String[] args) {

        UserController controller = new UserController();

        try {
            boolean exists = controller.emailExists(
                    "nonexistent-test@kahinbhi.local"
            );

            System.out.println("User module is working!");
            System.out.println(
                    "Test email already exists: " + exists
            );

        } catch (SQLException e) {
            System.out.println("User module test failed!");
            e.printStackTrace();
        }
    }
}
