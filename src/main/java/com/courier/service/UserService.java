package com.courier.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.mindrot.jbcrypt.BCrypt;

import com.courier.dao.CustomerDAO;
import com.courier.dao.UserDAO;
import com.courier.model.User;
import com.courier.util.DBConnection;

public class UserService {

	private final UserDAO userDAO;
	private final CustomerDAO customerDAO = new CustomerDAO();

	public UserService() {
		this.userDAO = new UserDAO();
	}

	// Checks email, password and ACTIVE status. Does NOT check the role.
	public User authenticate(String email, String password)
			throws SQLException {

		if (email == null || email.isBlank()
				|| password == null || password.isEmpty()) {
			return null;
		}

		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
		User user = userDAO.findByEmail(normalizedEmail);

		if (user == null) {
			return null;
		}

		if (!"ACTIVE".equals(user.getAccountStatus())) {
			return null;
		}

		if (!BCrypt.checkpw(password, user.getPasswordHash())) {
			return null;
		}

		return user;
	}

	// Login that succeeds only if the credentials are valid AND the
	// user's role in the database matches the role selected on screen.
	public User authenticateAsRole(
			String email,
			String password,
			String roleName) throws SQLException {

		if (roleName == null || roleName.isBlank()) {
			throw new IllegalArgumentException("Select a role.");
		}

		User user;

		try {
			user = authenticate(email, password);
		} catch (IllegalArgumentException e) {
			// e.g. a placeholder/invalid password hash in the database
			return null;
		}

		if (user == null) {
			return null;
		}

		String sql = "SELECT 1 FROM roles WHERE role_id = ? AND role_name = ?";

		try (Connection con = DBConnection.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, user.getRoleId());
			ps.setString(2, roleName);

			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? user : null;
			}
		}
	}

	// Kept so existing customer-only code keeps working.
	public User authenticateCustomer(String email, String password)
			throws SQLException {
		return authenticateAsRole(email, password, "CUSTOMER");
	}

	public boolean emailExists(String email) throws SQLException {
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is required.");
		}

		return userDAO.emailExists(
				email.trim().toLowerCase(Locale.ROOT)
		);
	}

	public String hashPassword(String password) {
		if (password == null || password.length() < 8) {
			throw new IllegalArgumentException(
					"Password must contain at least 8 characters."
			);
		}

		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	public long registerCustomer(
			String fullName,
			String email,
			String phone,
			String password) throws SQLException {

		if (fullName == null || fullName.isBlank()) {
			throw new IllegalArgumentException("Name is required.");
		}

		if (email == null || email.isBlank()
				|| !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new IllegalArgumentException("Enter a valid email.");
		}

		if (phone == null || !phone.trim().matches("^[0-9+() -]{7,20}$")) {
			throw new IllegalArgumentException("Enter a valid phone number.");
		}

		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
		String normalizedName = fullName.trim();
		String normalizedPhone = phone.trim();

		String passwordHash = hashPassword(password);

		try (Connection con = DBConnection.getConnection()) {

			con.setAutoCommit(false);

			try {
				// Get the CUSTOMER role ID instead of assuming its numeric ID.
				int customerRoleId;

				String roleSql =
						"SELECT role_id FROM roles WHERE role_name = ?";

				try (PreparedStatement ps = con.prepareStatement(roleSql)) {
					ps.setString(1, "CUSTOMER");

					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next()) {
							throw new SQLException(
									"CUSTOMER role is missing from the database.");
						}

						customerRoleId = rs.getInt("role_id");
					}
				}

				User user = new User(
						0,
						customerRoleId,
						normalizedName,
						normalizedEmail,
						passwordHash,
						"ACTIVE"
				);

				long userId = userDAO.createUser(con, user);

				customerDAO.createCustomer(
						con,
						userId,
						normalizedPhone
				);

				con.commit();
				return userId;

			} catch (SQLException | RuntimeException e) {
				try {
					con.rollback();
				} catch (SQLException rollbackError) {
					e.addSuppressed(rollbackError);
				}
				throw e;
			}
		}
	}

	public boolean changePassword(String email, String newPassword) {
        return false;
    }
}