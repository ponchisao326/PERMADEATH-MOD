package com.victorgponce.permadeath_mod.mixin.common;

import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.util.BanManager;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import com.victorgponce.permadeath_mod.util.DeathTrain;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ServerPlayerEntity.class)
public abstract class onDeathMixin {

    @Shadow public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String playerName = player.getName().getString();
        String cause = getSimpleDeathCause(damageSource);

        // DB connection
        String url = ConfigFileManager.readConfig().getJdbc();
        String user = ConfigFileManager.readConfig().getUser();
        String password = ConfigFileManager.readConfig().getPassword();

        // Validate URL
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid URL in the configuration file.");
        }

        // Escape single quotes for SQL
        String escapedPlayerName = playerName.replace("'", "''");
        String escapedCause = cause.replace("'", "''");

        // Update lives and player status
        String updateSql = "UPDATE Players SET Lives = Lives - 1, DeathCount = DeathCount + 1, " +
                "Status = CASE WHEN (Lives - 1) <= 0 THEN 'inactive' ELSE Status END " +
                "WHERE Username = ?";
        DataBaseHandler.databaseConnectorStatement(url, user, password, updateSql, escapedPlayerName);

        String insertDeathSql = "INSERT INTO Deaths (PlayerID, Cause) " +
                "VALUES ((SELECT PlayerID FROM Players WHERE Username = ?), ?)";
        DataBaseHandler.databaseConnectorStatement(url, user, password, insertDeathSql, escapedPlayerName, escapedCause);

        DeathTrain.enableDeathTrain(damageSource);
        // Call the CheckAndBan function
        BanManager.checkAndBan(player);
    }

    @Unique
    private String getSimpleDeathCause(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.IN_FIRE)) {
            return "fire";
        } else if (damageSource.isOf(DamageTypes.CAMPFIRE)) {
            return "campfire";
        } else if (damageSource.isOf(DamageTypes.LIGHTNING_BOLT)) {
            return "lightning";
        } else if (damageSource.isOf(DamageTypes.ON_FIRE)) {
            return "burns";
        } else if (damageSource.isOf(DamageTypes.LAVA)) {
            return "lava";
        } else if (damageSource.isOf(DamageTypes.HOT_FLOOR)) {
            return "hot floor";
        } else if (damageSource.isOf(DamageTypes.IN_WALL)) {
            return "suffocation";
        } else if (damageSource.isOf(DamageTypes.CRAMMING)) {
            return "cramming";
        } else if (damageSource.isOf(DamageTypes.DROWN)) {
            return "drowning";
        } else if (damageSource.isOf(DamageTypes.STARVE)) {
            return "starvation";
        } else if (damageSource.isOf(DamageTypes.CACTUS)) {
            return "cactus";
        } else if (damageSource.isOf(DamageTypes.FALL)) {
            return "fall";
        } else if (damageSource.isOf(DamageTypes.ENDER_PEARL)) {
            return "ender pearl";
        } else if (damageSource.isOf(DamageTypes.FLY_INTO_WALL)) {
            return "flying";
        } else if (damageSource.isOf(DamageTypes.OUT_OF_WORLD)) {
            return "void";
        } else if (damageSource.isOf(DamageTypes.MAGIC)) {
            return "magic";
        } else if (damageSource.isOf(DamageTypes.WITHER)) {
            return "wither";
        } else if (damageSource.isOf(DamageTypes.DRAGON_BREATH)) {
            return "dragon breath";
        } else if (damageSource.isOf(DamageTypes.DRY_OUT)) {
            return "dehydration";
        } else if (damageSource.isOf(DamageTypes.SWEET_BERRY_BUSH)) {
            return "berry bush";
        } else if (damageSource.isOf(DamageTypes.FREEZE)) {
            return "freezing";
        } else if (damageSource.isOf(DamageTypes.STALAGMITE)) {
            return "stalagmite";
        } else if (damageSource.isOf(DamageTypes.FALLING_BLOCK)) {
            return "falling block";
        } else if (damageSource.isOf(DamageTypes.FALLING_ANVIL)) {
            return "anvil";
        } else if (damageSource.isOf(DamageTypes.FALLING_STALACTITE)) {
            return "stalactite";
        } else if (damageSource.isOf(DamageTypes.STING)) {
            return "sting";
        } else if (damageSource.isOf(DamageTypes.MOB_ATTACK) || damageSource.isOf(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
            return "mob attack";
        } else if (damageSource.isOf(DamageTypes.PLAYER_ATTACK)) {
            return "PvP";
        } else if (damageSource.isOf(DamageTypes.ARROW)) {
            return "arrow";
        } else if (damageSource.isOf(DamageTypes.TRIDENT)) {
            return "trident";
        } else if (damageSource.isOf(DamageTypes.MOB_PROJECTILE) || damageSource.isOf(DamageTypes.SPIT)) {
            return "projectile";
        } else if (damageSource.isOf(DamageTypes.FIREWORKS)) {
            return "fireworks";
        } else if (damageSource.isOf(DamageTypes.FIREBALL)) {
            return "fireball";
        } else if (damageSource.isOf(DamageTypes.WITHER_SKULL)) {
            return "wither skull";
        } else if (damageSource.isOf(DamageTypes.THORNS)) {
            return "thorns";
        } else if (damageSource.isOf(DamageTypes.EXPLOSION) || damageSource.isOf(DamageTypes.PLAYER_EXPLOSION)) {
            return "explosion";
        } else if (damageSource.isOf(DamageTypes.SONIC_BOOM)) {
            return "sonic boom";
        } else if (damageSource.isOf(DamageTypes.BAD_RESPAWN_POINT)) {
            return "respawn point";
        } else if (damageSource.isOf(DamageTypes.OUTSIDE_BORDER)) {
            return "world border";
        } else if (damageSource.isOf(DamageTypes.MACE_SMASH)) {
            return "mace smash";
        } else {
            return "other";
        }
    }

}
