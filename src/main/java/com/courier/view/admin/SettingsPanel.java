package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;

public class SettingsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextField emailField;
    private JCheckBox notificationsBox;
    private JCheckBox refreshBox;

    public SettingsPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // ==========================================
        // TITLE
        // ==========================================

        JPanel titlePanel =
            new JPanel(new GridLayout(2, 1, 0, 3));

        titlePanel.setOpaque(false);

        JLabel title =
            new JLabel("Settings");

        title.setFont(
            FontManager.semiBold(26)
        );

        title.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        JLabel subtitle =
            new JLabel(
                "Manage administrator account and system preferences"
            );

        subtitle.setFont(
            FontManager.regular(13)
        );

        subtitle.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // MAIN CONTENT
        // ==========================================

        JPanel mainPanel =
            new JPanel(new GridLayout(1, 2, 20, 0));

        mainPanel.setOpaque(false);

        // ==========================================
        // ACCOUNT SETTINGS
        // ==========================================

        AppCard accountCard =
            new AppCard();

        accountCard.setLayout(
            new GridLayout(0, 1, 0, 12)
        );

        JLabel accountTitle =
            new JLabel("Account Information");

        accountTitle.setFont(
            FontManager.semiBold(17)
        );

        accountTitle.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        accountCard.add(accountTitle);

        JLabel nameLabel =
            new JLabel("Name");

        nameLabel.setFont(
            FontManager.medium(13)
        );

        nameField =
            new JTextField("Kunjal");

        nameField.setFont(
            FontManager.regular(13)
        );

        JLabel emailLabel =
            new JLabel("Email");

        emailLabel.setFont(
            FontManager.medium(13)
        );

        emailField =
            new JTextField("admin@courier.com");

        emailField.setFont(
            FontManager.regular(13)
        );

        JLabel roleLabel =
            new JLabel("Role: Administrator");

        roleLabel.setFont(
            FontManager.medium(13)
        );

        roleLabel.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        accountCard.add(nameLabel);
        accountCard.add(nameField);
        accountCard.add(emailLabel);
        accountCard.add(emailField);
        accountCard.add(roleLabel);

        JButton saveButton =
            new JButton("Save Changes");

        stylePrimaryButton(
            saveButton,
            130
        );

        accountCard.add(saveButton);

        // ==========================================
        // SYSTEM SETTINGS
        // ==========================================

        AppCard systemCard =
            new AppCard();

        systemCard.setLayout(
            new GridLayout(0, 1, 0, 12)
        );

        JLabel systemTitle =
            new JLabel("System Preferences");

        systemTitle.setFont(
            FontManager.semiBold(17)
        );

        systemTitle.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        systemCard.add(systemTitle);

        notificationsBox =
            new JCheckBox(
                "Enable Notifications",
                true
            );

        notificationsBox.setFont(
            FontManager.regular(13)
        );

        notificationsBox.setOpaque(false);

        refreshBox =
            new JCheckBox(
                "Enable Automatic Refresh",
                true
            );

        refreshBox.setFont(
            FontManager.regular(13)
        );

        refreshBox.setOpaque(false);

        systemCard.add(notificationsBox);
        systemCard.add(refreshBox);

        JButton passwordButton =
            new JButton("Change Password");

        styleSecondaryButton(
            passwordButton,
            140
        );

        systemCard.add(passwordButton);

        JButton logoutButton =
            new JButton("Logout");

        styleSecondaryButton(
            logoutButton,
            100
        );

        systemCard.add(logoutButton);

        mainPanel.add(accountCard);
        mainPanel.add(systemCard);

        add(
            mainPanel,
            BorderLayout.CENTER
        );

        // ==========================================
        // EVENT HANDLING
        // ==========================================

        saveButton.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                this,
                "Account settings saved successfully.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        notificationsBox.addActionListener(e -> {

            String status =
                notificationsBox.isSelected()
                ? "enabled"
                : "disabled";

            JOptionPane.showMessageDialog(
                this,
                "Notifications " + status + ".",
                "Notifications",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        refreshBox.addActionListener(e -> {

            String status =
                refreshBox.isSelected()
                ? "enabled"
                : "disabled";

            JOptionPane.showMessageDialog(
                this,
                "Automatic refresh " + status + ".",
                "Auto Refresh",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        passwordButton.addActionListener(
            e -> showChangePasswordDialog()
        );

        logoutButton.addActionListener(
            e -> {

                int choice =
                    JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                    );

                if (choice ==
                    JOptionPane.YES_OPTION) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Logout event triggered.",
                        "Logout",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        );
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    private void showChangePasswordDialog() {

        JPanel panel =
            new JPanel(
                new GridLayout(3, 2, 8, 8)
            );

        JPasswordField currentPassword =
            new JPasswordField();

        JPasswordField newPassword =
            new JPasswordField();

        JPasswordField confirmPassword =
            new JPasswordField();

        panel.add(
            new JLabel("Current Password:")
        );

        panel.add(currentPassword);

        panel.add(
            new JLabel("New Password:")
        );

        panel.add(newPassword);

        panel.add(
            new JLabel("Confirm Password:")
        );

        panel.add(confirmPassword);

        int result =
            JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION
            );

        if (result ==
            JOptionPane.OK_OPTION) {

            String newPass =
                new String(
                    newPassword.getPassword()
                );

            String confirmPass =
                new String(
                    confirmPassword.getPassword()
                );

            if (newPass.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "New password cannot be empty.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE
                );

            } else if (
                !newPass.equals(confirmPass)
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Password updated successfully.",
                    "Password",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }

    // ==========================================
    // BUTTON STYLING
    // ==========================================

    private void stylePrimaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.CARD
        );

        button.setBackground(
            AppTheme.PRIMARY
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }

    private void styleSecondaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        button.setBackground(
            AppTheme.SURFACE
        );

        button.setFocusPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }
}