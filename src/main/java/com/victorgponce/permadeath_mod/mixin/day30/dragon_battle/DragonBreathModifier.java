package com.victorgponce.permadeath_mod.mixin.day30.dragon_battle;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.phase.SittingFlamingPhase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(SittingFlamingPhase.class)
public abstract class DragonBreathModifier {

    /**
     * Redirects each call to world.spawnEntity(Entity) within serverTick(...)
     * If the entity to spawn is the breath cloud, instead
     * we create and launch a ghast-style fireball.
     */
    @Redirect(
            method = "serverTick(Lnet/minecraft/server/world/ServerWorld;)V",
            at = @At(
                    value = "INVOKE",
                    // targeting the spawnEntity(AreaEffectCloudEntity) that creates the cloud
                    target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private boolean onSpawnDragonBreath(ServerWorld world, Entity entity) {
        // If it's not the dragon cloud, proceed normally
        if (!(entity instanceof AreaEffectCloudEntity cloud)) {
            return world.spawnEntity(entity);
        }

        LivingEntity dragon = cloud.getOwner();
        if (dragon == null) {
            return world.spawnEntity(entity);
        }

        // Attempt to get its target (e.g., a player)
        PlayerEntity target = world.getClosestPlayer(
                cloud.getX(), cloud.getY(), cloud.getZ(),
                64,
                false
        );
        Vec3d dir;
        if (target != null) {
            // Calculate direction from cloud's position to target
            double f = target.getX() - cloud.getX();
            double g = target.getBodyY(0.5) - (cloud.getBodyY(0.5) + 0.5);
            double h = target.getZ() - cloud.getZ();
            dir = new Vec3d(f, g, h);
        } else {
            dir = dragon.getRotationVec(1.0F);
            dir = invertMotion(dir);
        }

        FireballEntity fireball = new FireballEntity(world, dragon, dir.normalize().multiply(2.0), 3 + Random.create().nextInt(3));
        fireball.setPosition(
                cloud.getX(),
                cloud.getBodyY(0.5) + 0.5,
                cloud.getZ()
        );

        // Launch it and discard the original cloud
        world.spawnEntity(fireball);
        return true;
    }

    /**
     * Inverts the motion vector of the fireball.
     *
     * @param vec The original motion vector.
     * @return The inverted motion vector.
     */
    @Unique
    private Vec3d invertMotion(Vec3d vec) {
        return new Vec3d(-vec.x, vec.y, -vec.z);
    }
}