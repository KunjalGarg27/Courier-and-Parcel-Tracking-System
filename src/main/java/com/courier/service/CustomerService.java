package com.courier.service;

import com.courier.model.Customer;
import com.courier.model.Parcel;

import java.util.ArrayList;
import java.util.List;

public class CustomerService {

    private List<Customer> customers;
    private List<Parcel> parcels;

    public CustomerService() {

        customers = new ArrayList<>();
        parcels = new ArrayList<>();

        addSampleData();
    }

    private void addSampleData() {

        customers.add(
                new Customer(
                        101,
                        "Rahul Sharma",
                        "rahul@gmail.com",
                        "9876543210",
                        "Delhi"
                )
        );

        customers.add(
                new Customer(
                        102,
                        "Priya Mehta",
                        "priya@gmail.com",
                        "9876501234",
                        "Faridabad"
                )
        );

        parcels.add(
                new Parcel(
                        "CP1001",
                        "Rahul Sharma",
                        "Amit Kumar",
                        "Delhi",
                        "Mumbai",
                        "In Transit"
                )
        );

        parcels.add(
                new Parcel(
                        "CP1002",
                        "Priya Mehta",
                        "Neha Singh",
                        "Faridabad",
                        "Jaipur",
                        "Delivered"
                )
        );
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public Parcel trackParcel(String trackingId) {

        for (Parcel parcel : parcels) {

            if (parcel.getTrackingId().equalsIgnoreCase(trackingId)) {
                return parcel;
            }
        }

        return null;
    }
}
