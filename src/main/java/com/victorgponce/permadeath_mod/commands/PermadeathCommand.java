package com.victorgponce.permadeath_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.DeathTrain;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PermadeathCommand implements CommandRegistrationCallback {

    private final Text info = Text.literal("Este mod es una creación inspirada en la original obra de ")
            .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD)))
            .append(Text.literal("KernelFreeze para ElRichMC.")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GRAY))))
            .append(Text.literal(" El mod fue adaptado por ")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))))
            .append(Text.literal("ponchisao326.")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))))
            .append(Text.literal(" Si deseas obtener más información o necesitas asistencia, puedes utilizar el comando ")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))))
            .append(Text.literal("/permadeath help")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.BLUE))))
            .append(Text.literal(" en el juego. Además, puedes visitar mi sitio web oficial en ")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))))
            .append(Text.literal("https://victorgponce.com")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.AQUA))));

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher,
                         CommandRegistryAccess registryAccess,
                         CommandManager.RegistrationEnvironment env) {
        dispatcher.register(
                literal("permadeath")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(() -> info, false);
                            return 1;
                        })
                        .then(literal("changeDay")
                                .requires(src -> src.hasPermissionLevel(4))
                                .then(argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            MinecraftServer server = ctx.getSource().getServer();

                                            if (value % 5 != 0) {
                                                ctx.getSource().sendFeedback(() -> Text.literal("Por favor, el numero ha de ser uno de los válidos!"), false);
                                                return value;
                                            }

                                            HashMap<Integer, String> lines = ConfigFileManager.readFile();
                                            int day = Integer.parseInt(lines.get(4));

                                            if (value == day) {
                                                ctx.getSource().sendFeedback(() -> Text.literal("Ya estamos en ese dia!"), false);
                                                return value;
                                            }

                                            try {
                                                DeathTrain.replaceLineInFile("config/PERMADEATH/config.txt", 4, String.valueOf(value));
                                                server.getPlayerManager()
                                                        .broadcast(Text.literal(
                                                                String.format(
                                                                        "El día ha sido cambiado, a partir de ahora es dia %d. Para aplicar los cambios correctamente el servidor se reiniciará en 5 segundos", value)
                                                        ), false);
                                                ctx.getSource().sendFeedback(() -> Text.literal("El servidor se va a stopear, por favor inicielo de nuevo manualmente!"), false);

                                                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                                                scheduler.schedule(() -> {
                                                    server.execute(() -> server.stop(false));
                                                    scheduler.shutdown();
                                                }, 5, TimeUnit.SECONDS);
                                            } catch (IOException e) {
                                                throw new RuntimeException(
                                                        "Error leyendo el archivo de configuración: " + e.getMessage(), e
                                                );
                                            }

                                            return value;
                                        })
                                )
                        )
                        .then(literal("getDay")
                                .executes(ctx -> {
                                    HashMap<Integer, String> lines = ConfigFileManager.readFile();
                                    int day = Integer.parseInt(lines.get(4));
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("Actualmente estamos en el día " + day), false
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
