package com.victorgponce.permadeath_mod.mixin.day30.explosive_shulker;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class ShulkerDeathMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void onShulkerDeath(Entity.RemovalReason reason, CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        Entity entity = (Entity) (Object) this;

        if (entity instanceof ShulkerEntity shulker) {
            if (reason == Entity.RemovalReason.KILLED) {
                World world = shulker.getWorld();
                BlockPos pos = shulker.getBlockPos();

                // Generate TNT on the shulker position
                TntEntity tnt = new TntEntity(EntityType.TNT, world);
                tnt.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                tnt.setFuse(40); // 2 seconds
                world.spawnEntity(tnt);
            }
        }

    }
}