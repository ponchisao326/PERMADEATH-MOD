package com.victorgponce.permadeath_mod.data;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NetheriteItemHelper {

    private static final List<Item> prohibitedItemList1 = Arrays.asList(
            Items.NETHERITE_BLOCK,
            Items.NETHERITE_AXE,
            Items.NETHERITE_BOOTS,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_HOE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_INGOT,
            Items.NETHERITE_SCRAP,
            Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE
    );

    private static final List<Item> prohibitedItemList2 = Arrays.asList(
            Items.NETHERITE_BLOCK,
            Items.NETHERITE_AXE,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_HOE,
            Items.NETHERITE_INGOT,
            Items.NETHERITE_SCRAP,
            Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE
    );

    public static boolean prohibitedItems(Item item) {
        int day = ConfigFileManager.readConfig().getDay();

        if (day < 25) {
            return prohibitedItemList1.contains(item);
        }
        return prohibitedItemList2.contains(item);
    }

}
