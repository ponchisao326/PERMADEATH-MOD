package com.victorgponce.permadeath_mod.network;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NetheriteProhibiter implements PlayerBlockBreakEvents.Before {
    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {

        if (playerEntity.isInCreativeMode()) return true;
        Block block = blockState.getBlock();

        if (block == Blocks.NETHERITE_BLOCK || block == Blocks.ANCIENT_DEBRIS) {
            // Send a message to the player
            playerEntity.sendMessage(Text.literal("You cannot break this block, netherite is prohibited")
                    .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
            // Cancel the event if the conditions are met
            return false;
        }
        // Nothing happens
        return true;
    }
}
