package com.victorgponce.permadeath_mod.mixin.day40;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class DisableHotbarSlotMixin {
    // Shadoweamos el inventario y el índice del slot
    @Shadow public abstract int getIndex();

    @Shadow @Final public Inventory inventory;

    /**
     * Cancels any attempt to insert an item into slots 5–9 (indices 4–8)
     * of the player's inventory.
     */
    @Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void onCanInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        Inventory inv = this.inventory;
        int idx = this.getIndex();
        // si es un PlayerInventory y estamos en hotbar ≥ 4, bloqueamos
        if (inv instanceof PlayerInventory && idx >= 4 && idx <= 8) {
            cir.setReturnValue(false);
        }
    }
}