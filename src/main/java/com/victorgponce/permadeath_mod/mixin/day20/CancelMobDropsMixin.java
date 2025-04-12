package com.victorgponce.permadeath_mod.mixin.day20;

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

    @Inject(method = "dropLoot", at = @At("HEAD"))
    private void onDropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (lines.get(4).equals("20")) {
            if (DropHelper.shouldCancelDrop(entity)) {
                // We cancel the drop  for this entites
                ci.cancel();
            }
        }
    }
}
