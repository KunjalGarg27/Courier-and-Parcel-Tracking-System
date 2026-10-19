package com.courier.view.common;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.courier.controller.UserController;
import com.courier.model.User;
import com.courier.view.customer.CustomerFrame;

public class LoginFrame extends JFrame {

    private final UserController userController = new UserController();
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        setTitle("KahinBhi - Customer Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginButton = new JButton("Login");
        form.add(new JLabel());
        form.add(loginButton);

        loginButton.addActionListener(e -> loginCustomer());

        add(form);
    }

    private void loginCustomer() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = userController.login(email, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this, "Invalid email or password, or inactive account."
                );
                return;
            }

            // Only customers should enter the customer dashboard.
            // Verify the CUSTOMER role against the shared database schema.
            JOptionPane.showMessageDialog(
                    this, "Login succeeded. Role verification is required before opening the dashboard."
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed because of a database error. Check your database connection.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

