package com.victorgponce.permadeath_mod.util.mobcaps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class MultiplayerHandler {

    private static MultiplayerHandler instance;
    private Map<ServerWorld, Map<EntityType, Integer>> worldEntityCounts = new HashMap<>();

    // Base constants for each mobcap type
    private static final int MONSTER_BASE = 70;
    private static final int CREATURE_BASE = 10;
    private static final int AMBIENT_BASE = 15;
    private static final int AXOLOTL_BASE = 5;
    private static final int WATER_CREATURE_BASE = 5;
    private static final int WATER_AMBIENT_BASE = 20;
    private static final int UNDERGROUND_WATER_CREATURE_BASE = 5;
    private static final int MISC_BASE = 15;

    private MultiplayerHandler() {
        // Private constructor for singleton
    }

    public static MultiplayerHandler getInstance() {
        if (instance == null) {
            instance = new MultiplayerHandler();
        }
        return instance;
    }

    public void addEntity(ServerWorld world, EntityType entityType) {
        worldEntityCounts.computeIfAbsent(world, k -> new HashMap<>());
        Map<EntityType, Integer> entityCounts = worldEntityCounts.get(world);
        entityCounts.put(entityType, entityCounts.getOrDefault(entityType, 0) + 1);
    }

    public void resetCounts() {
        worldEntityCounts.clear();
    }

    // New method to count all entities in all worlds
    public void countEntitiesInWorlds(Iterable<ServerWorld> worlds) {
        for (ServerWorld world : worlds) {
            Map<EntityType, Integer> entityCounts = worldEntityCounts.computeIfAbsent(world, k -> new HashMap<>());
            for (Entity entity : world.iterateEntities()) {
                entityCounts.put(entity.getType(), entityCounts.getOrDefault(entity.getType(), 0) + 1);
            }
        }
    }

    public Map<EntityType, Integer> getEntityCountsForWorld(ServerWorld world) {
        return worldEntityCounts.getOrDefault(world, new HashMap<>());
    }

    public void tick() {
        if (worldEntityCounts.isEmpty()) {
            return; // No data to display
        }

        worldEntityCounts.forEach((world, entityCounts) -> {
            // Group by SpawnGroup
            Map<SpawnGroup, Integer> mobcapCounts = new HashMap<>();

            entityCounts.forEach((entityType, count) -> {
                SpawnGroup spawnGroup = entityType.getSpawnGroup();
                mobcapCounts.put(spawnGroup, mobcapCounts.getOrDefault(spawnGroup, 0) + count);
            });

            // Calculate and display mobcaps only if there is data
            if (!mobcapCounts.isEmpty()) {
                int loadedChunks = world.getChunkManager().getLoadedChunkCount();
                LOGGER.debug("=== MOBCAPS FOR " + world.getRegistryKey().getValue() + " ===");
                LOGGER.debug("Loaded chunks: " + loadedChunks);

                mobcapCounts.forEach((spawnGroup, count) -> {
                    int limit = calculateMobcapLimit(spawnGroup, loadedChunks);
                    float percentage = limit > 0 ? (float) count / limit * 100 : 0;

                    LOGGER.debug(String.format(
                            "MOBCAP - %s: %d/%d (%.1f%%)",
                            formatSpawnGroupName(spawnGroup),
                            count,
                            limit,
                            percentage
                    ));
                });
                LOGGER.debug("========================");
            }
        });
    }

    private int calculateMobcapLimit(SpawnGroup spawnGroup, int loadedChunks) {
        // Prevent division by zero
        if (loadedChunks <= 0) return 0;

        // Apply the formula: mobCap = constant + (289/chunksEnRango)
        int baseValue = getBaseValue(spawnGroup);
        return (int) Math.ceil(baseValue + (289.0 / loadedChunks));
    }

    private int getBaseValue(SpawnGroup spawnGroup) {
        return switch (spawnGroup) {
            case MONSTER -> MONSTER_BASE;
            case CREATURE -> CREATURE_BASE;
            case AMBIENT -> AMBIENT_BASE;
            case AXOLOTLS -> AXOLOTL_BASE;
            case WATER_CREATURE -> WATER_CREATURE_BASE;
            case WATER_AMBIENT -> WATER_AMBIENT_BASE;
            case UNDERGROUND_WATER_CREATURE -> UNDERGROUND_WATER_CREATURE_BASE;
            default -> MISC_BASE;
        };
    }

    private String formatSpawnGroupName(SpawnGroup group) {
        return switch (group) {
            case MONSTER -> "Hostiles";
            case CREATURE -> "Pasivos";
            case AMBIENT -> "Ambientales";
            case AXOLOTLS -> "Axolotls";
            case WATER_CREATURE -> "Criaturas Acuáticas";
            case WATER_AMBIENT -> "Ambiente Acuático";
            case UNDERGROUND_WATER_CREATURE -> "Criaturas Acuáticas Subterráneas";
            default -> group.getName();
        };
    }
}