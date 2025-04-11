package com.victorgponce.permadeath_mod.mixin;

import com.victorgponce.permadeath_mod.data.DataBaseHandler;
import com.victorgponce.permadeath_mod.util.BanManager;
import com.victorgponce.permadeath_mod.util.ConfigFileManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ServerPlayerEntity.class)
public class onDeathMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String playerName = player.getName().getString();
        String cause = getSimpleDeathCause(damageSource);

        // DB connection
        HashMap<Integer, String> lines = ConfigFileManager.readFile();
        String url = lines.get(1);
        String user = lines.get(2);
        String password = lines.get(3);

        // Validate URL
        Pattern pattern = Pattern.compile("^jdbc:mysql://([\\w.-]+)(?::(\\d+))?/([\\w]+)$");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            throw new RuntimeException("URL inválida en el archivo de configuración.");
        }

        // Escape single quotes for SQL
        String escapedPlayerName = playerName.replace("'", "''");
        String escapedCause = cause.replace("'", "''");

        // Update lives and player status
        String updateSql = "UPDATE Players SET Lives = Lives - 1, DeathCount = DeathCount + 1, " +
                "Status = CASE WHEN (Lives - 1) <= 0 THEN 'inactive' ELSE Status END " +
                "WHERE Username = '" + escapedPlayerName + "'";
        DataBaseHandler.databaseConnector(url, user, password, updateSql);

        // Insert death cause
        String insertDeathSql = "INSERT INTO Deaths (PlayerID, Cause) " +
                "VALUES ((SELECT PlayerID FROM Players WHERE Username = '" + escapedPlayerName + "'), '" + escapedCause + "')";
        DataBaseHandler.databaseConnector(url, user, password, insertDeathSql);

        // Call the CheckAndBan function
        BanManager.checkAndBan(player);
    }

    @Unique
    private String getSimpleDeathCause(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.IN_FIRE)) {
            return "fuego";
        } else if (damageSource.isOf(DamageTypes.CAMPFIRE)) {
            return "hoguera";
        } else if (damageSource.isOf(DamageTypes.LIGHTNING_BOLT)) {
            return "rayo";
        } else if (damageSource.isOf(DamageTypes.ON_FIRE)) {
            return "quemaduras";
        } else if (damageSource.isOf(DamageTypes.LAVA)) {
            return "lava";
        } else if (damageSource.isOf(DamageTypes.HOT_FLOOR)) {
            return "suelo caliente";
        } else if (damageSource.isOf(DamageTypes.IN_WALL)) {
            return "asfixia";
        } else if (damageSource.isOf(DamageTypes.CRAMMING)) {
            return "aplastamiento";
        } else if (damageSource.isOf(DamageTypes.DROWN)) {
            return "ahogamiento";
        } else if (damageSource.isOf(DamageTypes.STARVE)) {
            return "hambre";
        } else if (damageSource.isOf(DamageTypes.CACTUS)) {
            return "cactus";
        } else if (damageSource.isOf(DamageTypes.FALL)) {
            return "caída";
        } else if (damageSource.isOf(DamageTypes.ENDER_PEARL)) {
            return "perla ender";
        } else if (damageSource.isOf(DamageTypes.FLY_INTO_WALL)) {
            return "vuelo";
        } else if (damageSource.isOf(DamageTypes.OUT_OF_WORLD)) {
            return "vacío";
        } else if (damageSource.isOf(DamageTypes.MAGIC)) {
            return "magia";
        } else if (damageSource.isOf(DamageTypes.WITHER)) {
            return "wither";
        } else if (damageSource.isOf(DamageTypes.DRAGON_BREATH)) {
            return "aliento de dragón";
        } else if (damageSource.isOf(DamageTypes.DRY_OUT)) {
            return "deshidratación";
        } else if (damageSource.isOf(DamageTypes.SWEET_BERRY_BUSH)) {
            return "arbusto de bayas";
        } else if (damageSource.isOf(DamageTypes.FREEZE)) {
            return "congelación";
        } else if (damageSource.isOf(DamageTypes.STALAGMITE)) {
            return "estalagmita";
        } else if (damageSource.isOf(DamageTypes.FALLING_BLOCK)) {
            return "bloque caído";
        } else if (damageSource.isOf(DamageTypes.FALLING_ANVIL)) {
            return "yunque";
        } else if (damageSource.isOf(DamageTypes.FALLING_STALACTITE)) {
            return "estalactita";
        } else if (damageSource.isOf(DamageTypes.STING)) {
            return "picadura";
        } else if (damageSource.isOf(DamageTypes.MOB_ATTACK) || damageSource.isOf(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
            return "ataque de mob";
        } else if (damageSource.isOf(DamageTypes.PLAYER_ATTACK)) {
            return "PvP";
        } else if (damageSource.isOf(DamageTypes.ARROW)) {
            return "flecha";
        } else if (damageSource.isOf(DamageTypes.TRIDENT)) {
            return "tridente";
        } else if (damageSource.isOf(DamageTypes.MOB_PROJECTILE) || damageSource.isOf(DamageTypes.SPIT)) {
            return "proyectil";
        } else if (damageSource.isOf(DamageTypes.FIREWORKS)) {
            return "fuegos artificiales";
        } else if (damageSource.isOf(DamageTypes.FIREBALL)) {
            return "bola de fuego";
        } else if (damageSource.isOf(DamageTypes.WITHER_SKULL)) {
            return "cráneo de wither";
        } else if (damageSource.isOf(DamageTypes.THORNS)) {
            return "espinas";
        } else if (damageSource.isOf(DamageTypes.EXPLOSION) || damageSource.isOf(DamageTypes.PLAYER_EXPLOSION)) {
            return "explosión";
        } else if (damageSource.isOf(DamageTypes.SONIC_BOOM)) {
            return "onda sónica";
        } else if (damageSource.isOf(DamageTypes.BAD_RESPAWN_POINT)) {
            return "punto de respawn";
        } else if (damageSource.isOf(DamageTypes.OUTSIDE_BORDER)) {
            return "borde del mundo";
        } else if (damageSource.isOf(DamageTypes.MACE_SMASH)) {
            return "golpe de maza";
        } else {
            return "otros";
        }
    }

}
