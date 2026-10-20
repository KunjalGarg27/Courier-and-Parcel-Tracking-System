package com.courier.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.courier.dao.DeliveryAgentDAO;

public class AgentDaoTest {

    public static void main(String[] args) {
        DeliveryAgentDAO dao = new DeliveryAgentDAO();

        try {
            long agentId = dao.getAgentIdByEmail(
                "agent.test@kahinbhi.com"
            );

            if (agentId == -1) {
                System.out.println("Agent account not found.");
                return;
            }

            System.out.println("Agent ID: " + agentId);

            List<String[]> deliveries =
                dao.getAssignedDeliveries(agentId);

            System.out.println("\nAssigned Deliveries: "
                + deliveries.size());

            for (String[] d : deliveries) {
                System.out.println(
                    d[2] + " | " + d[3] + " | " + d[6]
                );
            }

            Map<String, Integer> stats =
                dao.getDashboardStats(agentId);

            System.out.println("\nDashboard Stats: " + stats);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
