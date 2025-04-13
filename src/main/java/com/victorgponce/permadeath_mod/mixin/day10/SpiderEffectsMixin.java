package com.victorgponce.permadeath_mod.mixin.day10;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ServerWorld.class)
public class SpiderEffectsMixin {

    private List<StatusEffectInstance> efectosDisponibles = Arrays.asList(
            new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2),        // Velocidad III
            new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 3),     // Fuerza IV
            new StatusEffectInstance(StatusEffects.JUMP_BOOST, Integer.MAX_VALUE, 4),   // Salto V
            new StatusEffectInstance(StatusEffects.GLOWING, Integer.MAX_VALUE),         // Brillo
            new StatusEffectInstance(StatusEffects.REGENERATION, Integer.MAX_VALUE, 3), // Regeneración IV
            new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE),    // Invisibilidad
            new StatusEffectInstance(StatusEffects.SLOW_FALLING, Integer.MAX_VALUE),    // Caída lenta
            new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE)       // Resistencia
    );

    /**
     * Inject at the HEAD of the addEntity method.
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));

        if (day == 10) {
            // Check if it's a spider
            if (entity instanceof SpiderEntity) {
                SpiderEntity spider = (SpiderEntity) entity;

                Random random = new Random();
                Collections.shuffle(efectosDisponibles, random);
                int cantidadEfectos = 1 + random.nextInt(3); // Generate a number between 1 & 3

                for (int i = 0; i < cantidadEfectos; i++) {
                    spider.addStatusEffect(efectosDisponibles.get(i));
                }
            }
        } else if (day >= 20) {
            // Check if it's a spider
            if (entity instanceof SpiderEntity) {
                SpiderEntity spider = (SpiderEntity) entity;

                Random random = new Random();
                Collections.shuffle(efectosDisponibles, random);
                int cantidadEfectos = 3 + random.nextInt(3); // Generate a number between 3 & 5

                for (int i = 0; i < cantidadEfectos; i++) {
                    spider.addStatusEffect(efectosDisponibles.get(i));
                }
            }
        }
    }
}
