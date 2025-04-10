package com.victorgponce.permadeath_mod.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

}
