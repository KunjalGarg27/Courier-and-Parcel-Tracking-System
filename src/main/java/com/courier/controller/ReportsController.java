
package com.courier.controller;

import com.courier.dao.ReportsDAO;
import com.courier.service.ReportsService;

import java.sql.SQLException;
import java.util.List;

public class ReportsController {

    private final ReportsService service = new ReportsService();

    public ReportsDAO.Summary getSummary() throws SQLException {
        return service.getSummary();
    }

    public List<ReportsDAO.MonthCount> getMonthlyShipments()
            throws SQLException {
        return service.getMonthlyShipments();
    }

    public List<ReportsDAO.ReportRow> generateReport(String type)
            throws SQLException {
        return service.generateReport(type);
    }
}
