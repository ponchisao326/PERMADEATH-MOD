package com.victorgponce.permadeath_mod.mixin.day25.gigamobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Random;

// I remembered that I didn't comment today's code :). Sry if you encounter with some uncommented code
@Mixin(GhastEntity.class)
public class DemonicGhast {

    @Unique
    private static Random random = new Random();

    @Inject(method = "createGhastAttributes", at = @At("HEAD"), cancellable = true)
    private static void ghastAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));
        if (day >= 25) {
            // Min health 40, Max Health 60 (40 + 0-20)
            double total = 40 + random.nextInt(21);

            // Return the attributes
            cir.setReturnValue(MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, total).add(EntityAttributes.FOLLOW_RANGE, (double)100.0F));
        }
    }

    @Inject(method = "getFireballStrength", at = @At("HEAD"), cancellable = true)
    private void fireballStrength(CallbackInfoReturnable<Integer> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));
        if (day >= 25) {
            cir.setReturnValue(3 + random.nextInt(3)); // return the fireball value with a min value of 3 and a max value of 5 (3 + 0-2)
        }
    }
}
