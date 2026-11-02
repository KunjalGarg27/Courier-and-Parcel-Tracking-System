package com.courier.model;

import java.util.List;

public class TrackingResult {

    private final Shipment shipment;
    private final String currentStatus;
    private final List<String> history;
    private final String pickupCity;
    private final String deliveryCity;

    public TrackingResult(Shipment shipment,
                          String currentStatus,
                          List<String> history,
                          String pickupCity,
                          String deliveryCity) {
        this.shipment = shipment;
        this.currentStatus = currentStatus;
        this.history = history;
        this.pickupCity = pickupCity;
        this.deliveryCity = deliveryCity;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public List<String> getHistory() {
        return history;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }
}