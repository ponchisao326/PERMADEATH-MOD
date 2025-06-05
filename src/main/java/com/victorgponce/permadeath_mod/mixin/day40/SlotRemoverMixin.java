package com.victorgponce.permadeath_mod.mixin.day40;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerInventory.class)
public abstract class SlotRemoverMixin {

    // 1. Modify hotbar size
    @ModifyConstant(method = "getHotbarSize", constant = @Constant(intValue = 9))
    private static int modifyHotbarSize(int original) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return original;
        return 8;
    }

    // 2. Limit hotbar index
    @ModifyConstant(method = "isValidHotbarIndex", constant = @Constant(intValue = 9))
    private static int modifyHotbarValidation(int original) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return original;
        return 8;
    }
}