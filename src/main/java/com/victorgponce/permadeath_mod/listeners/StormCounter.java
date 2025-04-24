package com.victorgponce.permadeath_mod.listeners;

import com.victorgponce.permadeath_mod.mixin.common.ServerWorldAccessor;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.HashMap;

public class StormCounter implements ServerTickEvents.EndTick {

    // Variable to store the remaining storm time
    private static int stormTimeRemaining = 0;

    @Override
    public void onEndTick(MinecraftServer minecraftServer) {
                // Check if it's thundering and the configuration allows storm tracking
        if (minecraftServer.getOverworld().isThundering() && ConfigFileManager.readConfig().isDeathTrain()) {
            ServerWorld serverWorld = minecraftServer.getOverworld();
            // Get the remaining thunder time using the mixin accessor
            stormTimeRemaining = ((ServerWorldAccessor) serverWorld).worldProperties().getThunderTime();
        } else {
            stormTimeRemaining = 0;
        }

        // Iterate through all players in the server
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            // If there is storm time remaining, send a formatted message to the player
            if (stormTimeRemaining > 0) {
                player.sendMessage(Text.literal("Storm Time Remaining: ")
                        .formatted(Formatting.BOLD) // Bold formatting
                        .append(Text.literal(String.valueOf(stormTimeRemaining / 20))
                                .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)))) // Green color
                        .append(Text.literal(" seconds")
                                .formatted(Formatting.ITALIC)), true); // Italic formatting
            }
        }
    }
}
