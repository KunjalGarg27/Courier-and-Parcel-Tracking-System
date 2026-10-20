package com.courier.view.common;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

    // Labels shown on screen and the matching role_name values in the database.
    private static final String[] ROLE_LABELS =
            {"Customer", "Admin", "Delivery Agent"};
    private static final String[] ROLE_NAMES =
            {"CUSTOMER", "ADMIN", "DELIVERY_AGENT"};

    private final UserController userController = new UserController();
    private final JComboBox<String> roleBox = new JComboBox<>(ROLE_LABELS);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        setTitle("KahinBhi - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        form.add(new JLabel("Login as:"));
        form.add(roleBox);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginButton = new JButton("Login");
        form.add(new JLabel());
        form.add(loginButton);

        loginButton.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);

        add(form);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String roleName = ROLE_NAMES[roleBox.getSelectedIndex()];

        try {
            User user = userController.loginAsRole(email, password, roleName);

            if (user == null) {
                // Same message for every failure, so it does not reveal
                // whether the email exists or which part was wrong.
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid email, password or role, "
                                + "or the account is inactive.",
                        "Login Failed",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            openDashboard(user, roleName);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed because of a database error. "
                            + "Check your database connection.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openDashboard(User user, String roleName) {
        switch (roleName) {

            case "CUSTOMER" -> {
                new CustomerFrame(user).setVisible(true);
                dispose();
            }

            // TODO (Kunjal): open the Admin dashboard here, e.g.
            //   new AdminFrame(user).setVisible(true); dispose();
            case "ADMIN" -> showNotConnected("Admin");

            // TODO (Lovedeep): open the Agent dashboard here.
            case "DELIVERY_AGENT" -> showNotConnected("Delivery Agent");

            default -> showNotConnected(roleName);
        }
    }

    private void showNotConnected(String roleLabel) {
        JOptionPane.showMessageDialog(
                this,
                "Login succeeded, but the " + roleLabel
                        + " dashboard is not connected to this screen yet.",
                "Not Available Yet",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}