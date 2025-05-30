package com.victorgponce.permadeath_mod.client.data;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryDataHandler {
    private static final Logger LOGGER = LogManager.getLogger("PermadeathData");
    private static final String CONFIG_DIR = "PERMADEATH/data";
    private static final String FILE_NAME = "data.bin";

    private static BinaryDataHandler instance;
    private boolean firstExecution;
    private int day;
    private final Path configPath;

    private BinaryDataHandler() {
        // Crear directorio de configuración si no existe
        configPath = FabricLoader.getInstance().getConfigDir()
                .resolve(CONFIG_DIR)
                .resolve(FILE_NAME);

        createConfigDirectory();
        load();
    }

    public static BinaryDataHandler getInstance() {
        if (instance == null) {
            instance = new BinaryDataHandler();
        }
        return instance;
    }

    private void createConfigDirectory() {
        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            LOGGER.error("Error creando directorio de configuración: " + e.getMessage());
        }
    }

    public void load() {
        if (!Files.exists(configPath)) {
            // Archivo no existe, usar valor por defecto
            firstExecution = true;
            day = 0; // Valor por defecto para el día
            save();
            return;
        }

        try (DataInputStream dis = new DataInputStream(Files.newInputStream(configPath))) {
            firstExecution = dis.readBoolean();
            day = dis.readInt();
        } catch (IOException e) {
            LOGGER.error("Error cargando configuración: " + e.getMessage());
        }
    }

    public void save() {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(configPath))) {
            dos.writeBoolean(firstExecution);
            dos.writeInt(day);
        } catch (IOException e) {
            LOGGER.error("Error guardando configuración: " + e.getMessage());
        }
    }

    // Getter y Setter
    public boolean getFirstExecution() {
        return firstExecution;
    }

    public void setFirstExecution(boolean value) {
        this.firstExecution = value;
        save(); // Auto-guardado al modificar el valor
    }

    public int getDay() {return day;}

    public void setDay(int day) {
        this.day = day;
        save(); // Auto-guardado al modificar el día
    }
}