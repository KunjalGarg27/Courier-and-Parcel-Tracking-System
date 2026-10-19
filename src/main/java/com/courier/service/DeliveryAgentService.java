package com.courier.service;

import java.sql.SQLException;
import java.util.List;

import com.courier.dao.DeliveryAgentDAO;

public class DeliveryAgentService {

    private final DeliveryAgentDAO agentDAO =
            new DeliveryAgentDAO();

    public List<String[]> getAllAgents() throws SQLException {
        return agentDAO.findAllAgents();
    }

    public String[] getLatestLocation(long agentId)
            throws SQLException {

        if (agentId <= 0) {
            throw new IllegalArgumentException(
                    "A valid delivery agent ID is required."
            );
        }

        return agentDAO.findLatestLocation(agentId);
    }
}