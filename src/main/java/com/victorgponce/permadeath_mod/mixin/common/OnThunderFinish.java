package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.config.Config;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class OnThunderFinish {

    @Unique
    private boolean previousThunderingState = false;

    @Inject(method = "tickWeather", at = @At("TAIL"))
    private void onTickWeather(CallbackInfo ci) {
        // We obtain the server world
        ServerWorld serverWorld = (ServerWorld) (Object) this;

        // We check if the previous tick was thunder and if it isn't anymore
        if (previousThunderingState && !serverWorld.isThundering()) {
            // End of Storm
            if (ConfigFileManager.readConfig().isDeathTrain()) {
                onThunderEnd(serverWorld);
            }
        }

        // We update the state for the next tick
        previousThunderingState = serverWorld.isThundering();
    }

    @Unique
    private static void onThunderEnd(ServerWorld serverWorld) {
        serverWorld.getServer().getPlayerManager().broadcast(Text.literal("¡El Death Train ha llegado a su fin!")
                .formatted(Formatting.RED, Formatting.BOLD), false);
        Config cfg = ConfigFileManager.readConfig();
        cfg.setDeathTrain(false);
        // Guarda inmediatamente en el TOML:
        ConfigFileManager.saveConfig(cfg);
    }

}
