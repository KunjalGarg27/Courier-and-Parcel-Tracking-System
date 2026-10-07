package com.courier.view.common;

import com.courier.util.IconManager;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class SidebarPanel extends JPanel {
	private JPanel navigationPanel;
	private JButton settingsButton;
    public SidebarPanel(String[] menuItems) {

    	navigationPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        setPreferredSize(new Dimension(220, 0));
        setBackground(AppTheme.SURFACE);
        setBorder(
            BorderFactory.createMatteBorder(
                0, 0, 0, 1, AppTheme.BORDER
            )
        );

        setLayout(new BorderLayout());

        // =========================
        // LOGO / BRAND
        // =========================

        JPanel brandPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 20, 20
        ));

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


        // =========================
        // NAVIGATION
        // =========================

        
        navigationPanel.setOpaque(false);

        navigationPanel.setBorder(
            BorderFactory.createEmptyBorder(
                10, 12, 10, 12
            )
        );

        for (String item : menuItems) {
            navigationPanel.add(createMenuButton(item));
        }

        add(navigationPanel, BorderLayout.CENTER);


        // =========================
        // BOTTOM
        // =========================

        JPanel bottomPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 20, 15
        ));

        bottomPanel.setOpaque(false);

        settingsButton = createMenuButton("Settings");
        bottomPanel.add(settingsButton);

        add(bottomPanel, BorderLayout.SOUTH);
        
        
    }


    private JButton createMenuButton(String text) {
    	JButton button = new JButton(text);

    	String iconFile = getIconFileName(text);

    	ImageIcon icon = IconManager.getIcon(iconFile, 20, 20);

    	if (icon != null) {
    	    button.setIcon(icon);
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
    private String getIconFileName(String text) {

        switch (text) {

            case "Dashboard":
                return "dashboard-panel.png";

            case "Tracking":
                return "track.png";

            case "Shipments":
                return "box-alt.png";

            case "Customers":
                return "people-carry-box.png";

            case "Delivery Agents":
                return "delivery-man.png";

            case "Reports":
                return "data-report.png";

            case "Settings":
                return "settings.png";

            default:
                return "";
        }
    }
    
    public void setMenuAction(
            String menuText,
            Runnable action) {

        for (java.awt.Component component :
                navigationPanel.getComponents()) {

            if (component instanceof JButton button
                    && button.getText().equals(menuText)) {

                for (java.awt.event.ActionListener listener :
                        button.getActionListeners()) {

                    button.removeActionListener(listener);
                }

                button.addActionListener(
                    e -> action.run()
                );

                return;
            }
        }

        // Settings is located in the bottom section
        if ("Settings".equals(menuText)
                && settingsButton != null) {

            for (java.awt.event.ActionListener listener :
                    settingsButton.getActionListeners()) {

                settingsButton.removeActionListener(listener);
            }

            settingsButton.addActionListener(
                e -> action.run()
            );
        }
    }
}