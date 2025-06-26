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
        // Obtener el bloque debajo de la posición de spawn
        BlockPos downPos = pos.down();
        BlockState blockState = world.getBlockState(downPos);

        // Verificar si es micelio
        if (blockState.isOf(Blocks.MYCELIUM)) {
            // Crear un estado de bloque de hierba para la verificación
            BlockState grassState = Blocks.GRASS_BLOCK.getDefaultState();

            // Usar la lógica de spawn de hierba para micelio
            boolean canSpawn = grassState.allowsSpawning(world, downPos, type);
            if (canSpawn) {
                cir.setReturnValue(true);
            }
        }
    }
}