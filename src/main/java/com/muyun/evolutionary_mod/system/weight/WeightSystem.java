package com.muyun.evolutionary_mod.system.weight;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WeightSystem {
    // 物品到重量的缓存
    private static final Map<Item, Double> ITEM_WEIGHT_CACHE = new HashMap<>();

    /**
     * 获取玩家当前背包总重量
     */
    public static double getTotalWeight(Player player) {
        double totalWeight = 0.0;

        // 计算主背包重量
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                totalWeight += calculateStackWeight(stack);
            }
        }

        // 计算护甲槽重量
        for (ItemStack armor : player.getInventory().armor) {
            if (!armor.isEmpty()) {
                totalWeight += calculateStackWeight(armor);
            }
        }

        // 计算副手物品重量
        ItemStack offhand = player.getInventory().offhand.get(0);
        if (!offhand.isEmpty()) {
            totalWeight += calculateStackWeight(offhand);
        }

        return totalWeight;
    }

    /**
     * 计算单个物品堆叠的重量（线性模式）
     */
    public static double calculateStackWeight(ItemStack stack) {
        double itemWeight = getItemWeight(stack.getItem());
        int count = stack.getCount();
        return itemWeight * count;
    }

    /**
     * 获取单个物品的基础重量
     */
    public static double getItemWeight(Item item) {
        // 检查缓存
        if (ITEM_WEIGHT_CACHE.containsKey(item)) {
            return ITEM_WEIGHT_CACHE.get(item);
        }

        // 首先检查自定义重量配置
        if (WeightConfig.CUSTOM_WEIGHTS.containsKey(item)) {
            double weight = WeightConfig.CUSTOM_WEIGHTS.get(item);
            ITEM_WEIGHT_CACHE.put(item, weight);
            return weight;
        }

        double weight = calculateItemWeight(item);
        ITEM_WEIGHT_CACHE.put(item, weight);
        return weight;
    }

    /**
     * 计算物品重量（基于物品类型和材质）
     */
    private static double calculateItemWeight(Item item) {
        String itemName = item.toString().toLowerCase();

        // ====================
        // 工具和武器
        // ====================

        // 下界合金工具
        if (itemName.contains("netherite") && 
            (itemName.contains("sword") || itemName.contains("pickaxe") ||
             itemName.contains("axe") || itemName.contains("shovel") ||
             itemName.contains("hoe"))) {
            return WeightConfig.netheriteToolWeight;
        }

        // 钻石工具
        if (itemName.contains("diamond") && 
            (itemName.contains("sword") || itemName.contains("pickaxe") ||
             itemName.contains("axe") || itemName.contains("shovel") ||
             itemName.contains("hoe"))) {
            return WeightConfig.diamondToolWeight;
        }

        // 铁质工具
        if (itemName.contains("iron") && 
            (itemName.contains("sword") || itemName.contains("pickaxe") ||
             itemName.contains("axe") || itemName.contains("shovel") ||
             itemName.contains("hoe"))) {
            return WeightConfig.ironToolWeight;
        }

        // 木质工具
        if (itemName.contains("wooden") && 
            (itemName.contains("sword") || itemName.contains("pickaxe") ||
             itemName.contains("axe") || itemName.contains("shovel") ||
             itemName.contains("hoe"))) {
            return WeightConfig.woodenToolWeight;
        }

        // 石质工具
        if (itemName.contains("stone") && 
            (itemName.contains("sword") || itemName.contains("pickaxe") ||
             itemName.contains("axe") || itemName.contains("shovel") ||
             itemName.contains("hoe"))) {
            return WeightConfig.woodenToolWeight;
        }

        // 其他武器
        if (itemName.contains("bow") || itemName.contains("crossbow") ||
            itemName.contains("trident") || itemName.contains("shield") ||
            itemName.contains("flint_and_steel") || itemName.contains("shears") ||
            itemName.contains("fishing_rod")) {
            return WeightConfig.ironToolWeight; // 中等重量
        }

        // ====================
        // 护甲
        // ====================

        // 下界合金护甲
        if (itemName.contains("netherite") && 
            (itemName.contains("helmet") || itemName.contains("chestplate") ||
             itemName.contains("leggings") || itemName.contains("boots"))) {
            return WeightConfig.netheriteArmorWeight;
        }

        // 钻石护甲
        if (itemName.contains("diamond") && 
            (itemName.contains("helmet") || itemName.contains("chestplate") ||
             itemName.contains("leggings") || itemName.contains("boots"))) {
            return WeightConfig.diamondArmorWeight;
        }

        // 铁护甲
        if (itemName.contains("iron") && 
            (itemName.contains("helmet") || itemName.contains("chestplate") ||
             itemName.contains("leggings") || itemName.contains("boots"))) {
            return WeightConfig.ironArmorWeight;
        }

        // 锁链护甲（按铁护甲处理）
        if (itemName.contains("chainmail") && 
            (itemName.contains("helmet") || itemName.contains("chestplate") ||
             itemName.contains("leggings") || itemName.contains("boots"))) {
            return WeightConfig.ironArmorWeight * 0.8; // 比铁轻一点
        }

        // 皮革护甲
        if (itemName.contains("leather") && 
            (itemName.contains("helmet") || itemName.contains("chestplate") ||
             itemName.contains("leggings") || itemName.contains("boots"))) {
            return WeightConfig.leatherArmorWeight;
        }

        // ====================
        // 食物
        // ====================

        // 高级食物
        if (itemName.contains("golden") || itemName.contains("enchanted") ||
            itemName.contains("cake") || itemName.contains("pumpkin_pie")) {
            return WeightConfig.advancedFoodWeight;
        }

        // 基础食物
        if (itemName.contains("apple") || itemName.contains("bread") || 
            itemName.contains("steak") || itemName.contains("porkchop") ||
            itemName.contains("chicken") || itemName.contains("fish") ||
            itemName.contains("carrot") || itemName.contains("potato") ||
            itemName.contains("melon") || itemName.contains("beetroot") ||
            itemName.contains("cookie") || itemName.contains("mushroom") ||
            itemName.contains("stew") || itemName.contains("soup") ||
            itemName.contains("egg") || itemName.contains("rotten_flesh") ||
            itemName.contains("rabbit") || itemName.contains("honey")) {
            return WeightConfig.basicFoodWeight;
        }

        // ====================
        // 药水和瓶子
        // ====================

        if (itemName.contains("potion") || itemName.contains("splash_potion") ||
            itemName.contains("lingering_potion") || itemName.contains("experience_bottle")) {
            return WeightConfig.potionWeight;
        }

        // ====================
        // 箭矢和弹药
        // ====================

        if (itemName.contains("arrow") || itemName.contains("snowball") ||
            itemName.contains("egg") || itemName.contains("ender_pearl")) {
            return WeightConfig.arrowWeight;
        }

        // ====================
        // 红石和荧石
        // ====================

        if (itemName.contains("redstone") || itemName.contains("glowstone") ||
            itemName.contains("gunpowder") || itemName.contains("bone") ||
            itemName.contains("string") || itemName.contains("feather") ||
            itemName.contains("slime_ball") || itemName.contains("leather")) {
            return WeightConfig.redstoneWeight;
        }

        // ====================
        // 珍贵材料
        // ====================

        if (itemName.contains("diamond") || itemName.contains("emerald") ||
            itemName.contains("netherite_ingot") || itemName.contains("netherite_scrap")) {
            return WeightConfig.preciousMaterialWeight;
        }

        // ====================
        // 矿石
        // ====================

        if (itemName.contains("iron_ingot") || itemName.contains("gold_ingot") ||
            itemName.contains("copper_ingot") || itemName.contains("tin_ingot") ||
            itemName.contains("lead_ingot") || itemName.contains("silver_ingot") ||
            itemName.contains("ore") && !itemName.contains("deepslate")) {
            return WeightConfig.oreWeight;
        }

        // ====================
        // 家具和机械
        // ====================

        if (itemName.contains("furnace") || itemName.contains("anvil") ||
            itemName.contains("brewing_stand") || itemName.contains("smoker") ||
            itemName.contains("blast_furnace") || itemName.contains("hopper")) {
            return WeightConfig.machineWeight;
        }

        if (itemName.contains("chest") || itemName.contains("workbench") ||
            itemName.contains("bed") || itemName.contains("door") ||
            itemName.contains("fence") || itemName.contains("trapdoor")) {
            return WeightConfig.furnitureWeight;
        }

        // ====================
        // 建筑材料（默认）
        // ====================

        return WeightConfig.buildingMaterialWeight;
    }

    /**
     * 获取玩家负重上限（考虑等级加成）
     */
    public static double getMaxWeight(Player player) {
        double maxWeight = WeightConfig.baseMaxWeight;

        // 等级加成
        maxWeight += player.experienceLevel * WeightConfig.levelWeightBonusPerLevel;

        // TODO: 背包加成（需要背包物品实现后添加）
        // maxWeight += getBackpackBonus(player);

        // TODO: 成就加成（需要成就系统实现后添加）
        // maxWeight += getAchievementBonus(player);

        return maxWeight;
    }

    /**
     * 获取负重百分比
     */
    public static double getWeightPercentage(Player player) {
        double currentWeight = getTotalWeight(player);
        double maxWeight = getMaxWeight(player);
        return Math.min(currentWeight / maxWeight, 1.0);
    }

    /**
     * 获取当前负重状态
     */
    public static WeightStatus getWeightStatus(Player player) {
        double percentage = getWeightPercentage(player);

        if (percentage >= 1.0) {
            return WeightStatus.OVERLOADED;
        } else if (percentage >= WeightConfig.warningThreshold) {
            return WeightStatus.OVERWEIGHT;
        } else if (percentage >= WeightConfig.safeThreshold) {
            return WeightStatus.HEAVY;
        } else {
            return WeightStatus.NORMAL;
        }
    }

    /**
     * 判断是否超载
     */
    public static boolean isOverloaded(Player player) {
        return getWeightPercentage(player) >= 1.0;
    }

    /**
     * 获取速度惩罚系数（0.0-1.0，0表示无惩罚，1表示完全减速）
     */
    public static double getSpeedPenalty(Player player) {
        double percentage = getWeightPercentage(player);

        if (percentage < WeightConfig.safeThreshold) {
            return 0.0;
        }

        // 计算惩罚：在安全阈值到超载之间线性增加
        double normalized = (percentage - WeightConfig.safeThreshold) / (1.0 - WeightConfig.safeThreshold);
        return Math.min(normalized * WeightConfig.speedPenaltyMultiplier, WeightConfig.speedPenaltyMultiplier);
    }

    /**
     * 获取饱食消耗倍率
     */
    public static double getHungerMultiplier(Player player) {
        double percentage = getWeightPercentage(player);

        if (percentage < WeightConfig.safeThreshold) {
            return 1.0;
        } else if (percentage < WeightConfig.warningThreshold) {
            // 负重状态：轻微加速
            return 1.2;
        } else {
            // 超重/超载：显著加速
            return WeightConfig.hungerPenaltyMultiplier;
        }
    }

    /**
     * 清理缓存（配置修改时调用）
     */
    public static void invalidateCache() {
        ITEM_WEIGHT_CACHE.clear();
    }

    /**
     * 负重状态枚举
     */
    public enum WeightStatus {
        NORMAL,    // 正常（≤安全阈值）
        HEAVY,     // 负重（安全阈值~警戒阈值）
        OVERWEIGHT,// 超重（警戒阈值~上限）
        OVERLOADED // 超载（超过上限）
    }
}