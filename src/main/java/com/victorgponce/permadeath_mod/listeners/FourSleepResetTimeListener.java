package com.victorgponce.permadeath_mod.listeners;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

public class FourSleepResetTimeListener implements EntitySleepEvents.AllowResettingTime {


    @Override
    public boolean allowResettingTime(PlayerEntity playerEntity) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));

        if (day == 10) {
            return OnSleepEvent.sleepingCount >= 4;
        }
        return false;
    }
}
