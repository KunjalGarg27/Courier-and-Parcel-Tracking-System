package com.courier.view.common;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.courier.view.admin.DashboardPanel;
import com.courier.view.admin.TrackingPanel;
import com.courier.util.AppTheme;

public class BaseFrame extends JFrame {

    protected JPanel contentPanel;

    public BaseFrame(String title) {
        this(title, "Kunjal", "Administrator");
    }

    public BaseFrame(String title, String user, String role) {

        super(title);

        initializeFrame(title, user, role);
        
        HeaderPanel header = new HeaderPanel(title, user, role);
        add(header, BorderLayout.NORTH);
    }

    private void initializeFrame(String title, String user, String role) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 650));

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // =========================
        // HEADER
        // =========================

        HeaderPanel headerPanel =
                new HeaderPanel(title, user, role);

        add(headerPanel, BorderLayout.NORTH);

        // =========================
        // SIDEBAR
        // =========================

        
        SidebarPanel sidebarPanel = new SidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // =========================
        // CONTENT AREA
        // =========================

        contentPanel = new JPanel(new BorderLayout());

        contentPanel.setBackground(AppTheme.BACKGROUND);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void setContent(JPanel panel) {

        contentPanel.removeAll();

        contentPanel.add(panel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    protected void showPanel(JPanel panel) {
        setContent(panel);
    }
}