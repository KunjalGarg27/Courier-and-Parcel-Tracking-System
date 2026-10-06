package com.courier.util;

import java.awt.Color;

public final class AppTheme {
	
	public static final Color SURFACE = Color.WHITE;

    // Primary
    public static final Color PRIMARY = new Color(0x556B2F);
    public static final Color PRIMARY_HOVER = new Color(0x445A25);

    // Secondary
    public static final Color SECONDARY = new Color(0x8B5E3C);

    // Accent
    public static final Color ACCENT = new Color(0xA7C957);
    public static final Color ACCENT_HOVER = new Color(0x8FB848);

    // Backgrounds
    public static final Color BACKGROUND = new Color(0xFAF8F3);
    public static final Color CARD = new Color(0xFFFFFF);
    public static final Color SIDEBAR = new Color(0xF1EDE6);

    // Borders
    public static final Color BORDER = new Color(0xE5DFD6);

    // Text
    public static final Color TEXT_PRIMARY = new Color(0x2E2E2E);
    public static final Color TEXT_SECONDARY = new Color(0x6B7280);

    // Status
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color INFO = new Color(0x3B82F6);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);

	public static Color TEXT;


    private AppTheme() {
        // Prevent object creation
    }
}