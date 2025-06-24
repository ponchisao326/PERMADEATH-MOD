package com.victorgponce.permadeath_mod.network;

import com.victorgponce.permadeath_mod.data.WorldHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class WorldHolderHandler implements ServerLifecycleEvents.ServerStarted {

    /**
     * This method is called when the server starts.
     * It stores the Overworld and End world in the WorldHolder class.
     *
     * @param server The Minecraft server instance.
     */
    @Override
    public void onServerStarted(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        WorldHolder.setOverworld(overworld);
        LOGGER.info("The Overworld have been stored correctly on WorldHolder.");

        ServerWorld end = server.getWorld(World.END);
        WorldHolder.setEnd(end);
        LOGGER.info("The End have been stored correctly on WorldHolder.");
    }
}
