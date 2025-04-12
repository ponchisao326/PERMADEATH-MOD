package com.victorgponce.permadeath_mod.util;

import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;

public class DropHelper {

    // Drop Canceled
    public static final Set<Class<? extends LivingEntity>> NO_DROP_ENTITIES = Set.of(
            IronGolemEntity.class,
            ZombifiedPiglinEntity.class,
            GhastEntity.class,
            GuardianEntity.class,
            MagmaCubeEntity.class,
            EndermanEntity.class,
            WitchEntity.class,
            WitherSkeletonEntity.class,
            EvokerEntity.class,
            PhantomEntity.class,
            SlimeEntity.class,
            DrownedEntity.class,
            BlazeEntity.class
    );

    public static boolean shouldCancelDrop(LivingEntity entity) {
        return NO_DROP_ENTITIES.contains(entity.getClass());
    }
}
