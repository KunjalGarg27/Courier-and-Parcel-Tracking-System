package com.courier.main;

import javax.swing.SwingUtilities;

import com.courier.view.common.BaseFrame;

public class GUITest {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            BaseFrame frame = new BaseFrame(
                "Dashboard",
                "Kunjal",
                "Administrator"
            );

            frame.setVisible(true);
        });
    }
}