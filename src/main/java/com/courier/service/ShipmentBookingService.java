
package com.courier.service;

import com.courier.dao.AddressDAO;
import com.courier.dao.ShipmentDAO;
import com.courier.model.Address;
import com.courier.model.Shipment;
import com.courier.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class ShipmentBookingService {

    private final AddressDAO addressDAO = new AddressDAO();
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();

    public long bookShipment(
            Address pickupAddress,
            Address deliveryAddress,
            Shipment shipment
    ) throws SQLException {

        if (pickupAddress == null || deliveryAddress == null
                || shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment and both addresses are required."
            );
        }

        if (shipment.getCustomerId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid logged-in customer is required."
            );
        }

        if (shipment.getServiceTypeId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid service type is required."
            );
        }

        if (isBlank(shipment.getSenderName())
                || isBlank(shipment.getReceiverName())) {
            throw new IllegalArgumentException(
                    "Sender and receiver names are required."
            );
        }

        if (isBlank(pickupAddress.getAddressLine1())
                || isBlank(deliveryAddress.getAddressLine1())) {
            throw new IllegalArgumentException(
                    "Pickup and delivery addresses are required."
            );
        }

        if (isBlank(shipment.getTrackingNumber())) {
            shipment.setTrackingNumber(
                    "KB" + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase()
            );
        }

        try (Connection con = DBConnection.getConnection()) {
            boolean previousAutoCommit = con.getAutoCommit();

            try {
                con.setAutoCommit(false);

                long pickupId =
                        addressDAO.createAddress(con, pickupAddress);

                long deliveryId =
                        addressDAO.createAddress(con, deliveryAddress);

                shipment.setPickupAddressId(pickupId);
                shipment.setDeliveryAddressId(deliveryId);

                long shipmentId =
                        shipmentDAO.createShipment(con, shipment);

                con.commit();
                return shipmentId;

            } catch (SQLException | RuntimeException ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;

            } finally {
                try {
                    con.setAutoCommit(previousAutoCommit);
                } catch (SQLException ignored) {
                    // Connection is closed by try-with-resources.
                }
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
