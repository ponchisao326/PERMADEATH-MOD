package com.victorgponce.permadeath_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.BinaryServerDataHandler;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PermadeathCommand implements CommandRegistrationCallback {

    private final Text info = Text.literal("This mod is a creation inspired by the original work of ")
            .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD)))
            .append(Text.literal("KernelFreeze for ElRichMC.")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GRAY))))
            .append(Text.literal(" The mod was adapted by ")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))))
            .append(Text.literal("ponchisao326.")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))))
            .append(Text.literal(" If you want more information or need assistance, you can use the command ")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GOLD))))
            .append(Text.literal("/permadeath help")
                    .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.BLUE))))
            .append(Text.literal(" in the game. You can also visit my official website at ")
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
                                                ctx.getSource().sendFeedback(() -> Text.literal("Please, the number must be one of the valid ones!"), false);
                                                return value;
                                            }

                                            int day = ConfigFileManager.readConfig().getDay();

                                            if (value == day) {
                                                ctx.getSource().sendFeedback(() -> Text.literal("We are already on that day!"), false);
                                                return value;
                                            }

                                            Config cfg = ConfigFileManager.readConfig();
                                            cfg.setDay(value);
                                            // Save immediately to TOML:
                                            ConfigFileManager.saveConfig(cfg);

                                            server.getPlayerManager()
                                                    .broadcast(Text.literal(
                                                            String.format(
                                                                    "The day has been changed, from now on it is day %d. To apply the changes correctly, the server will restart in 5 seconds", value)
                                                    ), false);
                                            ctx.getSource().sendFeedback(() -> Text.literal("The server will stop, please start it again manually!"), false);

                                            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                                            scheduler.schedule(() -> {
                                                server.execute(() -> server.stop(false));
                                                scheduler.shutdown();
                                            }, 5, TimeUnit.SECONDS);

                                            return value;
                                        })
                                )
                        )
                        .then(literal("getDay")
                                .executes(ctx -> {
                                    int day = ConfigFileManager.readConfig().getDay();
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("We are currently on day " + day), false
                                    );
                                    return 1;
                                })
                        )
                        .then(literal("enableDoubleShulkerShells")
                                .requires(src -> src.hasPermissionLevel(4))
                                .executes(ctx -> {
                                    // Obtain instance
                                    BinaryServerDataHandler config = BinaryServerDataHandler.getInstance();
                                    if (config.getDoubleShulkerShell()) {
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Double Shulker Shells is already enabled!"), false
                                        );
                                        return 1;
                                    }
                                    config.setDoubleShulkerShell();
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("The value of Double Shulker Shells has been changed to " + config.getDoubleShulkerShell()), false
                                    );
                                    return 1;
                                })
                        )
                        .then(literal("disableDoubleShulkerShells")
                                .requires(src -> src.hasPermissionLevel(4))
                                .executes(ctx -> {
                                    // Obtain instance
                                    BinaryServerDataHandler config = BinaryServerDataHandler.getInstance();
                                    if (!config.getDoubleShulkerShell()) {
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Double Shulker Shells is already disabled!"), false
                                        );
                                        return 1;
                                    }
                                    config.setDoubleShulkerShell();
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("The value of Double Shulker Shells has been changed to " + config.getDoubleShulkerShell()), false
                                    );
                                    return 1;
                                })
                        )
        );
    }
}