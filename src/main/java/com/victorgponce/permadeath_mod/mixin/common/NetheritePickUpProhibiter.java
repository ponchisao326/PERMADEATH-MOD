package com.victorgponce.permadeath_mod.mixin.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.victorgponce.permadeath_mod.data.NetheriteItemHelper.prohibitedItems;

@Mixin(PlayerInventory.class)
public abstract class NetheritePickUpProhibiter {

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onItemPickup(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (player.isInCreativeMode()) return;

        if (prohibitedItems(stack.getItem())) {
            this.player.sendMessage(Text.literal("You cannot grab this item, the netherite is prohibited!")
                            .formatted(Formatting.BOLD)
                            .formatted(Formatting.RED),
                    false);
            stack.decrement(stack.getCount());
            cir.cancel();
        }
    }

}
