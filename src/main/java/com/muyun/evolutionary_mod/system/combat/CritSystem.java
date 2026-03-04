package com.muyun.evolutionary_mod.system.combat;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一暴击系统 - Unified Critical Hit System for All Accessory Types
 *
 * 此系统处理任何装备饰品的暴击计算和效果。
 * 为所有饰品类型提供集中管理暴击率和暴击伤害的方式。
 *
 * This system handles critical hit calculations and effects for any equipped accessories.
 * It provides a centralized way to manage crit chance and crit damage across all accessory types.
 *
 * 使用方法/USAGE:
 * 1. 在相应特效类中为物品注册暴击属性/Register crit stats for items in their respective Effects classes:
 *    CritSystem.registerCritStats(item, new CritStats().critChance(0.05).critDamageBonus(0.1));
 *
 * 2. 在伤害事件中处理暴击/Process critical hits in damage events:
 *    float finalDamage = CritSystem.processCriticalHit(player, baseDamage, target);
 *
 * 3. 查询暴击统计用于调试/Query crit stats for debugging:
 *    String summary = CritSystem.getCritStatsSummary(player);
 *
 * 特性/FEATURES:
 * - 全局暴击率上限控制 (95% max) / Global crit chance capping (95% max)
 * - 基础暴击伤害倍数 (150%) / Base crit damage multiplier (150%)
 * - 百分比暴击伤害加成 / Percentage-based crit damage bonuses
 * - 自动粒子和音效效果 / Automatic particle and sound effects
 * - 兼容所有饰品类型 (戒指、项链、手镯等) / Compatible with all accessory types (rings, necklaces, bracelets, etc.)
 */
public class CritSystem {

    // Base critical hit settings
    private static final double BASE_CRIT_DAMAGE_MULTIPLIER = 1.5; // 150% base damage
    private static final double MAX_CRIT_CHANCE = 0.95; // 95% max crit chance

    // Critical hit data structure
    public static class CritStats {

        public double critChance = 0; // percentage (0.0 - 1.0)
        public double critDamageBonus = 0; // percentage bonus added to base

        public CritStats critChance(double value) {
            this.critChance = value;
            return this;
        }

        public CritStats critDamageBonus(double value) {
            this.critDamageBonus = value;
            return this;
        }
    }

    // Global critical hit mappings for all accessory types
    private static final Map<Item, CritStats> GLOBAL_CRIT_STATS = new HashMap<>();

    /**
     * 为物品注册暴击属性 - Register critical hit stats for an item
     *
     * @param item 要注册的物品 / The item to register
     * @param stats 暴击属性数据 / The critical hit stats
     */
    public static void registerCritStats(Item item, CritStats stats) {
        GLOBAL_CRIT_STATS.put(item, stats);
    }

    /**
     * Get total critical hit chance from all equipped accessories
     */
    public static double getTotalCritChance(Player player) {

    // 优先从NBT读取随机属性值
    // Priority: read random attribute value from NBT

        // 如果没有随机属性，使用默认效果
        // If no random attributes, use default effects

        return 0;
    }

    /**
     * Get total critical hit damage bonus from all equipped accessories
     */
    public static double getTotalCritDamageBonus(Player player) {

    // 优先从NBT读取随机属性值
    // Priority: read random attribute value from NBT

        // 如果没有随机属性，使用默认效果
        // If no random attributes, use default effects

        return 0;
    }

    /**
     * Process a critical hit event
     * @param player The attacking player
     * @param baseDamage The original damage amount
     * @param target The target entity
     * @return The modified damage amount (crit damage if crit occurs, otherwise original)
     */
    public static float processCriticalHit(Player player, float baseDamage, LivingEntity target) {
        double critChance = getTotalCritChance(player);
        double critDamageBonus = getTotalCritDamageBonus(player);

        if (Math.random() < critChance) {
            // Critical hit!
            float critMultiplier = (float)(BASE_CRIT_DAMAGE_MULTIPLIER + critDamageBonus);
            float critDamage = baseDamage * critMultiplier;

            // Visual and audio effects
            spawnCritEffects(target);

            return critDamage;
        }

        return baseDamage;
    }

    /**
     * Spawn critical hit visual effects
     */
    private static void spawnCritEffects(LivingEntity target) {
        if (!target.level().isClientSide) {
            // Spawn crit particles
            for (int i = 0; i < 10; i++) {
                double offsetX = (Math.random() - 0.5) * 0.5;
                double offsetY = Math.random() * 0.5;
                double offsetZ = (Math.random() - 0.5) * 0.5;
                target.level().addParticle(ParticleTypes.CRIT,
                    target.getX() + offsetX,
                    target.getY() + target.getBbHeight() / 2 + offsetY,
                    target.getZ() + offsetZ,
                    0, 0, 0);
            }

            // Play crit sound
            target.level().playSound(null, target.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /**
     * Check if an item has critical hit stats
     */
    public static boolean hasCritStats(Item item) {
        return GLOBAL_CRIT_STATS.containsKey(item);
    }

    /**
     * Get critical hit stats for an item
     */
    public static CritStats getCritStats(Item item) {
        return GLOBAL_CRIT_STATS.get(item);
    }

    /**
     * Remove critical hit stats for an item (for dynamic updates)
     */
    public static void removeCritStats(Item item) {
        GLOBAL_CRIT_STATS.remove(item);
    }

    /**
     * Get current critical hit stats summary for debugging
     */
    public static String getCritStatsSummary(Player player) {
        double chance = getTotalCritChance(player);
        double bonus = getTotalCritDamageBonus(player);
        double totalMultiplier = BASE_CRIT_DAMAGE_MULTIPLIER + bonus;

        return String.format("Crit Chance: %.1f%%, Crit Damage: %.1f%% (Total: %.1fx)",
            chance * 100, bonus * 100, totalMultiplier);
    }
}

