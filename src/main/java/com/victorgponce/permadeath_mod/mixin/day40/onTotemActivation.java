package com.victorgponce.permadeath_mod.mixin.day40;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(LivingEntity.class)
public abstract class onTotemActivation {

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Inject(method = "tryUseDeathProtector", at = @At("HEAD"), cancellable = true)
    public void onTotemActivationFunction(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        // Grants the 3% chance of totem activation
        if (Random.create().nextInt(100) >= 3) return;


        // Verify if the entity is a player
        if (!((Object) this instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        Inventory inventory = player.getInventory();

        // Count all totems in the inventory
        int totemCount = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                totemCount++;
            }
        }

        // Verify if the player has at least 2 totems
        if (totemCount < 2) {
            LOGGER.info("Player {} has only {} totems. Canceling activation.", player.getName().getString(), totemCount);
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // Remove one additional totem (not the one in hand)
        int removed = 0;
        for (int i = 0; i < inventory.size() && removed < 1; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                // Check if the totem is in the player's hand
                boolean isInHand = false;
                for (Hand hand : Hand.values()) {
                    if (player.getStackInHand(hand) == stack) {
                        isInHand = true;
                        break;
                    }
                }

                if (!isInHand) {
                    inventory.removeStack(i, 1);
                    removed++;
                    LOGGER.info("Removed 1 additional totem from player {}'s inventory.", player.getName().getString());
                }
            }
        }

        // If no totem was removed, remove from the non-active hand
        if (removed < 1) {
            Hand nonActiveHand = (player.getActiveHand() == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack stack = player.getStackInHand(nonActiveHand);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                player.setStackInHand(nonActiveHand, ItemStack.EMPTY);
                LOGGER.info("Removed 1 totem from {} hand of player {}.", nonActiveHand, player.getName().getString());
            }
        }
    }
}