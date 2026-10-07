package com.courier.view.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.AppTable;

public class CustomerManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField searchField;
    private AppTable customerTable;

    public CustomerManagementPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // ==========================================
        // PAGE TITLE
        // ==========================================

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Customer Management");
        title.setFont(FontManager.semiBold(26));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
            "View and manage registered customers"
        );
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // MAIN CONTENT
        // ==========================================

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setOpaque(false);

        // ==========================================
        // SEARCH BAR
        // ==========================================

        AppCard searchCard = new AppCard();

        searchCard.setLayout(
            new FlowLayout(
                FlowLayout.LEFT,
                12,
                12
            )
        );

        JLabel searchLabel = new JLabel("Search:");

        searchLabel.setFont(
            FontManager.semiBold(13)
        );

        searchLabel.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        searchField = new JTextField();

        searchField.setFont(
            FontManager.regular(13)
        );

        searchField.setPreferredSize(
            new Dimension(300, 38)
        );

        JButton searchButton =
            new JButton("Search");

        stylePrimaryButton(searchButton);

        JButton resetButton =
            new JButton("Reset");

        styleSecondaryButton(resetButton);

        searchCard.add(searchLabel);
        searchCard.add(searchField);
        searchCard.add(searchButton);
        searchCard.add(resetButton);

        mainPanel.add(
            searchCard,
            BorderLayout.NORTH
        );

        // ==========================================
        // CUSTOMER TABLE
        // ==========================================

        String[] columns = {
            "Customer ID",
            "Name",
            "Email",
            "Phone",
            "Shipments",
            "Status"
        };

        Object[][] data = {

            {
                "C001",
                "Rahul Sharma",
                "rahul@gmail.com",
                "9876543210",
                "5",
                "Active"
            },

            {
                "C002",
                "Priya Mehta",
                "priya@gmail.com",
                "9876501234",
                "3",
                "Active"
            },

            {
                "C003",
                "Aman Verma",
                "aman@gmail.com",
                "9812345678",
                "7",
                "Active"
            },

            {
                "C004",
                "Simran Kaur",
                "simran@gmail.com",
                "9898765432",
                "2",
                "Inactive"
            },

            {
                "C005",
                "Arjun Gupta",
                "arjun@gmail.com",
                "9123456789",
                "9",
                "Active"
            }
        };

        customerTable =
            new AppTable(data, columns);

        JScrollPane scrollPane =
            new JScrollPane(customerTable);

        scrollPane.setBorder(
            BorderFactory.createLineBorder(
                AppTheme.BORDER
            )
        );

        scrollPane.getViewport()
                  .setBackground(AppTheme.CARD);

        mainPanel.add(
            scrollPane,
            BorderLayout.CENTER
        );

        // ==========================================
        // ACTION BUTTONS
        // ==========================================

        AppCard actionCard = new AppCard();

        actionCard.setLayout(
            new FlowLayout(
                FlowLayout.RIGHT,
                12,
                10
            )
        );

        JButton viewButton =
            new JButton("View Details");

        JButton editButton =
            new JButton("Edit");

        JButton deactivateButton =
            new JButton("Deactivate");

        stylePrimaryButton(viewButton);
        styleSecondaryButton(editButton);
        styleSecondaryButton(deactivateButton);

        actionCard.add(viewButton);
        actionCard.add(editButton);
        actionCard.add(deactivateButton);

        mainPanel.add(
            actionCard,
            BorderLayout.SOUTH
        );

        add(
            mainPanel,
            BorderLayout.CENTER
        );

        // ==========================================
        // EVENT HANDLING
        // ==========================================

        searchButton.addActionListener(
            e -> searchCustomer()
        );

        resetButton.addActionListener(
            e -> resetSearch()
        );

        viewButton.addActionListener(
            e -> viewCustomer()
        );

        editButton.addActionListener(
            e -> editCustomer()
        );

        deactivateButton.addActionListener(
            e -> deactivateCustomer()
        );
    }

    // ==========================================
    // SEARCH CUSTOMER
    // ==========================================

    private void searchCustomer() {

        String searchText =
            searchField.getText().trim();

        if (searchText.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a customer name, ID or email.",
                "Search Customer",
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
            this,
            "Searching customers for: "
                + searchText,
            "Customer Search",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // RESET SEARCH
    // ==========================================

    private void resetSearch() {

        searchField.setText("");

        JOptionPane.showMessageDialog(
            this,
            "Search has been reset.",
            "Reset",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // VIEW CUSTOMER
    // ==========================================

    private void viewCustomer() {

        int selectedRow =
            customerTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a customer first.",
                "View Customer",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String customerId =
            customerTable
                .getValueAt(selectedRow, 0)
                .toString();

        String name =
            customerTable
                .getValueAt(selectedRow, 1)
                .toString();

        String email =
            customerTable
                .getValueAt(selectedRow, 2)
                .toString();

        String phone =
            customerTable
                .getValueAt(selectedRow, 3)
                .toString();

        String shipments =
            customerTable
                .getValueAt(selectedRow, 4)
                .toString();

        String status =
            customerTable
                .getValueAt(selectedRow, 5)
                .toString();

        String message =
            "Customer ID: " + customerId
            + "\nName: " + name
            + "\nEmail: " + email
            + "\nPhone: " + phone
            + "\nTotal Shipments: " + shipments
            + "\nStatus: " + status;

        JOptionPane.showMessageDialog(
            this,
            message,
            "Customer Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // EDIT CUSTOMER
    // ==========================================

    private void editCustomer() {

        int selectedRow =
            customerTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a customer first.",
                "Edit Customer",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String customerName =
            customerTable
                .getValueAt(selectedRow, 1)
                .toString();

        JOptionPane.showMessageDialog(
            this,
            "Edit form for "
                + customerName
                + " will open here.",
            "Edit Customer",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==========================================
    // DEACTIVATE CUSTOMER
    // ==========================================

    private void deactivateCustomer() {

        int selectedRow =
            customerTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a customer first.",
                "Deactivate Customer",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String customerName =
            customerTable
                .getValueAt(selectedRow, 1)
                .toString();

        int choice =
            JOptionPane.showConfirmDialog(
                this,
                "Deactivate customer "
                    + customerName
                    + "?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION
            );

        if (choice ==
            JOptionPane.YES_OPTION) {

            customerTable.setValueAt(
                "Inactive",
                selectedRow,
                5
            );

            JOptionPane.showMessageDialog(
                this,
                "Customer "
                    + customerName
                    + " has been deactivated.",
                "Customer Updated",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // ==========================================
    // BUTTON STYLING
    // ==========================================

    private void stylePrimaryButton(
        JButton button
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
            new Dimension(125, 38)
        );
    }

    private void styleSecondaryButton(
        JButton button
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
            new Dimension(105, 38)
        );
    }
}