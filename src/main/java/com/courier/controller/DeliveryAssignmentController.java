
package com.courier.controller;

import java.sql.SQLException;

import com.courier.service.DeliveryAssignmentService;

public class DeliveryAssignmentController {

    private final DeliveryAssignmentService assignmentService =
            new DeliveryAssignmentService();

    public long assignShipment(
            long shipmentId,
            long agentId,
            long assignedByUserId
    ) throws SQLException {

        return assignmentService.assignShipment(
                shipmentId,
                agentId,
                assignedByUserId
        );
    }
}
