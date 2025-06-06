package com.victorgponce.permadeath_mod.client.mixin.gui;

import com.victorgponce.permadeath_mod.client.data.BinaryDataHandler;
import com.victorgponce.permadeath_mod.client.screens.CustomMainMenu;
import com.victorgponce.permadeath_mod.client.screens.FirstTimeScreen;
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

        // Obtain instance
        BinaryDataHandler config = BinaryDataHandler.getInstance();
        // Read value
        boolean currentValue = config.getFirstExecution();

        mc.submit(() -> {
            // Check if it's the first
            if (currentValue) {
                // Set the value to false
                config.setFirstExecution(false);

                FirstTimeScreen firstTimeScreen = new FirstTimeScreen();
                mc.setScreen(firstTimeScreen);
            } else {
                CustomMainMenu customMainMenu = new CustomMainMenu();
                mc.setScreen(customMainMenu);
            }
        });
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        MinecraftClient.getInstance().getWindow().setTitle("PERMADEATH 1.21.5");
    }
}
