package com.victorgponce.permadeath_mod.mixin.day30.dragon_battle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(NoiseChunkGenerator.class)
public class EndIslandModifier {

    // End stone variants list
    @Unique
    private static final List<Block> END_STONE_VARIANTS = Arrays.asList(
            Blocks.END_STONE,
            Blocks.END_STONE_BRICKS
    );

    // This method is called when generating the end island
    @Inject(
            method = "getBlockState(Lnet/minecraft/world/gen/chunk/ChunkNoiseSampler;III" +
                    "Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyEndIsland(ChunkNoiseSampler sampler, int x, int y, int z, BlockState originalState, CallbackInfoReturnable<BlockState> cir) {
        if (originalState.isOf(Blocks.END_STONE)) {
            cir.setReturnValue(getRandomVariantAtPos(x, y, z));
        }
    }

    /**
     * Returns a random variant of end stone based on the given coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return A random variant of end stone.
     */
    @Unique
    private BlockState getRandomVariantAtPos(int x, int y, int z) {
        // Semilla basada en posición para consistencia
        long seed = new BlockPos(x, y, z).asLong();
        Random rnd = new Random(seed);
        return END_STONE_VARIANTS.get(rnd.nextInt(END_STONE_VARIANTS.size()))
                .getDefaultState();
    }

}
