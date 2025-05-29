package com.victorgponce.permadeath_mod.client;

import net.fabricmc.api.ClientModInitializer;
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
    }
}
