package com.courier.view.common.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.courier.util.AppTheme;

public class AppCard extends JPanel {

    public AppCard() {

        setLayout(new BorderLayout());

        setBackground(AppTheme.SURFACE);

        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    AppTheme.BORDER,
                    1
                ),
                BorderFactory.createEmptyBorder(
                    16, 16, 16, 16
                )
            )
        );

        setPreferredSize(new Dimension(250, 140));
    }

    public AppCard(JPanel content) {

        this();

        add(content, BorderLayout.CENTER);
    }
}