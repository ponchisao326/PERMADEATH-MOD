package com.victorgponce.permadeath_mod.drops;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

public class RavagerDropDay20 extends RavagerDropBase {
    @Override
    public void applyDrops(LivingEntity entity, Item item, ServerWorld world) {
        if (Random.create().nextInt(100) < 1) {
            super.applyDrops(entity, item, world);
        }
    }
}
