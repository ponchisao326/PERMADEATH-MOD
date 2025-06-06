package com.victorgponce.permadeath_mod.mixin.day30;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class onTotemActivation {

    @Inject(method = "tryUseDeathProtector", at = @At("HEAD"), cancellable = true)
    public void onTotemActivationFunction(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;
        if (day >= 40) return;
        // Grants the 1%
        if (Random.create().nextInt(100) >= 1) return;

        cir.setReturnValue(false);
    }

}
