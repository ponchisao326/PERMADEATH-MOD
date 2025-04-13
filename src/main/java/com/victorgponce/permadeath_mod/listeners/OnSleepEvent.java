package com.victorgponce.permadeath_mod.listeners;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class OnSleepEvent implements EntitySleepEvents.StartSleeping {

    // Player counter actually sleeping in the world
    public static int sleepingCount = 0;

    @Override
    public void onStartSleeping(LivingEntity livingEntity, BlockPos bedPos) {
        sleepingCount++;
    }
}
