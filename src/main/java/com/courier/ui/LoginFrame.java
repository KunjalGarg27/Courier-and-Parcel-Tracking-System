package com.courier.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    // ===== THEME COLORS =====
    private static final Color CREAM = new Color(247, 246, 238);
    private static final Color WHITE = Color.WHITE;
    private static final Color OLIVE = new Color(111, 143, 82);
    private static final Color LIGHT_GREEN = new Color(232, 240, 223);
    private static final Color DARK_TEXT = new Color(38, 50, 56);
    private static final Color MUTED_TEXT = new Color(105, 117, 105);
    private static final Color BORDER = new Color(226, 229, 218);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {

        setTitle("Courier & Parcel Tracking System - Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CREAM);

        JPanel loginCard = new JPanel();
        loginCard.setPreferredSize(new Dimension(430, 500));
        loginCard.setBackground(WHITE);
        loginCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(35, 40, 35, 40)
                )
        );

        loginCard.setLayout(
                new BoxLayout(loginCard, BoxLayout.Y_AXIS)
        );

        // ================= LOGO / TITLE =================

        JLabel logo = new JLabel("▣");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 35));
        logo.setForeground(OLIVE);

        loginCard.add(logo);

        loginCard.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel(
                "Courier & Parcel Tracking System"
        );

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(
                new Font("SansSerif", Font.BOLD, 20)
        );
        title.setForeground(DARK_TEXT);

        loginCard.add(title);

        loginCard.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel(
                "Sign in to continue"
        );

        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        subtitle.setForeground(MUTED_TEXT);

        loginCard.add(subtitle);

        loginCard.add(Box.createVerticalStrut(30));

        // ================= USERNAME =================

        JLabel usernameLabel = createLabel("User ID / Email");

        loginCard.add(usernameLabel);
        loginCard.add(Box.createVerticalStrut(7));

        usernameField = new JTextField();

        styleTextField(usernameField);

        loginCard.add(usernameField);

        loginCard.add(Box.createVerticalStrut(18));

        // ================= PASSWORD =================

        JLabel passwordLabel = createLabel("Password");

        loginCard.add(passwordLabel);
        loginCard.add(Box.createVerticalStrut(7));

        passwordField = new JPasswordField();

        styleTextField(passwordField);

        loginCard.add(passwordField);

        loginCard.add(Box.createVerticalStrut(18));

        // ================= ROLE =================

        JLabel roleLabel = createLabel("Role");

        loginCard.add(roleLabel);
        loginCard.add(Box.createVerticalStrut(7));

        String[] roles = {
                "Customer",
                "Delivery Agent",
                "Admin"
        };

        roleBox = new JComboBox<>(roles);

        roleBox.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        roleBox.setBackground(WHITE);
        roleBox.setForeground(DARK_TEXT);
        roleBox.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        roleBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        loginCard.add(roleBox);

        loginCard.add(Box.createVerticalStrut(15));

        // ================= REMEMBER ME =================

        JCheckBox rememberMe =
                new JCheckBox("Remember me");

        rememberMe.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );

        rememberMe.setForeground(MUTED_TEXT);
        rememberMe.setBackground(WHITE);
        rememberMe.setFocusPainted(false);
        rememberMe.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginCard.add(rememberMe);

        loginCard.add(Box.createVerticalStrut(20));

        // ================= LOGIN BUTTON =================

        JButton loginButton = new JButton("Login →");

        loginButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginButton.setPreferredSize(
                new Dimension(330, 45)
        );

        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 45)
        );

        loginButton.setBackground(OLIVE);
        loginButton.setForeground(Color.WHITE);

        loginButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);

        loginCard.add(loginButton);

        loginCard.add(Box.createVerticalStrut(18));

        // ================= DEMO CREDENTIALS =================

        JLabel demo = new JLabel(
                "Phase 1 Demo: customer / customer123"
        );

        demo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        demo.setFont(
                new Font("SansSerif", Font.PLAIN, 11)
        );

        demo.setForeground(MUTED_TEXT);

        loginCard.add(demo);

        // ================= LOGIN ACTION =================

        loginButton.addActionListener(e -> authenticate());

        // Press Enter to login
        passwordField.addActionListener(e -> authenticate());

        mainPanel.add(loginCard);

        add(mainPanel);
    }

    // =========================================================
    // AUTHENTICATION
    // =========================================================

    private void authenticate() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        String role =
                (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter User ID and Password.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ================= CUSTOMER =================

        if (role.equals("Customer")
                && username.equals("customer")
                && password.equals("customer123")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login successful!",
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE
            );

            openCustomerDashboard();

        }

        // ================= OTHER ROLES =================

        else if (role.equals("Delivery Agent")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Delivery Agent portal will be connected later.",
                    "Phase 1",
                    JOptionPane.INFORMATION_MESSAGE
            );

        }

        else if (role.equals("Admin")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Admin portal will be connected later.",
                    "Phase 1",
                    JOptionPane.INFORMATION_MESSAGE
            );

        }

        else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid User ID, Password or Role.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // OPEN CUSTOMER DASHBOARD
    // =========================================================

    private void openCustomerDashboard() {

        CustomerDashboard dashboard =
                new CustomerDashboard();

        dashboard.setVisible(true);

        dispose();
    }

    // =========================================================
    // TEXT FIELD STYLE
    // =========================================================

    private void styleTextField(
            JTextField field
    ) {

        field.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        field.setForeground(DARK_TEXT);
        field.setBackground(WHITE);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(10, 12, 10, 12)
                )
        );

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
    }

    // =========================================================
    // LABEL
    // =========================================================

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        label.setForeground(DARK_TEXT);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LoginFrame login =
                    new LoginFrame();

            login.setVisible(true);
        });
    }
}
