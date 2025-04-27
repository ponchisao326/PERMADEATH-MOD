package com.victorgponce.permadeath_mod.mixin.day10;

import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnHelper.class)
public interface ChunkAreaAccessor {

    @Accessor("CHUNK_AREA")
    static int getChunkArea() {
        throw new AssertionError();
    }

}
