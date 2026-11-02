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

    // =========================================================
    // CUSTOMER MODULE
    // =========================================================

    public List<ServiceType> getActiveServiceTypes() throws SQLException {
        return serviceTypeDAO.findActive();
    }

    public CustomerStats getDashboardStats(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findStatsByUserId(userId);
    }

    public List<String[]> getMyShipments(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findShipmentsByUserId(userId);
    }

    public List<String[]> getDeliveryHistory(long userId) throws SQLException {
        requireLoggedIn(userId);
        return customerShipmentDAO.findDeliveredByUserId(userId);
    }

    /**
     * Books a shipment for the logged-in customer.
     * Returns the generated tracking number.
     */
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

    // =========================================================
    // ADMIN / EXISTING SHIPMENT BOOKING WORKFLOW
    // =========================================================

    /**
     * Books a shipment for a specified customer.
     * Returns the new shipment ID.
     */
    public long bookShipment(
            Shipment shipment,
            Address pickupAddress,
            Address deliveryAddress,
            long customerId,
            Long recordedByUserId) throws SQLException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid customer ID is required.");
        }

        validateShipment(shipment);
        validateAddress(pickupAddress, "Pickup");
        validateAddress(deliveryAddress, "Delivery");

        shipment.setCustomerId(customerId);
        shipment.setTrackingNumber(generateTrackingNumber());

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                long pickupAddressId =
                        addressDAO.createAddress(con, pickupAddress);

                long deliveryAddressId =
                        addressDAO.createAddress(con, deliveryAddress);

                shipment.setPickupAddressId(pickupAddressId);
                shipment.setDeliveryAddressId(deliveryAddressId);

                long shipmentId = shipmentDAO.createShipment(con, shipment);

                trackingDAO.createTrackingEvent(
                        con,
                        shipmentId,
                        "BOOKED",
                        "Shipment booking created",
                        "Shipment booked successfully.",
                        recordedByUserId);

                con.commit();
                return shipmentId;

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

    // =========================================================
    // VALIDATION AND HELPERS
    // =========================================================

    private void requireLoggedIn(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "You are not logged in. Please log in again.");
        }
    }

    private String generateTrackingNumber() {
        return "KB" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    private void validateShipment(Shipment shipment) {
        if (shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment details are missing.");
        }

        shipment.setSenderName(
                requireText(shipment.getSenderName(), "Sender name", 120));
        shipment.setSenderPhone(
                requirePhone(shipment.getSenderPhone(), "Sender phone"));
        shipment.setReceiverName(
                requireText(shipment.getReceiverName(), "Receiver name", 120));
        shipment.setReceiverPhone(
                requirePhone(shipment.getReceiverPhone(), "Receiver phone"));

        if (shipment.getServiceTypeId() <= 0) {
            throw new IllegalArgumentException("Select a service type.");
        }

        shipment.setPackageDescription(optionalText(
                shipment.getPackageDescription(), "Package description", 500));

        BigDecimal weight = shipment.getWeightKg();

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

        shipment.setWeightKg(weight);
    }

    private void validateAddress(Address address, String label) {
        if (address == null) {
            throw new IllegalArgumentException(
                    label + " address is missing.");
        }

        address.setAddressLine1(requireText(
                address.getAddressLine1(),
                label + " address line 1", 180));

        address.setAddressLine2(optionalText(
                address.getAddressLine2(),
                label + " address line 2", 180));

        address.setLandmark(optionalText(
                address.getLandmark(), label + " landmark", 120));

        address.setCity(requireText(
                address.getCity(), label + " city", 100));

        address.setState(requireText(
                address.getState(), label + " state", 100));

        String postalCode = requireText(
                address.getPostalCode(), label + " postal code", 15);

        if (!postalCode.matches(POSTAL_PATTERN)) {
            throw new IllegalArgumentException(
                    "Enter a valid "
                            + label.toLowerCase(Locale.ROOT)
                            + " postal code.");
        }

        address.setPostalCode(postalCode);

        String country = optionalText(
                address.getCountry(), label + " country", 100);

        address.setCountry(country == null ? "India" : country);
    }

    private String requireText(
            String value, String label, int maxLength) {

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

    private String optionalText(
            String value, String label, int maxLength) {

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
                    "Enter a valid "
                            + label.toLowerCase(Locale.ROOT) + ".");
        }

        return phone;
    }
}
