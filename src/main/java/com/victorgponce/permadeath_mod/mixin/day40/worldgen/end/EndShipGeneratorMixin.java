package com.victorgponce.permadeath_mod.mixin.day40.worldgen.end;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCityGenerator.Piece.class)
public class EndShipGeneratorMixin {

    @Inject(method = "handleMetadata", at = @At("HEAD"), cancellable = true)
    private void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo ci) {
        if (metadata.startsWith("Elytra") && boundingBox.contains(pos) && World.isValid(pos)) {

            // Create a new ItemFrameEntity at the specified position
            ItemFrameEntity itemFrameEntity = new ItemFrameEntity(
                world.toServerWorld(),
                pos,
                ((EndCityGenerator.Piece)(Object)this).getPlacementData().getRotation().rotate(Direction.SOUTH)
            );

            // Replace normal Elytra with a custom Elytra
            ItemStack customElytra = new ItemStack(Items.ELYTRA);
            // Set custom properties for the Elytra
            customElytra.setDamage(432);

            // Set the ItemFrameEntity to hold the custom Elytra
            itemFrameEntity.setHeldItemStack(customElytra, false);
            world.spawnEntity(itemFrameEntity);

            // Cancel the original method to prevent default Elytra from spawning
            ci.cancel();
        }
    }

}
