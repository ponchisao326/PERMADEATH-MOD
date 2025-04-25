package com.victorgponce.permadeath_mod.mixin.day30;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.EntitiesCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityDeathMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;

        // Only process if the entity was removed due to death
        if (reason != Entity.RemovalReason.KILLED) return;

        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;

        // Check for custom Guardian
        if (entity instanceof GuardianEntity guardian) {
            Text customName = guardian.getCustomName();
            if (customName != null && customName.getString().equals("Speed Guardian")) {
                EntitiesCounter.guardianCount--;
            }
        }

        // Check for custom Blaze
        if (entity instanceof BlazeEntity blaze) {
            Text customName = blaze.getCustomName();
            if (customName != null && customName.getString().equals("Resistance Blaze")) {
                EntitiesCounter.blazeCount--;
            }
        }
    }
}
