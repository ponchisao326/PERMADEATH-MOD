package com.victorgponce.permadeath_mod.data;

import net.minecraft.server.world.ServerWorld;

/**
 * Class to get the current World everytime we want
 */
public class WorldHolder {
    private static ServerWorld overworld;

    public static void setOverworld(ServerWorld world) {
        overworld = world;
    }

    public static ServerWorld getOverworld() {
        if (overworld == null) {
            throw new IllegalStateException("The overworld hasn't been loaded yet");
        }
        return overworld;
    }
}

