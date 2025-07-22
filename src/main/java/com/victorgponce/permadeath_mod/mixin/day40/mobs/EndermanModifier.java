package com.victorgponce.permadeath_mod.mixin.day40.mobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public abstract class EndermanModifier extends HostileEntity {

    protected EndermanModifier(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // Inject at the end of the constructor to make them aggressive from the start
    @Inject(method = "<init>", at = @At("TAIL"))
    private void makeAggressiveByDefault(EntityType<? extends EndermanEntity> entityType, World world, CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        // 60% of the time, make the enderman aggressive
        if (world.random.nextFloat() >= 0.6f) return;

        EndermanEntity self = (EndermanEntity)(Object)this;
        // Provoke the enderman to make it aggressive
        self.setProvoked();

        // If it's on the server, find nearby players
        if (!world.isClient()) {
            PlayerEntity nearestPlayer = world.getClosestPlayer(self, 64.0);
            if (nearestPlayer != null) {
                self.setTarget(nearestPlayer);
            }
        }
    }

    // Modify the goals to add a target goal for players with high priority
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addAggressiveTargeting(CallbackInfo ci) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        // 60% of the time, make the enderman aggressive
        if (this.getEntityWorld().random.nextFloat() >= 0.6f) return;

        // Agregate an objective to find and attack players with high priority (1)
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
}