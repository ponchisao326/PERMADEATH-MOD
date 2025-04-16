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
    private final List<StatusEffectInstance> efectosDisponiblesDeathTrain = Arrays.asList(
            new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 1),        // Fuerza I
            new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 1),        // Velocidad I
            new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 1)         // Resistencia I
    );

    /**
     * Inject at the HEAD of the addEntity method.
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));
        boolean deathTrainStatus = Boolean.parseBoolean(lines.get(5));

        if (day >= 25 && deathTrainStatus) {
            if (entity instanceof Monster) {
                // Hacemos cast a LivingEntity para poder usar addStatusEffect
                LivingEntity living = (LivingEntity) entity;
                for (StatusEffectInstance efecto : efectosDisponiblesDeathTrain) {
                    // Crea una nueva instancia para evitar referencias compartidas
                    StatusEffectInstance nuevoEfecto = new StatusEffectInstance(
                            efecto.getEffectType(),
                            efecto.getDuration(),
                            efecto.getAmplifier()
                    );
                    living.addStatusEffect(nuevoEfecto);
                }
            }
        }
    }

}
