package com.victorgponce.permadeath_mod.mixin.day20.passivemobs;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractCowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(AbstractCowEntity.class)
public abstract class AggressiveCowMixin extends PassiveEntity {

    protected AggressiveCowMixin(EntityType<? extends PassiveEntity> type, World world) {
        super(type, world);
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

    @Inject(method = "createCowAttributes", at = @At("RETURN"), cancellable = true)
    private static void injectAttackDamage(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

            DefaultAttributeContainer.Builder builder = cir.getReturnValue();
            // We add the attribute "attack_damage" with a predetermined value (Changeable if you want. recommended to be high to grant the difficulty)
            builder.add(EntityAttributes.ATTACK_DAMAGE, 4.0D);
            cir.setReturnValue(builder);
    }
}
