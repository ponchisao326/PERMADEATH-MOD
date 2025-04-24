package com.victorgponce.permadeath_mod.mixin.day30;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public class CustomSkeletonsMixinDay30 {

    @Unique private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (inCustomSpawn.get()) return;
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));
        if (day < 30) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();
            if (!(world instanceof ServerWorld serverWorld)) return;

            if (entity instanceof SpiderEntity) {
                // Create a skeleton instance
                int skeletonType = Random.create().nextInt(5);
                SkeletonEntity skeleton = createCustomSkeletonDay25(serverWorld, skeletonType);

                // Position the skeleton where the spider is (you can adjust an offset if desired)
                skeleton.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                // Add the skeleton as a passenger to the spider.
                // Ensure the passenger entity is registered in the world.
                serverWorld.spawnEntity(skeleton);
                skeleton.startRiding(entity);
            }

            if (entity instanceof SkeletonEntity skeletonEntity) {
                cir.setReturnValue(false);
                // Create a skeleton instance
                int skeletonType = Random.create().nextInt(5);
                skeletonEntity = createCustomSkeletonDay25(serverWorld, skeletonType);

                // Position the skeleton where the spider is (you can adjust an offset if desired)
                skeletonEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                // Add the skeleton as a passenger to the spider.
                // Ensure the passenger entity is registered in the world.
                serverWorld.spawnEntity(skeletonEntity);
            }
        } finally {
            inCustomSpawn.set(false);
        }
    }

    @Unique
    private SkeletonEntity createCustomSkeletonDay25(ServerWorld world, int type) {
        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, world);

        switch (type) {
            case 0:
                // Type 0: Esqueleto Guerrero
                ItemStack normalBow = new ItemStack(Items.BOW);
                ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
                ItemStack diamondChestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
                ItemStack diamondLeggins = new ItemStack(Items.DIAMOND_LEGGINGS);
                ItemStack diamondBoots = new ItemStack(Items.DIAMOND_BOOTS);
                diamondHelmet.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), 4);
                diamondChestplate.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), 4);
                diamondLeggins.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), 4);
                diamondBoots.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), 4);

                skeleton.equipStack(EquipmentSlot.HEAD, diamondHelmet);
                skeleton.equipStack(EquipmentSlot.CHEST, diamondChestplate);
                skeleton.equipStack(EquipmentSlot.LEGS, diamondLeggins);
                skeleton.equipStack(EquipmentSlot.FEET, diamondBoots);
                skeleton.equipStack(EquipmentSlot.MAINHAND, normalBow);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);
                skeleton.setCustomName(Text.of("Esqueleto Guerrero"));
                skeleton.setCustomNameVisible(true);
                break;
            case 1:
                // Type 1: Esqueleto Infernal
                ItemStack diamondAxe = new ItemStack(Items.DIAMOND_AXE);
                diamondAxe.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), 10);

                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, diamondAxe);

                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);

                skeleton.setCustomName(Text.of("Esqueleto Infernal"));
                skeleton.setCustomNameVisible(true);
                break;
            case 2:
                // Type 2: Esqueleto Asesino
                ItemStack crossBow = new ItemStack(Items.CROSSBOW);
                crossBow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), 25);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, crossBow);

                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);

                skeleton.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 2));

                skeleton.setCustomName(Text.of("Esqueleto Asesino"));
                skeleton.setCustomNameVisible(true);
                break;
            case 3:
                // Type 3: Esqueleto Táctico
                ItemStack bow = new ItemStack(Items.BOW);
                bow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PUNCH), 30);
                bow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.POWER), 25);

                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, bow);

                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);

                skeleton.setCustomName(Text.of("Esqueleto Táctico"));
                skeleton.setCustomNameVisible(true);
                break;
            case 4:
                // Type 4: Esqueleto Pesadilla
                ItemStack bowX = new ItemStack(Items.BOW);
                bowX.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.POWER), 50);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, bowX);

                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);

                skeleton.setCustomName(Text.of("Esqueleto Pesadilla"));
                skeleton.setCustomNameVisible(true);
                break;
        }

        return skeleton;
    }

}
