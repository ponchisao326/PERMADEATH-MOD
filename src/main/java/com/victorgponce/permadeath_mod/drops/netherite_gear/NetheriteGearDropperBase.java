package com.victorgponce.permadeath_mod.drops.netherite_gear;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public abstract class NetheriteGearDropperBase {
    protected void applyDrops(LivingEntity entity, ItemStack item) {
        entity.dropItem(item, true, true);
    }
}
