
package com.courier.view.agent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.BaseFrame;

public class AgentFrame extends BaseFrame {

    private static final long serialVersionUID = 1L;

    private static final Color GREEN = new Color(83, 108, 43);
    private static final Color WHITE = Color.WHITE;

    private final JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 8));

    public AgentFrame(String userName) {
        super("Delivery Agent Dashboard", userName, "Delivery Agent");

        navigation.setBackground(WHITE);
        navigation.setBorder(
            BorderFactory.createEmptyBorder(20, 12, 20, 12)
        );
        navigation.setPreferredSize(new Dimension(235, 0));

        JLabel brand = new JLabel("  KahinBhi");
        brand.setFont(FontManager.semiBold(22));
        brand.setForeground(GREEN);
        brand.setBorder(
            BorderFactory.createEmptyBorder(20, 12, 20, 12)
        );

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(WHITE);
        sidebar.setBorder(
            BorderFactory.createMatteBorder(
                0, 0, 0, 1, new Color(225, 219, 208)
            )
        );
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(navigation, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);

        addMenu("Dashboard", () ->
            setContent(new AgentDashboardPanel())
        );

        addMenu("My Deliveries", () ->
            setContent(new MyDeliveriesPanel())
        );

        addMenu("Delivery History", () ->
            setContent(new DeliveryHistoryPanel())
        );

        addMenu("Tracking", () ->
            setContent(new TrackingPanel())
        );

        addMenu("Settings", () ->
            setContent(new AgentSettingsPanel())
        );

        setContent(new AgentDashboardPanel());
    }

    private void addMenu(String title, Runnable action) {
        JButton button = new JButton(title);

        button.setFont(FontManager.semiBold(14));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(
            BorderFactory.createEmptyBorder(13, 16, 13, 10)
        );

        button.addActionListener(e -> action.run());

        navigation.add(button);
    }
}
