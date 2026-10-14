
package com.courier.main;

import java.math.BigDecimal;

import com.courier.model.Address;
import com.courier.model.Shipment;
import com.courier.service.ShipmentService;

public class ShipmentBookingTest {

    public static void main(String[] args) {

        // Use the customer_id of an existing row in customers.
        long customerId = 1;

        // Use the users.user_id belonging to that customer.
        Long recordedByUserId = 1L;

        Address pickup = new Address();
        pickup.setAddressLine1("House 10, Sector 14");
        pickup.setCity("Faridabad");
        pickup.setState("Haryana");
        pickup.setPostalCode("121007");
        pickup.setCountry("India");

        Address delivery = new Address();
        delivery.setAddressLine1("House 25, Sector 18");
        delivery.setCity("Gurugram");
        delivery.setState("Haryana");
        delivery.setPostalCode("122015");
        delivery.setCountry("India");

        Shipment shipment = new Shipment();
        shipment.setSenderName("Test Sender");
        shipment.setSenderPhone("9876543210");
        shipment.setReceiverName("Test Receiver");
        shipment.setReceiverPhone("9876543211");
        shipment.setServiceTypeId(1); // Verify STANDARD's ID first.
        shipment.setPackageDescription("Test parcel");
        shipment.setWeightKg(new BigDecimal("2.50"));

        try {
            ShipmentService service = new ShipmentService();

            long shipmentId = service.bookShipment(
                    shipment,
                    pickup,
                    delivery,
                    customerId,
                    recordedByUserId
            );

            System.out.println("Shipment booking successful!");
            System.out.println("Shipment ID: " + shipmentId);
            System.out.println(
                    "Tracking number: " + shipment.getTrackingNumber()
            );

        } catch (Exception e) {
            System.err.println("Shipment booking failed.");
            e.printStackTrace();
        }
    }
}
