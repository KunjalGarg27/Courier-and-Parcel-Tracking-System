
package com.courier.view.agent;

import com.courier.dao.DeliveryAgentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class AgentSettingsPanel extends JPanel {

    private final DeliveryAgentDAO dao;
    private final long agentId;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton saveButton;

    private String originalEmail = "";
    private String originalPhone = "";

    public AgentSettingsPanel(long agentId) {
        this.agentId = agentId;
        this.dao = new DeliveryAgentDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(250, 248, 243));
        setBorder(new EmptyBorder(30, 35, 30, 35));

        buildUI();
        loadAgentProfile();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(250, 248, 243));

        JLabel heading = new JLabel("Agent Settings");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(new Color(55, 65, 45));

        JLabel subtitle = new JLabel(
                "Manage your account contact information."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(new Color(250, 248, 243));
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(7));
        headingPanel.add(subtitle);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 0);

        nameField = createField();
        nameField.setText("Lovedeep Singh");
        nameField.setEditable(false);
        nameField.setBackground(new Color(240, 240, 240));

        emailField = createField();
        emailField.setEditable(true);

        phoneField = createField();
        phoneField.setEditable(true);

        addFormRow(formPanel, gbc, "Full Name", nameField);
        addFormRow(formPanel, gbc, "Email Address", emailField);
        addFormRow(formPanel, gbc, "Phone Number", phoneField);

        JLabel note = new JLabel(
                "Your name is system-managed. Email and phone are editable."
        );
        note.setFont(new Font("SansSerif", Font.ITALIC, 12));
        note.setForeground(new Color(100, 100, 100));

        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(85, 107, 47));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        saveButton.setBorder(new EmptyBorder(12, 22, 12, 22));
        saveButton.addActionListener(e -> saveProfile());

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 15));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(0, 25, 25, 25));
        bottomPanel.add(note, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225))
        );
        card.add(formPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(headingPanel, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            String labelText,
            JTextField field
    ) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(55, 55, 55));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1;
        gbc.insets = new Insets(12, 0, 4, 0);
        panel.add(label, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(field, gbc);
    }

    private JTextField createField() {
        JTextField field = new JTextField(30);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void loadAgentProfile() {
        try {
            String[] profile = dao.getAgentProfile(agentId);

            if (profile == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Agent profile could not be found.",
                        "Profile Error",
                        JOptionPane.ERROR_MESSAGE
                );
                saveButton.setEnabled(false);
                return;
            }

            // The displayed name is fixed as requested.
            nameField.setText("Lovedeep Singh");

            String email = profile[1] == null ? "" : profile[1];
            String phone = profile[2] == null ? "" : profile[2];

            emailField.setText(email);
            phoneField.setText(phone);

            originalEmail = email;
            originalPhone = phone;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load agent profile:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            saveButton.setEnabled(false);
        }
    }

    private void saveProfile() {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty()) {
            showValidationError("Email address cannot be empty.");
            emailField.requestFocusInWindow();
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            showValidationError("Please enter a valid email address.");
            emailField.requestFocusInWindow();
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            showValidationError(
                    "Phone number must contain exactly 10 digits."
            );
            phoneField.requestFocusInWindow();
            return;
        }

        boolean emailChanged = !email.equals(originalEmail);
        boolean phoneChanged = !phone.equals(originalPhone);

        if (!emailChanged && !phoneChanged) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no changes to save.",
                    "No Changes",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        saveButton.setEnabled(false);

        try {
            boolean emailUpdated = true;
            boolean phoneUpdated = true;

            if (emailChanged) {
                emailUpdated = dao.updateAgentEmail(agentId, email);
            }

            if (phoneChanged) {
                phoneUpdated = dao.updateAgentPhoneNumber(agentId, phone);
            }

            if (emailUpdated && phoneUpdated) {
                originalEmail = email;
                originalPhone = phone;

                JOptionPane.showMessageDialog(
                        this,
                        "Your profile has been updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                loadAgentProfile();

                JOptionPane.showMessageDialog(
                        this,
                        "Some changes could not be saved. "
                                + "Please check your profile and try again.",
                        "Update Incomplete",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update profile.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            saveButton.setEnabled(true);
        }
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
