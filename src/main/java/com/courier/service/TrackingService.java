package com.courier.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.courier.dao.TrackingDAO;
import com.courier.model.Shipment;
import com.courier.model.TrackingResult;

public class TrackingService {

    private final TrackingDAO trackingDAO = new TrackingDAO();

    // Returns null when no shipment has this tracking number.
    public TrackingResult track(String trackingNumber) throws SQLException {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter a tracking number.");
        }

        String normalized = trackingNumber.trim().toUpperCase(Locale.ROOT);

        if (normalized.length() > 40) {
            throw new IllegalArgumentException(
                    "Tracking number is too long.");
        }

        Shipment shipment = trackingDAO.findShipmentByTrackingNumber(normalized);

        if (shipment == null) {
            return null;
        }

        String status = trackingDAO.findCurrentStatus(shipment.getShipmentId());
        List<String> history =
                trackingDAO.findTrackingHistory(shipment.getShipmentId());
        String[] cities =
                trackingDAO.findRouteCities(shipment.getShipmentId());

        return new TrackingResult(
                shipment,
                status == null ? "UNKNOWN" : status,
                history,
                cities[0],
                cities[1]);
    }
}