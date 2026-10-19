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

            System.out.println("\n=== KAHINBHI SHIPMENT MANAGEMENT ===");

            System.out.printf(
                "%-12s %-20s %-22s %-18s %-18s %-22s %-20s%n",
                "Shipment ID",
                "Tracking Number",
                "Customer",
                "Origin",
                "Destination",
                "Delivery Agent",
                "Status"
            );

            System.out.println("-".repeat(135));

            for (String[] shipment : shipments) {
                System.out.printf(
                    "%-12s %-20s %-22s %-18s %-18s %-22s %-20s%n",
                    shipment[0],
                    shipment[1],
                    shipment[2],
                    shipment[3],
                    shipment[4],
                    shipment[5],
                    shipment[6]
                );
            }

        } catch (SQLException e) {
            System.err.println("Failed to load shipments.");
            e.printStackTrace();
        }
    }
}