
package com.courier.main;

import java.sql.SQLException;
import java.util.List;

import com.courier.dao.TrackingDAO;
import com.courier.model.Shipment;

public class TrackingTest {

    public static void main(String[] args) {

        String trackingNumber = "KB493D431DE2F8";

        TrackingDAO trackingDAO = new TrackingDAO();

        try {
            // 1. Retrieve shipment details
            Shipment shipment =
                    trackingDAO.findShipmentByTrackingNumber(
                            trackingNumber
                    );

            if (shipment == null) {
                System.out.println("Shipment not found.");
                return;
            }

            System.out.println("=== SHIPMENT DETAILS ===");
            System.out.println(
                    "Shipment ID: " + shipment.getShipmentId()
            );
            System.out.println(
                    "Tracking Number: " + shipment.getTrackingNumber()
            );
            System.out.println(
                    "Sender: " + shipment.getSenderName()
            );
            System.out.println(
                    "Receiver: " + shipment.getReceiverName()
            );
            System.out.println(
                    "Package: " + shipment.getPackageDescription()
            );

            // 2. Retrieve current status
            String status =
                    trackingDAO.findCurrentStatus(
                            shipment.getShipmentId()
                    );

            System.out.println("\n=== CURRENT STATUS ===");
            System.out.println(status);

            // 3. Retrieve tracking history
            List<String> history =
                    trackingDAO.findTrackingHistory(
                            shipment.getShipmentId()
                    );

            System.out.println("\n=== TRACKING HISTORY ===");

            if (history.isEmpty()) {
                System.out.println("No tracking events found.");
            } else {
                for (String event : history) {
                    System.out.println(event);
                }
            }

        } catch (SQLException e) {
            System.err.println("Tracking test failed.");
            e.printStackTrace();
        }
    }
}
