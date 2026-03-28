package com.muyun.evolutionary_mod.system.combat;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.system.effects.AbstractAccessoryEffectHandler;
import com.muyun.evolutionary_mod.system.sets.SetSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一伤害减免系统 - Unified Damage Reduction System
 *
 * NeoForge 1.21.1: 使用 getData() 替代 getCapability()。
 * ItemStack.getTag() 已废弃，暂时仅使用默认效果值。
 */
public class DamageReductionSystem {

    private static final double MAX_DAMAGE_REDUCTION = 0.8;

    public static class DamageReductionStats {
        public double damageReduction = 0;
        public DamageReductionStats damageReduction(double value) { this.damageReduction = value; return this; }
    }

    private static final Map<Item, DamageReductionStats> GLOBAL_DAMAGE_REDUCTION_STATS = new HashMap<>();

    public static void registerDamageReductionStats(Item item, DamageReductionStats stats) {
        GLOBAL_DAMAGE_REDUCTION_STATS.put(item, stats);
    }

    /**
     * 获取玩家总伤害减免百分比（来自饰品 + 套装加成）。
     * NeoForge 1.21.1: getData() 替代 getCapability()。
     */
    public static double getTotalDamageReduction(Player player) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        double total = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (!stack.isEmpty()) {
                DamageReductionStats stats = GLOBAL_DAMAGE_REDUCTION_STATS.get(stack.getItem());
                if (stats != null) total += stats.damageReduction;
                // 叠加随机词条伤害减免
                AccessoryAttributes rolled = AbstractAccessoryEffectHandler.getRolledAttributes(stack);
                total += rolled.damageReduction();
            }
        }
        // 叠加套装伤害减免
        total += SetSystem.getSetDamageReduction(player);
        return Math.min(total, MAX_DAMAGE_REDUCTION);
    }

    public static float applyDamageReduction(Player player, float originalDamage) {
        double reduction = getTotalDamageReduction(player);
        if (reduction > 0) return (float)(originalDamage * (1.0 - reduction));
        return originalDamage;
    }

    public static boolean hasDamageReductionStats(Item item) {
        return GLOBAL_DAMAGE_REDUCTION_STATS.containsKey(item);
    }

    public static DamageReductionStats getDamageReductionStats(Item item) {
        return GLOBAL_DAMAGE_REDUCTION_STATS.get(item);
    }

    public static void removeDamageReductionStats(Item item) {
        GLOBAL_DAMAGE_REDUCTION_STATS.remove(item);
    }

    public static String getDamageReductionStatsSummary(Player player) {
        double reduction = getTotalDamageReduction(player);
        return String.format("Damage Reduction: %.1f%% (Max: %.1f%%)",
                reduction * 100, MAX_DAMAGE_REDUCTION * 100);
    }
}
