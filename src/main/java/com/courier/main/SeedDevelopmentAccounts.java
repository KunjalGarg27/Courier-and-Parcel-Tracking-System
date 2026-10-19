
package com.courier.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.courier.util.DBConnection;

public class SeedDevelopmentAccounts {

    private static final String ADMIN_EMAIL =
            "admin.dev@kahinbhi.com";

    private static final String ADMIN_PASSWORD =
            "KahinBhiAdmin@2026";

    private static final String AGENT_EMAIL =
            "agent.test@kahinbhi.com";

    private static final String AGENT_PASSWORD =
            "KahinBhiAgent@2026";

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                createOrUpdateAdmin(con);
                updateAgentPassword(con);

                con.commit();

                System.out.println(
                        "Development accounts configured successfully."
                );
                System.out.println(
                        "Admin email: " + ADMIN_EMAIL
                );
                System.out.println(
                        "Admin password: " + ADMIN_PASSWORD
                );
                System.out.println(
                        "Agent email: " + AGENT_EMAIL
                );
                System.out.println(
                        "Agent password: " + AGENT_PASSWORD
                );
                System.out.println(
                        "Change these development passwords before "
                        + "using the application beyond testing."
                );

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.err.println(
                    "Could not configure development accounts."
            );
            e.printStackTrace();
        }
    }

    private static void createOrUpdateAdmin(Connection con)
            throws SQLException {

        String sql = """
                INSERT INTO users (
                    role_id,
                    full_name,
                    email,
                    password_hash,
                    account_status
                )
                SELECT
                    role_id,
                    ?,
                    ?,
                    ?,
                    'ACTIVE'
                FROM roles
                WHERE role_name = 'ADMIN'
                ON DUPLICATE KEY UPDATE
                    role_id = VALUES(role_id),
                    full_name = VALUES(full_name),
                    password_hash = VALUES(password_hash),
                    account_status = 'ACTIVE'
                """;

        String hash = BCrypt.hashpw(
                ADMIN_PASSWORD,
                BCrypt.gensalt(12)
        );

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "KahinBhi Development Admin");
            ps.setString(2, ADMIN_EMAIL);
            ps.setString(3, hash);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Admin setup failed. Check that the ADMIN "
                        + "role exists in the roles table."
                );
            }
        }

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT user_id FROM users WHERE email = ?")) {

            ps.setString(1, ADMIN_EMAIL);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "Admin account was not found after setup."
                    );
                }

                System.out.println(
                        "Admin user ID: " + rs.getLong("user_id")
                );
            }
        }
    }

    private static void updateAgentPassword(Connection con)
            throws SQLException {

        String sql = """
                UPDATE users
                SET password_hash = ?,
                    account_status = 'ACTIVE'
                WHERE email = ?
                  AND role_id = (
                      SELECT role_id
                      FROM roles
                      WHERE role_name = 'DELIVERY_AGENT'
                  )
                """;

        String hash = BCrypt.hashpw(
                AGENT_PASSWORD,
                BCrypt.gensalt(12)
        );

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, AGENT_EMAIL);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Test agent was not found. Check that "
                        + AGENT_EMAIL
                        + " exists with the DELIVERY_AGENT role."
                );
            }
        }
    }
}
