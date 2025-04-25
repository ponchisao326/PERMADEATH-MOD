package com.victorgponce.permadeath_mod.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class EndSpawnConfig {
    public static void init() {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        // Selects all biomes in the End (Highlands, Midlands, Small Islands…)
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInTheEnd(),
                SpawnGroup.MONSTER,
                EntityType.GHAST,
                8,  // spawn weight
                1,   // minimum group size
                4    // maximum group size
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
