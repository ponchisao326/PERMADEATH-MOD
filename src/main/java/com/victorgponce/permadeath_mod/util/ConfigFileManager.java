package com.victorgponce.permadeath_mod.util;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.victorgponce.permadeath_mod.config.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class ConfigFileManager {
    private static final String CONFIG_DIR = "config/PERMADEATH";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.toml";

    /** Ensures the config/PERMADEATH folder exists */
    public static void createConfigFolder() {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Error creating the configuration folder");
        }
    }

    /** Reads and parses config.toml into a Config object */
    public static @NotNull Config readConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            LOGGER.warn("config.toml not found, creating one with default values...");
            initialize();  // generates config.toml with default values
        }

        Toml toml = new Toml().read(file);
        Config cfg = new Config();
        cfg.setJdbc(toml.getString("jdbc"));
        cfg.setUser(toml.getString("user"));
        cfg.setPassword(toml.getString("password"));
        cfg.setDay(((Long) toml.getLong("day")).intValue());
        cfg.setDeathTrain(toml.getBoolean("deathTrain"));
        return cfg;
    }

    /**
     * Dumps the Config object to the TOML file,
     * overwriting the entire file with new values.
     */
    public static void saveConfig(@NotNull Config cfg) {
        File file = new File(CONFIG_FILE);
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            Map<String, String> updates = new HashMap<>();
            updates.put("jdbc", "\"" + cfg.getJdbc() + "\"");
            updates.put("user", "\"" + cfg.getUser() + "\"");
            updates.put("password", "\"" + cfg.getPassword() + "\"");
            updates.put("day", String.valueOf(cfg.getDay()));
            updates.put("deathTrain", String.valueOf(cfg.isDeathTrain()));

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                for (Map.Entry<String, String> entry : updates.entrySet()) {
                    if (line.startsWith(entry.getKey())) {
                        lines.set(i, entry.getKey() + " = " + entry.getValue());
                        updates.remove(entry.getKey());
                        break;
                    }
                }
            }

            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            LOGGER.info("Configuration updated in {}", CONFIG_FILE);

        } catch (IOException e) {
            throw new RuntimeException("Error updating config.toml: " + e.getMessage(), e);
        }
    }

    /** Initializes folder and file */
    public static void initialize() {
        File cfgFile = new File(CONFIG_FILE);
        if (!cfgFile.exists()) {
            // Opens the embedded resource in the JAR
            try (InputStream in = ConfigFileManager.class
                    .getClassLoader()
                    .getResourceAsStream("config/PERMADEATH/config.toml")) {
                if (in == null) {
                    throw new RuntimeException("Template config.toml not found in resources (Contact with the creator and make a new issue: https://github.com/ponchisao326/PERMADEATH-MOD)");
                }
                // Ensures the destination folder
                createConfigFolder();
                // Copies bytes to disk using Java NIO
                Files.copy(in, cfgFile.toPath());  // copies the entire InputStream to the file
                LOGGER.info("Template config.toml installed in {}", CONFIG_FILE);
            } catch (IOException e) {
                throw new RuntimeException("Error copying config.toml from resources", e);
            }
        }
        // Now reads the configuration (or creates it if readConfig() requires)
        readConfig();
    }

}
