
package com.courier.controller;

import java.sql.SQLException;

import com.courier.model.User;
import com.courier.service.UserService;

public class UserController {

    private final UserService userService = new UserService();

    public User login(String email, String password)
            throws SQLException {
        return userService.authenticate(email, password);
    }

    public boolean emailExists(String email)
            throws SQLException {
        return userService.emailExists(email);
    }

    public String hashPassword(String password) {
        return userService.hashPassword(password);
    }

    public long registerCustomer(
            String fullName,
            String email,
            String phone,
            String password) throws SQLException {
        return userService.registerCustomer(
                fullName, email, phone, password);
    }
    public boolean changePassword(String email, String newPassword)
            throws SQLException {
        return userService.changePassword(email, newPassword);
    }
    public User loginCustomer(String email, String password)
            throws SQLException {
        return userService.authenticateCustomer(email, password);
    }
    public User loginAsRole(String email, String password, String roleName)
            throws SQLException {
        return userService.authenticateAsRole(email, password, roleName);
    }
}
