package com.courier.main;

import com.courier.controller.UserController;

public class RegisterTestCustomer {

    public static void main(String[] args) {
        try {
            long userId = new UserController().registerCustomer(
                    "Test Customer Two",
                    "customer2@kahinbhi.com",
                    "9876543210",
                    "Test@12345");

            System.out.println("Customer registered. user_id = " + userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
