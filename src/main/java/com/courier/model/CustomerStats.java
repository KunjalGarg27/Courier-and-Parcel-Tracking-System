package com.courier.model;

public class CustomerStats {

    private final int total;
    private final int booked;
    private final int inTransit;
    private final int delivered;

    public CustomerStats(int total, int booked, int inTransit, int delivered) {
        this.total = total;
        this.booked = booked;
        this.inTransit = inTransit;
        this.delivered = delivered;
    }

    public int getTotal() {
        return total;
    }

    public int getBooked() {
        return booked;
    }

    public int getInTransit() {
        return inTransit;
    }

    public int getDelivered() {
        return delivered;
    }
}
