package com.muyun.evolutionary_mod.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 锻造材料掉率配置 - Forge Material Drops
 *
 * 数据驱动主配置：data/evolutionary_mod/forge/material_drops.json（策划案 §8.2 / §8.2.1）
 * - 档位（category）：原版普通怪 / 原版精英向 / 自定义精英三档 / Boss / 高级与稀世宝箱
 * - 每档独立判定三种材料（类型精华 / 品阶碎片 / 属性精华）与三种重锻石
 * - 子类权重：类型精华 1:1:1、品阶碎片 6/5/4/3/2/1、属性精华按方向权重
 *
 * 掉率不与 drop_table.json 耦合（§8.1 双通道）。
 */
public class ForgeMaterialDrops {

    public record StoneConfig(double chance, int countMin, int countMax, boolean fixed, boolean lucky) {
        public static StoneConfig NONE = new StoneConfig(0, 0, 0, true, false);
    }

    public record TierConfig(
            double essenceChance, int essenceCountMin, int essenceCountMax,
            double shardChance, int shardCountMin, int shardCountMax,
            double attributeChance, int attributeCountMin, int attributeCountMax,
            StoneConfig normalStone, StoneConfig advancedStone, StoneConfig lockStone) {}

    public record WeightConfig(Map<String, Integer> essenceWeights, Map<String, Integer> shardWeights, Map<String, Integer> attributeWeights) {}

    private static final String DATA_DRIVEN_PATH = "data/evolutionary_mod/forge/material_drops.json";
    private static final AtomicInteger INVALID_ENTRY_COUNT = new AtomicInteger(0);

    /** 每个 loot_table_id 对应的档位配置（JSON 优先，回退档位） */
    private static final Map<ResourceLocation, TierConfig> TIER_BY_TABLE = new HashMap<>();
    private static final Map<ResourceLocation, TierConfig> FALLBACK_TIER_BY_TABLE = new HashMap<>();
    private static final Map<String, Integer> FALLBACK_ESSENCE_WEIGHTS = new HashMap<>();
    private static final Map<String, Integer> FALLBACK_SHARD_WEIGHTS = new HashMap<>();
    private static final Map<String, Integer> FALLBACK_ATTRIBUTE_WEIGHTS = new HashMap<>();

    private static final WeightConfig WEIGHTS = loadWeights();

    static {
        loadFallbackDefaults();
        loadDataDrivenTable();
    }

