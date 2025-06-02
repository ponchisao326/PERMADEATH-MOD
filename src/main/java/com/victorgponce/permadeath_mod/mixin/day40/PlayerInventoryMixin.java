package com.victorgponce.permadeath_mod.mixin.day40;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    // Shadow the `main` inventory list (slots 0–35, where 0–8 is the hotbar).
    @Shadow private DefaultedList<ItemStack> main;

    /**
     * Override getEmptySlot() so that it never returns any index in [4..9].
     * In PlayerInventory, getEmptySlot() is used by insertStack(slot = -1,…)
     * and also indirectly by addStack(…) to find a free slot. By injecting
     * here, we force all “auto‐insert” logic to ignore indices 4 through 9.
     */
    @Inject(
            method = "getEmptySlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getEmptySlot(CallbackInfoReturnable<Integer> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        for (int i = 0; i < this.main.size(); i++) {
            // Skip slots 4..9 entirely:
            if (i >= 4 && i <= 9) continue;

            ItemStack stack = this.main.get(i);
            if (stack.isEmpty()) {
                cir.setReturnValue(i);
                return;
            }
        }
        // If no valid slot outside 4..9 was empty, return –1 (just like vanilla)
        cir.setReturnValue(-1);
    }
}
