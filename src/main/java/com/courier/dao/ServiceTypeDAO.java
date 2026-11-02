package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.courier.model.ServiceType;
import com.courier.util.DBConnection;

public class ServiceTypeDAO {

    public List<ServiceType> findActive() throws SQLException {

        List<ServiceType> types = new ArrayList<>();

        String sql = """
                SELECT service_type_id, service_name
                FROM service_types
                WHERE is_active = 1
                ORDER BY service_type_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                types.add(new ServiceType(
                        rs.getInt("service_type_id"),
                        rs.getString("service_name")));
            }
        }

        return types;
    }
}
