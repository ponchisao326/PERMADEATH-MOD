package com.victorgponce.permadeath_mod.mixin.day30.explosive_shulker;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBulletEntity.class)
public class ShulkerProjectileMixin {

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onProjectileHit(HitResult hitResult, CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        ShulkerBulletEntity projectile = (ShulkerBulletEntity) (Object) this;

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof PlayerEntity) {
                World world = projectile.getWorld();
                Vec3d pos = hitResult.getPos();

                // Generate TNT on the impact point
                TntEntity tnt = new TntEntity(EntityType.TNT, world);
                tnt.setPosition(pos.x, pos.y, pos.z);
                tnt.setFuse(20); // 1 second
                world.spawnEntity(tnt);
            }
        }
    }

}
