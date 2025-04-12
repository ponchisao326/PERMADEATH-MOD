package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(SleepManager.class)
public class OnSleepListener {

    @Inject(method = "getNightSkippingRequirement", at = @At("HEAD"), cancellable = true)
    private void nightSkippingReq(int percentage, CallbackInfoReturnable<Integer> cir) {
        // File Reader
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (lines.get(4).equals("10")) {
            cir.setReturnValue(4);
        }

    }

    @Inject(method = "canSkipNight", at = @At("HEAD"), cancellable = true)
    private void skipNightprohibited(int percentage, CallbackInfoReturnable<Boolean> cir) {
        // File reader
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (lines.get(4).equals("20")) {
            cir.setReturnValue(false);
        }
    }

}
