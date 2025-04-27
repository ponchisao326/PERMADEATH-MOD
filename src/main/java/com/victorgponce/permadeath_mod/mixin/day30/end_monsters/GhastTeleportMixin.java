package com.victorgponce.permadeath_mod.mixin.day30.end_monsters;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.entity.mob.GhastEntity$ShootFireballGoal")
public abstract class GhastTeleportMixin {

    @Shadow @Final private GhastEntity ghast;
    @Shadow private int cooldown;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onAfterShoot(CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        if (this.cooldown != -40 || ghast.isDead()) return;

        LivingEntity target = ghast.getTarget();
        if (target == null) return;

        ServerWorld world = (ServerWorld) ghast.getEntityWorld();
        if (!world.getRegistryKey().equals(World.END)) return;

        // Try up to 5 different positions
        for (int i = 0; i < 5; i++) {
            double dx = target.getX() + (world.random.nextDouble() * 20 - 10);
            double dz = target.getZ() + (world.random.nextDouble() * 20 - 10);

            // Calculate safe height (minimum 10 blocks above the ground)
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) Math.floor(dx), (int) Math.floor(dz));
            double dy = Math.max(target.getY() + 10, topY + 15.0); // +15 for extra safety

            // Adjust to avoid low teleportation
            dy = Math.max(dy, world.getBottomY() + 15);

            // Check space in 3x3x3 blocks around
            if (isAreaClear(world, dx, dy, dz)) {
                boolean success = ghast.teleport(dx, dy, dz, true);
                if (success) return; // Exit if successful
            }
        }
    }

    @Unique
    private boolean isAreaClear(ServerWorld world, double x, double y, double z) {
        // Create an area of 3x3x3 blocks around the position
        return world.isSpaceEmpty(ghast.getBoundingBox()
                .expand(1.5) // Add extra margin
                .offset(x - ghast.getX(), y - ghast.getY(), z - ghast.getZ()));
    }
}
