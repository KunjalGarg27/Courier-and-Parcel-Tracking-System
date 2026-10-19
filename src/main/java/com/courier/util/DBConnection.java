
package com.courier.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/kahinbhi_db";

    private static final String USER =
            System.getenv("KAHINBHI_DB_USER");

    private static final String PASSWORD =
            System.getenv("KAHINBHI_DB_PASSWORD");

    private DBConnection() {
        // Prevent object creation
    }

    public static Connection getConnection() throws SQLException {

        if (USER == null || PASSWORD == null) {
            throw new SQLException(
                "Set KAHINBHI_DB_USER and KAHINBHI_DB_PASSWORD "
                + "environment variables before connecting."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
