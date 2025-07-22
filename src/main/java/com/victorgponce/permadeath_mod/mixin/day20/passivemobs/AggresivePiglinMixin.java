package com.victorgponce.permadeath_mod.mixin.day20.passivemobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ZombifiedPiglinEntity.class)
public class AggresivePiglinMixin {

    // We inject in the builder to force angerTime
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setDefaultAnger(EntityType<? extends ZombifiedPiglinEntity> type, World world, CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 20) return;

        ZombifiedPiglinEntity self = (ZombifiedPiglinEntity) (Object) this;
        // We force anger time to the maximum value integers lets us (2^31-1).
        self.setAngerTime(Integer.MAX_VALUE);
    }

    // We inject at final (TAIL) of the initCustomGoals method to force a target assignation (Player) if it doesn't already have it
    @Inject(method = "initCustomGoals", at = @At("TAIL"))
    private void forceTargetAssignment(CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 20) return;

        ZombifiedPiglinEntity self = (ZombifiedPiglinEntity) (Object) this;
        // Only in server
        if (!self.getWorld().isClient()) {
            // If it not have an assigned target, it looks for the nearest player in a radius of 128 block (despawn sphere)
            if (self.getTarget() == null) {
                PlayerEntity nearestPlayer = self.getWorld().getClosestPlayer(self, 128);
                if (nearestPlayer != null) {
                    self.setTarget(nearestPlayer);
                }
            }
        }
    }


}
