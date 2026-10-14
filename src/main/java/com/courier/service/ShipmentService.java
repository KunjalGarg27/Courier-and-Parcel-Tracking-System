
package com.courier.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import com.courier.dao.AddressDAO;
import com.courier.dao.ShipmentDAO;
import com.courier.dao.ShipmentTrackingDAO;
import com.courier.model.Address;
import com.courier.model.Shipment;
import com.courier.util.DBConnection;

public class ShipmentService {

    private final AddressDAO addressDAO = new AddressDAO();
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final ShipmentTrackingDAO trackingDAO =
            new ShipmentTrackingDAO();

    public long bookShipment(
            Shipment shipment,
            Address pickupAddress,
            Address deliveryAddress,
            long customerId,
            Long recordedByUserId
    ) throws SQLException {

        if (shipment == null
                || pickupAddress == null
                || deliveryAddress == null) {
            throw new IllegalArgumentException(
                    "Shipment and both addresses are required."
            );
        }

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid customer ID is required."
            );
        }

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

                long shipmentId =
                        shipmentDAO.createShipment(con, shipment);

                trackingDAO.createTrackingEvent(
                        con,
                        shipmentId,
                        "BOOKED",
                        "Shipment booking created",
                        "Shipment booked successfully.",
                        recordedByUserId
                );

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

    private String generateTrackingNumber() {
        return "KB" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private void validateAddress(Address address, String label) {
        if (isBlank(address.getAddressLine1())
                || isBlank(address.getCity())
                || isBlank(address.getState())
                || isBlank(address.getPostalCode())) {
            throw new IllegalArgumentException(
                    label + " address must include address line, city, "
                    + "state, and postal code."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
