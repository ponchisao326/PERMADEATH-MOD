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

    // ThreadLocal flag to prevent recursive custom spawns
    @Unique
    private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    // List of available status effects to apply to certain entities
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

    // Injects at the start of addEntity to customize entity spawning
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // Prevents recursion if already in a custom spawn
        if (inCustomSpawn.get()) return;
        int day = ConfigFileManager.readConfig().getDay();
        // Only apply changes after day 30
        if (day < 30) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();
            // Only proceed if in a ServerWorld
            if (!(world instanceof ServerWorld serverWorld)) return;

            // Transform Squids into Guardians if under the limit
            if (entity instanceof SquidEntity && guardianCount < 20) {
                cir.setReturnValue(false);

                GuardianEntity guardian = new GuardianEntity(EntityType.GUARDIAN, serverWorld);
                guardian.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                guardian.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 999999, 2));
                guardian.setCustomName(Text.literal("Speed Guardian"));

                serverWorld.spawnEntity(guardian);

                guardianCount++;
            }

            // Prevent spawning more Squids or Blazes if limits are reached
            if ((entity instanceof SquidEntity && guardianCount >= 20) || (entity instanceof BlazeEntity && blazeCount >= 15)) cir.setReturnValue(false);

            // Transform Bats into Blazes if under the limit
            if (entity instanceof BatEntity && blazeCount < 15) {
                cir.setReturnValue(false);

                BlazeEntity blaze = new BlazeEntity(EntityType.BLAZE, serverWorld);
                blaze.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

                blaze.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 999999, 2));
                blaze.setCustomName(Text.literal("Resistance Blaze"));

                serverWorld.spawnEntity(blaze);

                blazeCount++;
            }

            // Make all Creepers charged
            if (entity instanceof CreeperEntity creeper) {
                CreeperEntityAccessor accessor = (CreeperEntityAccessor) creeper;
                creeper.getDataTracker().set(accessor.charged(), true);
            }

            // Give Pillagers a super-enchanted crossbow and invisibility
            if (entity instanceof PillagerEntity pillager) {
                ItemStack crossBow = new ItemStack(Items.CROSSBOW);
                crossBow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.QUICK_CHARGE), 10);

                pillager.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 999999));
                pillager.equipStack(EquipmentSlot.MAINHAND, crossBow);
            }

            // Give Skeletons a stack of strong harming tipped arrows in offhand
            if (entity instanceof SkeletonEntity skeleton) {
                ItemStack tippedArrow = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.STRONG_HARMING);
                tippedArrow.setCount(64);
                skeleton.equipStack(EquipmentSlot.OFFHAND, tippedArrow);
                skeleton.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.0f);
            }

            // Equip Zombified Piglins with full diamond armor
            if (entity instanceof ZombifiedPiglinEntity piglin) {
                piglin.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                piglin.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                piglin.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                piglin.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            }

            // Make Iron Golems super fast
            if (entity instanceof IronGolemEntity ironGolem) {
                ironGolem.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 999999, 4));
            }

            // Make Endermen extra strong
            if (entity instanceof EndermanEntity enderman) {
                enderman.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 999999, 2));
            }

            // Give Silverfish several powerful effects
            if (entity instanceof SilverfishEntity silverfish) {
                for (int i = 0; i < 5; i++) {
                    silverfish.addStatusEffect(efectosDisponibles.get(i));
                }
            }

        } finally {
            // Always reset the custom spawn flag
            inCustomSpawn.set(false);
        }
    }
}