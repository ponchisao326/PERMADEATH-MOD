package com.victorgponce.permadeath_mod.network;

import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Unique;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

public class PlayerJoinListener implements ServerPlayConnectionEvents.Join {

    /**
     * Registers the player join event listener.
     * This method is called when the server initializes.
     */
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
                "VALUES (?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE LastConnection = CURRENT_TIMESTAMP";

        DataBaseHandler.databaseConnectorStatement(url, user, password, sql, playerName);
        LOGGER.info("Player inserted or updated: " + playerName);

        DayPacketPayloadHandler(player);
        setPlayerHealth(player);
    }

    /**
     * Sends the current day to the player when they join the server.
     * This is used to inform the player about the current game state.
     *
     * @param player The player who just joined the server.
     */
    private void DayPacketPayloadHandler(ServerPlayerEntity player) {
        int day = ConfigFileManager.readConfig().getDay();

        DayPacketS2CPayload payload = new DayPacketS2CPayload(day);
        ServerPlayNetworking.send(player, payload);
    }

    /**
     * Sets the player's health based on the current day.
     * If the day is greater than 40, the player's maximum health is reduced.
     *
     * @param player The player whose health is being set.
     */
    private void setPlayerHealth(ServerPlayerEntity player) {
        int day = ConfigFileManager.readConfig().getDay();
        EntityAttributeInstance maxHealthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (day >= 40) {
            LOGGER.info("Setting player health to 12 (day > 40)");
            maxHealthAttr.setBaseValue(maxHealthAttr.getBaseValue() - 8);
        } else {
            // Honestly, this is practically useless, but I want to keep the health at 20 if the admin reverts the day ;)
            maxHealthAttr.setBaseValue(20);
        }
    }
}
