package com.victorgponce.permadeath_mod.util;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.victorgponce.permadeath_mod.config.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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

    /** Creates the TOML file with default values if it doesn't exist */
    public static void createDefaultConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            LOGGER.info("The configuration file already exists.");
            return;
        }

        Config defaultConfig = new Config();
        defaultConfig.setJdbc("jdbc:mysql://BDIP:3306/your_database");
        defaultConfig.setUser("your_user");
        defaultConfig.setPassword("your_password");
        defaultConfig.setDay(1);
        defaultConfig.setDeathTrain(false);

        TomlWriter writer = new TomlWriter();
        try {
            writer.write(defaultConfig, file);
            LOGGER.info("Configuration file created with default values.");
        } catch (IOException e) {
            throw new RuntimeException("Error writing config.toml: " + e.getMessage(), e);
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
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(cfg, file);
            LOGGER.info("Configuration saved to {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Error saving config.toml: " + e.getMessage(), e);
        }
    }

    /** Initializes folder and file */
    public static void initialize() {
        File cfgFile = new File(CONFIG_FILE);
        if (!cfgFile.exists()) {
            // Abre el recurso embebido en el JAR
            try (InputStream in = ConfigFileManager.class
                    .getClassLoader()
                    .getResourceAsStream("config/PERMADEATH/config.toml")) {
                if (in == null) {
                    throw new RuntimeException("Plantilla config.toml no encontrada en resources");
                }
                // Asegura la carpeta de destino
                createConfigFolder();
                // Copia bytes al disco con Java NIO
                Files.copy(in, cfgFile.toPath());  // copia todo el InputStream al fichero :contentReference[oaicite:1]{index=1}
                LOGGER.info("Plantilla config.toml instalada en {}", CONFIG_FILE);
            } catch (IOException e) {
                throw new RuntimeException("Error copiando config.toml desde resources", e);
            }
        }
        // Ahora lee la configuración (o la creas si readConfig() lo requiere)
        readConfig();
    }
}
