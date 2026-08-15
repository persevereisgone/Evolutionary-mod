package com.muyun.evolutionary_mod.system.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.loot.AccessoryDropRarity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 锻造台配置 - Forge Config
 *
 * 数据驱动主配置（策划案 §9.1）：
 * - enhance_rolls.json：强化方向权重、品阶每级系数、抽条概率（§4.3 / §4.3.1）
 * - enhance_costs.json：强化上限、单次材料量、成功率 BonusTable（§4.4）
 * - reroll_rules.json：重锻消耗、锁定上限、粉碎返还率（§5 / §6）
 * - attribute_essence_ranges.json：品阶标准区间（§5.2.1，属性精华添加/重锻回滚取值）
 *
 * 各 JSON 解析失败时回退到本类内置常量。
 */
public class ForgeConfig {

    // ---------- enhance_rolls ----------
    public record EnhanceRolls(int maxLevel, double rollOneChance, double rollTwoChance,
                               Map<String, Integer> directionWeights,
                               Map<String, Double> rarityFactor) {}

    // ---------- enhance_costs ----------
    public record EnhanceCosts(int maxLevel, int maxShardsPerTry, int essencePerTry,
                               Map<Integer, Double> successBonus) {}

    // ---------- reroll_rules ----------
    public record RerollRules(int normalStoneCost, int epicStoneCost, AccessoryDropRarity epicMinRarity,
                              double advancedBiasShift, int maxLockStones, int lockPerStat,
                              double smashShardRate, double smashEssenceRate) {}

    private static final String ENHANCE_ROLLS_PATH = "data/evolutionary_mod/forge/enhance_rolls.json";
    private static final String ENHANCE_COSTS_PATH = "data/evolutionary_mod/forge/enhance_costs.json";
    private static final String REROLL_RULES_PATH = "data/evolutionary_mod/forge/reroll_rules.json";
    private static final String ESSENCE_RANGES_PATH = "data/evolutionary_mod/forge/attribute_essence_ranges.json";

    // ---------- 品阶标准区间（§5.2.1，属性 -> 品阶 -> [min,max]）----------
    private static final Map<String, Map<AccessoryDropRarity, double[]>> ESSENCE_RANGES = new HashMap<>();

    private static final EnhanceRolls ENHANCE_ROLLS = loadEnhanceRolls();
    private static final EnhanceCosts ENHANCE_COSTS = loadEnhanceCosts();
    private static final RerollRules REROLL_RULES = loadRerollRules();

    static {
        loadEssenceRanges();
    }

    public static EnhanceRolls enhanceRolls() { return ENHANCE_ROLLS; }
    public static EnhanceCosts enhanceCosts() { return ENHANCE_COSTS; }
    public static RerollRules rerollRules() { return REROLL_RULES; }

