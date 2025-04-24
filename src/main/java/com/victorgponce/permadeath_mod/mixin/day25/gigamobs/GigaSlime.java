package com.victorgponce.permadeath_mod.mixin.day25.gigamobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(SlimeEntity.class)
public class GigaSlime {

    @Inject(method = "initialize", at = @At("TAIL"))
    public void SlimeSizeIncrease(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        int day = ConfigFileManager.readConfig().getDay();

        if (day >= 25) {
            SlimeEntity slime = (SlimeEntity) (Object) this;
            slime.setSize(15, false);
            slime.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(32);
            slime.setHealth(32);
        }
    }

}
