package com.muyun.evolutionary_mod.system.combat;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.system.effects.AbstractAccessoryEffectHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一暴击系统 - Unified Critical Hit System
 *
 * NeoForge 1.21.1: 使用 getData() 替代 getCapability()。
 * ItemStack NBT（getTag）已废弃，暂时仅使用默认效果（DataComponents 迁移后再补全随机属性读取）。
 */
public class CritSystem {

    private static final double BASE_CRIT_DAMAGE_MULTIPLIER = 1.5;
    private static final double MAX_CRIT_CHANCE = 0.95;

    public static class CritStats {
        public double critChance = 0;
        public double critDamageBonus = 0;

        public CritStats critChance(double value) { this.critChance = value; return this; }
        public CritStats critDamageBonus(double value) { this.critDamageBonus = value; return this; }
    }

    private static final Map<Item, CritStats> GLOBAL_CRIT_STATS = new HashMap<>();

    public static void registerCritStats(Item item, CritStats stats) {
        GLOBAL_CRIT_STATS.put(item, stats);
    }

    /**
     * 获取玩家全部饰品的总暴击率。
     * NeoForge 1.21.1: getData() 替代 getCapability()。
     * ItemStack.getTag() 已废弃，此版本仅读取默认效果值。
     */
    public static double getTotalCritChance(Player player) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        double total = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (!stack.isEmpty()) {
                CritStats stats = GLOBAL_CRIT_STATS.get(stack.getItem());
                if (stats != null) total += stats.critChance;
                // 叠加随机词条暴击率
                AccessoryAttributes rolled = AbstractAccessoryEffectHandler.getRolledAttributes(stack);
                total += rolled.critChance();
            }
        }
        return Math.min(total, MAX_CRIT_CHANCE);
    }

    /**
     * 获取玩家全部饰品的总暴击伤害加成。
     * NeoForge 1.21.1: getData() 替代 getCapability()。
     */
    public static double getTotalCritDamageBonus(Player player) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        double total = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (!stack.isEmpty()) {
                CritStats stats = GLOBAL_CRIT_STATS.get(stack.getItem());
                if (stats != null) total += stats.critDamageBonus;
                // 叠加随机词条暴击伤害
                AccessoryAttributes rolled = AbstractAccessoryEffectHandler.getRolledAttributes(stack);
                total += rolled.critDamage();
            }
        }
        return total;
    }

    public static float processCriticalHit(Player player, float baseDamage, LivingEntity target) {
        double critChance = getTotalCritChance(player);
        double critDamageBonus = getTotalCritDamageBonus(player);
        if (Math.random() < critChance) {
            float critMultiplier = (float)(BASE_CRIT_DAMAGE_MULTIPLIER + critDamageBonus);
            spawnCritEffects(target);
            return baseDamage * critMultiplier;
        }
        return baseDamage;
    }

    private static void spawnCritEffects(LivingEntity target) {
        if (target.level().isClientSide) return;
        for (int i = 0; i < 10; i++) {
            double ox = (Math.random() - 0.5) * 0.5;
            double oy = Math.random() * 0.5;
            double oz = (Math.random() - 0.5) * 0.5;
            target.level().addParticle(ParticleTypes.CRIT,
                    target.getX() + ox,
                    target.getY() + target.getBbHeight() / 2 + oy,
                    target.getZ() + oz, 0, 0, 0);
        }
        target.level().playSound(null, target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static boolean hasCritStats(Item item) { return GLOBAL_CRIT_STATS.containsKey(item); }
    public static CritStats getCritStats(Item item) { return GLOBAL_CRIT_STATS.get(item); }
    public static void removeCritStats(Item item) { GLOBAL_CRIT_STATS.remove(item); }

    public static String getCritStatsSummary(Player player) {
        double chance = getTotalCritChance(player);
        double bonus = getTotalCritDamageBonus(player);
        return String.format("Crit Chance: %.1f%%, Crit Damage: %.1f%% (Total: %.1fx)",
                chance * 100, bonus * 100, BASE_CRIT_DAMAGE_MULTIPLIER + bonus);
    }
}
