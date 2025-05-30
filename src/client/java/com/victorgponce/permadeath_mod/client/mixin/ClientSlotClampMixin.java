package com.victorgponce.permadeath_mod.client.mixin;

import com.victorgponce.permadeath_mod.client.data.BinaryDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(PlayerInventory.class)
public abstract class ClientSlotClampMixin {

    // 1) Shadow al campo private int selectedSlot
    @Shadow private int selectedSlot;

    /**
     * Intercepts any attempt to change the selected slot
     * and clamps the value to [0..3], writing it directly to selectedSlot.
     */
    @Inject(
            method = "setSelectedSlot(I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void clampHotbarSlot(int slot, CallbackInfo ci) {
        BinaryDataHandler config = BinaryDataHandler.getInstance();
        if (config.getDay() < 40) return;

        // 1) Clampeamos el valor de slot a [0..3]
        int clamped = slot < 4 ? slot : 3;
        // 2) Asignamos directamente al campo shadow, sin llamar al método otra vez
        this.selectedSlot = clamped;
        // 3) Cancelamos para evitar la implementación original
        ci.cancel();
    }
}
