package com.victorgponce.permadeath_mod.mixin.day40.worldgen.structure_disabler;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(TradeOffers.SellMapFactory.class)
public class MapTradeDisablerMixin {

    @Shadow @Final private TagKey<Structure> structure;
    @Shadow @Final private int maxUses;
    @Shadow @Final private int experience;

    /**
     * Lista de estructuras que queremos bloquear (ya sea porque las deshabilitamos globalmente
     * o porque pasa el día 40).
     */
    @Unique
    private static final Set<String> BLOCKED_STRUCTURES = Set.of(
            "minecraft:on_trial_chambers_maps",
            "minecraft:on_treasure_maps",
            "minecraft:on_woodland_explorer_maps",
            "minecraft:shipwreck",
            "minecraft:on_ocean_explorer_maps",
            "minecraft:on_jungle_explorer_maps"
    );


    @Inject(method = "create", at = @At(value = "HEAD"), cancellable = true)
    private void onCreate(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;
        TagKey<Structure> structure = this.structure;

        // Get the structure ID as a string
        String structureIdString = structure.id().toString();

        // Item Renamed
        ItemStack structureVoidRenamed = new ItemStack(Items.STRUCTURE_VOID);
        structureVoidRenamed.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Structure Maps Trade Disabled"));

        LOGGER.info("Attempting to create trade for structure: {}, with id: {}", structure, structureIdString);
        if (BLOCKED_STRUCTURES.contains(structureIdString)) {
            cir.setReturnValue(new TradeOffer(
                    new TradedItem(Items.STRUCTURE_VOID, 1), structureVoidRenamed, this.maxUses, this.experience, 0.2F
            ));
        }
    }

}
