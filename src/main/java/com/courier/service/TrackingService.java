package com.courier.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.courier.dao.TrackingDAO;
import com.courier.model.Shipment;
import com.courier.model.TrackingResult;

public class TrackingService {

    private final TrackingDAO trackingDAO = new TrackingDAO();

    // =========================================================
    // ADMIN / EXISTING TRACKING METHODS
    // =========================================================

    public Shipment trackShipment(String trackingNumber)
            throws SQLException {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number cannot be empty.");
        }

        return trackingDAO.findShipmentByTrackingNumber(
                trackingNumber.trim().toUpperCase(Locale.ROOT));
    }

    public String getCurrentStatus(long shipmentId) throws SQLException {
        validateShipmentId(shipmentId);
        return trackingDAO.findCurrentStatus(shipmentId);
    }

    public List<String> getTrackingHistory(long shipmentId)
            throws SQLException {
        validateShipmentId(shipmentId);
        return trackingDAO.findTrackingHistory(shipmentId);
    }

    public String getLatestLocation(long shipmentId) throws SQLException {
        validateShipmentId(shipmentId);
        return trackingDAO.findLatestLocation(shipmentId);
    }

    // =========================================================
    // CUSTOMER TRACKING METHOD
    // =========================================================

    /**
     * Returns null when no shipment matches the tracking number.
     */
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

        Shipment shipment =
                trackingDAO.findShipmentByTrackingNumber(normalized);

        if (shipment == null) {
            return null;
        }

        long shipmentId = shipment.getShipmentId();

        String status = trackingDAO.findCurrentStatus(shipmentId);
        List<String> history = trackingDAO.findTrackingHistory(shipmentId);
        String[] cities = trackingDAO.findRouteCities(shipmentId);

        String pickupCity =
                cities != null && cities.length > 0 ? cities[0] : null;
        String deliveryCity =
                cities != null && cities.length > 1 ? cities[1] : null;

        return new TrackingResult(
                shipment,
                status == null ? "UNKNOWN" : status,
                history,
                pickupCity,
                deliveryCity);
    }

    private void validateShipmentId(long shipmentId) {
        if (shipmentId <= 0) {
            throw new IllegalArgumentException("Invalid shipment ID.");
        }
    }
}
