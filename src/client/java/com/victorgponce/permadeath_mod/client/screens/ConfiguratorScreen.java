package com.victorgponce.permadeath_mod.client.screens;

import com.victorgponce.permadeath_mod.client.config.ClientConfig;
import com.victorgponce.permadeath_mod.client.util.ClientConfigFileManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfiguratorScreen extends Screen {

    private final ClientConfig clientConfig;
    private final CustomMainMenu customMainMenu = new CustomMainMenu();
    private TextFieldWidget serverField;
    private TextFieldWidget portField;

    public ConfiguratorScreen() {
        super(Text.translatable("gui.permadeath_mod.first_time_title"));
        this.clientConfig = ClientConfigFileManager.readConfig();
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int yStart = this.height / 2 - 30;
        int fieldWidth = 150;
        int labelOffset = 80;

        // Server address input field
        serverField = new TextFieldWidget(
                this.textRenderer,
                centerX - fieldWidth/2 + labelOffset,
                yStart,
                fieldWidth,
                20,
                Text.translatable("gui.permadeath_mod.first_time_server_label")
        );
        serverField.setText(clientConfig.getServerAddress());
        this.addDrawableChild(serverField);

        // Server port input field
        portField = new TextFieldWidget(
                this.textRenderer,
                centerX - fieldWidth/2 + labelOffset,
                yStart + 25,
                fieldWidth,
                20,
                Text.translatable("gui.permadeath_mod.first_time_port_label")
        );
        portField.setText(String.valueOf(clientConfig.getServerPort()));
        this.addDrawableChild(portField);

        // Configuration save button
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.permadeath_mod.first_time_done_button"),
                        this::saveConfiguration)
                .dimensions(centerX - 50, yStart + 60, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Draw screen title
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width/2,
                this.height/2 - 60,
                0xFFFFFF);

        // Draw field labels
        int labelX = this.width / 2 - 75;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("gui.permadeath_mod.first_time_server_label"),
                labelX,
                this.height/2 - 26,
                0xAAAAAA);

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("gui.permadeath_mod.first_time_port_label"),
                labelX,
                this.height/2 - 1,
                0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    private void saveConfiguration(ButtonWidget button) {
        // Validate and parse port number
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            port = clientConfig.getServerPort();
        }

        // Update configuration
        clientConfig.setServerAddress(serverField.getText().trim());
        clientConfig.setServerPort(port);
        ClientConfigFileManager.saveConfig(clientConfig);

        this.client.scheduleStop();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void applyBlur() {
        // Disable screen blur effect
    }

    @Override
    public void close() {
        return;
    }
}