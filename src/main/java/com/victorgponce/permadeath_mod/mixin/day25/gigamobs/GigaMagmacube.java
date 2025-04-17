package com.victorgponce.permadeath_mod.mixin.day25.gigamobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(MagmaCubeEntity.class)
public class GigaMagmacube extends SlimeEntity {

    public GigaMagmacube(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "setSize", at = @At("HEAD"))
    private void setSize(int size, boolean heal, CallbackInfo ci) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));

        if (day >= 25) {
            super.setSize(16, false);
        }
    }

}
