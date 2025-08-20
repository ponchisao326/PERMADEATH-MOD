package com.victorgponce.permadeath_mod;

import com.victorgponce.permadeath_mod.util.mobcaps.MultiplayerHandler;
import com.victorgponce.permadeath_mod.util.mobcaps.SinglePlayerHandler;
import com.victorgponce.permadeath_mod.util.tickcounter.TaskManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Permadeath_modMain implements ModInitializer {

    public static final String MOD_ID = "PERMADEATH-COMMON";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            TaskManager.tick();

            // Only reset the counters after displaying the information
            SinglePlayerHandler singlePlayerHandler = SinglePlayerHandler.getInstance();
            MultiplayerHandler multiplayerHandler = MultiplayerHandler.getInstance();

            if (server != null && server.getWorlds() != null) {
                // First, count all existing entities
                multiplayerHandler.countEntitiesInWorlds(server.getWorlds());

                // In singleplayer mode, we also count the entities
                ServerWorld overworld = server.getOverworld();
                if (overworld != null) {
                    singlePlayerHandler.countEntitiesInWorld(overworld);
                }

                // Display information
                singlePlayerHandler.tick();
                multiplayerHandler.tick();

                // After displaying, clear for the next cycle
                singlePlayerHandler.resetCounts();
                multiplayerHandler.resetCounts();
            }
        });
    }
}
