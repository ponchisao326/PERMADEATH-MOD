package com.victorgponce.permadeath_mod.client.screens;

import com.victorgponce.permadeath_mod.client.config.ClientConfig;
import com.victorgponce.permadeath_mod.client.util.ClientConfigFileManager;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.Socket;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

public class CustomMainMenu extends Screen {
    private TextWidget pingTextWidget;

    ClientConfig clientConfig;
    private static String NODE;
    private static int PORT;
    private static boolean SERVER_CHECK_ENABLED;

    private Screen parent;
    private static final Identifier menuTitleId = Identifier.of("permadeath-mod", "textures/gui/title.png");
    private static final Identifier backgroundTextureId = Identifier.of("permadeath-mod", "textures/gui/background.png");

    private String STATUS = "El servidor se encuentra OFFLINE";
    private static final long PING_CHECK_INTERVAL_MS = 5000; // Intervalo de 5 segundos
    private long lastPingCheckTime = 0; // Momento del último chequeo
    private Thread pingThread; // Hilo para la verificación del servidor
    private boolean stopPingThread = false; // Variable para detener el hilo cuando sea necesario

    public CustomMainMenu() {
        super(Text.of("Main Menu"));
        startPingThread();

        clientConfig = ClientConfigFileManager.readConfig();
        NODE = clientConfig.getServerAddress();
        PORT = clientConfig.getServerPort();
        SERVER_CHECK_ENABLED = clientConfig.isEnabledServerCheck();
    }

    // Método para iniciar el hilo de verificación del servidor
    private void startPingThread() {
        pingThread = new Thread(() -> {
            while (!stopPingThread) {
                if (Pinger(NODE, PORT)) {
                    STATUS = "El servidor se encuentra ONLINE";
                } else {
                    STATUS = "El servidor se encuentra OFFLINE";
                }
                // Esperar el intervalo de tiempo definido antes de la siguiente verificación
                try {
                    Thread.sleep(PING_CHECK_INTERVAL_MS);
                } catch (InterruptedException e) {
                    LOGGER.error("El hilo de verificación de ping fue interrumpido: " + e.getMessage());
                }
            }
        });
        pingThread.start();
    }

    public static boolean Pinger(String address, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(address, port), 1000); // Tiempo de espera de 1 segundo
            return true; // Si la conexión es exitosa
        } catch (IOException e) {
            LOGGER.error("Error al conectar con el servidor: " + e.getMessage());
        }
        return false; // Si ocurre algún error o no se puede conectar
    }

    @Override
    protected void init() {
        super.init();

        // Texto PingText alineado a la izquierda
        pingTextWidget = new TextWidget(10, this.height - 20, this.textRenderer.getWidth(STATUS), 10, Text.of(STATUS), this.textRenderer);

        this.addDrawableChild(pingTextWidget);
    }

    @Override
    public void close() {
        stopPingThread = true; // Indicar que el hilo debe detenerse
        try {
            if (pingThread != null && pingThread.isAlive()) {
                pingThread.join(); // Esperar a que el hilo termine
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error al detener el hilo de ping: " + e.getMessage());
        }
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Obtener el tiempo actual
        long currentTimePing = System.currentTimeMillis();

        // Actualizar el contenido del TextWidget con el nuevo estado
        pingTextWidget.setMessage(Text.of(STATUS));

        // Renderizar la textura de fondo
        assert this.client != null;

        // La textura se cargó correctamente, renderizarla
        context.drawTexture(RenderLayer::getGuiTextured, backgroundTextureId, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);

        // 2) Calculamos el scale y tamaño del título según “media-queries”
        final int originalTitleW = 1062;
        final int originalTitleH = 155;

        float scale;
        if (this.width < 400) {
            // Pantallas muy pequeñas
            scale = 1.0f / 4.0f;
        } else if (this.width < 800) {
            // Pantallas de tamaño medio
            scale = 1.0f / 3.5f;
        } else if (this.width < 1200) {
            // Pantallas grandes
            scale = 1.0f / 3.0f;
        } else {
            // Pantallas muy grandes (ultra-wide, etc)
            scale = 1.0f / 2.5f;
        }

        int titleWidth  = Math.round(originalTitleW * scale);
        int titleHeight = Math.round(originalTitleH * scale);

        int titleX = (this.width - titleWidth) / 2 + 3;
        int titleY = 35;

        context.drawTexture(RenderLayer::getGuiTextured, menuTitleId, titleX, titleY, 0.0F, 0.0F, titleWidth, titleHeight, titleWidth, titleHeight);

        // Renderizar elementos en la pantalla
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    protected void applyBlur() {
        // It will only blur the background, the rest remains unblurred
        return;
    }
}
