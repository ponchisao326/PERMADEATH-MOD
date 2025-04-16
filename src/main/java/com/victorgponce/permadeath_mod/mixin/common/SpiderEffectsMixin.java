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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ServerWorld.class)
public class SpiderEffectsMixin {

    private List<StatusEffectInstance> efectosDisponibles = Arrays.asList(
            new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2),
            new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 3),
            new StatusEffectInstance(StatusEffects.JUMP_BOOST, Integer.MAX_VALUE, 4),
            new StatusEffectInstance(StatusEffects.GLOWING, Integer.MAX_VALUE),
            new StatusEffectInstance(StatusEffects.REGENERATION, Integer.MAX_VALUE, 3),
            new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE),
            new StatusEffectInstance(StatusEffects.SLOW_FALLING, Integer.MAX_VALUE),
            new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE)
    );

    @Inject(method = "addEntity", at = @At("HEAD"))
    private void onSpiderSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();
        int day = Integer.parseInt(lines.get(4));

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
