package com.victorgponce.permadeath_mod.mixin.day10;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
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
     * Inject at HEAD of the addEntity method.
     */
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (lines.get(4).equals("10")) {
            // Check if it's a spider
            if (entity instanceof SpiderEntity) {
                SpiderEntity spider = (SpiderEntity) entity;

                Random random = new Random();
                Collections.shuffle(efectosDisponibles, random);
                int cantidadEfectos = 1 + random.nextInt(3); // Genera un número entre 1 y 3

                for (int i = 0; i < cantidadEfectos; i++) {
                    spider.addStatusEffect(efectosDisponibles.get(i));
                }
            }

            /*
              Double the mob (It should be double the mobcap, but because of the performance I'm gonna do this)
              If you want to modify this and set it to the mobcap, you should just search for the mobcap mixin and override
              the default mobcap to double or search for a method in the code that override this value
             */
            if (entity instanceof Monster) {
                EntityType<?> entityType = entity.getType();
                Entity newEntity = entityType.create(entity.getWorld(), SpawnReason.NATURAL);

                if (newEntity != null) {
                    newEntity.copyPositionAndRotation(entity);
                    entity.getWorld().spawnEntity(newEntity);
                }
            }
        }
    }

}
