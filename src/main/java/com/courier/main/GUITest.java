package com.courier.main;

import javax.swing.SwingUtilities;

import com.courier.view.admin.AdminFrame;

public class GUITest {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AdminFrame frame =
                    new AdminFrame("Kunjal");

            frame.setVisible(true);
        });
    }
}