package com.victorgponce.permadeath_mod;

import com.victorgponce.permadeath_mod.commands.PermadeathCommand;
import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.listeners.*;
import com.victorgponce.permadeath_mod.loot_tables.LootTableOverwriter;
import com.victorgponce.permadeath_mod.mobs.EndSpawnConfig;
import com.victorgponce.permadeath_mod.mobs.MushroomBiomeModifier;
import com.victorgponce.permadeath_mod.network.*;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.tick_counter.TaskManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Permadeath_mod implements DedicatedServerModInitializer {

    public static final String MOD_ID = "PERMADEATH-SERVER";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initiating Permadeath (Server Side)");
        LOGGER.info("Permadeath Original author: KernelFreeze");
        LOGGER.info("This mod is a Fan-Made mod for the PERMADEATH Series by ELRICHMC");
        LOGGER.info("Made with ❤ by Ponchisao326");

        // Config Folder and File creator
        ConfigFileManager.initialize();
        Config cfg = ConfigFileManager.readConfig();

        LOGGER.info("JDBC URL: {}", cfg.getJdbc());

        String url = cfg.getJdbc();
        String user = cfg.getUser();
        String password = cfg.getPassword();

        // Regex for URL validating
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid URL found (line 1) on the config file, must be in this format: jdbc:mysql://BDIP:3306/your_database");
        }

        // Players Table
        DataBaseHandler.databaseConnector(url, user, password, SQLCommands.createPlayersTable);
        DataBaseHandler.databaseConnector(url, user, password, SQLCommands.createDeathsTable);

        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinListener());
        PlayerBlockBreakEvents.BEFORE.register(new NetheriteProhibiter());

        ServerLifecycleEvents.SERVER_STARTED.register(new WorldHolderHandler());

        // These callbacks control if the time resets (night skip)
        EntitySleepEvents.START_SLEEPING.register(new OnSleepEvent());
        EntitySleepEvents.ALLOW_SLEEPING.register(new CanSleepEvent());
        EntitySleepEvents.STOP_SLEEPING.register(new OnStopSleepEvent());
        EntitySleepEvents.ALLOW_RESETTING_TIME.register(new FourSleepResetTimeListener());

        // This callback is to show the remaining storm time on the HUD
        ServerTickEvents.END_SERVER_TICK.register(new StormCounter());

        // Commands
        CommandRegistrationCallback.EVENT.register(new PermadeathCommand());

        // Initialize end mobs
        FabricDefaultAttributeRegistry.register(
                EntityType.GHAST,
                GhastEntity.createMobAttributes()
        );

        // Register and initialize the mob spawn modifiers
        EndSpawnConfig.init();
        MushroomBiomeModifier.init();

        PayloadTypeRegistry.playS2C().register(DayPacketS2CPayload.ID, DayPacketS2CPayload.CODEC);

        // Register the LootTableOverwriter
        LootTableOverwriter.register();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            TaskManager.tick();
        });
    }

}
