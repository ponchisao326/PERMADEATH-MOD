package com.victorgponce.permadeath_mod.client.util;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.victorgponce.permadeath_mod.Permadeath_mod;
import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

public class ClientConfigFileManager {
    private static final String CONFIG_DIR = "config/PERMADEATH";
    private static final String CONFIG_FILE = CONFIG_DIR + "/client_config.toml";

    /** Ensures the config/PERMADEATH folder exists */
    public static void createConfigFolder() {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Error creating the configuration folder");
        }
    }

    /**
     * Dumps the Config object to the TOML file,
     * overwriting the entire file with new values.
     */
    public static void saveConfig(@NotNull Config cfg) {
        File file = new File(CONFIG_FILE);
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(cfg, file);
            Permadeath_mod.LOGGER.info("Configuration saved to {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Error saving client_config.toml: " + e.getMessage(), e);
        }
    }

    /** Reads and parses client_config.toml into a Config object */
    public static @NotNull com.victorgponce.permadeath_mod.client.config.ClientConfig readConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            LOGGER.warn("client_config.toml not found, creating one with default values...");
            initialize();  // generates client_config.toml with default values
        }

        Toml toml = new Toml().read(file);
        com.victorgponce.permadeath_mod.client.config.ClientConfig cfg = new com.victorgponce.permadeath_mod.client.config.ClientConfig();
        cfg.setServerAddress(toml.getString("serverAddress"));
        cfg.setServerPort(((Long) toml.getLong("serverPort")).intValue());
        cfg.setEnabledServerCheck(toml.getBoolean("enabledServerCheck"));
        return cfg;
    }

    /** Initializes folder and file */
    public static void initialize() {
        File cfgFile = new File(CONFIG_FILE);
        if (!cfgFile.exists()) {
            // Opens the embedded resource in the JAR
            try (InputStream in = ConfigFileManager.class
                    .getClassLoader()
                    .getResourceAsStream("config/PERMADEATH/client_config.toml")) {
                if (in == null) {
                    throw new RuntimeException("Template client_config.toml not found in resources (Contact with the creator and make a new issue: https://github.com/ponchisao326/PERMADEATH-MOD)");
                }
                // Ensures the destination folder
                createConfigFolder();
                // Copies bytes to disk using Java NIO
                Files.copy(in, cfgFile.toPath());  // copies the entire InputStream to the file
                Permadeath_mod.LOGGER.info("Template client_config.toml installed in {}", CONFIG_FILE);
            } catch (IOException e) {
                throw new RuntimeException("Error copying client_config.toml from resources", e);
            }
        }
        // Now reads the configuration (or creates it if readConfig() requires)
        readConfig();
    }
}
