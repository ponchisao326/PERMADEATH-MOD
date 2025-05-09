package com.victorgponce.permadeath_mod.mixin.day20.drops;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.DropHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(LivingEntity.class)
public class CancelMobDropsMixin {

    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    private void onDropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        int day = ConfigFileManager.readConfig().getDay();

        if (day >= 20) {
            if (DropHelper.shouldCancelDrop(entity)) {
                // We cancel the drop for this entites
                ci.cancel();
            }
        }
    }
}
