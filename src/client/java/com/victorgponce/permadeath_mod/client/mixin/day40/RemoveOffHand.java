package com.victorgponce.permadeath_mod.client.mixin.day40;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class RemoveOffHand {

    @Shadow @Final public GameOptions options;

    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void removeOffHand(CallbackInfo ci) {
        // Disable off-hand usage
        if (this.options.swapHandsKey.wasPressed()) {
            ci.cancel();
        }
    }

}
