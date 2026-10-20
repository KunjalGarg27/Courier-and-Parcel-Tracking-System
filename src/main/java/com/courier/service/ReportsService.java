
package com.courier.service;

import com.courier.dao.ReportsDAO;
import java.sql.SQLException;
import java.util.List;

public class ReportsService {

    private final ReportsDAO dao = new ReportsDAO();

    public ReportsDAO.Summary getSummary() throws SQLException {
        return dao.getSummary();
    }

    public List<ReportsDAO.MonthCount> getMonthlyShipments()
            throws SQLException {
        return dao.getMonthlyShipments();
    }

    public List<ReportsDAO.ReportRow> generateReport(String type)
            throws SQLException {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                "Please select a report type.");
        }

        return dao.getReport(type);
    }
}
