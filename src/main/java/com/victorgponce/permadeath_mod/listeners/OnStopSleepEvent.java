package com.victorgponce.permadeath_mod.listeners;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class OnStopSleepEvent implements EntitySleepEvents.StopSleeping {
    @Override
    public void onStopSleeping(LivingEntity livingEntity, BlockPos bedPos) {
        OnSleepEvent.sleepingCount--;
    }
}
