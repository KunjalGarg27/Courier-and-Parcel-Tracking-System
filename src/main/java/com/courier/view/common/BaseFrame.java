package com.courier.view.common;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.courier.util.AppTheme;

public class BaseFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    protected JPanel contentPanel;

    public BaseFrame(String title) {
        this(title, "User", "User");
    }

    public BaseFrame(String title, String user, String role) {

        super(title);

        initializeFrame(title, user, role);
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