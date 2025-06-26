package com.victorgponce.permadeath_mod.mixin.day40;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(CraftingScreenHandler.class)
public class TorchCraftingMixin {

    /**
     * Prevents crafting of torches after day 40 in Permadeath mode.
     * If the player tries to craft a torch, it will cancel the crafting and send a message.
     *
     * @param handler The screen handler for crafting.
     * @param world   The server world.
     * @param player  The player attempting to craft.
     * @param craftingInventory The inventory used for crafting.
     * @param resultInventory The inventory where the result is placed.
     * @param recipe The recipe being used for crafting.
     * @param ci Callback information to cancel the method if necessary.
     */
    @Inject(
            method = "updateResult",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void preventTorchCrafting(
            ScreenHandler handler,
            ServerWorld world,
            PlayerEntity player,
            RecipeInputInventory craftingInventory,
            CraftingResultInventory resultInventory,
            @Nullable RecipeEntry<CraftingRecipe> recipe,
            CallbackInfo ci
    ) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;

        // Check if the result inventory is not null
        if (!(resultInventory == null)) {
            // Check if the output is a torch
            ItemStack output = resultInventory.getStack(1);
            if (output.isOf(Items.TORCH) ||
                    output.isOf(Items.SOUL_TORCH) ||
                    output.isOf(Items.REDSTONE_TORCH)) {

                // Cancel the crafting
                resultInventory.setStack(0, ItemStack.EMPTY);
                ci.cancel();

                // Debug message
                if (player instanceof ServerPlayerEntity) {
                    (player).sendMessage(
                            Text.literal("¡You cannot craft torches after day 40!"),
                            false
                    );
                }
            }
        } else {
            LOGGER.warn("CraftingResultInventory is null, cannot check for torch crafting.");
        }
    }
}