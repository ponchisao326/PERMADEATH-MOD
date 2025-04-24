package com.victorgponce.permadeath_mod.mixin.day25;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public class RavagerEffects {

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();

        if (entity instanceof RavagerEntity ravager && day >= 25) {
            ravager.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 1)); // Speed I
            ravager.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 2)); // Strength II
        }
    }

}