    // ------------------------------------------------------------------
    // enhance_rolls.json
    // ------------------------------------------------------------------
    private static EnhanceRolls loadEnhanceRolls() {
        int maxLevel = 10;
        double rollOne = 0.75, rollTwo = 0.25;
        Map<String, Integer> weights = new HashMap<>();
        Map<String, Double> factors = new HashMap<>();

        try (InputStream is = ForgeConfig.class.getClassLoader().getResourceAsStream(ENHANCE_ROLLS_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[ForgeConfig] 未找到 enhance_rolls.json，使用回退常量。");
            } else {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
                maxLevel = root.has("max_level") ? root.get("max_level").getAsInt() : 10;
                rollOne = root.has("roll_one_chance") ? root.get("roll_one_chance").getAsDouble() : 0.75;
                rollTwo = root.has("roll_two_chance") ? root.get("roll_two_chance").getAsDouble() : 0.25;
                weights.putAll(parseIntMap(root.getAsJsonObject("direction_weights")));
                factors.putAll(parseDoubleMap(root.getAsJsonObject("rarity_factor")));
            }
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeConfig] 加载 enhance_rolls.json 失败，使用回退常量。", e);
        }
        // 回退填充
        if (weights.isEmpty()) {
            weights.put("max_health", 20); weights.put("attack_damage", 18); weights.put("armor", 15);
            weights.put("crit_chance", 12); weights.put("crit_damage", 10); weights.put("damage_reduction", 10);
            weights.put("armor_penetration", 9); weights.put("movement_speed", 8);
            weights.put("health_regen", 7); weights.put("luck", 6);
        }
        if (factors.isEmpty()) {
            factors.put("BROKEN", 0.07); factors.put("NORMAL", 0.06); factors.put("EXCELLENT", 0.055);
            factors.put("EPIC", 0.05); factors.put("LEGENDARY", 0.045); factors.put("MYTHIC", 0.04);
        }
        return new EnhanceRolls(maxLevel, rollOne, rollTwo, weights, factors);
    }

    // ------------------------------------------------------------------
    // enhance_costs.json
    // ------------------------------------------------------------------
    private static EnhanceCosts loadEnhanceCosts() {
        int maxLevel = 10, maxShards = 10, essence = 1;
        Map<Integer, Double> bonus = new HashMap<>();
        try (InputStream is = ForgeConfig.class.getClassLoader().getResourceAsStream(ENHANCE_COSTS_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[ForgeConfig] 未找到 enhance_costs.json，使用回退常量。");
            } else {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
                maxLevel = root.has("max_level") ? root.get("max_level").getAsInt() : 10;
                maxShards = root.has("max_shards_per_try") ? root.get("max_shards_per_try").getAsInt() : 10;
                essence = root.has("essence_per_try") ? root.get("essence_per_try").getAsInt() : 1;
                JsonObject sb = root.getAsJsonObject("success_bonus");
                for (Map.Entry<String, JsonElement> e : sb.entrySet()) {
                    try {
                        bonus.put(Integer.parseInt(e.getKey()), e.getValue().getAsDouble());
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeConfig] 加载 enhance_costs.json 失败，使用回退常量。", e);
        }
        if (bonus.isEmpty()) {
            bonus.put(-5, 0.70); bonus.put(-4, 0.65); bonus.put(-3, 0.60); bonus.put(-2, 0.55);
            bonus.put(-1, 0.45); bonus.put(0, 0.25); bonus.put(1, 0.08); bonus.put(2, 0.04);
            bonus.put(3, 0.03); bonus.put(4, 0.02); bonus.put(5, 0.01);
        }
        return new EnhanceCosts(maxLevel, maxShards, essence, bonus);
    }

    // ------------------------------------------------------------------
    // reroll_rules.json
    // ------------------------------------------------------------------
    private static RerollRules loadRerollRules() {
        int normal = 1, epicCost = 2, maxLock = 2, lockPerStat = 1;
        double bias = 0.3, smashShard = 0.5, smashEssence = 0.25;
        AccessoryDropRarity epicMin = AccessoryDropRarity.EPIC;
        try (InputStream is = ForgeConfig.class.getClassLoader().getResourceAsStream(REROLL_RULES_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[ForgeConfig] 未找到 reroll_rules.json，使用回退常量。");
            } else {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
                normal = root.has("normal_stone_cost") ? root.get("normal_stone_cost").getAsInt() : 1;
                epicCost = root.has("epic_stone_cost") ? root.get("epic_stone_cost").getAsInt() : 2;
                maxLock = root.has("max_lock_stones") ? root.get("max_lock_stones").getAsInt() : 2;
                lockPerStat = root.has("lock_per_stat") ? root.get("lock_per_stat").getAsInt() : 1;
                bias = root.has("advanced_bias_shift") ? root.get("advanced_bias_shift").getAsDouble() : 0.3;
                smashShard = root.has("smash_shard_rate") ? root.get("smash_shard_rate").getAsDouble() : 0.5;
                smashEssence = root.has("smash_essence_rate") ? root.get("smash_essence_rate").getAsDouble() : 0.25;
                try {
                    epicMin = AccessoryDropRarity.valueOf(root.has("epic_min_rarity") ? root.get("epic_min_rarity").getAsString().toUpperCase() : "EPIC");
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeConfig] 加载 reroll_rules.json 失败，使用回退常量。", e);
        }
        return new RerollRules(normal, epicCost, epicMin, bias, maxLock, lockPerStat, smashShard, smashEssence);
    }

    // ------------------------------------------------------------------
    // attribute_essence_ranges.json（品阶标准区间）
    // ------------------------------------------------------------------
    private static void loadEssenceRanges() {
        try (InputStream is = ForgeConfig.class.getClassLoader().getResourceAsStream(ESSENCE_RANGES_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[ForgeConfig] 未找到 attribute_essence_ranges.json，使用回退常量。");
                loadFallbackEssenceRanges();
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> attrEntry : root.entrySet()) {
                if (!attrEntry.getValue().isJsonObject()) continue;
                Map<AccessoryDropRarity, double[]> byRarity = new HashMap<>();
                for (Map.Entry<String, JsonElement> rEntry : attrEntry.getValue().getAsJsonObject().entrySet()) {
                    try {
                        AccessoryDropRarity rarity = AccessoryDropRarity.valueOf(rEntry.getKey().toUpperCase());
                        var arr = rEntry.getValue().getAsJsonArray();
                        byRarity.put(rarity, new double[]{arr.get(0).getAsDouble(), arr.get(1).getAsDouble()});
                    } catch (Exception ignored) {}
                }
                if (!byRarity.isEmpty()) ESSENCE_RANGES.put(attrEntry.getKey(), byRarity);
            }
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeConfig] 加载 attribute_essence_ranges.json 失败，使用回退常量。", e);
            loadFallbackEssenceRanges();
        }
        if (ESSENCE_RANGES.isEmpty()) loadFallbackEssenceRanges();
    }

    private static void loadFallbackEssenceRanges() {
        ESSENCE_RANGES.clear();
        putRange("max_health", new double[][]{{1,3},{4,6},{6,10},{10,14},{15,20},{20,30}});
        putRange("attack_damage", new double[][]{{0.1,0.5},{0.5,1.5},{1.5,3},{2.5,4},{4,6},{6,9}});
        putRange("armor", new double[][]{{0.1,0.5},{0.5,1},{1.5,2.5},{2.5,3.5},{4,6},{6,8}});
        putRange("movement_speed", new double[][]{{0.01,0.02},{0.03,0.05},{0.08,0.12},{0.12,0.18},{0.18,0.25},{0.25,0.4}});
        putRange("luck", new double[][]{{0.05,0.15},{0.25,0.5},{0.6,1},{1,1.8},{2,3.5},{3,5}});
        putRange("health_regen", new double[][]{{0.05,0.12},{0.2,0.3},{0.3,0.5},{0.5,0.8},{0.8,1},{1,1.5}});
        putRange("armor_penetration", new double[][]{{0.1,0.3},{0.3,0.7},{0.7,1.2},{1,1.8},{2,2.5},{2.5,4}});
        putRange("crit_chance", new double[][]{{0.005,0.015},{0.015,0.035},{0.03,0.06},{0.05,0.1},{0.08,0.14},{0.12,0.2}});
        putRange("crit_damage", new double[][]{{0.01,0.05},{0.05,0.1},{0.1,0.18},{0.18,0.28},{0.28,0.4},{0.4,0.55}});
        putRange("damage_reduction", new double[][]{{0.005,0.01},{0.01,0.02},{0.02,0.04},{0.04,0.06},{0.06,0.09},{0.1,0.15}});
    }

    private static void putRange(String attr, double[][] ranges) {
        Map<AccessoryDropRarity, double[]> byRarity = new HashMap<>();
        AccessoryDropRarity[] order = AccessoryDropRarity.values();
        for (int i = 0; i < order.length && i < ranges.length; i++) {
            byRarity.put(order[i], ranges[i]);
        }
        ESSENCE_RANGES.put(attr, byRarity);
    }

    /** 品阶标准区间查询；未命中返回 null。 */
    public static double[] essenceRange(String attr, AccessoryDropRarity rarity) {
        Map<AccessoryDropRarity, double[]> byRarity = ESSENCE_RANGES.get(attr);
        if (byRarity == null) return null;
        return byRarity.get(rarity);
    }

    private static Map<String, Integer> parseIntMap(JsonObject obj) {
        Map<String, Integer> out = new HashMap<>();
        if (obj == null) return out;
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            try { out.put(e.getKey(), e.getValue().getAsInt()); } catch (Exception ignored) {}
        }
        return out;
    }

    private static Map<String, Double> parseDoubleMap(JsonObject obj) {
        Map<String, Double> out = new HashMap<>();
        if (obj == null) return out;
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            try { out.put(e.getKey(), e.getValue().getAsDouble()); } catch (Exception ignored) {}
        }
        return out;
    }
}
