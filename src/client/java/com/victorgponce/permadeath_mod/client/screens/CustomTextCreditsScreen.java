package com.victorgponce.permadeath_mod.client.screens;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

@Environment(EnvType.CLIENT)
public class CustomTextCreditsScreen extends Screen {
    private static final Identifier VIGNETTE = Identifier.ofVanilla("textures/misc/credits_vignette.png");
    private static final float SPACE_MULT = 5.0f;

    private final Runnable onClose;
    private final List<OrderedText> lines = new ArrayList<>();
    private final IntSet centeredLines = new IntOpenHashSet();

    private static final Identifier[] TEXT_SOURCES = {
            Identifier.of("permadeath-mod", "texts/endpoem.txt"),
            Identifier.of("permadeath-mod", "texts/credits.txt"),
            Identifier.of("permadeath-mod", "texts/postcredits.txt")
    };

    private float scrollTime = 0f;
    private final float baseSpeed = 0.5f; // velocidad normal
    private boolean spaceKeyPressed = false;
    private float speed = baseSpeed;

    private int totalHeight;

    public CustomTextCreditsScreen(Runnable onClose) {
        super(NarratorManager.EMPTY);
        this.onClose = onClose;
    }

    @Override
    protected void init() {
        // Cargar sólo una vez
        if (lines.isEmpty()) {
            int index = 0;
            for (Identifier src : TEXT_SOURCES) {
                loadText(src, index++);
            }
            totalHeight = lines.size() * 12;
        }
    }

    private void loadText(Identifier id, int section) {
        try (Reader r = client.getResourceManager().openAsReader(id)) {
            BufferedReader br = new BufferedReader(r);
            String line;
            boolean center = (section % 2 == 1);  // por ejemplo, centrar la sección de credits
            while ((line = br.readLine()) != null) {
                OrderedText text = Text.literal(line).asOrderedText();
                if (center) centeredLines.add(lines.size());
                lines.add(text);
            }
            // añadir espacio tras cada sección
            lines.add(OrderedText.EMPTY);
            lines.add(OrderedText.EMPTY);
        } catch (IOException e) {
            LOGGER.error("No se pudo cargar créditos: " + id, e);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Si pulsamos SPACE, activamos multiplicador
        if (keyCode == GLFW.GLFW_KEY_SPACE) {
            spaceKeyPressed = true;
            updateSpeed();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Al soltar SPACE, volvemos a velocidad normal
        if (keyCode == GLFW.GLFW_KEY_SPACE) {
            spaceKeyPressed = false;
            updateSpeed();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void updateSpeed() {
        speed = baseSpeed * (spaceKeyPressed ? SPACE_MULT : 1f);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // Fondo de viñeta
        super.render(ctx, mx, my, delta);
        ctx.drawTexture(RenderLayer::getVignette, VIGNETTE, 0, 0, 0, 0, width, height, width, height);

        // Avanzar scroll
        scrollTime += delta * speed;
        float offset = -scrollTime;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, offset, 0);

        int y = height + 50;
        int x = width / 2 - 128;
        LogoDrawer drawer = new LogoDrawer(false);
        drawer.draw(ctx, width, 1f, y);

        y += 100;
        for (int i = 0; i < lines.size(); i++) {
            if (centeredLines.contains(i)) {
                ctx.drawCenteredTextWithShadow(textRenderer, lines.get(i), width/2, y, 0xFFFFFF);
            } else {
                ctx.drawTextWithShadow(textRenderer, lines.get(i), x, y, 0xFFFFFF);
            }
            y += 12;
        }

        ctx.getMatrices().pop();

        // Cerrar al terminar
        if (scrollTime > totalHeight + height + 50) {
            this.onClose.run();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
