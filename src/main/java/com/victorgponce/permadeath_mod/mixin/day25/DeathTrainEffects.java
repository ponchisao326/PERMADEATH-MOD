package com.victorgponce.permadeath_mod.mixin.day25;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ServerWorld.class)
public class DeathTrainEffects {

    @Unique
    private final List<StatusEffectInstance> availableDeathTrainEffects = Arrays.asList(
            new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 1),        // Strength I
            new StatusEffectInstance(StatusEffects.SPEED, 999999, 1),        // Speed I
            new StatusEffectInstance(StatusEffects.RESISTANCE, 999999, 1)         // Resistance I
    );

    /**
     * Inject at the HEAD of the addEntity method.
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        boolean deathTrainStatus = ConfigFileManager.readConfig().isDeathTrain();

        if (day >= 25 && deathTrainStatus) {
            if (entity instanceof Monster) {
                // Cast to LivingEntity to use addStatusEffect
                LivingEntity living = (LivingEntity) entity;
                for (StatusEffectInstance effect : availableDeathTrainEffects) {
                    // Create a new instance to avoid shared references
                    StatusEffectInstance newEffect = new StatusEffectInstance(
                            effect.getEffectType(),
                            effect.getDuration(),
                            effect.getAmplifier()
                    );
                    living.addStatusEffect(newEffect);
                }
            }
        }
    }

}
