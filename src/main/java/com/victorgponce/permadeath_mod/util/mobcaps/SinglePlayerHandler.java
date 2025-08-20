package com.victorgponce.permadeath_mod.util.mobcaps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class SinglePlayerHandler {

    private static SinglePlayerHandler instance;
    private HashMap<EntityType, Integer> entityTypeMap = new HashMap<>();
    // Constants for mobcap limits
    private static final int MONSTER_CAP = 70;
    private static final int CREATURE_CAP = 10;
    private static final int AMBIENT_CAP = 15;
    private static final int AXOLOTL_CAP = 5;
    private static final int WATER_CREATURE_CAP = 5;
    private static final int WATER_AMBIENT_CAP = 20;
    private static final int UNDERGROUND_WATER_CREATURE_CAP = 5;
    private static final int MISC_CAP = 15;

    private SinglePlayerHandler() {
        // Private constructor for singleton
    }

    public static SinglePlayerHandler getInstance() {
        if (instance == null) {
            instance = new SinglePlayerHandler();
        }
        return instance;
    }

    public HashMap<EntityType, Integer> getEntityTypeMap() {
        return entityTypeMap;
    }

    public void setEntityTypeMap(HashMap<EntityType, Integer> entityTypeMap) {
        this.entityTypeMap = entityTypeMap;
    }

    public void addEntityType(EntityType entityType, int value) {
        entityTypeMap.put(entityType, entityTypeMap.getOrDefault(entityType, 0) + value);
    }

    public void resetCounts() {
        entityTypeMap.clear();
    }

    // New method to count all entities in the world
    public void countEntitiesInWorld(ServerWorld world) {
        for (Entity entity : world.iterateEntities()) {
            addEntityType(entity.getType(), 1);
        }
    }

    public void tick() {
        // Initialize counters by category
        Map<SpawnGroup, Integer> mobcapCounts = new HashMap<>();

        // Count entities by mobcap category
        this.entityTypeMap.forEach((entityType, value) -> {
            SpawnGroup spawnGroup = entityType.getSpawnGroup();
            mobcapCounts.put(spawnGroup, mobcapCounts.getOrDefault(spawnGroup, 0) + value);
        });

        // Display summary by category only if there is data
        if (!mobcapCounts.isEmpty()) {
            LOGGER.info("=== MOBCAP SUMMARY ===");
            mobcapCounts.forEach((spawnGroup, count) -> {
                int limit = getMobcapLimit(spawnGroup);
                float percentage = (float) count / limit * 100;

                LOGGER.info(String.format(
                        "MOBCAP - %s: %d/%d (%.1f%%)",
                        formatSpawnGroupName(spawnGroup),
                        count,
                        limit,
                        percentage
                ));
            });
            LOGGER.info("========================");
        }
    }

    private int getMobcapLimit(SpawnGroup spawnGroup) {
        return switch (spawnGroup) {
            case MONSTER -> MONSTER_CAP;
            case CREATURE -> CREATURE_CAP;
            case AMBIENT -> AMBIENT_CAP;
            case AXOLOTLS -> AXOLOTL_CAP;
            case WATER_CREATURE -> WATER_CREATURE_CAP;
            case WATER_AMBIENT -> WATER_AMBIENT_CAP;
            case UNDERGROUND_WATER_CREATURE -> UNDERGROUND_WATER_CREATURE_CAP;
            default -> MISC_CAP;
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