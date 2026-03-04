package com.muyun.evolutionary_mod.system.combat;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.system.sets.SetSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一伤害减免系统 - Unified Damage Reduction System for All Accessory Types
 *
 * 此系统处理所有装备饰品的伤害减免计算，为所有饰品类型提供统一的伤害减免机制。
 * 任何饰品都可以注册伤害减免属性，系统会自动累加并应用到玩家受到的伤害上。
 *
 * This system handles damage reduction calculations for any equipped accessories,
 * providing unified damage reduction mechanism for all accessory types.
 * Any accessory can register damage reduction properties, and the system will
 * automatically accumulate and apply them to damage taken by the player.
 */
public class DamageReductionSystem {

    // Damage reduction settings
    private static final double MAX_DAMAGE_REDUCTION = 0.8; // 80% max damage reduction

    // Damage reduction data structure
    public static class DamageReductionStats {
        public double damageReduction = 0; // percentage (0.0 - 1.0)

        public DamageReductionStats damageReduction(double value) {
            this.damageReduction = value;
            return this;
        }
    }

    // Global damage reduction mappings for all accessory types
    private static final Map<Item, DamageReductionStats> GLOBAL_DAMAGE_REDUCTION_STATS = new HashMap<>();

    /**
     * 为物品注册伤害减免属性 - Register damage reduction stats for an item
     *
     * @param item 要注册的物品 / The item to register
     * @param stats 伤害减免属性数据 / The damage reduction stats
     */
    public static void registerDamageReductionStats(Item item, DamageReductionStats stats) {
        GLOBAL_DAMAGE_REDUCTION_STATS.put(item, stats);
    }

    /**
     * 获取玩家总伤害减免百分比 - Get total damage reduction percentage for player
     *
     * 计算玩家所有装备饰品的伤害减免总和，包括套装效果，自动应用上限限制。
     *
     * Calculate total damage reduction from all player's equipped accessories,
     * including set bonuses, automatically applying upper limit restrictions.
     *
     * @param player 要计算的玩家 / The player to calculate for
     * @return 伤害减免百分比 (0.0-0.8) / Damage reduction percentage (0.0-0.8)
     */
    public static double getTotalDamageReduction(Player player) {

                            // 优先从NBT读取随机属性值
                            // Priority: read random attribute value from NBT

                                // 如果没有随机属性，使用默认效果
                                // If no random attributes, use default effects

        
        // 添加套装伤害减免
        // Add set bonus damage reduction

        return 0;
    }

    /**
     * 应用伤害减免到伤害值 - Apply damage reduction to damage amount
     *
     * 根据玩家的总伤害减免百分比计算最终伤害值。
     *
     * Calculate final damage amount based on player's total damage reduction percentage.
     *
     * @param player 受伤的玩家 / The player taking damage
     * @param originalDamage 原始伤害值 / The original damage amount
     * @return 减免后的伤害值 / Damage amount after reduction
     */
    public static float applyDamageReduction(Player player, float originalDamage) {
        double reduction = getTotalDamageReduction(player);
        if (reduction > 0) {
            return (float)(originalDamage * (1.0 - reduction));
        }
        return originalDamage;
    }

    /**
     * 检查物品是否有伤害减免属性 - Check if item has damage reduction stats
     *
     * @param item 要检查的物品 / The item to check
     * @return 是否有伤害减免属性 / Whether the item has damage reduction properties
     */
    public static boolean hasDamageReductionStats(Item item) {
        return GLOBAL_DAMAGE_REDUCTION_STATS.containsKey(item);
    }

    /**
     * 获取物品的伤害减免属性 - Get damage reduction stats for item
     *
     * @param item 要获取属性的物品 / The item to get stats for
     * @return 伤害减免属性数据，如果没有则返回null / Damage reduction stats, or null if none
     */
    public static DamageReductionStats getDamageReductionStats(Item item) {
        return GLOBAL_DAMAGE_REDUCTION_STATS.get(item);
    }

    /**
     * 移除物品的伤害减免属性 - Remove damage reduction stats for item (for dynamic updates)
     *
     * @param item 要移除属性的物品 / The item to remove stats for
     */
    public static void removeDamageReductionStats(Item item) {
        GLOBAL_DAMAGE_REDUCTION_STATS.remove(item);
    }

    /**
     * 获取伤害减免统计摘要用于调试 - Get damage reduction stats summary for debugging
     *
     * @param player 要分析的玩家 / The player to analyze
     * @return 统计摘要字符串 / Statistics summary string
     */
    public static String getDamageReductionStatsSummary(Player player) {
        double reduction = getTotalDamageReduction(player);
        return String.format("Damage Reduction: %.1f%% (Max: %.1f%%)",
            reduction * 100, MAX_DAMAGE_REDUCTION * 100);
    }
}

