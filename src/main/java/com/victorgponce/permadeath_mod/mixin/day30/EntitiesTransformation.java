package com.victorgponce.permadeath_mod.mixin.day30;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.victorgponce.permadeath_mod.util.EntitiesCounter.blazeCount;
import static com.victorgponce.permadeath_mod.util.EntitiesCounter.guardianCount;

@Mixin(ServerWorld.class)
public class EntitiesTransformation {

    @Unique
    private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (inCustomSpawn.get()) return;
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 30) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();
            if (!(world instanceof ServerWorld serverWorld)) return;

            if (entity instanceof SquidEntity && guardianCount < 20) {
                cir.setReturnValue(false);

                GuardianEntity guardian = new GuardianEntity(EntityType.GUARDIAN, serverWorld);
                guardian.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                guardian.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2));

                guardian.setCustomName(Text.literal("Speed Guardian"));

                serverWorld.spawnEntity(guardian);

                guardianCount++;
            }

            if ((entity instanceof SquidEntity && guardianCount >= 20) || (entity instanceof BlazeEntity && blazeCount >= 15)) cir.setReturnValue(false);

            if (entity instanceof BatEntity && blazeCount < 15) {
                cir.setReturnValue(false);

                BlazeEntity blaze = new BlazeEntity(EntityType.BLAZE, serverWorld);
                blaze.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                blaze.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 2));

                blaze.setCustomName(Text.literal("Resistance Blaze"));

                serverWorld.spawnEntity(blaze);

                blazeCount++;
            }

            if (entity instanceof CreeperEntity creeper) {
                CreeperEntityAccessor accessor = (CreeperEntityAccessor) creeper;
                creeper.getDataTracker().set(accessor.charged(), true);
            }

        } finally {
            inCustomSpawn.set(false);
        }
    }
}