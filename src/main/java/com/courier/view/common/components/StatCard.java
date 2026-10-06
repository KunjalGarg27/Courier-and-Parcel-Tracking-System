package com.courier.view.common.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class StatCard extends JPanel {

    private JLabel titleLabel;
    private JLabel valueLabel;

    public StatCard(String title, String value) {

        setLayout(new BorderLayout(0, 10));

        setBackground(AppTheme.SURFACE);

        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    AppTheme.BORDER
                ),
                BorderFactory.createEmptyBorder(
                    20, 22, 20, 22
                )
            )
        );

        setPreferredSize(new Dimension(0, 120));

        // =========================
        // TITLE
        // =========================

        titleLabel = new JLabel(title);

        titleLabel.setFont(
            FontManager.regular(14)
        );

        titleLabel.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        add(
            titleLabel,
            BorderLayout.NORTH
        );

        // =========================
        // VALUE
        // =========================

        valueLabel = new JLabel(value);

        valueLabel.setFont(
            FontManager.semiBold(28)
        );

        valueLabel.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        add(
            valueLabel,
            BorderLayout.CENTER
        );
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}