package com.victorgponce.permadeath_mod.mixin.day30;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

import static com.victorgponce.permadeath_mod.util.EntitiesCounter.blazeCount;
import static com.victorgponce.permadeath_mod.util.EntitiesCounter.guardianCount;

@Mixin(ServerWorld.class)
public class EntitiesTransformation {

    @Unique
    private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    @Unique
    public final List<StatusEffectInstance> efectosDisponibles = Arrays.asList(
            new StatusEffectInstance(StatusEffects.SPEED, 999999, 2),
            new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 3),
            new StatusEffectInstance(StatusEffects.JUMP_BOOST, 999999, 4),
            new StatusEffectInstance(StatusEffects.GLOWING, 999999),
            new StatusEffectInstance(StatusEffects.REGENERATION, 999999, 3),
            new StatusEffectInstance(StatusEffects.INVISIBILITY, 999999),
            new StatusEffectInstance(StatusEffects.SLOW_FALLING, 999999),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 999999)
    );

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

                guardian.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 999999, 2));

                guardian.setCustomName(Text.literal("Speed Guardian"));

                serverWorld.spawnEntity(guardian);

                guardianCount++;
            }

            if ((entity instanceof SquidEntity && guardianCount >= 20) || (entity instanceof BlazeEntity && blazeCount >= 15)) cir.setReturnValue(false);

            if (entity instanceof BatEntity && blazeCount < 15) {
                cir.setReturnValue(false);

                BlazeEntity blaze = new BlazeEntity(EntityType.BLAZE, serverWorld);
                blaze.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                blaze.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 999999, 2));

                blaze.setCustomName(Text.literal("Resistance Blaze"));

                serverWorld.spawnEntity(blaze);

                blazeCount++;
            }

            if (entity instanceof CreeperEntity creeper) {
                CreeperEntityAccessor accessor = (CreeperEntityAccessor) creeper;
                creeper.getDataTracker().set(accessor.charged(), true);
            }

            if (entity instanceof PillagerEntity pillager) {
                ItemStack crossBow = new ItemStack(Items.CROSSBOW);
                crossBow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.QUICK_CHARGE), 10);

                pillager.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 999999));
                pillager.equipStack(EquipmentSlot.MAINHAND, crossBow);
            }

            if (entity instanceof SkeletonEntity skeleton) {
                ItemStack tippedArrow = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.STRONG_HARMING);
                tippedArrow.setCount(64);
                skeleton.equipStack(EquipmentSlot.OFFHAND, tippedArrow);
                skeleton.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.0f);
            }

            if (entity instanceof ZombifiedPiglinEntity piglin) {
                piglin.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                piglin.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                piglin.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                piglin.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            }

            if (entity instanceof IronGolemEntity ironGolem) {
                ironGolem.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 999999, 4));
            }

            if (entity instanceof EndermanEntity enderman) {
                enderman.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 2));
            }

            if (entity instanceof SilverfishEntity silverfish) {
                for (int i = 0; i < 5; i++) {
                    silverfish.addStatusEffect(efectosDisponibles.get(i));
                }
            }

        } finally {
            inCustomSpawn.set(false);
        }
    }
}