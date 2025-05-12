package com.victorgponce.permadeath_mod.mixin.day30.dragon_battle;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.phase.SittingFlamingPhase;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
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
        LivingEntity target = dragon.getAttacking();
        Vec3d dir;
        if (target != null) {
            LOGGER.info("Target found for dragon breath: {}", target);
            // Vector from the spawn position to the center of the target
            Vec3d vec3d = dragon.getRotationVec(1.0F);
            double f = target.getX() - (cloud.getX() + vec3d.x * (double)4.0F);
            double g = target.getBodyY((double)0.5F) - ((double)0.5F + cloud.getBodyY((double)0.5F));
            double h = target.getZ() - (cloud.getZ() + vec3d.z * (double)4.0F);
            dir = new Vec3d(f, g, h);
        } else {
            // If there's no valid target, use the dragon's look direction
            LOGGER.info("No target found for dragon breath, using dragon's look direction");
            dir = dragon.getRotationVec(1.0F);
            // Invert the motion to make it go in the opposite direction
            dir = invertMotion(dir);
        }

        FireballEntity fireball = new FireballEntity(world, dragon, dir.normalize(), 3 + Random.create().nextInt(3));
        fireball.setPosition(cloud.getX() + dir.x * (double)4.0F, cloud.getBodyY((double)0.5F) + (double)0.5F, fireball.getZ() + dir.z * (double)4.0F);

        // Launch it and discard the original cloud
        world.spawnEntity(fireball);
        return true;
    }

    private Vec3d invertMotion(Vec3d vec) {
        return new Vec3d(-vec.x, vec.y, -vec.z);
    }
}