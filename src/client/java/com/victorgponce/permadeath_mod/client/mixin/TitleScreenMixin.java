package com.victorgponce.permadeath_mod.client.mixin;

import com.victorgponce.permadeath_mod.client.screens.CustomMainMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.submit(() -> {
            CustomMainMenu pressToContinueScreen = new CustomMainMenu();
            mc.setScreen(pressToContinueScreen);
        });
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        MinecraftClient.getInstance().getWindow().setTitle("PERMADEATH 1.21.5");
    }
}
