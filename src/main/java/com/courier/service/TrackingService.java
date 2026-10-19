
package com.courier.service;

import java.sql.SQLException;
import java.util.List;

import com.courier.dao.TrackingDAO;
import com.courier.model.Shipment;

public class TrackingService {

    private final TrackingDAO trackingDAO = new TrackingDAO();

    public Shipment trackShipment(String trackingNumber)
            throws SQLException {

        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Tracking number cannot be empty.");
        }

        return trackingDAO.findShipmentByTrackingNumber(
                trackingNumber.trim().toUpperCase());
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

    private void validateShipmentId(long shipmentId) {
        if (shipmentId <= 0) {
            throw new IllegalArgumentException("Invalid shipment ID.");
        }
    }
}
