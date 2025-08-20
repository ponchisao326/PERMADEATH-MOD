package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.util.mobcaps.MultiplayerHandler;
import com.victorgponce.permadeath_mod.util.mobcaps.SinglePlayerHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class MobcapCounterAdder {

    @Unique
    private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);
    @Unique
    private static final SinglePlayerHandler singlePlayerHandler = SinglePlayerHandler.getInstance();

    @Inject(method = "addEntity", at = @At(value = "HEAD"))
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (inCustomSpawn.get()) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();
            if (world instanceof ServerWorld serverWorld) {
                // Update the MultiplayerHandler counter with the entity type
                MultiplayerHandler.getInstance().addEntity(serverWorld, entity.getType());

                // Update the SinglePlayerHandler counter with the entity type
                singlePlayerHandler.addEntityType(entity.getType(), 1);
            }
        } finally {
            inCustomSpawn.set(false);
        }
    }
}