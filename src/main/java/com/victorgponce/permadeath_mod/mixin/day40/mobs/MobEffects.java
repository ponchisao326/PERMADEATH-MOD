package com.victorgponce.permadeath_mod.mixin.day40.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GuardianEntity;
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

    // List of status effects to apply to Creepers
    @Unique
    private static final List<StatusEffectInstance> statusEffectsCreeper = List.of(
            new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2),
            new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 2)
    );

    // List of status effects to apply to Guardians
    @Unique
    private static final List<StatusEffectInstance> statusEffectsGuardian = List.of(
            new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 2),
            new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2)
    );

    /**
     * This mixin applies custom effects to Creepers and Golems when they spawn after day 40.
     * Creepers will receive Speed and Resistance effects, while Golems will receive Strength.
     * The effects are applied indefinitely (Integer.MAX_VALUE duration). <br><br>
     *
     * This is linked to the {@link GuardianModifier} class, due to the fact that is the same change
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;

        if (entity instanceof CreeperEntity) {
            for (StatusEffectInstance effect : statusEffectsCreeper) {
                ((CreeperEntity) entity).addStatusEffect(effect);
            }
        }

        if (entity instanceof GolemEntity) {
            ((GolemEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 1));
        }

        if (entity instanceof GuardianEntity) {
            for (StatusEffectInstance effect : statusEffectsGuardian) {
                ((GuardianEntity) entity).addStatusEffect(effect);
            }
        }
    }
}
