package com.victorgponce.permadeath_mod.mixin.day40.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.mob.GuardianEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuardianEntity.class)
public class GuardianModifier {

    /**
     * This mixin modifies the warmup time of Guardians to 40 ticks.
     * This is linked to the {@link MobEffects} class, due to the fact that is the same change
     */
    @Inject(method = "getWarmupTime", at = @At("HEAD"), cancellable = true)
    private static void modifyGuardianAttributes(CallbackInfoReturnable<Integer> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;

        cir.setReturnValue(40);
    }
}
