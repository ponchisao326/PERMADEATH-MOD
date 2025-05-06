package com.victorgponce.permadeath_mod.client.screens;

import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;

public class CustomOptionsScreen extends OptionsScreen {
    private final Screen parent;

    public CustomOptionsScreen(Screen parent, GameOptions gameOptions) {
        // Llamamos al constructor de la clase base (OptionsScreen)
        super(parent, gameOptions);
        this.parent = parent; // Almacenamos la pantalla padre
    }

    // Sobrescribir el método close para que regrese a CustomMainMenu
    @Override
    public void close() {
        if (this.client != null) {
            // Asegurarnos de que se regrese a tu CustomMainMenu
            this.client.setScreen(this.parent);
        }
    }
}