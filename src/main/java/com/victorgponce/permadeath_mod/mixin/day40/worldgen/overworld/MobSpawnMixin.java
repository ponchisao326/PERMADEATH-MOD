package com.victorgponce.permadeath_mod.mixin.day40.worldgen.overworld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobSpawnMixin {

    /**
     * This mixin allows mobs to spawn on mycelium blocks by using the grass block spawning logic.
     * It checks if the block below the spawn position is mycelium and allows spawning if it is.
     */
    @Inject(
            method = "canMobSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void allowSpawnOnMycelium(
            EntityType<? extends MobEntity> type,
            WorldAccess world,
            SpawnReason spawnReason,
            BlockPos pos,
            Random random,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // Obtain the block below the spawn position
        BlockPos downPos = pos.down();
        BlockState blockState = world.getBlockState(downPos);

        // Verify if the block is mycelium
        if (blockState.isOf(Blocks.MYCELIUM)) {
            // Create a grass block state for the check
            BlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();

            // Use the grass spawn logic for mycelium
            boolean canSpawn = grassState.allowsSpawning(world, downPos, type);
            if (canSpawn) {
                cir.setReturnValue(true);
            }
        }
    }
}