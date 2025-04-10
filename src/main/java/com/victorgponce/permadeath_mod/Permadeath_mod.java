package com.victorgponce.permadeath_mod;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Permadeath_mod implements ModInitializer {

    public static final String MOD_ID = "PERMADEATH-SERVER";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initiating Permadeath (Server Side)");
        LOGGER.info("Permadeath Original autor: KernelFreeze");
        LOGGER.info("This mod is a Fan-Made mod for the PERMADEATH Series by ELRICHMC");
        LOGGER.info("Made with Love by Ponchisao326");

        // Config Folder and File creator
        ConfigFileManager.createConfigFolder();
        ConfigFileManager.createFile();

        // Connection to the DB
        HashMap<Integer, String> lines;
        lines = ConfigFileManager.readFile();

        String url = lines.get(1);
        String user = lines.get(2);
        String password = lines.get(3);

        // Regex for URL validating
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid URL found (Line 1) on the config file, must be in this format: jdbc:mysql://BDIP:3306/your_database");
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            LOGGER.info("DataBase connection established correctly");
        } catch (SQLException e) {
            throw new RuntimeException("Error while connecting to the database, please check the config.txt file found on config/PERMADEATH/", e);
        }
    }
}
