package com.courier.view.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class HeaderPanel extends JPanel {

    private JLabel pageTitle;
    private JLabel userName;
    private JLabel roleLabel;

    public HeaderPanel(String title, String user, String role) {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 72));

        setBackground(AppTheme.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        // LEFT SIDE - Page title
        pageTitle = new JLabel(title);
        pageTitle.setFont(FontManager.semiBold.deriveFont(22f));
        pageTitle.setForeground(AppTheme.TEXT_PRIMARY);

        add(pageTitle, BorderLayout.WEST);

        // RIGHT SIDE - User information
        JPanel userPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 18)
        );

        userPanel.setOpaque(false);

        userName = new JLabel(user);
        userName.setFont(FontManager.medium.deriveFont(14f));
        userName.setForeground(AppTheme.TEXT_PRIMARY);

        roleLabel = new JLabel(role);
        roleLabel.setFont(FontManager.regular.deriveFont(12f));
        roleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        userPanel.add(userName);
        userPanel.add(roleLabel);

        add(userPanel, BorderLayout.EAST);
    }

    public void setPageTitle(String title) {
        pageTitle.setText(title);
    }

    public void setUser(String user) {
        userName.setText(user);
    }

    public void setRole(String role) {
        roleLabel.setText(role);
    }
}