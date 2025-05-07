package com.victorgponce.permadeath_mod.client.screens;

import com.victorgponce.permadeath_mod.client.config.ClientConfig;
import com.victorgponce.permadeath_mod.client.util.ClientConfigFileManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class FirstTimeScreen extends Screen {

    private final ClientConfig clientConfig;
    private final CustomMainMenu customMainMenu = new CustomMainMenu();
    private final ConfiguratorScreen configuratorScreen = new ConfiguratorScreen();

    public FirstTimeScreen() {
        super(Text.translatable("gui.permadeath_mod.first_time_title"));
        clientConfig = ClientConfigFileManager.readConfig();
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Initialize decision buttons
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.permadeath_mod.yes_button"),
                        button -> enableServerCheck())
                .dimensions(centerX - 105, centerY + 20, 100, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.permadeath_mod.no_button"),
                        button -> disableServerCheck())
                .dimensions(centerX + 5, centerY + 20, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Render wrapped instruction text
        Text message = Text.translatable("gui.permadeath_mod.first_time_message");
        int maxWidth = this.width - 40;
        List<OrderedText> wrapped = this.textRenderer.wrapLines(message, maxWidth);

        int startY = this.height / 2 - 30;
        int lineHeight = this.textRenderer.fontHeight + 2;
        for (int i = 0; i < wrapped.size(); i++) {
            OrderedText line = wrapped.get(i);
            int lineWidth = this.textRenderer.getWidth(line);
            context.drawTextWithShadow(this.textRenderer, line,
                    (this.width - lineWidth) / 2,
                    startY + i * lineHeight,
                    0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void enableServerCheck() {
        clientConfig.setEnabledServerCheck(true);
        ClientConfigFileManager.saveConfig(clientConfig);
        this.client.setScreen(configuratorScreen);
    }

    private void disableServerCheck() {
        clientConfig.setEnabledServerCheck(false);
        ClientConfigFileManager.saveConfig(clientConfig);
        this.client.setScreen(customMainMenu);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        return;
    }

    @Override
    protected void applyBlur() {
        // Disable screen blur effect
    }
}