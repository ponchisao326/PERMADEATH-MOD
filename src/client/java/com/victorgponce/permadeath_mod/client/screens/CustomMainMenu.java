package com.victorgponce.permadeath_mod.client.screens;

import com.victorgponce.permadeath_mod.client.config.ClientConfig;
import com.victorgponce.permadeath_mod.client.util.ClientConfigFileManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.narration.Narration;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.Socket;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

@Environment(EnvType.CLIENT)
public class CustomMainMenu extends Screen {
    private TextWidget pingTextWidget;
    private ButtonWidget playButton;
    private ButtonWidget survivalButton;
    private ButtonWidget optionsButton;
    private ButtonWidget quitButton;
    private ButtonWidget creditsButton;
    private TexturedButtonWidget configButton;

    ClientConfig clientConfig;
    private static String NODE;
    private static int PORT;
    private static boolean SERVER_CHECK_ENABLED;

    private Screen parent;
    private static final Identifier menuTitleId = Identifier.of("permadeath-mod", "textures/gui/title.png");
    private static final Identifier backgroundTextureId = Identifier.of("permadeath-mod", "textures/gui/background.png");
    private static final Text MENU = Text.literal("Menu by PonchisaoHosting");
    private static final ButtonTextures buttonTextures = new ButtonTextures(Identifier.of("permadeath-mod", "textures/gui/widgets/config.png"), Identifier.of("permadeath-mod", "textures/gui/widgets/config_toggle.png"));

    private Text STATUS = Text.translatable("gui.permadeath_mod.status.offline");
    private static final long PING_CHECK_INTERVAL_MS = 5000; // 5 second interval
    private long lastPingCheckTime = 0; // Last check timestamp
    private Thread pingThread; // Thread for server status checks
    private boolean stopPingThread = false; // Flag to stop the thread

    public CustomMainMenu() {
        super(Text.translatable("gui.permadeath_mod.title"));

        clientConfig = ClientConfigFileManager.readConfig();
        NODE = clientConfig.getServerAddress();
        PORT = clientConfig.getServerPort();
        SERVER_CHECK_ENABLED = clientConfig.isEnabledServerCheck();

        if (SERVER_CHECK_ENABLED) {
            startPingThread();
        }
    }

    // Start server status check thread
    private void startPingThread() {
        pingThread = new Thread(() -> {
            while (!stopPingThread) {
                if (pinger(NODE, PORT)) {
                    STATUS = Text.translatable("gui.permadeath_mod.status.online");
                } else {
                    STATUS = Text.translatable("gui.permadeath_mod.status.offline");
                }
                // Wait for next check
                try {
                    Thread.sleep(PING_CHECK_INTERVAL_MS);
                } catch (InterruptedException ignored) {}
            }
        });
        pingThread.start();
    }

