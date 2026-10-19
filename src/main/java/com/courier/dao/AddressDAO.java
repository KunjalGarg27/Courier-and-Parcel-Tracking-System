 package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.courier.model.Address;

public class AddressDAO {

    // Saves an address using the caller's transaction.
    public long createAddress(
            Connection con,
            Address address) throws SQLException {

        String sql = """
                INSERT INTO addresses (
                    address_line1,
                    address_line2,
                    landmark,
                    city,
                    state,
                    postal_code,
                    country,
                    latitude,
                    longitude
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, address.getAddressLine1());
            ps.setString(2, address.getAddressLine2());
            ps.setString(3, address.getLandmark());
            ps.setString(4, address.getCity());
            ps.setString(5, address.getState());
            ps.setString(6, address.getPostalCode());
            ps.setString(7, address.getCountry());

            if (address.getLatitude() != null) {
                ps.setBigDecimal(8, address.getLatitude());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            if (address.getLongitude() != null) {
                ps.setBigDecimal(9, address.getLongitude());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Address creation failed.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new SQLException(
                    "Address created but no address ID was returned."
            );
        }
    }
}
