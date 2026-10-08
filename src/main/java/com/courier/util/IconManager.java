package com.courier.util;

import java.awt.Image;
import javax.swing.ImageIcon;

public class IconManager {

    private static final String ICON_PATH = "/icons/";

    public static ImageIcon getIcon(String fileName, int width, int height) {

        java.net.URL resource =
                IconManager.class.getResource(ICON_PATH + fileName);

        if (resource == null) {
            System.err.println("Icon not found: " + fileName);
            return null;
        }

        ImageIcon icon = new ImageIcon(resource);

        Image image = icon.getImage().getScaledInstance(
                width,
                height,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(image);
    }
}