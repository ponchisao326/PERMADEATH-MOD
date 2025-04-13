package com.victorgponce.permadeath_mod.listeners;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CanSleepEvent implements EntitySleepEvents.AllowSleeping {

    @Override
    public PlayerEntity.@Nullable SleepFailureReason allowSleep(PlayerEntity playerEntity, BlockPos blockPos) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();
        int day = Integer.parseInt(lines.get(4));

        // For day 10 we let pass the sleep to make the counter increment
        // then we will force the player to wake up if they're not 4 players sleeping
        if (day >= 20) {
            playerEntity.sendMessage(Text.literal("Está prohibido dormir. Se ha reseteado su contador de phantoms"), false);
            playerEntity.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
            return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
        }
        return null;
    }
}
