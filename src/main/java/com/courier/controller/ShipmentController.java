package com.courier.controller;

import java.sql.SQLException;
import java.util.List;

import com.courier.model.Address;
import com.courier.model.ServiceType;
import com.courier.model.Shipment;
import com.courier.service.ShipmentService;

public class ShipmentController {

    private final ShipmentService shipmentService = new ShipmentService();

    public List<ServiceType> getServiceTypes() throws SQLException {
        return shipmentService.getActiveServiceTypes();
    }

    public String bookShipment(
            long userId,
            Shipment shipment,
            Address pickup,
            Address delivery) throws SQLException {

        return shipmentService.bookShipment(
                userId, shipment, pickup, delivery);
    }
}
