package com.courier.controller;

import java.sql.SQLException;

import com.courier.model.TrackingResult;
import com.courier.service.TrackingService;

public class TrackingController {

    private final TrackingService trackingService = new TrackingService();

    public TrackingResult track(String trackingNumber) throws SQLException {
        return trackingService.track(trackingNumber);
    }
}
