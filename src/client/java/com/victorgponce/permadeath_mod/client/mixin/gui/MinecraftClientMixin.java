package com.victorgponce.permadeath_mod.client.mixin.gui;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    private void onGetWindowTitle(CallbackInfoReturnable<String> cir) {
        String customTitle = "PERMADEATH 1.21.5";
        cir.setReturnValue(customTitle);
    }
}
