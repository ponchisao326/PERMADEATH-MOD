package com.victorgponce.permadeath_mod.network;

import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerJoinListener implements ServerPlayConnectionEvents.Join {
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();

        String playerName = player.getName().getString();

        // Connection to the DB
        Config cfg = ConfigFileManager.readConfig();
        String url = cfg.getJdbc();
        String user = cfg.getUser();
        String password = cfg.getPassword();

        // Regex for URL validating
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid URL found (line 1) on the config file, must be in this format: jdbc:mysql://BDIP:3306/your_database");
        }

        String sql = "INSERT INTO Players (Username, LastConnection) " +
                "VALUES ('" + playerName + "', CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE LastConnection = CURRENT_TIMESTAMP";;

        DataBaseHandler.databaseConnector(url, user, password, sql);
    }
}
