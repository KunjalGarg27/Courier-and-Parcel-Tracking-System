
package com.courier.service;

import java.sql.SQLException;
import java.util.Map;

import com.courier.dao.DashboardDAO;

public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public Map<String, Integer> getShipmentStatistics()
            throws SQLException {
        return dashboardDAO.getShipmentStatistics();
    }
}
