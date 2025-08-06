package com.victorgponce.permadeath_mod.mixin.day40.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerWorld.class)
public class MobEffects {

    @Unique
    private static final List<StatusEffectInstance> statusEffectsCreeper = List.of(
            new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SPEED, Integer.MAX_VALUE, 2),
            new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.RESISTANCE, Integer.MAX_VALUE, 2)
    );

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof CreeperEntity) {
            for (StatusEffectInstance effect : statusEffectsCreeper) {
                ((CreeperEntity) entity).addStatusEffect(effect);
            }
        }

        if (entity instanceof GolemEntity) {
            ((GolemEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 1));
        }
    }
}
