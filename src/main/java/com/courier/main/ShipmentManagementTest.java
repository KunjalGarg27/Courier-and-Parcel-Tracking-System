
package com.courier.main;

import java.sql.SQLException;
import java.util.List;

import com.courier.controller.ShipmentManagementController;

public class ShipmentManagementTest {

    public static void main(String[] args) {

        ShipmentManagementController controller =
                new ShipmentManagementController();

        try {
            List<String[]> shipments = controller.getAllShipments();

            if (shipments.isEmpty()) {
                System.out.println("No shipments found.");
                return;
            }

            System.out.println("=== KAHINBHI SHIPMENT MANAGEMENT ===");

            System.out.printf(
                    "%-12s %-20s %-18s %-18s %-20s %-20s%n",
                    "Shipment ID",
                    "Tracking Number",
                    "Sender",
                    "Receiver",
                    "Status",
                    "Delivery Agent"
            );

            System.out.println("-".repeat(112));

            for (String[] shipment : shipments) {
                System.out.printf(
                        "%-12s %-20s %-18s %-18s %-20s %-20s%n",
                        shipment[0],
                        shipment[1],
                        shipment[2],
                        shipment[3],
                        shipment[4],
                        shipment[5]
                );
            }

        } catch (SQLException e) {
            System.err.println("Failed to retrieve shipments.");
            e.printStackTrace();
        }
    }
}
