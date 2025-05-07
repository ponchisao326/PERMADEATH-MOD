package com.victorgponce.permadeath_mod.client.screens;

import com.victorgponce.permadeath_mod.client.config.ClientConfig;
import com.victorgponce.permadeath_mod.client.util.ClientConfigFileManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class FirstTimeScreen extends Screen {

    ClientConfig clientConfig;
    CustomMainMenu customMainMenu = new CustomMainMenu();
    private Text STATUS = Text.of("Server address: ");

    public FirstTimeScreen() {
        super(Text.translatable("gui.permadeath_mod.first_time_title"));

        clientConfig = ClientConfigFileManager.readConfig();
    }

    @Override
    protected void init() {
        super.init();

        // Calcular posición central
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        TextFieldWidget serverField = new TextFieldWidget(this.textRenderer ,centerX, centerY, 100, 20, STATUS);

        // Botón Sí (Yes)
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.permadeath_mod.yes_button"), button -> {
                    clientConfig.setEnabledServerCheck(true);
                    ClientConfigFileManager.saveConfig(clientConfig);



                    // this.client.setScreen(customMainMenu);
                })
                .dimensions(centerX - 105, centerY + 20, 100, 20)
                .build());

        // Botón No
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.permadeath_mod.no_button"), button -> {
                    clientConfig.setEnabledServerCheck(false);
                    ClientConfigFileManager.saveConfig(clientConfig);
                    this.client.setScreen(customMainMenu);
                })
                .dimensions(centerX + 5, centerY + 20, 100, 20)
                .build());
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Texto principal centrado
        Text message = Text.translatable("gui.permadeath_mod.first_time_message");
        int textWidth = this.textRenderer.getWidth(message);
        context.drawCenteredTextWithShadow(this.textRenderer, message, this.width / 2, this.height / 2 - 30, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        return;
    }

    @Override
    protected void applyBlur() {
        return;
    }
}