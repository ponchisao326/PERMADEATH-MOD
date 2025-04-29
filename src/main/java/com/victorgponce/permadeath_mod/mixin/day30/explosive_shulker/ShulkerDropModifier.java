package com.victorgponce.permadeath_mod.mixin.day30.explosive_shulker;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ShulkerDropModifier {

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    public void onDropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getType() == EntityType.SHULKER) {
            // 20% chance to drop the item
            if (Random.create().nextInt(100) > 20) {
                cir.cancel();
            }
        }
    }
}
