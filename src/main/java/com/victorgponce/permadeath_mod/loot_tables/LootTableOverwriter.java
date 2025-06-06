package com.victorgponce.permadeath_mod.loot_tables;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.item.Items;

public class LootTableOverwriter {
    public static void register() {
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            int day = ConfigFileManager.readConfig().getDay();
            if (day < 40) return null;
            if (key.equals(LootTables.SHIPWRECK_MAP_CHEST)) {
                LootTable.Builder builder = LootTable.builder();

                builder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(3))
                        .with(ItemEntry.builder(Items.COMPASS))
                        .with(ItemEntry.builder(Items.MAP))
                        .with(ItemEntry.builder(Items.CLOCK))
                        .with(ItemEntry.builder(Items.PAPER)
                                .weight(20)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 10))))
                        .with(ItemEntry.builder(Items.FEATHER)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 5))))
                        .with(ItemEntry.builder(Items.BOOK)
                                .weight(5)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 5))))
                );

                builder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(EmptyEntry.builder().weight(5))
                        .with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE)
                                .apply(SetCountLootFunction.builder(
                                        ConstantLootNumberProvider.create(2))))
                );

                return builder.build();
            } else if (key.equals(LootTables.UNDERWATER_RUIN_BIG_CHEST)) {
                LootTable.Builder builder = LootTable.builder();

                builder.pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(2, 8))
                        .with(ItemEntry.builder(Items.COAL)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.GOLD_NUGGET)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 3))))
                        .with(ItemEntry.builder(Items.EMERALD))
                        .with(ItemEntry.builder(Items.WHEAT)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(2, 3))))
                );

                builder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.GOLDEN_APPLE))
                        .with(ItemEntry.builder(Items.BOOK)
                                .weight(5)
                                .apply(EnchantRandomlyLootFunction.builder(registries)))
                        .with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
                        .with(ItemEntry.builder(Items.GOLDEN_HELMET))
                        .with(ItemEntry.builder(Items.FISHING_ROD)
                                .weight(5)
                                .apply(EnchantRandomlyLootFunction.builder(registries)))
                );

                return builder.build();
            } else if (key.equals(LootTables.UNDERWATER_RUIN_SMALL_CHEST)) {
                LootTable.Builder builder = LootTable.builder();

                builder.pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(2, 8))
                        .with(ItemEntry.builder(Items.COAL)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.STONE_AXE)
                                .weight(2))
                        .with(ItemEntry.builder(Items.ROTTEN_FLESH)
                                .weight(5))
                        .with(ItemEntry.builder(Items.EMERALD))
                        .with(ItemEntry.builder(Items.WHEAT)
                                .weight(10)
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(2, 3))))
                );

                builder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
                        .with(ItemEntry.builder(Items.GOLDEN_HELMET))
                        .with(ItemEntry.builder(Items.FISHING_ROD)
                                .weight(5)
                                .apply(EnchantRandomlyLootFunction.builder(registries)))
                );

                return builder.build();
            }
            return null;
        });
    }
}