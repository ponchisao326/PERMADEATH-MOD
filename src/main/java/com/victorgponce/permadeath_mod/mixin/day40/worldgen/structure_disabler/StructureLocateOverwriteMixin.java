package com.victorgponce.permadeath_mod.mixin.day40.worldgen.structure_disabler;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

import static com.victorgponce.permadeath_mod.Permadeath_mod.LOGGER;

@Mixin(LocateCommand.class)
public class StructureLocateOverwriteMixin {

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
    );;

    /**
     * This mixin overwrites the locate command to disable the ability to locate certain structures after day 40.
     * The structures are defined in the BLOCKED_STRUCTURES set.
     * If the structure is in the set, it will not be located.
     * This is useful to prevent the recursive loop of the locate command when the structure is not generated.
     */
    @Inject(method = "executeLocateStructure", at = @At("HEAD"), cancellable = true)
    private static void onExecuteLocateStructure(
            ServerCommandSource source,
            RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate,
            CallbackInfoReturnable<Integer> cir
    ) {
        int day = ConfigFileManager.readConfig().getDay();
        if (day < 40) return; // before day 40, we allow all structures

        // We try to extract the ID "minecraft:structure_name" that the player requested.
        String estructuraId = null;

        // First case: if it comes as a direct ID (Key-based):
        Optional<RegistryKey<Structure>> maybeLeft = predicate.getKey().left();
        if (maybeLeft.isPresent()) {
            estructuraId = maybeLeft.get().getValue().toString();
        } else {
            // If not, we check if it came as a Tag (Tag-based):
            Optional<TagKey<Structure>> maybeRight = predicate.getKey().right();
            if (maybeRight.isPresent()) {
                Identifier tagId = maybeRight.get().id();
                String namespace = tagId.getNamespace();
                String[] partes = tagId.getPath().split("/");
                String nombreFinal = partes[partes.length - 1];
                estructuraId = namespace + ":" + nombreFinal;
            }
        }

        // If we managed to extract an ID, we check against our blocked structures:
        if (estructuraId != null && BLOCKED_STRUCTURES.contains(estructuraId)) {
            LOGGER.info("Structure " + estructuraId + " is blocked after day 40.");
            source.sendError(Text.literal("This structure cannot be located after day 40."));
            cir.setReturnValue(0); // abortamos el comando devolviendo código 0
        }

        // If we reach here, it means the structure is not blocked, so we allow the command to proceed.
    }


}