    private static void loadFallbackDefaults() {
        // 回退：按 loot_table_id 前缀匹配档位（完整 JSON 缺失时保证基本功能）。
        // 注意：自定义精英（14 只）走 loot_tables/forge/elite_materials_t{1,2,3}.json，
        // 此处不注册精英回退，避免 GLM 与实体战利品表双重掉落。
        FALLBACK_TIER_BY_TABLE.put(ResourceLocation.parse("minecraft:entities/zombie"), tier(0.20, 1, 2, 0.067, 1, 1, 0.033, 1, 1, stone(0.03, 1, 1, true, false)));
        FALLBACK_TIER_BY_TABLE.put(ResourceLocation.parse("minecraft:entities/witch"), tier(0.35, 1, 2, 0.117, 1, 1, 0.058, 1, 1, stone(0.05, 1, 1, true, false)));
        FALLBACK_TIER_BY_TABLE.put(ResourceLocation.parse("minecraft:entities/wither"), tier(1.00, 1, 3, 0.50, 1, 2, 0.25, 1, 1, stone(0.42, 2, 4, false, true)));

        // 子类回退权重
        FALLBACK_ESSENCE_WEIGHTS.put("evolutionary_mod:accessory_essence", 1);
        FALLBACK_ESSENCE_WEIGHTS.put("evolutionary_mod:weapon_essence", 1);
        FALLBACK_ESSENCE_WEIGHTS.put("evolutionary_mod:armor_essence", 1);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_broken", 6);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_normal", 5);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_excellent", 4);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_epic", 3);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_legendary", 2);
        FALLBACK_SHARD_WEIGHTS.put("evolutionary_mod:rank_shard_mythic", 1);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_life", 20);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_attack", 18);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_armor", 15);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_crit_chance", 12);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_crit_damage", 10);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_reduction", 10);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_penetration", 9);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_speed", 8);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_regen", 7);
        FALLBACK_ATTRIBUTE_WEIGHTS.put("evolutionary_mod:attribute_essence_luck", 6);
    }

    private static TierConfig tier(double ec, int ecMin, int ecMax, double sc, int scMin, int scMax,
                                   double ac, int acMin, int acMax, StoneConfig normal) {
        return new TierConfig(ec, ecMin, ecMax, sc, scMin, scMax, ac, acMin, acMax,
                normal, StoneConfig.NONE, StoneConfig.NONE);
    }

    private static StoneConfig stone(double chance, int min, int max, boolean fixed, boolean lucky) {
        return new StoneConfig(chance, min, max, fixed, lucky);
    }

    private static void loadDataDrivenTable() {
        try (InputStream is = ForgeMaterialDrops.class.getClassLoader().getResourceAsStream(DATA_DRIVEN_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[ForgeMaterialDrops] 未找到数据配置: {}，将使用代码回退。", DATA_DRIVEN_PATH);
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject categories = root.has("categories") && root.get("categories").isJsonObject()
                    ? root.getAsJsonObject("categories") : new JsonObject();
            for (Map.Entry<String, JsonElement> e : categories.entrySet()) {
                JsonObject cat = e.getValue().getAsJsonObject();
                TierConfig tier = parseTier(cat);
                if (tier == null) continue;
                for (JsonElement t : cat.getAsJsonArray("loot_tables")) {
                    try {
                        TIER_BY_TABLE.put(ResourceLocation.parse(t.getAsString()), tier);
                    } catch (Exception ex) {
                        INVALID_ENTRY_COUNT.incrementAndGet();
                    }
                }
            }
            EvolutionaryMod.LOGGER.info("[ForgeMaterialDrops] 已加载材料掉率配置: {} 个来源，{} 条无效条目。",
                    TIER_BY_TABLE.size(), INVALID_ENTRY_COUNT.get());
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeMaterialDrops] 加载材料掉率配置失败，将使用代码回退。", e);
        }
    }

    private static TierConfig parseTier(JsonObject cat) {
        try {
            double ec = cat.get("essence_chance").getAsDouble();
            int ecMin = cat.get("essence_count_min").getAsInt();
            int ecMax = cat.get("essence_count_max").getAsInt();
            double sc = cat.get("shard_chance").getAsDouble();
            int scMin = cat.get("shard_count_min").getAsInt();
            int scMax = cat.get("shard_count_max").getAsInt();
            double ac = cat.get("attribute_chance").getAsDouble();
            int acMin = cat.get("attribute_count_min").getAsInt();
            int acMax = cat.get("attribute_count_max").getAsInt();
            StoneConfig normal = parseStone(cat.has("reroll_normal") ? cat.getAsJsonObject("reroll_normal") : null);
            StoneConfig advanced = parseStone(cat.has("reroll_advanced") ? cat.getAsJsonObject("reroll_advanced") : null);
            StoneConfig lock = parseStone(cat.has("reroll_lock") ? cat.getAsJsonObject("reroll_lock") : null);
            return new TierConfig(ec, ecMin, ecMax, sc, scMin, scMax, ac, acMin, acMax,
                    normal, advanced, lock);
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.warn("[ForgeMaterialDrops] 非法档位条目: {}，已忽略。", cat);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return null;
        }
    }

    private static StoneConfig parseStone(JsonObject obj) {
        if (obj == null) return StoneConfig.NONE;
        return new StoneConfig(
                obj.has("chance") ? obj.get("chance").getAsDouble() : 0,
                obj.has("count_min") ? obj.get("count_min").getAsInt() : 0,
                obj.has("count_max") ? obj.get("count_max").getAsInt() : 0,
                obj.has("fixed") ? obj.get("fixed").getAsBoolean() : false,
                obj.has("lucky") ? obj.get("lucky").getAsBoolean() : false);
    }

    private static WeightConfig loadWeights() {
        Map<String, Integer> essence = new HashMap<>(FALLBACK_ESSENCE_WEIGHTS);
        Map<String, Integer> shard = new HashMap<>(FALLBACK_SHARD_WEIGHTS);
        Map<String, Integer> attribute = new HashMap<>(FALLBACK_ATTRIBUTE_WEIGHTS);
        try (InputStream is = ForgeMaterialDrops.class.getClassLoader().getResourceAsStream(DATA_DRIVEN_PATH)) {
            if (is == null) return new WeightConfig(essence, shard, attribute);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.has("subtype_weights") && root.get("subtype_weights").isJsonObject()) {
                JsonObject sw = root.getAsJsonObject("subtype_weights");
                essence.putAll(parseWeights(sw.getAsJsonObject("essence")));
                shard.putAll(parseWeights(sw.getAsJsonObject("shard")));
                attribute.putAll(parseWeights(sw.getAsJsonObject("attribute")));
            }
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[ForgeMaterialDrops] 加载子类权重失败，使用回退。", e);
        }
        return new WeightConfig(essence, shard, attribute);
    }

    private static Map<String, Integer> parseWeights(JsonObject obj) {
        Map<String, Integer> out = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            try {
                out.put(e.getKey(), e.getValue().getAsInt());
            } catch (Exception ignored) {}
        }
        return out;
    }

    /** 查询某战利品表对应的材料档位；未命中返回 null。 */
    public static TierConfig getTier(ResourceLocation lootTableId) {
        TierConfig fromData = TIER_BY_TABLE.get(lootTableId);
        if (fromData != null) return fromData;
        // 回退：用 entity 前缀的默认档位
        TierConfig fallback = FALLBACK_TIER_BY_TABLE.get(lootTableId);
        if (fallback != null) {
            EvolutionaryMod.LOGGER.warn("[ForgeMaterialDrops] 数据未命中，使用代码回退: {}", lootTableId);
        }
        return fallback;
    }

    public static WeightConfig getWeights() {
        return WEIGHTS;
    }

    public static int getLoadedSourceCount() { return TIER_BY_TABLE.size(); }
    public static int getInvalidEntryCount() { return INVALID_ENTRY_COUNT.get(); }
}
