package com.victorgponce.permadeath_mod.mixin.day10;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public class DoubleMobsMixin {

    // Flag to avoid recursive duplication
    @Unique
    private static boolean duplicating = false;

    /**
     * Double the mob (It should be double the mobcap, but because of the performance I'm going to do this)
     * If you want to modify this and set it to the mobcap, you should just search for the mobcap mixin and override
     * the default mobcap to double or search for a method in the code that override this value.
     * An additional check is added: if the entity spawns in the Nether, duplication is skipped
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();

        if (entity instanceof Monster && day >= 10) {
            World world = entity.getWorld();
            // Avoid dupping if the entity is generating on the nether (Dupped on tp to other dim)
            if (world.getRegistryKey().equals(World.NETHER)
            || entity instanceof SlimeEntity
            || entity instanceof MagmaCubeEntity
            || entity instanceof GhastEntity) {
                return;
            }

            if (day >= 30 && (entity instanceof GuardianEntity || entity instanceof BlazeEntity)) return;

            if (!duplicating) {
                duplicating = true;
                try {
                    EntityType<?> entityType = entity.getType();
                    Entity newEntity = entityType.create(world, SpawnReason.NATURAL);
                    if (newEntity != null) {
                        newEntity.copyPositionAndRotation(entity);
                        world.spawnEntity(newEntity);

                        if (newEntity instanceof PhantomEntity phantomEntity && day >= 20) {
                            phantomEntity.setPhantomSize(9);
                            phantomEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
                            phantomEntity.setHealth(40);
                        }
                    }
                } finally {
                    duplicating = false;
                }
            }
        }
    }
}
