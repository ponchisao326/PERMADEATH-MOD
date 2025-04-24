package com.victorgponce.permadeath_mod.util;

import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.WorldHolder;
import com.victorgponce.permadeath_mod.mixin.common.ServerWorldAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;

public class DeathTrain {
    public static void enableDeathTrain(DamageSource damageSource) {
        ServerWorld serverWorld = WorldHolder.getOverworld();

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
        Config cfg = ConfigFileManager.readConfig();
        cfg.setDeathTrain(true);
        // Guarda inmediatamente en el TOML:
        ConfigFileManager.saveConfig(cfg);
    }
}
