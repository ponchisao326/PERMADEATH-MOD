package com.victorgponce.permadeath_mod.drops;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class RavagerDropDay25 extends RavagerDropBase {
    @Override
    public void applyDrops(LivingEntity entity, Item item, ServerWorld world) {
        // Lógica específica para el día 25
        if (Random.create().nextInt(100) <= 20) {
            super.applyDrops(entity, item, world);
        }
    }
}
