package com.victorgponce.permadeath_mod.effects;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SpiderEntity;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class SpiderEffectsBase {
    protected List<StatusEffectInstance> efectosDisponibles;

    public SpiderEffectsBase(List<StatusEffectInstance> efectosDisponibles) {
        this.efectosDisponibles = efectosDisponibles;
    }

    protected void applyEffects(SpiderEntity spider, int minEffects, int maxEffects) {
        Random random = new Random();
        Collections.shuffle(efectosDisponibles, random);
        int cantidadEfectos = minEffects + random.nextInt(maxEffects - minEffects + 1);

        for (int i = 0; i < cantidadEfectos; i++) {
            spider.addStatusEffect(efectosDisponibles.get(i));
        }
    }
}
