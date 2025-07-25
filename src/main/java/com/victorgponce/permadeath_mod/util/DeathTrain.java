package com.victorgponce.permadeath_mod.util;

import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.WorldHolder;
import com.victorgponce.permadeath_mod.mixin.common.ServerWorldAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DeathTrain {
    public static void enableDeathTrain(DamageSource damageSource) {
        ServerWorld serverWorld = WorldHolder.getOverworld();

        if (serverWorld.isThundering()) {
            int currentTime = ((ServerWorldAccessor) serverWorld).worldProperties().getThunderTime();
            int newDuration = currentTime + 72000;
            serverWorld.setWeather(0, newDuration, true, true);
            serverWorld.getServer().getPlayerManager().broadcast(Text.literal("The DeathTrain have been set to " +
                            (((ServerWorldAccessor) serverWorld).worldProperties().getThunderTime() / 20) + "seconds")
                    .formatted(Formatting.RED, Formatting.BOLD), false);
        } else {
            serverWorld.setWeather(0, 72000, true, true);
            serverWorld.getServer().getPlayerManager().broadcast(Text.literal("The DeathTrain have been set to 1 hour")
                    .formatted(Formatting.RED, Formatting.BOLD), false);
        }
        Config cfg = ConfigFileManager.readConfig();
        cfg.setDeathTrain(true);
        // Guarda inmediatamente en el TOML:
        ConfigFileManager.saveConfig(cfg);
    }
}
