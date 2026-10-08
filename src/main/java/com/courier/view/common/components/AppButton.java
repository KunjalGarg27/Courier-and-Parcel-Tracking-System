package com.courier.view.common.components;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class AppButton extends JButton {

    public AppButton(String text) {

        super(text);

        setFont(FontManager.medium(14));

        setForeground(AppTheme.TEXT_PRIMARY);

        setBackground(AppTheme.PRIMARY);

        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);

        setCursor(
            new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR
            )
        );

        setBorder(
            BorderFactory.createEmptyBorder(
                10, 18, 10, 18
            )
        );

        setMargin(
            new Insets(0, 0, 0, 0)
        );

        setPreferredSize(
            new Dimension(140, 42)
        );
    }
}