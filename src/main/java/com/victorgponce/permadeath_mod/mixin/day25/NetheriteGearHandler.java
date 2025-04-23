package com.victorgponce.permadeath_mod.mixin.day25;

import com.victorgponce.permadeath_mod.drops.netherite_gear.DropHandler;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Unit;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(LivingEntity.class)
public class NetheriteGearHandler {

    @Unique
    private static final ComponentType<Unit> UNBREAKABLE = DataComponentTypes.UNBREAKABLE;

    @Inject(method = "onDeath", at = @At("TAIL"), cancellable = true)
    private void onDropLoot(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        HashMap<Integer, String> lines = ConfigFileManager.readFile();

        int day = Integer.parseInt(lines.get(4));

        if (day >= 25 && day < 30) {
            if (entity instanceof SlimeEntity slime) {
                if (Random.create().nextInt(100) < 5) {
                    ItemStack gear = new ItemStack(Items.NETHERITE_HELMET);
                    gear.set(UNBREAKABLE, Unit.INSTANCE);
                    new DropHandler().applyDrops(entity, gear);
                }
            } else if (entity instanceof MagmaCubeEntity) {
                if (Random.create().nextInt(100) < 3) {
                    ItemStack gear = new ItemStack(Items.NETHERITE_CHESTPLATE);
                    gear.set(UNBREAKABLE, Unit.INSTANCE);
                    new DropHandler().applyDrops(entity, gear);
                }
            } else if (entity instanceof CaveSpiderEntity) {
                if (Random.create().nextInt(100) < 4) {
                    ItemStack gear = new ItemStack(Items.NETHERITE_LEGGINGS);
                    gear.set(UNBREAKABLE, Unit.INSTANCE);
                    new DropHandler().applyDrops(entity, gear);
                }
            } else if (entity instanceof GhastEntity) {
                if (Random.create().nextInt(100) < 2) {
                    ItemStack gear = new ItemStack(Items.NETHERITE_BOOTS);
                    gear.set(UNBREAKABLE, Unit.INSTANCE);
                    new DropHandler().applyDrops(entity, gear);
                }
            }
            ci.cancel();
        }
    }
}
