package com.victorgponce.permadeath_mod.data;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryServerDataHandler {
    private static final Logger LOGGER = LogManager.getLogger("PermadeathServerData");
    private static final String CONFIG_DIR = "PERMADEATH/data";
    private static final String FILE_NAME = "server-data.bin";

    private static BinaryServerDataHandler instance;
    private boolean doubleShulkerShell;

    private final Path configPath;

    private BinaryServerDataHandler() {
        // Create config directory if it does not exist
        configPath = FabricLoader.getInstance().getConfigDir()
                .resolve(CONFIG_DIR)
                .resolve(FILE_NAME);

        createConfigDirectory();
        load();
    }

    public static BinaryServerDataHandler getInstance() {
        if (instance == null) {
            instance = new BinaryServerDataHandler();
        }
        return instance;
    }

    private void createConfigDirectory() {
        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            LOGGER.error("Error creating config directory: " + e.getMessage());
        }
    }

    public void load() {
        if (!Files.exists(configPath)) {
            // File does not exist, use default value
            doubleShulkerShell = false;
            save();
            return;
        }

        try (DataInputStream dis = new DataInputStream(Files.newInputStream(configPath))) {
            doubleShulkerShell = dis.readBoolean();
        } catch (IOException e) {
            LOGGER.error("Error loading config: " + e.getMessage());
        }
    }

    public void save() {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(configPath))) {
            dos.writeBoolean(doubleShulkerShell);
        } catch (IOException e) {
            LOGGER.error("Error saving config: " + e.getMessage());
        }
    }

    public boolean getDoubleShulkerShell() {
        return doubleShulkerShell;
    }

    public void setDoubleShulkerShell() {
        this.doubleShulkerShell = !this.doubleShulkerShell;
        save(); // Auto-save when modifying the value
    }
}