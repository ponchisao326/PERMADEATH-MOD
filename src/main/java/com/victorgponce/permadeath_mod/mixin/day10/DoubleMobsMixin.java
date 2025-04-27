package com.victorgponce.permadeath_mod.mixin.day10;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(SpawnHelper.Info.class)
public class DoubleMobsMixin {
    @Shadow @Final private int spawningChunkCount;

    @Shadow @Final private Object2IntOpenHashMap<SpawnGroup> groupToCount;

    @Inject(
            method = "isBelowCap",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void doubleMobcap(SpawnGroup group, CallbackInfoReturnable<Boolean> cir) {
        int baseCap = group.getCapacity() * this.spawningChunkCount / ChunkAreaAccessor.getChunkArea();
        int day = ConfigFileManager.readConfig().getDay();
        if (day >= 10) {
            cir.setReturnValue(this.groupToCount.getInt(group) < (baseCap * 2));
        }
    }

    // Log mobcap info cada vez que se spawnea una entidad, usando el mobcap real
    @Inject(method = "run", at = @At("TAIL"))
    private void logMobcapAfterSpawn(net.minecraft.entity.mob.MobEntity entity, net.minecraft.world.chunk.Chunk chunk, CallbackInfo ci) {
        SpawnGroup group = entity.getType().getSpawnGroup();
        int day = ConfigFileManager.readConfig().getDay();
        int baseCap = group.getCapacity() * this.spawningChunkCount / ChunkAreaAccessor.getChunkArea();
        int mobcap = (day >= 10) ? baseCap * 2 : baseCap;
        int current = this.groupToCount.getInt(group);
        LOGGER.info("[PermadeathMod] Mobcap para {}: {}/{}", group.getName(), current, mobcap);
    }
}