package com.courier.main;

import javax.swing.SwingUtilities;

import com.courier.view.customer.CustomerFrame;

public class CustomerGUITest {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            CustomerFrame frame =
                    new CustomerFrame("Charu");

            frame.setVisible(true);
        });
    }
}
