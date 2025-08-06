package com.victorgponce.permadeath_mod.mixin.day40.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.EntitiesCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class SpiderTransformation {

    // ThreadLocal to avoid recursion in custom spawns
    @Unique
    private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    // Limit for the maximum number of cave spiders
    @Unique
    private static final int MAX_CAVE_SPIDERS = 30;

    /**
     * This mixin transforms normal spiders into cave spiders after day 40.
     * It limits the number of cave spiders to 30 and applies a speed effect.
     * The cave spider will have a custom name "Araña Venenosa".
     */
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // Avoids recursion if already in a custom spawn
        if (inCustomSpawn.get()) return;

        int day = ConfigFileManager.readConfig().getDay();
        // Only apply after day 40
        if (day < 40) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();

            // Only proceed if we are in a ServerWorld
            if (!(world instanceof ServerWorld serverWorld)) return;

            // Transform normal spiders into cave spiders
            if (entity instanceof SpiderEntity && EntitiesCounter.caveSpiderCount < MAX_CAVE_SPIDERS) {
                cir.setReturnValue(false);

                CaveSpiderEntity caveSpider = new CaveSpiderEntity(EntityType.CAVE_SPIDER, serverWorld);
                caveSpider.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                serverWorld.spawnEntity(caveSpider);
                EntitiesCounter.caveSpiderCount++;
            }

            // Avoid spawning more normal spiders if the limit is reached
            if (entity instanceof SpiderEntity && EntitiesCounter.caveSpiderCount >= MAX_CAVE_SPIDERS) {
                cir.setReturnValue(false);
            }

        } finally {
            // This ensures that the flag is reset after processing
            inCustomSpawn.set(false);
        }
    }
}