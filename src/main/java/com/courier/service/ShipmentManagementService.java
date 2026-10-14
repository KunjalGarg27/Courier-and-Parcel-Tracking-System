
package com.courier.service;

import java.sql.SQLException;
import java.util.List;

import com.courier.dao.ShipmentManagementDAO;

public class ShipmentManagementService {

    private final ShipmentManagementDAO shipmentDAO =
            new ShipmentManagementDAO();

    public List<String[]> getAllShipments() throws SQLException {
        return shipmentDAO.findAllShipments();
    }
}
