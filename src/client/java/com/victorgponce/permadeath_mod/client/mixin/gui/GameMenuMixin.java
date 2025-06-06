package com.victorgponce.permadeath_mod.client.mixin.gui;

import com.victorgponce.permadeath_mod.client.screens.CustomMainMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

@Mixin(GameMenuScreen.class)
public class GameMenuMixin {
    @Inject(method = "disconnect", at = @At("TAIL"))
    public void disconnect(CallbackInfo ci) {

        LOGGER.info("Setting CustomMainMenu as Parent");

        MinecraftClient.getInstance().disconnect();

        CustomMainMenu customMainMenu = new CustomMainMenu();
        MinecraftClient.getInstance().setScreen(new MultiplayerScreen(customMainMenu));

    }
}