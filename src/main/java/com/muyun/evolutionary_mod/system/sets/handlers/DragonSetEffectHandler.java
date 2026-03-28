package com.muyun.evolutionary_mod.system.sets.handlers;

import com.muyun.evolutionary_mod.system.sets.SetEffectHandler;
import com.muyun.evolutionary_mod.system.sets.SetType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 龙族套装效果事件处理器 - Dragon Set Effect Event Handler
 *
 * NeoForge 1.21.1: LivingHurtEvent -> LivingIncomingDamageEvent
 */
public class DragonSetEffectHandler implements SetEffectHandler {

    private static final Map<UUID, Integer> DRAGON_SCALE_SHIELD_COOLDOWN = new HashMap<>();
    private static final int DRAGON_SCALE_SHIELD_COOLDOWN_TICKS = 200;

    @Override
    public SetType getSetType() {
        return SetType.DRAGON;
    }

    /**
     * 4件套效果：龙鳞护体 - 受伤时 30% 概率吸收最多 10 点伤害（冷却 10 秒）。
     */
    @Override
    public void onPlayerHurt(Player player, LivingIncomingDamageEvent event, int pieceCount) {
        if (pieceCount < 4) return;

        UUID playerUUID = player.getUUID();
        int currentTick = (int) player.level().getGameTime();

        Integer lastTrigger = DRAGON_SCALE_SHIELD_COOLDOWN.get(playerUUID);
        if (lastTrigger != null && currentTick - lastTrigger < DRAGON_SCALE_SHIELD_COOLDOWN_TICKS) return;

        if (player.getRandom().nextFloat() < 0.30f) {
            float originalDamage = event.getAmount();
            float shieldAbsorb = Math.min(10.0f, originalDamage);
            event.setAmount(originalDamage - shieldAbsorb);
            DRAGON_SCALE_SHIELD_COOLDOWN.put(playerUUID, currentTick);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.2f);
            for (int i = 0; i < 10; i++) {
                double ox = (player.getRandom().nextDouble() - 0.5) * 2.0;
                double oy = player.getRandom().nextDouble() * 2.0;
                double oz = (player.getRandom().nextDouble() - 0.5) * 2.0;
                player.level().addParticle(ParticleTypes.ENCHANT,
                        player.getX() + ox, player.getY() + oy, player.getZ() + oz, 0, 0.1, 0);
            }
        }
    }

    /**
     * 6件套效果：龙息爆发 - 攻击时 25% 概率对前方 5 格内敌人造成 10 点火焰伤害。
     */
    @Override
    public void onPlayerAttack(Player player, LivingIncomingDamageEvent event, LivingEntity target, int pieceCount) {
        if (pieceCount < 6) return;

        if (player.getRandom().nextFloat() < 0.25f) {
            double range = 5.0;
            double playerYaw = Math.toRadians(player.getYRot());
            double forwardX = -Math.sin(playerYaw);
            double forwardZ = Math.cos(playerYaw);
            double px = player.getX();
            double py = player.getY() + player.getEyeHeight();
            double pz = player.getZ();

            for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(range))) {
                if (entity == player || entity == target || !entity.isAlive()) continue;
                double dx = entity.getX() - px;
                double dy = entity.getY() + entity.getEyeHeight() - py;
                double dz = entity.getZ() - pz;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist > range) continue;
                double dot = (dx * forwardX + dz * forwardZ) / dist;
                if (dot < 0.5) continue;
                entity.hurt(player.damageSources().onFire(), 10.0f);
                entity.setRemainingFireTicks(100);
                player.level().addParticle(ParticleTypes.FLAME,
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 0, 0.1, 0);
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.8f);
            for (int i = 0; i < 20; i++) {
                double angle = (i / 20.0) * Math.PI * 2;
                player.level().addParticle(ParticleTypes.FLAME,
                        player.getX() + Math.cos(angle) * 0.5, player.getY() + 1.0,
                        player.getZ() + Math.sin(angle) * 0.5, 0, 0.05, 0);
            }
        }
    }
}
