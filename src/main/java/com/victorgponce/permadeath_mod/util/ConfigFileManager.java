package com.victorgponce.permadeath_mod.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class ConfigFileManager {

    public static void createConfigFolder() {
        // Config Directory Creation
        File configDir = new File("config/PERMADEATH");
        if (!configDir.exists()) {
            boolean created = configDir.mkdirs();
            if (!created) {
                throw new RuntimeException("There was an error while creating the config folder");
            }
        }
    }

    public static void createFile() {
        // Main Config File Creation
        String dir = "config/PERMADEATH/config.txt";
        try {
            File configFile = new File(dir);
            if (configFile.createNewFile()) {
                LOGGER.info("File created correctly.");
            } else {
                LOGGER.info("The file already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException("There was an error while creating de config file (config.txt): " + e.getMessage(), e);
        }

        try {
            HashMap<Integer, String> lines;
            lines = ConfigFileManager.readFile();
            if (lines.isEmpty()) {
                // Write the Default Config
                FileWriter writer = new FileWriter(dir);

                // JDBC
                writer.write("jdbc:mysql://BDIP:3306/your_database");
                writer.write(System.lineSeparator());
                // User
                writer.write("your_user");
                writer.write(System.lineSeparator());
                // Password
                writer.write("your_password");
                writer.write(System.lineSeparator());
                // Day
                writer.write("1");
                writer.write(System.lineSeparator());
                // DeathTrain
                writer.write("false");
                writer.write(System.lineSeparator());
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("There was an error writing the config file " + e.getMessage(), e);
        }
    }

    /**
     * Key - Value <br>
     * 1 - JDBC <br>
     * 2 - USER <br>
     * 3 - PASSWORD <br>
     * 4 - DAY <br>
     * 5 - DeathTrain <br>
     */
    public static @NotNull HashMap<Integer, String> readFile() {
        HashMap<Integer, String> lines = new HashMap<>();
        String dir = "config/PERMADEATH/config.txt";
        int acc = 0;

        try {
            File config = new File(dir);
            Scanner reader = new Scanner(config);
            while (reader.hasNextLine()) {
                acc++;
                String line = reader.nextLine();
                lines.put(acc, line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("There was an error while reading the config file, this might not have been created correctly! " + e.getMessage(), e);
        }

        return lines;
    }
}
