
package com.courier.service;

import java.sql.SQLException;
import java.util.List;

import com.courier.dao.AssignmentOptionsDAO;

public class AssignmentOptionsService {

    private final AssignmentOptionsDAO optionsDAO =
            new AssignmentOptionsDAO();

    public List<String[]> getUnassignedShipments()
            throws SQLException {
        return optionsDAO.findUnassignedShipments();
    }
}
