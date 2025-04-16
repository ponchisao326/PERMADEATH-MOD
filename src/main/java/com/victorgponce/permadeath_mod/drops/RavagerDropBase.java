package com.victorgponce.permadeath_mod.drops;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public abstract class RavagerDropBase {
    protected void applyDrops(LivingEntity entity, Item item, ServerWorld world) {
        if (entity instanceof RavagerEntity) {
            // Add Item
            entity.dropItem(new ItemStack(item), true, true);
        }
    }
}
