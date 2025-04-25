package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.effects.SpiderEffectsDay10;
import com.victorgponce.permadeath_mod.effects.SpiderEffectsDay20;
import com.victorgponce.permadeath_mod.effects.SpiderEffectsDay25;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ServerWorld.class)
public class SpiderEffectsMixin {

    @Unique
    private final List<StatusEffectInstance> efectosDisponibles = Arrays.asList(
            new StatusEffectInstance(StatusEffects.SPEED, 999999, 2),
            new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 3),
            new StatusEffectInstance(StatusEffects.JUMP_BOOST, 999999, 4),
            new StatusEffectInstance(StatusEffects.GLOWING, 999999),
            new StatusEffectInstance(StatusEffects.REGENERATION, 999999, 3),
            new StatusEffectInstance(StatusEffects.INVISIBILITY, 999999),
            new StatusEffectInstance(StatusEffects.SLOW_FALLING, 999999),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 999999)
    );

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();

        if (entity instanceof SpiderEntity spider) {
            if (day >= 10 && day < 20) {
                new SpiderEffectsDay10(efectosDisponibles).applyEffects(spider);
            } else if (day >= 20 && day < 25) {
                new SpiderEffectsDay20(efectosDisponibles).applyEffects(spider);
            } else if (day >= 25) {
                new SpiderEffectsDay25(efectosDisponibles).applyEffects(spider);
            }
        }
    }
}
