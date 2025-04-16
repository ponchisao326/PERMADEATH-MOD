package com.victorgponce.permadeath_mod.effects;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SpiderEntity;

import java.util.List;

public class SpiderEffectsDay25 extends SpiderEffectsBase {
    public SpiderEffectsDay25(List<StatusEffectInstance> efectosDisponibles) {
        super(efectosDisponibles);
    }

    public void applyEffects(SpiderEntity spider) {
        super.applyEffects(spider, 5, 5);
    }
}
