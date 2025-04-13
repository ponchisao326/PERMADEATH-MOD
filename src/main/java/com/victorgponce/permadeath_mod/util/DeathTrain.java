package com.victorgponce.permadeath_mod.util;

import com.victorgponce.permadeath_mod.data.WorldHolder;
import com.victorgponce.permadeath_mod.mixin.common.ServerWorldAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.*;

public class DeathTrain {
    public static void enableDeathTrain(DamageSource damageSource) {
        ServerWorld serverWorld = WorldHolder.getOverworld();

        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (serverWorld.isThundering()) {
            int currentTime = ((ServerWorldAccessor) serverWorld).worldProperties().getThunderTime();
            int newDuration = currentTime + 72000;
            serverWorld.setWeather(0, newDuration, true, true);
            serverWorld.getServer().getPlayerManager().broadcast(Text.literal("El deathTrain Se ha seteado a " +
                            (((ServerWorldAccessor) serverWorld).worldProperties().getThunderTime() / 20) + "segundos")
                    .formatted(Formatting.RED, Formatting.BOLD), false);
        } else {
            serverWorld.setWeather(0, 72000, true, true);
            serverWorld.getServer().getPlayerManager().broadcast(Text.literal("El deathTrain Se ha seteado a 1 hora")
                    .formatted(Formatting.RED, Formatting.BOLD), false);
        }
        try {
            replaceLineInFile("config/PERMADEATH/config.txt", 5, "true");
        } catch (IOException e) {
            throw new RuntimeException("There was an error writing the config file " + e.getMessage(), e);
        }
    }

    public static void replaceLineInFile(String filePath, int lineNumber, String newLine) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int currentLine = 1;
        while ((line = reader.readLine()) != null) {
            if (currentLine == lineNumber) {
                lines.add(newLine);
            } else {
                lines.add(line);
            }
            currentLine++;
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (String l : lines) {
            writer.write(l);
            writer.newLine();
        }
        writer.close();
    }
}
