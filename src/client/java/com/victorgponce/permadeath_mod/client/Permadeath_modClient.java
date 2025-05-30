package com.victorgponce.permadeath_mod.client;

import com.victorgponce.permadeath_mod.client.data.BinaryDataHandler;
import com.victorgponce.permadeath_mod.network.DayPacketS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Permadeath_modClient implements ClientModInitializer {

    public static final String MOD_ID = "PERMADEATH-CLIENT";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initiating Permadeath (Client Side)");
        LOGGER.info("Permadeath Original autor: KernelFreeze");
        LOGGER.info("This mod is a Fan-Made mod for the PERMADEATH Series by ELRICHMC");
        LOGGER.info("Made with Love by Ponchisao326");

        BinaryDataHandler config = BinaryDataHandler.getInstance();

        PayloadTypeRegistry.playS2C().register(DayPacketS2CPayload.ID, DayPacketS2CPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(DayPacketS2CPayload.ID, (payload, context)-> {
            // Set value
            config.setDay(payload.day());
            // Log the day received

            LOGGER.info("Received day from server: " + payload.day() + " Saving on the binary...");
        });

        LOGGER.info("Current day from binary: " + config.getDay());
    }
}
