package com.victorgponce.permadeath_mod.mixin.common;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {

    @Accessor("worldProperties")
    ServerWorldProperties worldProperties();
}
