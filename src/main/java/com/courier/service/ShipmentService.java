package com.courier.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.courier.dao.AddressDAO;
import com.courier.dao.CustomerDAO;
import com.courier.dao.CustomerShipmentDAO;
import com.courier.dao.ServiceTypeDAO;
import com.courier.dao.ShipmentDAO;
import com.courier.dao.ShipmentTrackingDAO;
import com.courier.model.Address;
import com.courier.model.CustomerStats;
import com.courier.model.ServiceType;
import com.courier.model.Shipment;
import com.courier.util.DBConnection;

public class ShipmentService {

    private static final String PHONE_PATTERN = "^[0-9+() -]{7,20}$";
    private static final String POSTAL_PATTERN = "^[A-Za-z0-9 -]{3,15}$";
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("99999.999");

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AddressDAO addressDAO = new AddressDAO();
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final ShipmentTrackingDAO trackingDAO = new ShipmentTrackingDAO();
    private final ServiceTypeDAO serviceTypeDAO = new ServiceTypeDAO();
    private final CustomerShipmentDAO customerShipmentDAO =
            new CustomerShipmentDAO();

    public List<ServiceType> getActiveServiceTypes() throws SQLException {
        return serviceTypeDAO.findActive();
    }

    // Shipment counts for the logged-in customer's dashboard.
    public CustomerStats getDashboardStats(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findStatsByUserId(userId);
    }

    // Rows for the My Shipments table: only this customer's shipments.
    public List<String[]> getMyShipments(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findShipmentsByUserId(userId);
    }

    // Rows for Delivery History: only this customer's DELIVERED shipments.
    public List<String[]> getDeliveryHistory(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findDeliveredByUserId(userId);
    }

    // Books a shipment for the logged-in user and returns the tracking number.
    public String bookShipment(
            long userId,
            Shipment shipment,
            Address pickup,
            Address delivery) throws SQLException {

        requireLoggedIn(userId);

        validateShipment(shipment);
        validateAddress(pickup, "Pickup");
        validateAddress(delivery, "Delivery");

        String trackingNumber = generateTrackingNumber();

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                long customerId =
                        customerDAO.findCustomerIdByUserId(con, userId);

                long pickupId = addressDAO.createAddress(con, pickup);
                long deliveryId = addressDAO.createAddress(con, delivery);

                shipment.setTrackingNumber(trackingNumber);
                shipment.setCustomerId(customerId);
                shipment.setPickupAddressId(pickupId);
                shipment.setDeliveryAddressId(deliveryId);

                long shipmentId = shipmentDAO.createShipment(con, shipment);

                // Status lives in shipment_tracking_events, so add BOOKED.
                trackingDAO.createTrackingEvent(
                        con,
                        shipmentId,
                        "BOOKED",
                        "Shipment booking created",
                        "Shipment booked successfully.",
                        userId);

                con.commit();
                return trackingNumber;

            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    private void requireLoggedIn(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "You are not logged in. Please log in again.");
        }
    }

    // Same format as existing rows: "KB" + 12 uppercase hex characters.
    private String generateTrackingNumber() {
        return "KB" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    private void validateShipment(Shipment s) {
        if (s == null) {
            throw new IllegalArgumentException(
                    "Shipment details are missing.");
        }

        s.setSenderName(requireText(s.getSenderName(), "Sender name", 120));
        s.setSenderPhone(requirePhone(s.getSenderPhone(), "Sender phone"));
        s.setReceiverName(
                requireText(s.getReceiverName(), "Receiver name", 120));
        s.setReceiverPhone(
                requirePhone(s.getReceiverPhone(), "Receiver phone"));

        if (s.getServiceTypeId() <= 0) {
            throw new IllegalArgumentException("Select a service type.");
        }

        s.setPackageDescription(optionalText(
                s.getPackageDescription(), "Package description", 500));

        BigDecimal weight = s.getWeightKg();
        if (weight == null) {
            throw new IllegalArgumentException(
                    "Enter the package weight in kg.");
        }

        weight = weight.setScale(3, RoundingMode.HALF_UP);

        if (weight.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be greater than 0.");
        }
        if (weight.compareTo(MAX_WEIGHT) > 0) {
            throw new IllegalArgumentException(
                    "Weight is too large (maximum 99999.999 kg).");
        }

        s.setWeightKg(weight);
    }

    private void validateAddress(Address a, String label) {
        if (a == null) {
            throw new IllegalArgumentException(
                    label + " address is missing.");
        }

        a.setAddressLine1(requireText(
                a.getAddressLine1(), label + " address line 1", 180));
        a.setAddressLine2(optionalText(
                a.getAddressLine2(), label + " address line 2", 180));
        a.setLandmark(optionalText(
                a.getLandmark(), label + " landmark", 120));
        a.setCity(requireText(a.getCity(), label + " city", 100));
        a.setState(requireText(a.getState(), label + " state", 100));

        String postal = requireText(
                a.getPostalCode(), label + " postal code", 15);
        if (!postal.matches(POSTAL_PATTERN)) {
            throw new IllegalArgumentException(
                    "Enter a valid " + label.toLowerCase(Locale.ROOT)
                            + " postal code.");
        }
        a.setPostalCode(postal);

        String country = optionalText(
                a.getCountry(), label + " country", 100);
        a.setCountry(country == null ? "India" : country);
    }

    private String requireText(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        String trimmed = value.trim();

        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    label + " is too long (maximum "
                            + maxLength + " characters).");
        }

        return trimmed;
    }

    private String optionalText(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();

        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    label + " is too long (maximum "
                            + maxLength + " characters).");
        }

        return trimmed;
    }

    private String requirePhone(String value, String label) {
        String phone = requireText(value, label, 20);

        if (!phone.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException(
                    "Enter a valid " + label.toLowerCase(Locale.ROOT) + ".");
        }

        return phone;
    }
}