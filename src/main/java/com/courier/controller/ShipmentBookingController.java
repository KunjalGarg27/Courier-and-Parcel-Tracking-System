
package com.courier.controller;

import com.courier.model.Address;
import com.courier.model.Shipment;
import com.courier.service.ShipmentBookingService;

import java.sql.SQLException;

public class ShipmentBookingController {

    private final ShipmentBookingService bookingService =
            new ShipmentBookingService();

    public long bookShipment(
            long customerId,
            int serviceTypeId,
            String senderName,
            String receiverName,
            String senderPhone,
            String receiverPhone,
            String pickupAddressText,
            String deliveryAddressText
    ) throws SQLException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Please log in with a valid customer account."
            );
        }

        if (serviceTypeId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid shipment service."
            );
        }

        requireText(senderName, "Sender name");
        requireText(receiverName, "Receiver name");
        requireText(senderPhone, "Sender phone");
        requireText(receiverPhone, "Receiver phone");
        requireText(pickupAddressText, "Pickup address");
        requireText(deliveryAddressText, "Delivery address");

        Address pickup = new Address();
        pickup.setAddressLine1(pickupAddressText.trim());

        Address delivery = new Address();
        delivery.setAddressLine1(deliveryAddressText.trim());

        Shipment shipment = new Shipment();
        shipment.setCustomerId(customerId);
        shipment.setServiceTypeId(serviceTypeId);
        shipment.setSenderName(senderName.trim());
        shipment.setReceiverName(receiverName.trim());
        shipment.setSenderPhone(senderPhone.trim());
        shipment.setReceiverPhone(receiverPhone.trim());

        return bookingService.bookShipment(
                pickup, delivery, shipment
        );
    }

    private void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    field + " is required."
            );
        }
    }
}