    // Attempt connection to server
    public static boolean pinger(String address, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(address, port), 1000); // 1 second timeout
            return true; // Connection successful
        } catch (IOException ignored) {}
        return false; // Connection failed
    }

    @Override
    protected void init() {
        super.init();

        String SERVER_ADDRESS = NODE + ":" + PORT;

        // Calculate screen center
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (SERVER_CHECK_ENABLED) {
            // Create server connection button
            playButton = new ButtonWidget.Builder(Text.translatable("gui.permadeath_mod.play"), (buttonWidget) -> {
                ServerInfo info = new ServerInfo(I18n.translate("selectServer.defaultName"), SERVER_ADDRESS, ServerInfo.ServerType.OTHER);
                info.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.PROMPT);
                ConnectScreen.connect(this, this.client, ServerAddress.parse(SERVER_ADDRESS), info, true, (CookieStorage)null);
                buttonWidget.playDownSound(this.client.getSoundManager());
            }).dimensions(centerX - 100, centerY - 12, 200, 20).build();

            // Singleplayer button
            survivalButton = new ButtonWidget.Builder(Text.translatable("menu.singleplayer"), (buttonWidget) -> {
                this.client.setScreen(new SelectWorldScreen(this));
                buttonWidget.playDownSound(this.client.getSoundManager());
            }).dimensions(centerX - 100, centerY + 12, 200, 20).build();
        } else {
            survivalButton = new ButtonWidget.Builder(Text.translatable("menu.singleplayer"), (buttonWidget) -> {
                this.client.setScreen(new SelectWorldScreen(this));
                buttonWidget.playDownSound(this.client.getSoundManager());
            }).dimensions(centerX - 100, centerY - 12, 200, 20).build();

            // Multiplayer button
            playButton = new ButtonWidget.Builder(Text.translatable("menu.multiplayer"), (buttonWidget) -> {
                this.client.setScreen(new MultiplayerScreen(this));
                buttonWidget.playDownSound(this.client.getSoundManager());
            }).dimensions(centerX - 100, centerY + 12, 200, 20).build();
        }

        // Credits button
        creditsButton = new ButtonWidget.Builder(Text.translatable("gui.permadeath_mod.credits"), (buttonWidget) -> {
            this.client.setScreen(new CustomTextCreditsScreen(() -> {
                client.setScreen(this);
            }));
            buttonWidget.playDownSound(this.client.getSoundManager());
        }).dimensions(centerX - 100, centerY + 36, 200, 20).build();

        // Options button
        optionsButton = new ButtonWidget.Builder(Text.translatable("menu.options"), (buttonWidget) -> {
            MinecraftClient.getInstance().setScreen(new CustomOptionsScreen(this, this.client.options));
            buttonWidget.playDownSound(this.client.getSoundManager());
        }).dimensions(centerX - 100, centerY + 60, 99, 20).build();

        // Quit button
        quitButton = new ButtonWidget.Builder(Text.translatable("menu.quit"), (buttonWidget) -> {
            this.client.scheduleStop();
            buttonWidget.playDownSound(this.client.getSoundManager());
        }).dimensions(centerX + 1, centerY + 60, 99, 20).build();

        if (SERVER_CHECK_ENABLED) {
            // Status text at bottom left
            pingTextWidget = new TextWidget(10, this.height - 20, this.textRenderer.getWidth(STATUS), 10, STATUS, this.textRenderer);
            this.addDrawableChild(pingTextWidget);
        }

        // Config button
        configButton = new TexturedButtonWidget(centerX - 100, centerY + 84, 20, 20, buttonTextures, (button) -> {
            this.client.setScreen(new ConfiguratorScreen());
        }, Text.translatable("gui.permadeath_mod.config"));

        LOGGER.info(String.valueOf(buttonTextures.get(true, true)));

        this.addDrawableChild(playButton);
        this.addDrawableChild(survivalButton);
        this.addDrawableChild(optionsButton);
        this.addDrawableChild(quitButton);
        this.addDrawableChild(creditsButton);
        this.addDrawableChild(configButton);

        // Menu credit text at bottom right
        int i = this.textRenderer.getWidth(MENU);
        int j = this.width - i - 2;
        this.addDrawableChild(new PressableTextWidget(j - 3, this.height - 20, i, 10, MENU, (button -> Util.getOperatingSystem().open("https://victorgponce.com")), this.textRenderer));
    }

    @Override
    public void close() {
        stopPingThread = true; // Signal thread to stop
        try {
            if (pingThread != null && pingThread.isAlive()) {
                pingThread.join(); // Wait for thread termination
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error stopping ping thread: " + e.getMessage());
        }
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Update status text
        if (SERVER_CHECK_ENABLED) {
            pingTextWidget.setMessage(STATUS);
        }

        // Render background
        assert this.client != null;
        context.drawTexture(RenderLayer::getGuiTextured, backgroundTextureId, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);

        drawTitle(context);
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    private void drawTitle(DrawContext context) {
        final int origW = 1062, origH = 155;
        float tScale = this.width < 400 ? 1/4f
                : this.width < 800 ? 1/3.5f
                : this.width < 1200 ? 1/3f
                : 1/2.5f;
        int w = Math.round(origW * tScale), h = Math.round(origH * tScale);
        int x = (this.width - w) / 2 + 3, y = 35;
        context.drawTexture(RenderLayer::getGuiTextured, menuTitleId, x, y, 0, 0, w, h, w, h);
    }

    @Override
    protected void applyBlur() {
        return;
    }
}