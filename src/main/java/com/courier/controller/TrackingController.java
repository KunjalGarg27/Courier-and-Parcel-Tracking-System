package com.courier.controller;

import java.sql.SQLException;
import java.util.List;

import com.courier.model.Shipment;
import com.courier.model.TrackingResult;
import com.courier.service.TrackingService;

public class TrackingController {

    private final TrackingService trackingService = new TrackingService();

    // Admin and existing tracking interface
    public Shipment trackShipment(String trackingNumber) throws SQLException {
        return trackingService.trackShipment(trackingNumber);
    }

    public String getCurrentStatus(long shipmentId) throws SQLException {
        return trackingService.getCurrentStatus(shipmentId);
    }

    public List<String> getTrackingHistory(long shipmentId)
            throws SQLException {
        return trackingService.getTrackingHistory(shipmentId);
    }

    public String getLatestLocation(long shipmentId) throws SQLException {
        return trackingService.getLatestLocation(shipmentId);
    }

    // Customer-facing tracking interface
    public TrackingResult track(String trackingNumber) throws SQLException {
        return trackingService.track(trackingNumber);
    }
}
