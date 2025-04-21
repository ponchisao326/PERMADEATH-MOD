package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.drops.ravager.RavagerDropDay20;
import com.victorgponce.permadeath_mod.drops.ravager.RavagerDropDay25;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
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

        if (day >= 20 && day < 25) {
            new RavagerDropDay20().applyDrops(entity, Items.TOTEM_OF_UNDYING, world);
            ci.cancel();
        } else if (day >= 25) {
            new RavagerDropDay25().applyDrops(entity, Items.TOTEM_OF_UNDYING, world);
            ci.cancel();
        }
    }
}
