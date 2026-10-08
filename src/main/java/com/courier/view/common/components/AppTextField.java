package com.courier.view.common.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class AppTextField extends JTextField {

    public AppTextField(int columns) {

        super(columns);

        setFont(
            FontManager.regular(14)
        );

        setForeground(
            AppTheme.TEXT_PRIMARY
        );

        setBackground(
            AppTheme.SURFACE
        );

        setCaretColor(
            AppTheme.PRIMARY
        );

        setBorder(
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

        setPreferredSize(
            new Dimension(250, 42)
        );

        setMargin(
            new Insets(0, 0, 0, 0)
        );
    }
}