package com.courier.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public final class FontManager {

    public static Font regular;
    public static Font medium;
    public static Font semiBold;

    static {
        loadFonts();
    }

    private static void loadFonts() {
        try {
            regular = loadFont("/fonts/Poppins-Regular.ttf");
            medium = loadFont("/fonts/Poppins-Medium.ttf");
            semiBold = loadFont("/fonts/Poppins-SemiBold.ttf");
        } catch (Exception e) {
            System.err.println("Could not load Poppins fonts. Using fallback fonts.");
            
            regular = new Font("SansSerif", Font.PLAIN, 14);
            medium = new Font("SansSerif", Font.PLAIN, 14);
            semiBold = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    private static Font loadFont(String path) throws Exception {
        InputStream stream = FontManager.class.getResourceAsStream(path);

        if (stream == null) {
            throw new Exception("Font not found: " + path);
        }

        Font font = Font.createFont(Font.TRUETYPE_FONT, stream);

        GraphicsEnvironment.getLocalGraphicsEnvironment()
                .registerFont(font);

        return font;
    }

    public static Font regular(int size) {
        return regular.deriveFont(Font.PLAIN, (float) size);
    }

    public static Font medium(int size) {
        return medium.deriveFont(Font.PLAIN, (float) size);
    }

    public static Font semiBold(int size) {
        return semiBold.deriveFont(Font.PLAIN, (float) size);
    }

    private FontManager() {
    }
}