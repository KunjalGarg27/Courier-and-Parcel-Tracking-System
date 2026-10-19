
package com.courier.controller;

import java.sql.SQLException;

import com.courier.service.ShipmentStatusService;

public class ShipmentStatusController {

    private final ShipmentStatusService service =
            new ShipmentStatusService();

    public void updateStatus(
            long shipmentId,
            String statusName,
            String locationDescription,
            String notes,
            Long recordedByUserId
    ) throws SQLException {

        service.updateStatus(
                shipmentId,
                statusName,
                locationDescription,
                notes,
                recordedByUserId
        );
    }
}
