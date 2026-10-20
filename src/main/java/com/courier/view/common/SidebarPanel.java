
package com.courier.view.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.util.IconManager;

public class SidebarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JPanel navigationPanel;
    private JButton settingsButton;

    public SidebarPanel(String[] menuItems) {
        navigationPanel = new JPanel(new GridLayout(0, 1, 0, 8));

        setPreferredSize(new Dimension(220, 0));
        setBackground(AppTheme.SURFACE);
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, AppTheme.BORDER));
        setLayout(new BorderLayout());

        // Brand section
        JPanel brandPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 20));
        brandPanel.setOpaque(false);

        ImageIcon logoIcon = IconManager.getIcon("logo.jpg", 32, 32);
        JLabel logoLabel = new JLabel();

        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        }

        JLabel brand = new JLabel("KahinBhi");
        brand.setFont(FontManager.semiBold(20));
        brand.setForeground(AppTheme.PRIMARY);

        brandPanel.add(logoLabel);
        brandPanel.add(brand);
        add(brandPanel, BorderLayout.NORTH);

        // Navigation section
        navigationPanel.setOpaque(false);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(
                10, 12, 10, 12));

        if (menuItems != null) {
            for (String item : menuItems) {
                if (item != null && !item.isBlank()
                        && !"Settings".equals(item)) {
                    navigationPanel.add(createMenuButton(item));
                }
            }
        }

        add(navigationPanel, BorderLayout.CENTER);

        // Settings remains at the bottom for every role
        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 20, 15));
        bottomPanel.setOpaque(false);

        settingsButton = createMenuButton("Settings");
        bottomPanel.add(settingsButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Default constructor for the Admin menu
    public SidebarPanel() {
        this(new String[] {
                "Dashboard",
                "Tracking",
                "Shipments",
                "Customers",
                "Delivery Agents",
                "Reports"
        });
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);

        String iconFile = getIconFileName(text);

        if (!iconFile.isEmpty()) {
            ImageIcon icon = IconManager.getIcon(iconFile, 20, 20);

            if (icon != null) {
                button.setIcon(icon);
            }
        }

        button.setIconTextGap(12);
        button.setFont(FontManager.medium(14));
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(AppTheme.SURFACE);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(190, 42));

        return button;
    }

    private String getIconFileName(String text) {
        switch (text) {
            case "Dashboard":
                return "dashboard-panel.png";

            case "Tracking":
                return "track.png";

            case "My Deliveries":
            case "Shipments":
            case "My Shipments":
            case "Book Shipment":
                return "box-alt.png";

            case "Delivery History":
            case "Reports":
                return "data-report.png";

            case "Customers":
                return "people-carry-box.png";

            case "Delivery Agents":
                return "delivery-man.png";

            case "Profile":
                return "user.png";

            case "Settings":
                return "settings.png";

            default:
                return "";
        }
    }

    // Connect a sidebar item to its corresponding screen
    public void setMenuAction(String menuText, Runnable action) {
        if (menuText == null || action == null) {
            return;
        }

        for (Component component : navigationPanel.getComponents()) {
            if (component instanceof JButton button
                    && button.getText().equals(menuText)) {
                replaceAction(button, action);
                return;
            }
        }

        // Settings is in the bottom section, not the navigation panel
        if ("Settings".equals(menuText) && settingsButton != null) {
            replaceAction(settingsButton, action);
        }
    }

    private void replaceAction(JButton button, Runnable action) {
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }

        button.addActionListener(e -> action.run());
    }
}
