
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.courier.model.Shipment;

public class ShipmentDAO {

    public long createShipment(Connection con, Shipment shipment)
            throws SQLException {

        String sql = """
                INSERT INTO shipments (
                    tracking_number,
                    customer_id,
                    sender_name,
                    sender_phone,
                    receiver_name,
                    receiver_phone,
                    pickup_address_id,
                    delivery_address_id,
                    service_type_id,
                    package_description,
                    weight_kg,
                    expected_delivery_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, shipment.getTrackingNumber());
            ps.setLong(2, shipment.getCustomerId());
            ps.setString(3, shipment.getSenderName());
            ps.setString(4, shipment.getSenderPhone());
            ps.setString(5, shipment.getReceiverName());
            ps.setString(6, shipment.getReceiverPhone());
            ps.setLong(7, shipment.getPickupAddressId());
            ps.setLong(8, shipment.getDeliveryAddressId());
            ps.setInt(9, shipment.getServiceTypeId());
            ps.setString(10, shipment.getPackageDescription());

            if (shipment.getWeightKg() != null) {
                ps.setBigDecimal(11, shipment.getWeightKg());
            } else {
                ps.setNull(11, java.sql.Types.DECIMAL);
            }

            if (shipment.getExpectedDeliveryAt() != null) {
                ps.setTimestamp(
                        12,
                        java.sql.Timestamp.valueOf(
                                shipment.getExpectedDeliveryAt()
                        )
                );
            } else {
                ps.setNull(12, java.sql.Types.TIMESTAMP);
            }

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Shipment creation failed.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new SQLException(
                    "Shipment created but no shipment ID was returned."
            );
        }
    }
}
