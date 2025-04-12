package com.victorgponce.permadeath_mod;

import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.data.WorldHolder;
import com.victorgponce.permadeath_mod.network.NetheriteProhibiter;
import com.victorgponce.permadeath_mod.network.PlayerJoinListener;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Permadeath_mod implements DedicatedServerModInitializer {

    public static final String MOD_ID = "PERMADEATH-SERVER";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static String url;
    public static String user;
    public static String password;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initiating Permadeath (Server Side)");
        LOGGER.info("Permadeath Original autor: KernelFreeze");
        LOGGER.info("This mod is a Fan-Made mod for the PERMADEATH Series by ELRICHMC");
        LOGGER.info("Made with ❤ by Ponchisao326");

        // Config Folder and File creator
        ConfigFileManager.createConfigFolder();
        ConfigFileManager.createFile();

        // Connection to the DB
        HashMap<Integer, String> lines;
        lines = ConfigFileManager.readFile();

        String url = lines.get(1);
        String user = lines.get(2);
        String password = lines.get(3);

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

        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            WorldHolder.setOverworld(overworld);
            LOGGER.info("The Overworld have been stored correctly on WorldHolder.");
        });
    }

}
