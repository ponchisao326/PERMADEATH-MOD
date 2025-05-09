package com.victorgponce.permadeath_mod.data;

import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.core.jmx.Server;

/**
 * Class to get the current World everytime we want
 */
public class WorldHolder {
    private static ServerWorld overworld;
    private static ServerWorld end;

    public static void setOverworld(ServerWorld world) {
        overworld = world;
    }

    public static ServerWorld getOverworld() {
        if (overworld == null) {
            throw new IllegalStateException("The overworld hasn't been loaded yet");
        }
        return overworld;
    }

    public static void setEnd(ServerWorld world) {
        end = world;
    }

    public static ServerWorld getEnd() {
        if (end == null) {
            throw new IllegalStateException("The end hasn't been loaded yet");
        }
        return end;
    }
}

