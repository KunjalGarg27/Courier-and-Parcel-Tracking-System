
package com.courier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.courier.model.User;
import com.courier.util.DBConnection;

public class UserDAO {

    // Find a user by their unique email address.
    public User findByEmail(String email) throws SQLException {

        String sql = """
                SELECT user_id, role_id, full_name, email,
                       password_hash, account_status
                FROM users
                WHERE email = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getLong("user_id"),
                            rs.getInt("role_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("account_status")
                    );
                }
            }
        }

        return null;
    }

    // Check whether an email is already registered.
    public boolean emailExists(String email) throws SQLException {

        String sql = """
                SELECT 1
                FROM users
                WHERE email = ?
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Insert a user and return the generated user ID.
    public long createUser(User user) throws SQLException {

        String sql = """
                INSERT INTO users
                    (role_id, full_name, email,
                     password_hash, account_status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getAccountStatus());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new SQLException(
                    "Creating user failed: no generated ID returned."
            );
        }
    }

	public long createUser(
	        Connection con,
	        User user) throws SQLException {
	
	    String sql = """
	            INSERT INTO users
	                (role_id, full_name, email,
	                 password_hash, account_status)
	            VALUES (?, ?, ?, ?, ?)
	            """;
	
	    try (PreparedStatement ps = con.prepareStatement(
	            sql, Statement.RETURN_GENERATED_KEYS)) {
	
	        ps.setInt(1, user.getRoleId());
	        ps.setString(2, user.getFullName());
	        ps.setString(3, user.getEmail());
	        ps.setString(4, user.getPasswordHash());
	        ps.setString(5, user.getAccountStatus());
	
	        if (ps.executeUpdate() != 1) {
	            throw new SQLException("User creation failed.");
	        }
	
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) {
	                return keys.getLong(1);
	            }
	        }
	
	        throw new SQLException("No user ID was generated.");
	    }
	}
    public boolean updatePasswordHash(String email, String passwordHash)
            throws SQLException {

        String sql = """
            UPDATE users
            SET password_hash = ?
            WHERE email = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));

            return ps.executeUpdate() == 1;
        }
    }

 
}
