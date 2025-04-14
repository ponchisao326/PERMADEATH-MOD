package com.victorgponce.permadeath_mod.mixin.day20.drops;

import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(LivingEntity.class)
public class RavagerDropChanger {

    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    private void onDropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));

        if (day >= 20) {
            if (entity instanceof RavagerEntity) {
                // Cancel the default drop
                ci.cancel();

                // 1% chance to drop a Totem of Undying
                if (Random.create().nextInt(100) < 1) {
                    ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                    entity.dropItem(totem, true, true);
                }
            }
        }
    }
}
