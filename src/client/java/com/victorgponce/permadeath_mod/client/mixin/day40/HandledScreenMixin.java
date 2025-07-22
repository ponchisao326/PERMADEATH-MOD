package com.victorgponce.permadeath_mod.client.mixin.day40;

import com.victorgponce.permadeath_mod.client.data.BinaryDataHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    /**
     * This mixin disables the off-hand key (default 'F') in the HandledScreen class.
     * It prevents the player from swapping items in their off-hand while interacting with a screen.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void disableOffHandKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        // Check if the day is less than 40, if so, do nothing
        BinaryDataHandler config = BinaryDataHandler.getInstance();
        if (config.getDay() < 40) return;

        // Only runs on the client side
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        // Check if the player is not null and if the swap hands key is pressed
        if (player != null && MinecraftClient.getInstance().options.swapHandsKey.matchesKey(keyCode, scanCode)) {
            cir.setReturnValue(true); // Prevent the default behavior of swapping hands
            cir.cancel();
        }
    }
}
