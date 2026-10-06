package com.courier.util;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

public final class UIComponents {

    private UIComponents() {
    }

    // =========================
    // PRIMARY BUTTON
    // =========================
    public static JButton primaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(FontManager.medium(14));
        button.setForeground(AppTheme.CARD);
        button.setBackground(AppTheme.PRIMARY);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setPreferredSize(
                new Dimension(
                        150,
                        UIConstants.BUTTON_HEIGHT
                )
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 20, 8, 20
                )
        );

        return button;
    }

    // =========================
    // SECONDARY BUTTON
    // =========================
    public static JButton secondaryButton(String text) {

        JButton button = new JButton(text);

        button.setFont(FontManager.medium(14));
        button.setForeground(AppTheme.PRIMARY);
        button.setBackground(AppTheme.CARD);

        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);

        button.setPreferredSize(
                new Dimension(
                        150,
                        UIConstants.BUTTON_HEIGHT
                )
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                AppTheme.PRIMARY,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                7, 19, 7, 19
                        )
                )
        );

        return button;
    }

    // =========================
    // TEXT FIELD
    // =========================
    public static JTextField textField() {

        JTextField field = new JTextField();

        field.setFont(FontManager.regular(14));
        field.setForeground(AppTheme.TEXT_PRIMARY);
        field.setBackground(AppTheme.CARD);

        field.setPreferredSize(
                new Dimension(
                        250,
                        UIConstants.INPUT_HEIGHT
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                AppTheme.BORDER,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                8, 12, 8, 12
                        )
                )
        );

        return field;
    }
}