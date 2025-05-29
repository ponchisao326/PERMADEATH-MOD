package com.victorgponce.permadeath_mod.mixin.day40;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(PlayerInventory.class)
public abstract class SlotRemoverMixin {

    // 1. Modify hotbar size
    @ModifyConstant(method = "getHotbarSize", constant = @Constant(intValue = 9))
    private static int modifyHotbarSize(int original) {
        LOGGER.info("Modifying hotbar size from {} to 4", original);
        return 4;
    }

    // 2. Limit hotbar index
    @ModifyConstant(method = "isValidHotbarIndex", constant = @Constant(intValue = 9))
    private static int modifyHotbarValidation(int original) {
        return 4;
    }
}