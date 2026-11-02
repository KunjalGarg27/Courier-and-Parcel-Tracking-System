package com.courier.main;

import javax.swing.SwingUtilities;

import com.courier.view.common.LoginFrame;

public class CustomerGUITest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
