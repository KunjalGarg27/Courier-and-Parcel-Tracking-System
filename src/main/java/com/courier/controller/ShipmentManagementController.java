
package com.courier.controller;

import java.sql.SQLException;
import java.util.List;

import com.courier.service.ShipmentManagementService;

public class ShipmentManagementController {

    private final ShipmentManagementService shipmentService =
            new ShipmentManagementService();

    public List<String[]> getAllShipments() throws SQLException {
        return shipmentService.getAllShipments();
    }
}
