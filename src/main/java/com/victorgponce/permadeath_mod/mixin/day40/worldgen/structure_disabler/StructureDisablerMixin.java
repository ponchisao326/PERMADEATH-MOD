package com.victorgponce.permadeath_mod.mixin.day40.worldgen.structure_disabler;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

@Mixin(ChunkGenerator.class)
public class StructureDisablerMixin {

    // We define a set of structures that we want to block after day 40
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

    /**
     * This mixin disables the generation of certain structures after day 40.
     * The structures are defined in the BLOCKED_STRUCTURES set.
     * If the structure is in the set, it will not be generated.
     */
    @Inject(
            method = "trySetStructureStart",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onTrySetStructureStart(
            StructureSet.WeightedEntry weightedEntry, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager, NoiseConfig noiseConfig, StructureTemplateManager structureManager, long seed, Chunk chunk, ChunkPos pos, ChunkSectionPos sectionPos, RegistryKey<World> dimension, CallbackInfoReturnable<Boolean> cir
    ) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return;

        // We obtain the structure from the WeightedEntry
        Optional<RegistryKey<Structure>> estructuraKey = weightedEntry.structure().getKey();

        // If the structure is not present, we return early
        estructuraKey.ifPresent(key -> {
            // We obtain the structure ID as a string
            String estructuraId = key.getValue().toString();

            // If the structure is in the blocked list, we cancel the method
            if (BLOCKED_STRUCTURES.contains(estructuraId)) {
                cir.setReturnValue(false);
            }

        });
    }

}
