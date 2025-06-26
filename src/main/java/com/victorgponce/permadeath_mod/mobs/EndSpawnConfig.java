package com.victorgponce.permadeath_mod.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeKeys;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class EndSpawnConfig {
    public static void init() {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        LOGGER.info("[PermadeathMod] Configurando spawns del End (Día: {})", day);
        // Selects all biomes in the End (Highlands, Midlands, Small Islands…)
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInTheEnd(),
                SpawnGroup.MONSTER,
                EntityType.GHAST,
                65,  // spawn weigh
                1,   // minimum 1
                3    // maximum 3
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.foundInTheEnd(),
                SpawnGroup.MONSTER,
                EntityType.CREEPER,
                8,
                1,
                2
        );
    }
}
