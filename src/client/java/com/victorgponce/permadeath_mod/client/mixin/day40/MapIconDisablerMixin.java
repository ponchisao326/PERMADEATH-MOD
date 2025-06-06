package com.victorgponce.permadeath_mod.client.mixin.day40;

import com.victorgponce.permadeath_mod.client.data.BinaryDataHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static com.victorgponce.permadeath_mod.client.Permadeath_modClient.LOGGER;

@Mixin(MapState.class)
public class MapIconDisablerMixin {

    @Unique
    private static final Set<String> BLOCKED_STRUCTURES = Set.of(
            "minecraft:ancient_city",
            "minecraft:mansion",
            "minecraft:fortress",
            "minecraft:bastion_remnant",
            "minecraft:trial_chambers",
            "minecraft:monument",
            "minecraft:desert_pyramid",
            "minecraft:jungle_pyramid"
    );

    @Inject(method = "addDecorationsNbt", at = @At("HEAD"), cancellable = true)
    private static void onAddDecorationsNbt(ItemStack stack, BlockPos pos, String id, RegistryEntry<MapDecorationType> decorationType, CallbackInfo ci) {
        BinaryDataHandler config = BinaryDataHandler.getInstance();
        if (config.getDay() < 40) return;

        // Log the blocked structure + all information
        LOGGER.info("Blocked map decoration for structure: {}, at position: {}, with id: {}, type: {}", id, pos, decorationType, stack);

        // If the structure is in the blocked list, we cancel the addition of the decoration
        if (BLOCKED_STRUCTURES.contains(id)) {
            ci.cancel();
        }
    }

}
