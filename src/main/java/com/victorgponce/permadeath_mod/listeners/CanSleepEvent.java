package com.victorgponce.permadeath_mod.listeners;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CanSleepEvent implements EntitySleepEvents.AllowSleeping {

    @Override
    public PlayerEntity.@Nullable SleepFailureReason allowSleep(PlayerEntity playerEntity, BlockPos blockPos) {
        int day = ConfigFileManager.readConfig().getDay();

        // For day 10 we let the sleep pass to make the counter increment
        // then we will force the player to wake up if there are not 4 players sleeping
        if (day >= 20) {
            playerEntity.sendMessage(Text.literal("Sleeping is forbidden. Your phantom counter has been reset."), false);
            playerEntity.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
            return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
        }
        return null;
    }
}