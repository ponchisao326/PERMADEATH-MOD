package com.victorgponce.permadeath_mod.client.screens;

import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;

public class CustomOptionsScreen extends OptionsScreen {
    private final Screen parent;

    public CustomOptionsScreen(Screen parent, GameOptions gameOptions) {
        // We call the base class constructor (OptionsScreen)
        super(parent, gameOptions);
        this.parent = parent; // We store the parent screen
    }

    // Override the close method to return to CustomMainMenu
    @Override
    public void close() {
        if (this.client != null) {
            // We ensure that we return to your CustomMainMenu
            this.client.setScreen(this.parent);
        }
    }
}