package com.victorgponce.permadeath_mod.mixin.day20.passivemobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(PigEntity.class)
public abstract class AggresivePigMixin extends PassiveEntity {
    protected AggresivePigMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addAggressiveGoals(CallbackInfo ci) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        if (lines.get(4).equals("20")) {
            // We aggregate an attack goal to make the passiveEntity to follow and attack to the target
            // We make priority to 0 (highest) to force the execution
            this.goalSelector.add(0, new MeleeAttackGoal((PathAwareEntity) (Object) this, 1.2D, false));
            // Configuramos la asignación de target:
            // We configure the assignation of the target
            this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
            this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
            // LookAtEntityGoal to make the entity look at the player (Optional but if you don't add it the entity may attack you looking at any random position)
            this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        }
    }

    @Inject(method = "createPigAttributes", at = @At("RETURN"), cancellable = true)
    private static void injectAttackDamage(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        DefaultAttributeContainer.Builder builder = cir.getReturnValue();
        // We add the attribute "attack_damage" with a predetermined value (Changeable if you want. recommended to be high to grant the difficulty)
        builder.add(EntityAttributes.ATTACK_DAMAGE, 4.0D);
        cir.setReturnValue(builder);
    }
}
