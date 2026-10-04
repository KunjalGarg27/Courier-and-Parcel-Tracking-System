package com.courier.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerDashboard extends JFrame {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final Color BACKGROUND = new Color(247, 247, 239);
    private static final Color SIDEBAR = new Color(241, 242, 232);
    private static final Color WHITE = new Color(255, 255, 255);

    private static final Color PRIMARY_GREEN = new Color(112, 150, 83);
    private static final Color LIGHT_GREEN = new Color(230, 240, 221);

    private static final Color TEXT = new Color(41, 54, 58);
    private static final Color MUTED = new Color(107, 121, 109);
    private static final Color BORDER = new Color(221, 226, 216);

    private static final Color STATUS_BLUE_BG = new Color(221, 236, 243);
    private static final Color STATUS_BLUE_TEXT = new Color(70, 116, 138);

    private static final Color STATUS_GREEN_BG = new Color(224, 241, 216);
    private static final Color STATUS_GREEN_TEXT = new Color(79, 130, 58);

    private static final Color STATUS_YELLOW_BG = new Color(245, 235, 203);
    private static final Color STATUS_YELLOW_TEXT = new Color(146, 117, 34);

    private static final Color STATUS_RED_BG = new Color(246, 221, 213);
    private static final Color STATUS_RED_TEXT = new Color(180, 81, 62);

    // =========================================================
    // CUSTOMER INFORMATION
    // =========================================================

    private final String customerName;

    private JLabel greetingLabel;
    private JLabel initialLabel;
    private JLabel customerNameLabel;

    // =========================================================
    // SAMPLE PARCEL DATA
    // =========================================================

    private final Object[][] parcelData = {
            {"CP2026002", "Electronics", "Mumbai", "In Transit", "16 Sep 2026", "20 Sep 2026"},
            {"CP2026001", "Books", "Bengaluru", "Delivered", "10 Sep 2026", "14 Sep 2026"},
            {"CP2025998", "Clothes", "Hyderabad", "Out for Delivery", "18 Sep 2026", "21 Sep 2026"},
            {"CP2025990", "Documents", "Chennai", "Cancelled / RTO", "05 Sep 2026", "--"}
    };

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CustomerDashboard(String customerName) {

        if (customerName == null || customerName.trim().isEmpty()) {
            this.customerName = "Customer";
        } else {
            this.customerName = customerName.trim();
        }

        setTitle("Courier & Parcel Tracking System");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createDashboard();
    }

    // Compatibility constructor
    // Allows old code using new CustomerDashboard() to compile.
    public CustomerDashboard() {
        this("Customer");
    }

    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    private void createDashboard() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // -----------------------------------------------------
        // TOP HEADER
        // -----------------------------------------------------

        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        JPanel content = createContent();

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, BORDER
                )
        );

        header.setPreferredSize(new Dimension(0, 70));

        // Left side
        JPanel left = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        20,
                        18
                )
        );
        left.setOpaque(false);

        JLabel logoIcon = new JLabel("▣");
        logoIcon.setFont(new Font("SansSerif", Font.BOLD, 20));
        logoIcon.setForeground(TEXT);

        JLabel systemName = new JLabel(
                "Courier & Parcel Tracking System"
        );
        systemName.setFont(
                new Font("SansSerif", Font.BOLD, 20)
        );
        systemName.setForeground(TEXT);

        left.add(logoIcon);
        left.add(systemName);

        // Right side
        JPanel right = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        13
                )
        );
        right.setOpaque(false);

        JLabel onlineDot = new JLabel("●");
        onlineDot.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        onlineDot.setForeground(PRIMARY_GREEN);

        initialLabel = new JLabel(
                getInitial(customerName)
        );
        initialLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        initialLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );
        initialLabel.setForeground(PRIMARY_GREEN);
        initialLabel.setOpaque(true);
        initialLabel.setBackground(LIGHT_GREEN);
        initialLabel.setPreferredSize(
                new Dimension(38, 38)
        );

        customerNameLabel = new JLabel(
                customerName
        );
        customerNameLabel.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );
        customerNameLabel.setForeground(TEXT);

        JLabel arrow = new JLabel("▾");
        arrow.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        arrow.setForeground(TEXT);

        right.add(onlineDot);
        right.add(initialLabel);
        right.add(customerNameLabel);
        right.add(arrow);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );
        sidebar.setBorder(
                new EmptyBorder(28, 16, 20, 16)
        );

        JLabel portalLabel = new JLabel("CUSTOMER PORTAL");
        portalLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        portalLabel.setForeground(MUTED);
        portalLabel.setBorder(
                new EmptyBorder(0, 12, 20, 0)
        );

        sidebar.add(portalLabel);

        JButton dashboardButton =
                createMenuButton("⌂", "Dashboard", true);

        JButton bookButton =
                createMenuButton("□", "Book Parcel", false);

        JButton parcelsButton =
                createMenuButton("▣", "My Parcels", false);

        JButton trackButton =
                createMenuButton("⌕", "Track Parcel", false);

        JButton historyButton =
                createMenuButton("◷", "History", false);

        JButton profileButton =
                createMenuButton("●", "Profile", false);

        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(bookButton);
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(parcelsButton);
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(trackButton);
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(historyButton);
        sidebar.add(Box.createVerticalStrut(7));

        sidebar.add(profileButton);

        // Push logout to bottom
        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton =
                createMenuButton("↪", "Logout", false);

        logoutButton.addActionListener(
                e -> performLogout()
        );

        sidebar.add(logoutButton);

        // Actions
        bookButton.addActionListener(
                e -> showBookParcelDialog()
        );

        parcelsButton.addActionListener(
                e -> showMyParcelsDialog()
        );

        trackButton.addActionListener(
                e -> showTrackDialog()
        );

        historyButton.addActionListener(
                e -> showHistoryDialog()
        );

        profileButton.addActionListener(
                e -> showProfileDialog()
        );

        return sidebar;
    }

    // =========================================================
    // CONTENT
    // =========================================================

    private JPanel createContent() {

        JPanel content = new JPanel();
        content.setBackground(BACKGROUND);
        content.setBorder(
                new EmptyBorder(30, 32, 35, 32)
        );

        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS)
        );

        // -----------------------------------------------------
        // GREETING
        // -----------------------------------------------------

        greetingLabel = new JLabel(
                "Good morning, " + customerName
        );
        greetingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 32)
        );
        greetingLabel.setForeground(TEXT);
        greetingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Track your parcels or book a new shipment."
        );
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 16)
        );
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(greetingLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);

        content.add(Box.createVerticalStrut(28));

        // -----------------------------------------------------
        // TRACK CARD
        // -----------------------------------------------------

        JPanel trackCard = createCard();

        trackCard.setLayout(
                new BorderLayout(20, 15)
        );

        JLabel trackTitle = new JLabel(
                "Track your parcel"
        );
        trackTitle.setFont(
                new Font("SansSerif", Font.BOLD, 21)
        );
        trackTitle.setForeground(TEXT);

        JLabel trackSubtitle = new JLabel(
                "Enter your tracking ID to check the current status."
        );
        trackSubtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        trackSubtitle.setForeground(MUTED);

        JPanel trackText = new JPanel();
        trackText.setOpaque(false);
        trackText.setLayout(
                new BoxLayout(
                        trackText,
                        BoxLayout.Y_AXIS
                )
        );

        trackText.add(trackTitle);
        trackText.add(Box.createVerticalStrut(4));
        trackText.add(trackSubtitle);

        JPanel trackInputPanel = new JPanel(
                new BorderLayout(10, 0)
        );
        trackInputPanel.setOpaque(false);

        JTextField trackingField = new JTextField();
        trackingField.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );
        trackingField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                12, 14, 12, 14
                        )
                )
        );

        JButton trackButton = createPrimaryButton(
                "Track"
        );

        trackButton.addActionListener(
                e -> trackParcel(
                        trackingField.getText()
                )
        );

        trackInputPanel.add(
                trackingField,
                BorderLayout.CENTER
        );
        trackInputPanel.add(
                trackButton,
                BorderLayout.EAST
        );

        JPanel trackBottom = new JPanel(
                new BorderLayout()
        );
        trackBottom.setOpaque(false);
        trackBottom.add(
                trackInputPanel,
                BorderLayout.CENTER
        );

        trackCard.add(
                trackText,
                BorderLayout.NORTH
        );

        trackCard.add(
                trackBottom,
                BorderLayout.CENTER
        );

        trackCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(trackCard);

        content.add(Box.createVerticalStrut(20));

        // -----------------------------------------------------
        // STAT CARDS
        // -----------------------------------------------------

        JPanel statsPanel = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        statsPanel.add(
                createStatCard(
                        "Total Shipments",
                        "12",
                        "All parcels"
                )
        );

        statsPanel.add(
                createStatCard(
                        "In Transit",
                        "3",
                        "Currently moving"
                )
        );

        statsPanel.add(
                createStatCard(
                        "Delivered",
                        "8",
                        "Successfully delivered"
                )
        );

        statsPanel.add(
                createStatCard(
                        "Pending",
                        "1",
                        "Awaiting processing"
                )
        );

        content.add(statsPanel);

        content.add(Box.createVerticalStrut(22));

        // -----------------------------------------------------
        // RECENT PARCELS
        // -----------------------------------------------------

        JPanel recentCard = createRecentParcelsCard();

        recentCard.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(recentCard);

        content.add(Box.createVerticalStrut(18));

        // -----------------------------------------------------
        // QUICK ACTIONS
        // -----------------------------------------------------

        JPanel actionsPanel = new JPanel(
                new GridLayout(1, 4, 15, 0)
        );
        actionsPanel.setOpaque(false);
        actionsPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        actionsPanel.add(
                createQuickAction(
                        "□",
                        "Book a Parcel",
                        "Schedule a new shipment",
                        this::showBookParcelDialog
                )
        );

        actionsPanel.add(
                createQuickAction(
                        "⌕",
                        "Track Parcel",
                        "Real-time parcel tracking",
                        this::showTrackDialog
                )
        );

        actionsPanel.add(
                createQuickAction(
                        "▣",
                        "My Parcels",
                        "View your shipments",
                        this::showMyParcelsDialog
                )
        );

        actionsPanel.add(
                createQuickAction(
                        "●",
                        "Profile",
                        "Manage your account",
                        this::showProfileDialog
                )
        );

        content.add(actionsPanel);

        return content;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private JPanel createStatCard(
            String title,
            String value,
            String description
    ) {

        JPanel card = createCard();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        titleLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );
        valueLabel.setForeground(TEXT);

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(descriptionLabel);

        return card;
    }

    // =========================================================
    // RECENT PARCELS CARD
    // =========================================================

    private JPanel createRecentParcelsCard() {

        JPanel card = createCard();

        card.setLayout(
                new BorderLayout(0, 15)
        );

        JLabel title = new JLabel(
                "My Recent Parcels"
        );

        title.setFont(
                new Font("SansSerif", Font.BOLD, 21)
        );

        title.setForeground(TEXT);

        card.add(
                title,
                BorderLayout.NORTH
        );

        String[] columns = {
                "Tracking ID",
                "Item",
                "Destination",
                "Status",
                "Booked On",
                "ETA",
                "Action"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        parcelData,
                        columns
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        JTable table = new JTable(model);

        table.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        table.setRowHeight(42);

        table.setForeground(TEXT);
        table.setBackground(WHITE);

        table.setGridColor(BORDER);

        table.setSelectionBackground(
                LIGHT_GREEN
        );

        table.setSelectionForeground(TEXT);

        table.getTableHeader().setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(WHITE);

        table.getTableHeader().setPreferredSize(
                new Dimension(0, 38)
        );

        // Column widths
        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(110);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(120);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(110);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(110);

        table.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(70);

        // Status renderer
        table.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new StatusRenderer()
                );

        // View action
        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (e.getClickCount() == 2) {

                            int row =
                                    table.getSelectedRow();

                            if (row >= 0) {

                                String trackingId =
                                        String.valueOf(
                                                table.getValueAt(
                                                        row,
                                                        0
                                                )
                                        );

                                String status =
                                        String.valueOf(
                                                table.getValueAt(
                                                        row,
                                                        3
                                                )
                                        );

                                JOptionPane.showMessageDialog(
                                        CustomerDashboard.this,
                                        "Tracking ID: "
                                                + trackingId
                                                + "\nStatus: "
                                                + status,
                                        "Parcel Details",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                            }
                        }
                    }
                }
        );

        JScrollPane tableScroll =
                new JScrollPane(table);

        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        tableScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        card.add(
                tableScroll,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================================================
    // STATUS RENDERER
    // =========================================================

    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label =
                    (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            false,
                            false,
                            row,
                            column
                    );

            String status =
                    value == null
                            ? ""
                            : value.toString();

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    )
            );

            if (status.equalsIgnoreCase(
                    "Delivered"
            )) {

                label.setBackground(
                        STATUS_GREEN_BG
                );

                label.setForeground(
                        STATUS_GREEN_TEXT
                );

            } else if (status.equalsIgnoreCase(
                    "In Transit"
            )) {

                label.setBackground(
                        STATUS_BLUE_BG
                );

                label.setForeground(
                        STATUS_BLUE_TEXT
                );

            } else if (status.equalsIgnoreCase(
                    "Out for Delivery"
            )) {

                label.setBackground(
                        STATUS_YELLOW_BG
                );

                label.setForeground(
                        STATUS_YELLOW_TEXT
                );

            } else if (status.equalsIgnoreCase(
                    "Cancelled / RTO"
            )) {

                label.setBackground(
                        STATUS_RED_BG
                );

                label.setForeground(
                        STATUS_RED_TEXT
                );

            } else {

                label.setBackground(WHITE);
                label.setForeground(TEXT);
            }

            return label;
        }
    }

    // =========================================================
    // QUICK ACTION
    // =========================================================

    private JPanel createQuickAction(
            String icon,
            String title,
            String subtitle,
            Runnable action
    ) {

        JPanel panel = createCard();

        panel.setLayout(
                new BorderLayout(12, 0)
        );

        JLabel iconLabel = new JLabel(icon);

        iconLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        23
                )
        );

        iconLabel.setForeground(
                PRIMARY_GREEN
        );

        JPanel textPanel = new JPanel();

        textPanel.setOpaque(false);

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel =
                new JLabel(subtitle);

        subtitleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        subtitleLabel.setForeground(MUTED);

        textPanel.add(titleLabel);
        textPanel.add(
                Box.createVerticalStrut(3)
        );
        textPanel.add(subtitleLabel);

        panel.add(
                iconLabel,
                BorderLayout.WEST
        );

        panel.add(
                textPanel,
                BorderLayout.CENTER
        );

        panel.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        panel.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {
                        action.run();
                    }
                }
        );

        return panel;
    }

    // =========================================================
    // CARD
    // =========================================================

    private JPanel createCard() {

        JPanel card = new JPanel();

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        return card;
    }

    // =========================================================
    // MENU BUTTON
    // =========================================================

    private JButton createMenuButton(
            String icon,
            String text,
            boolean active
    ) {

        JButton button =
                new JButton(
                        icon + "   " + text
                );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        44
                )
        );

        button.setPreferredSize(
                new Dimension(170, 44)
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(TEXT);

        button.setBackground(
                active
                        ? LIGHT_GREEN
                        : SIDEBAR
        );

        button.setBorder(
                new EmptyBorder(
                        10, 12, 10, 10
                )
        );

        button.setFocusPainted(false);
        button.setOpaque(true);

        return button;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(WHITE);
        button.setBackground(PRIMARY_GREEN);

        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(
                        12, 25, 12, 25
                )
        );

        return button;
    }

    // =========================================================
    // INITIAL
    // =========================================================

    private String getInitial(
            String name
    ) {

        if (name == null ||
                name.trim().isEmpty()) {

            return "C";
        }

        return name
                .trim()
                .substring(0, 1)
                .toUpperCase();
    }

    // =========================================================
    // TRACK PARCEL
    // =========================================================

    private void trackParcel(
            String trackingNumber
    ) {

        String id =
                trackingNumber == null
                        ? ""
                        : trackingNumber.trim();

        if (id.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a tracking ID.",
                    "Tracking",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        for (Object[] parcel : parcelData) {

            if (parcel[0]
                    .toString()
                    .equalsIgnoreCase(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Tracking ID: "
                                + parcel[0]
                                + "\n\n"
                                + "Item: "
                                + parcel[1]
                                + "\n"
                                + "Destination: "
                                + parcel[2]
                                + "\n"
                                + "Status: "
                                + parcel[3]
                                + "\n"
                                + "Booked On: "
                                + parcel[4]
                                + "\n"
                                + "ETA: "
                                + parcel[5],
                        "Parcel Tracking",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "No parcel found for tracking ID: "
                        + id,
                "Parcel Not Found",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =========================================================
    // BOOK PARCEL
    // =========================================================

    private void showBookParcelDialog() {

        JOptionPane.showMessageDialog(
                this,
                "Book Parcel module\n\n"
                        + "This section will allow the customer "
                        + "to schedule a new shipment.",
                "Book Parcel",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // MY PARCELS
    // =========================================================

    private void showMyParcelsDialog() {

        JOptionPane.showMessageDialog(
                this,
                "My Parcels\n\n"
                        + "This section displays all parcels "
                        + "belonging to the logged-in customer.",
                "My Parcels",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // TRACK DIALOG
    // =========================================================

    private void showTrackDialog() {

        JTextField field =
                new JTextField();

        JPanel panel = new JPanel(
                new BorderLayout(8, 0)
        );

        panel.add(
                new JLabel("Tracking ID:"),
                BorderLayout.WEST
        );

        panel.add(
                field,
                BorderLayout.CENTER
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Track Parcel",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result ==
                JOptionPane.OK_OPTION) {

            trackParcel(
                    field.getText()
            );
        }
    }

    // =========================================================
    // HISTORY
    // =========================================================

    private void showHistoryDialog() {

        JOptionPane.showMessageDialog(
                this,
                "Shipment History\n\n"
                        + "Previous parcel activity "
                        + "will be displayed here.",
                "History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // PROFILE
    // =========================================================

    private void showProfileDialog() {

        JOptionPane.showMessageDialog(
                this,
                "Customer Profile\n\n"
                        + "Name: "
                        + customerName
                        + "\nRole: Customer",
                "Profile",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void performLogout() {

        int option =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (option ==
                JOptionPane.YES_OPTION) {

            dispose();

            SwingUtilities.invokeLater(
                    () -> {

                        LoginFrame login =
                                new LoginFrame();

                        login.setVisible(true);
                    }
            );
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    CustomerDashboard dashboard =
                            new CustomerDashboard(
                                    "Customer"
                            );

                    dashboard.setVisible(true);
                }
        );
    }
}
