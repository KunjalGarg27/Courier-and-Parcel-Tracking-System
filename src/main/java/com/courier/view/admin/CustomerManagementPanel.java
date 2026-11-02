package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.courier.dao.CustomerDAO;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;

public class CustomerManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {
        "Customer ID", "Name", "Email", "Phone", "Shipments", "Status"
    };

    private final CustomerDAO customerDAO = new CustomerDAO();
    private JTextField searchField;
    private AppTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Customer Management");
        title.setFont(FontManager.semiBold(26));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("View and manage registered customers");
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        AppCard searchCard = new AppCard();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(FontManager.semiBold(13));
        searchLabel.setForeground(AppTheme.TEXT_PRIMARY);

        searchField = new JTextField();
        searchField.setFont(FontManager.regular(13));
        searchField.setPreferredSize(new Dimension(300, 38));

        JButton searchButton = new JButton("Search");
        stylePrimaryButton(searchButton);
        JButton resetButton = new JButton("Reset");
        styleSecondaryButton(resetButton);

        searchCard.add(searchLabel);
        searchCard.add(searchField);
        searchCard.add(searchButton);
        searchCard.add(resetButton);
        mainPanel.add(searchCard, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[0][COLUMNS.length], COLUMNS) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new AppTable(new Object[0][COLUMNS.length], COLUMNS);
        customerTable.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scrollPane.getViewport().setBackground(AppTheme.CARD);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        AppCard actionCard = new AppCard();
        actionCard.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        JButton viewButton = new JButton("View Details");
        JButton editButton = new JButton("Edit");
        JButton deactivateButton = new JButton("Deactivate");

        stylePrimaryButton(viewButton);
        styleSecondaryButton(editButton);
        styleSecondaryButton(deactivateButton);

        actionCard.add(viewButton);
        actionCard.add(editButton);
        actionCard.add(deactivateButton);
        mainPanel.add(actionCard, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchCustomer());
        searchField.addActionListener(e -> searchCustomer());
        resetButton.addActionListener(e -> resetSearch());
        viewButton.addActionListener(e -> viewCustomer());
        editButton.addActionListener(e -> editCustomer());
        deactivateButton.addActionListener(e -> toggleCustomerStatus());

        loadCustomers("");
    }

    private void loadCustomers(String query) {
        try {
            List<String[]> rows = customerDAO.findCustomers(query);
            tableModel.setRowCount(0);
            for (String[] row : rows) {
                tableModel.addRow(row);
            }
            if (rows.isEmpty()) {
                setToolTipText("No customers found.");
            } else {
                setToolTipText(null);
            }
        } catch (SQLException ex) {
            showDatabaseError("Could not load customers.", ex);
        }
    }

    private void searchCustomer() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadCustomers("");
            return;
        }
        loadCustomers(searchText);
    }

    private void resetSearch() {
        searchField.setText("");
        loadCustomers("");
    }

    private int selectedModelRow() {
        int viewRow = customerTable.getSelectedRow();
        return viewRow < 0 ? -1 : customerTable.convertRowIndexToModel(viewRow);
    }

    private String selectedCustomerId(int modelRow) {
        return tableModel.getValueAt(modelRow, 0).toString();
    }

    private void viewCustomer() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "View Customer", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String[] customer = customerDAO.findCustomerDetails(selectedCustomerId(row));
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer record was not found.",
                        "View Customer", JOptionPane.WARNING_MESSAGE);
                loadCustomers(searchField.getText().trim());
                return;
            }
            String message = "Customer ID: " + customer[0]
                    + "\nName: " + customer[1]
                    + "\nEmail: " + customer[2]
                    + "\nPhone: " + customer[3]
                    + "\nTotal Shipments: " + customer[4]
                    + "\nStatus: " + customer[5];
            JOptionPane.showMessageDialog(this, message, "Customer Details",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError("Could not load customer details.", ex);
        }
    }

    private void editCustomer() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "Edit Customer", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = selectedCustomerId(row);
        String currentName = tableModel.getValueAt(row, 1).toString();
        String currentEmail = tableModel.getValueAt(row, 2).toString();
        String currentPhone = tableModel.getValueAt(row, 3).toString();

        JTextField nameField = new JTextField(currentName, 22);
        JTextField emailField = new JTextField(currentEmail, 22);
        JTextField phoneField = new JTextField(currentPhone, 22);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Full name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Edit Customer " + id, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and email are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            customerDAO.updateCustomer(id, name, email, phone);
            loadCustomers(searchField.getText().trim());
            JOptionPane.showMessageDialog(this, "Customer details updated successfully.",
                    "Customer Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError("Could not update customer. Check that the email is not already in use.", ex);
        }
    }

    private void toggleCustomerStatus() {
        int row = selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "Customer Status", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = selectedCustomerId(row);
        String name = tableModel.getValueAt(row, 1).toString();
        String status = tableModel.getValueAt(row, 5).toString();
        boolean currentlyActive = "Active".equalsIgnoreCase(status);
        boolean newActive = !currentlyActive;
        String action = newActive ? "activate" : "deactivate";

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + action + " " + name + "?",
                "Confirm " + (newActive ? "Activation" : "Deactivation"),
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.setCustomerActive(id, newActive);
            loadCustomers(searchField.getText().trim());
            JOptionPane.showMessageDialog(this, "Customer status updated successfully.",
                    "Customer Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showDatabaseError("Could not update customer status.", ex);
        }
    }

    private void showDatabaseError(String message, SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                message + "\n\nDetails: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.CARD);
        button.setBackground(AppTheme.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(125, 38));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(FontManager.semiBold(13));
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(AppTheme.SURFACE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(105, 38));
    }
}
