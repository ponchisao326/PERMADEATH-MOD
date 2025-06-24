package com.victorgponce.permadeath_mod.network;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class RecipeDisabler implements ServerLifecycleEvents.ServerStarted {
    @Override
    public void onServerStarted(MinecraftServer server) {
        int day = ConfigFileManager.readConfig().getDay();

        if (day >= 40) {
            // Disable torch recipes
            server.getRecipeManager().getAllOfType(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe.id().getValue().equals(Identifier.of("minecraft", "torch")) || recipe.id().getValue().equals(Identifier.of("minecraft", "redstone_torch"))) {
                    LOGGER.info("Disabling recipe: " + recipe.id().getValue());
                }
            });
            LOGGER.info("Torches and redstone torches recipes have been disabled (day ≥ 40).");
        }
    }
}
