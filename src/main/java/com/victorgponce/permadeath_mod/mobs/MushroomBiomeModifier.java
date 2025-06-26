package com.victorgponce.permadeath_mod.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeKeys;

public class MushroomBiomeModifier {

    /**
     * This method initializes the Mushroom Biome modifier.
     * It adds various hostile mobs to the Mushroom Fields biome after day 40.
     */
    public static void init() {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;

        // Spider
        addSpawn(EntityType.SPIDER, 100, 4, 4);
        // Zombie
        addSpawn(EntityType.ZOMBIE, 95, 4, 4);
        // Zombie Villager
        addSpawn(EntityType.ZOMBIE_VILLAGER, 5, 1, 1);
        // Skeleton
        addSpawn(EntityType.SKELETON, 100, 4, 4);
        // Creeper
        addSpawn(EntityType.CREEPER, 100, 4, 4);
        // Slime
        addSpawn(EntityType.SLIME, 100, 4, 4);
        // Enderman
        addSpawn(EntityType.ENDERMAN, 10, 1, 4);
        // Witch
        addSpawn(EntityType.WITCH, 5, 1, 1);
    }

    /**
     * Adds a spawn entry for the specified entity type in the Mushroom Fields biome.
     *
     * @param entityType The type of entity to spawn.
     * @param weight The weight of the spawn entry.
     * @param minCount The minimum number of entities to spawn.
     * @param maxCount The maximum number of entities to spawn.
     */
    private static void addSpawn(EntityType<?> entityType, int weight, int minCount, int maxCount) {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS),
                SpawnGroup.MONSTER,
                entityType,
                weight,
                minCount,
                maxCount
        );
    }
}