package com.victorgponce.permadeath_mod.mixin.common;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public abstract class PlayerNetheriteArmorMixin {

    private boolean wasWearingFullNetherite = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        boolean wearingFullNetherite = checkFullNetheriteArmor(player);
        EntityAttributeInstance maxHealthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (wearingFullNetherite && !wasWearingFullNetherite) {
            // Aplicar modificador de salud
            player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(28);
            wasWearingFullNetherite = true;

        } else if (!wearingFullNetherite && wasWearingFullNetherite) {
            // Remover modificador de salud
            wasWearingFullNetherite = false;

            player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20);

            // Ajustar salud actual si excede el nuevo máximo
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @Unique
    private boolean checkFullNetheriteArmor(PlayerEntity player) {
        return isNetheritePiece(player.getEquippedStack(EquipmentSlot.HEAD), Items.NETHERITE_HELMET) &&
               isNetheritePiece(player.getEquippedStack(EquipmentSlot.CHEST), Items.NETHERITE_CHESTPLATE) &&
               isNetheritePiece(player.getEquippedStack(EquipmentSlot.LEGS), Items.NETHERITE_LEGGINGS) &&
               isNetheritePiece(player.getEquippedStack(EquipmentSlot.FEET), Items.NETHERITE_BOOTS);
    }

    @Unique
    private boolean isNetheritePiece(ItemStack stack, Item item) {
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
