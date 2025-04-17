package com.victorgponce.permadeath_mod.util;

import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class BanManager {

    public static void checkAndBan(ServerPlayerEntity player) {
        String playerName = player.getName().getString();

        // Read BD config
        HashMap<Integer, String> lines = ConfigFileManager.readFile();
        String url = lines.get(1);
        String user = lines.get(2);
        String password = lines.get(3);

        // Validate URL format
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            throw new RuntimeException("Formato de URL inválido en el archivo de configuración");
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // check lives and player status
            String sql = "SELECT Lives, Status FROM Players WHERE Username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int lives = rs.getInt("Lives");
                    String status = rs.getString("Status");

                    // Ban if conditions are met
                    if (lives <= 0 && "inactive".equalsIgnoreCase(status)) {
                        MinecraftServer server = player.getServer();
                        if (server != null) {
                            banPlayer(server, player);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en la base de datos: " + e.getMessage());
        }
    }

    private static void banPlayer(MinecraftServer server, ServerPlayerEntity player) {
        // Broadcast message to all the players
        Text broadcastMsg = Text.literal("Este es el comienzo del sufrimiento eterno de ")
                .formatted(Formatting.BOLD)
                .append(Text.literal(player.getName().getString())
                        .formatted(Formatting.GOLD, Formatting.BOLD))
                .append(Text.literal(". ¡HA SIDO PERMABANEADO!")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD));

        server.getPlayerManager().broadcast(broadcastMsg, false);

        sendPermadeathTitle(server, player);

        // Add to BanList
        server.getPlayerManager().getUserBanList().add(
                new BannedPlayerEntry(
                        player.getGameProfile(),
                        null,
                        "☠ Permadeath System ☠",
                        null,
                        "¡Has sido Permabaneado!"
                )
        );

        // Disconnect player with a message
        player.networkHandler.disconnect(
                Text.literal("¡HAS SIDO PERMABANEADO!")
                        .formatted(Formatting.RED, Formatting.BOLD)
        );
    }

    private static void sendPermadeathTitle(MinecraftServer server, ServerPlayerEntity bannedPlayer) {
        // Configure Times (in ticks)
        int fadeIn = 10;   // 0.5 segundos
        int stay = 80;     // 4 segundos
        int fadeOut = 20;  // 1 segundo

        // Create packets such as the official command does it
        TitleFadeS2CPacket timesPacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        TitleS2CPacket titlePacket = new TitleS2CPacket(
                Text.literal("¡Permadeath!")
                        .formatted(Formatting.RED, Formatting.BOLD)
        );
        SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(
                Text.literal(bannedPlayer.getName().getString() + " ha muerto")
                        .formatted(Formatting.WHITE)
        );

        // Send to all the players
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(timesPacket);
            player.networkHandler.sendPacket(titlePacket);
            player.networkHandler.sendPacket(subtitlePacket);
        }
    }
}