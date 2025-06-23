package com.victorgponce.permadeath_mod.client.mixin.day40;

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

        // 1) We clamp the value of slot to [0..3]
        int clamped = slot == 8 ? 8 : slot;
        // 2) We assign directly to the shadow field, without calling the method again
        this.selectedSlot = clamped;
        // 3) Cancel the original method to prevent further processing
        ci.cancel();
    }
}
