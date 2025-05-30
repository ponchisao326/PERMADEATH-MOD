package com.victorgponce.permadeath_mod.mixin.day40;


import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    /**
     * Interceptamos cualquier inserción de ItemStack en el inventario
     * (incluye recogida de ítems del suelo, shulkers, cofres, etc.).
     */
    @Inject(
            method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInsertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PlayerInventory inv = (PlayerInventory) (Object) this;

        // 1. Buscamos ranura válida fuera de 5–9
        int slot = findValidSlot(inv, stack);
        if (slot == -1) {
            // no hay espacio válido → cancelamos la inserción
            cir.setReturnValue(false);
        } else {
            // 2. Insertamos el stack manualmente y cancelamos el método original
            inv.setStack(slot, stack);
            cir.setReturnValue(true);
        }
    }

    private int findValidSlot(PlayerInventory inv, ItemStack stack) {
        // recorremos todas las ranuras del inventario
        for (int i = 0; i < inv.size(); i++) {
            // saltamos las slots 4–9
            if (i >= 4 && i <= 9) continue;

            ItemStack inSlot = inv.getStack(i);
            if (inSlot.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}