package com.victorgponce.permadeath_mod.mixin.day30.dragon_battle;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Mixin(EnderDragonFight.class)
public class DragonBuilderMixin {

    @Shadow @Nullable private UUID dragonUuid;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Nullable private List<EndCrystalEntity> crystals;
    EnderDragonEntity dragon;
    private static int accumulatedTicks = 0;

    @Unique
    private final List<StatusEffectInstance> efectosDisponibles = Arrays.asList(
            new StatusEffectInstance(StatusEffects.SPEED, 999999, 4),
            new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 3),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 999999, 3),
            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 999999)
    );

    @Inject(method = "createDragon", at = @At("RETURN"))
    private void onDragonCreation(CallbackInfoReturnable<EnderDragonEntity> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day <= 30) return;

        dragon = cir.getReturnValue();

        if (dragon != null) {
            LOGGER.info("LLAMANDO AL DRAGON: {}", dragon);
            // Set the dragon's max health to 2000
            dragon.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(600);
            // Set the dragon's health to 2000
            dragon.setHealth(600);

            // Set the dragon's armour to +10
            dragon.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(10);

            // Set the dragon's effects
            for (StatusEffectInstance efecto : efectosDisponibles) {
                dragon.addStatusEffect(efecto);
            }

            // Set knockback resistance
            dragon.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        } else {
            LOGGER.error("El dragón no se ha creado correctamente: {}", dragon);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onDragonTick(CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day <= 30) return;

        accumulatedTicks++;

        if (accumulatedTicks == 20*30) { // 20 ticks per second * 30 seconds
            accumulatedTicks = 0;
            Vec3d dragonPosition = dragon.getPos();

            for (int i = 0; i < 2; i++) {
                GhastEntity ghast = new GhastEntity(EntityType.GHAST, dragon.getWorld());
                ghast.setPos(dragonPosition.x, dragonPosition.y, dragonPosition.z);
                dragon.getWorld().spawnEntity(ghast);
            }
        }
    }
}
