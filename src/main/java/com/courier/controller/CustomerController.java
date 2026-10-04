package com.courier.controller;

import com.courier.model.Parcel;
import com.courier.service.CustomerService;

public class CustomerController {

    private CustomerService customerService;

    public CustomerController() {
        customerService = new CustomerService();
    }

    public Parcel trackParcel(String trackingId) {
        return customerService.trackParcel(trackingId);
    }

    public CustomerService getCustomerService() {
        return customerService;
    }
}
