package com.victorgponce.permadeath_mod.mixin.day30.end_monsters;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.entity.mob.GhastEntity$ShootFireballGoal")
public abstract class GhastTeleportMixin {

    @Shadow @Final private GhastEntity ghast;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onAfterShoot(CallbackInfo ci) {
        LivingEntity target = ghast.getTarget();
        if (target == null) return;
        ServerWorld world = (ServerWorld) ghast.getEntityWorld();
        if (!world.getRegistryKey().equals(World.END)) return;

        double dx = (world.random.nextDouble() * 10) - 5;
        double dz = (world.random.nextDouble() * 10) - 5;
        double dy = (world.random.nextDouble() * 2) - 1;

        ghast.teleport(target.getX() + dx, target.getY() + dy, target.getZ() + dz, true);
    }
}

