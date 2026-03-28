package com.muyun.evolutionary_mod.loot;

import java.util.Random;

/**
 * 饰品掉落品质权重配置 - Accessory Drop Rarity Weights
 *
 * 每个品质等级拥有一个相对权重，权重越高越容易掉落。
 * 修改权重只需改动本枚举，掉落逻辑自动适应。
 *
 * 品质对应物品注册名前缀：
 *   BROKEN    -> broken_
 *   NORMAL    -> (无前缀，如 life_essence_ring)
 *   EXCELLENT -> excellent_
 *   EPIC      -> epic_
 *   LEGENDARY -> legendary_
 *   MYTHIC    -> mythic_
 */
public enum AccessoryDropRarity {

    //              权重（相对值，越大越常见）
    BROKEN    (50),
    NORMAL    (30),
    EXCELLENT (12),
    EPIC      ( 5),
    LEGENDARY ( 2),
    MYTHIC    ( 1);

    public final int weight;

    AccessoryDropRarity(int weight) {
        this.weight = weight;
    }

    /** 物品注册名前缀，用于从物品池中筛选对应品质的物品 */
    public String registryPrefix() {
        return switch (this) {
            case BROKEN    -> "broken_";
            case NORMAL    -> "";
            case EXCELLENT -> "excellent_";
            case EPIC      -> "epic_";
            case LEGENDARY -> "legendary_";
            case MYTHIC    -> "mythic_";
        };
    }

    /**
     * 从允许的品质列表中按权重随机选择一个品质。
     *
     * @param allowed 允许出现的品质数组
     * @param random  随机数源
     * @return 选中的品质
     */
    public static AccessoryDropRarity rollRarity(AccessoryDropRarity[] allowed, Random random) {
        int totalWeight = 0;
        for (AccessoryDropRarity r : allowed) totalWeight += r.weight;
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (AccessoryDropRarity r : allowed) {
            cumulative += r.weight;
            if (roll < cumulative) return r;
        }
        return allowed[allowed.length - 1];
    }
}

