package com.victorgponce.permadeath_mod.mixin.day20;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(ServerWorld.class)
public class CustomSkeletonsMixin {

    @Unique private static final ThreadLocal<Boolean> inCustomSpawn = ThreadLocal.withInitial(() -> false);

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (inCustomSpawn.get()) return;
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));
        if (day < 20 || day >= 30) return;

        try {
            inCustomSpawn.set(true);
            World world = entity.getWorld();
            if (!(world instanceof ServerWorld serverWorld)) return;

            if (entity instanceof SpiderEntity) {
                // Create a skeleton instance
                int skeletonType = Random.create().nextInt(5);
                SkeletonEntity skeleton = createCustomSkeleton(serverWorld, skeletonType);

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
                skeletonEntity = createCustomSkeleton(serverWorld, skeletonType);

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
    private SkeletonEntity createCustomSkeleton(ServerWorld world, int type) {
        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, world);

        switch (type) {
            case 0:
                // Type 0: Nothing Special
                ItemStack normalBow = new ItemStack(Items.BOW);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, normalBow);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);
                break;
            case 1:
                // Type 1: Punch XX
                ItemStack bow = new ItemStack(Items.BOW);
                bow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PUNCH), 20);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, bow);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
                skeleton.setHealth(40);
                break;
            case 2:
                // Type 2: Iron Axe
                ItemStack ironAxe = new ItemStack(Items.IRON_AXE);
                ironAxe.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), 2);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, ironAxe);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
                skeleton.setHealth(20);
                break;
            case 3:
                // Type 3: Sharp 20
                ItemStack crossbow = new ItemStack(Items.CROSSBOW);
                crossbow.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), 20);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, crossbow);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
                skeleton.setHealth(40);
                break;
            case 4:
                // Type 4: Power X
                ItemStack bowX = new ItemStack(Items.BOW);
                bowX.addEnchantment(world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.POWER), 10);
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
                skeleton.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                skeleton.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
                skeleton.equipStack(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
                skeleton.equipStack(EquipmentSlot.MAINHAND, bowX);
                skeleton.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
                skeleton.setHealth(40);
                break;
        }

        return skeleton;
    }
}