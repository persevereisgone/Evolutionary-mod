package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.system.effects.CacheManager;
import com.muyun.evolutionary_mod.system.sets.SetSystem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * 饰品效果计算器 - Accessory Effect Calculator
 * 
 * 统一管理所有饰品的属性计算和应用。
 * 包括：
 * - 普通饰品的属性生效（戒指、项链、手镯、耳环、头饰、腰带、手套、肩饰、脚饰等）
 * - 套装的所有可生效的属性效果
 * - 方便未来添加新类型的饰品
 * 
 * Unified manager for all accessory attribute calculation and application.
 * Includes:
 * - Individual accessory attribute effects (rings, necklaces, bracelets, earrings, headwear, belts, gloves, shoulders, anklets, etc.)
 * - All applicable set bonus attribute effects
 * - Easy to extend for new accessory types in the future
 * 
 * 使用方法 / Usage:
 * AccessoryEffectCalculator.calculateAndApplyAllEffects(player);
 */

public class AccessoryEffectCalculator {

    // 所有饰品效果处理器列表
    private static final List<AbstractAccessoryEffectHandler<?>> handlers = new ArrayList<>();

    /**
     * 注册饰品效果处理器
     *
     * @param handler 饰品效果处理器
     */
    public static void registerHandler(AbstractAccessoryEffectHandler<?> handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
            CacheManager.registerHandler(handler);
        }
    }

    /**
     * 计算并应用所有饰品效果
     * 包括单个饰品的属性和套装效果
     *
     * 计算顺序：
     * 1. 先应用所有单个饰品的数值增加（ADDITION操作）
     * 2. 然后应用套装效果的百分比加成（MULTIPLY_TOTAL操作，基于总值计算）
     *
     * 这样确保百分比加成基于"基础值+所有数值增加"，而不是只基于基础值
     *
     * Calculate and apply all accessory effects
     * Includes individual accessory attributes and set bonuses
     *
     * Calculation order:
     * 1. First apply all individual accessory numerical additions (ADDITION operations)
     * 2. Then apply set bonus percentage bonuses (MULTIPLY_TOTAL operations, calculated based on total value)
     *
     * This ensures percentage bonuses are based on "base value + all numerical additions", not just base value
     *
     * @param player 玩家 / Player
     */
    public static void calculateAndApplyAllEffects(Player player) {
        if (player == null || player.level().isClientSide) {
            return; // 只在服务端执行 / Only execute on server side
        }

        // 1. 先应用所有单个饰品的属性效果（数值增加，ADDITION操作）
        // First apply all individual accessory attribute effects (numerical additions, ADDITION operations)
        applyIndividualAccessoryEffects(player);

        // 2. 然后应用套装效果（百分比加成，MULTIPLY_TOTAL操作，基于总值计算）
        // Then apply set bonuses (percentage bonuses, MULTIPLY_TOTAL operations, calculated based on total value)
        applySetBonuses(player);
    }

    /**
     * 应用所有单个饰品的属性效果
     * 按顺序应用各个饰品部位的效果
     *
     * Apply all individual accessory attribute effects
     * Apply effects from each accessory type in order
     *
     * @param player 玩家 / Player
     */
    public static void applyIndividualAccessoryEffects(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        // 应用所有注册的饰品效果处理器
        // Apply all registered accessory effect handlers
        for (AbstractAccessoryEffectHandler<?> handler : handlers) {
            handler.applyEffects(player);
        }

        // 未来可以通过注册新的处理器来添加新的饰品类型
        // Future accessory types can be added by registering new handlers
    }

    /**
     * 应用套装效果
     *
     * Apply set bonuses
     *
     * @param player 玩家 / Player
     */
    public static void applySetBonuses(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        // 应用所有套装的套装效果
        // Apply all set bonuses
        SetSystem.applySetBonuses(player);
    }

    /**
     * 仅应用单个饰品的属性效果（不包含套装效果）
     * 用于测试或特殊场景
     *
     * Apply only individual accessory effects (excluding set bonuses)
     * Used for testing or special scenarios
     *
     * @param player 玩家 / Player
     */
    public static void applyOnlyIndividualEffects(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        applyIndividualAccessoryEffects(player);
    }

    /**
     * 仅应用套装效果（不包含单个饰品属性）
     * 用于测试或特殊场景
     *
     * Apply only set bonuses (excluding individual accessory attributes)
     * Used for testing or special scenarios
     *
     * @param player 玩家 / Player
     */
    public static void applyOnlySetBonuses(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        applySetBonuses(player);
    }

    /**
     * 应用指定类型的饰品效果
     * 用于测试特定饰品类型的效果
     *
     * Apply effects from specified accessory type
     * Used for testing specific accessory type effects
     *
     * @param player 玩家 / Player
     * @param accessoryType 饰品类型（"ring", "bracelet", "necklace", "earring", "headwear", "belt", "glove", "shoulder", "anklet"）
     */
    public static void applySpecificAccessoryType(Player player, String accessoryType) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        // 根据饰品类型应用对应处理器的效果
        // Apply effects from the corresponding handler based on accessory type
        switch (accessoryType.toLowerCase()) {
            case "ring":
                RingEffectsHandler.getInstance().applyEffects(player);
                break;
            case "bracelet":
                BraceletEffectsHandler.getInstance().applyEffects(player);
                break;
            case "necklace":
                NecklaceEffectsHandler.getInstance().applyEffects(player);
                break;
            case "earring":
                EarringEffectsHandler.getInstance().applyEffects(player);
                break;
            case "headwear":
                HeadwearEffectsHandler.getInstance().applyEffects(player);
                break;
            case "belt":
                BeltEffectsHandler.getInstance().applyEffects(player);
                break;
            case "glove":
                GloveEffectsHandler.getInstance().applyEffects(player);
                break;
            case "shoulder":
                ShoulderEffectsHandler.getInstance().applyEffects(player);
                break;
            case "anklet":
                AnkletEffectsHandler.getInstance().applyEffects(player);
                break;
            default:
                // 未知类型，不执行任何操作
                // Unknown type, do nothing
                break;
        }
    }

}

