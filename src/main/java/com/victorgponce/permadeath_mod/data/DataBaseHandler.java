package com.victorgponce.permadeath_mod.data;

import java.sql.*;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class DataBaseHandler {

    public static void databaseConnector(String url, String user, String password, String sql) {

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            LOGGER.info("DataBase connection established correctly");

            // Create Players Table
            statement.executeUpdate(sql);
            LOGGER.info("SQL syntax inserted correctly");
        } catch (SQLException e) {
            throw new RuntimeException("Error while connecting to the database, please check the config.txt file found on config/PERMADEATH/", e);
        }
    }

    public static void databaseConnectorStatement(String url, String user, String password, String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
