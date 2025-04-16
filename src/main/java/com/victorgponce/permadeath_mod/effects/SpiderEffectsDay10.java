package com.victorgponce.permadeath_mod.effects;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SpiderEntity;

import java.util.List;

public class SpiderEffectsDay10 extends SpiderEffectsBase {
    public SpiderEffectsDay10(List<StatusEffectInstance> efectosDisponibles) {
        super(efectosDisponibles);
    }

    public void applyEffects(SpiderEntity spider) {
        super.applyEffects(spider, 1, 3);
    }
}
