package com.victorgponce.permadeath_mod.mixin.day30;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccessor {

    @Accessor("CHARGED")
    TrackedData<Boolean> charged();

}
