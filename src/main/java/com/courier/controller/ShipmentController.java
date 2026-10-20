package com.courier.controller;

import java.sql.SQLException;
import java.util.List;

import com.courier.model.Address;
import com.courier.model.CustomerStats;
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

    public CustomerStats getDashboardStats(long userId) throws SQLException {
        return shipmentService.getDashboardStats(userId);
    }

    public List<String[]> getMyShipments(long userId) throws SQLException {
        return shipmentService.getMyShipments(userId);
    }

    public List<String[]> getDeliveryHistory(long userId) throws SQLException {
        return shipmentService.getDeliveryHistory(userId);
    }
}