package com.victorgponce.permadeath_mod.mixin.day40;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerInventory.class)
public abstract class SlotRemoverMixin {

    // 1. Modificar tamaño de hotbar
    @ModifyConstant(method = "getHotbarSize", constant = @Constant(intValue = 9))
    private static int modifyHotbarSize(int original) {
        return 4;
    }

    // 2. Limitar selección de slots
    @ModifyConstant(method = "isValidHotbarIndex", constant = @Constant(intValue = 9))
    private static int modifyHotbarValidation(int original) {
        return 4;
    }
}