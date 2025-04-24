package com.victorgponce.permadeath_mod.mixin.day20;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(PhantomEntity.class)
public class PhantomModifierMixin {

    @Inject(method = "initialize", at = @At("TAIL"))
    public void phantomSizeIncrease(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        int day = ConfigFileManager.readConfig().getDay();

        if (day >= 20) {
            // We obtain the entity and set custom params
            PhantomEntity phantom = (PhantomEntity) (Object) this;
            phantom.setPhantomSize(9);
            phantom.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
            phantom.setHealth(40);
        }
    }

}
