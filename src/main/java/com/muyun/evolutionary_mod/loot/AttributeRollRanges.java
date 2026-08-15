package com.muyun.evolutionary_mod.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyun.evolutionary_mod.Config;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 随机词条区间配置表 - Attribute Roll Ranges
 *
 * 所有饰品随机词条的 [min, max] 区间均在此处集中定义。
 * 修改数值时只需改动本文件，无需触碰滚动逻辑。
 *
 * 命名规则：
 *   [类型]_[词条]_MIN / [类型]_[词条]_MAX
 *
 * 百分比类属性（移动速度、暴击率、暴击伤害、伤害减免）以小数存储：
 *   0.05 = 5%
 * 生命恢复以每 tick 值存储（×20 = 每秒值）：
 *   0.05 = 1.0 HP/s
 */
public final class AttributeRollRanges {

    private AttributeRollRanges() {}
    private static final String DATA_DRIVEN_PATH = "data/evolutionary_mod/attributes/ranges.json";
    private static final AtomicInteger INVALID_ENTRY_COUNT = new AtomicInteger(0);
    private static final AtomicInteger FALLBACK_HIT_COUNT = new AtomicInteger(0);
    private static final Set<String> FALLBACK_ITEMS_LOGGED = ConcurrentHashMap.newKeySet();
    private static final Map<String, Map<String, RangeSpec>> DATA_DRIVEN_RANGES = loadDataDrivenRanges();

    // =========================================================
    // 戒指 - Ring
    // =========================================================

    // 生命/治愈系
    public static final double RING_LIFE_MAX_HEALTH_MIN   = 2;
    public static final double RING_LIFE_MAX_HEALTH_MAX   = 20;
    public static final double RING_LIFE_HEALTH_REGEN_MIN = 0.005;  // 0.1 HP/s
    public static final double RING_LIFE_HEALTH_REGEN_MAX = 0.075;  // 1.5 HP/s

    // 战斗/锋利系
    public static final double RING_BATTLE_ATTACK_DAMAGE_MIN = 0.5;
    public static final double RING_BATTLE_ATTACK_DAMAGE_MAX = 6;
    public static final double RING_BATTLE_CRIT_CHANCE_MIN   = 0.01;  // 1%
    public static final double RING_BATTLE_CRIT_CHANCE_MAX   = 0.25;  // 25%
    public static final double RING_BATTLE_CRIT_DAMAGE_MIN   = 0.05;  // 5%
    public static final double RING_BATTLE_CRIT_DAMAGE_MAX   = 0.5;   // 50%

    // 防御系
    public static final double RING_SHIELD_ARMOR_MIN            = 0.5;
    public static final double RING_SHIELD_ARMOR_MAX            = 8;
    public static final double RING_SHIELD_DAMAGE_REDUCTION_MIN = 0.005;  // 0.5%
    public static final double RING_SHIELD_DAMAGE_REDUCTION_MAX = 0.12;   // 12%

    // 疾风系
    public static final double RING_GALE_MOVEMENT_SPEED_MIN = 0.01;   // 1%
    public static final double RING_GALE_MOVEMENT_SPEED_MAX = 0.025;  // 2.5%

    // 幸运系
    public static final double RING_FORTUNE_LUCK_MIN = 0.1;
    public static final double RING_FORTUNE_LUCK_MAX = 3;

    // 破甲系
    public static final double RING_BREAKER_ARMOR_PENETRATION_MIN = 0.3;
    public static final double RING_BREAKER_ARMOR_PENETRATION_MAX = 2.5;
    public static final double RING_BREAKER_ATTACK_DAMAGE_MIN     = 0.3;
    public static final double RING_BREAKER_ATTACK_DAMAGE_MAX     = 2;

    // 通用回退
    public static final double RING_GENERIC_MAX_HEALTH_MIN    = 1;
    public static final double RING_GENERIC_MAX_HEALTH_MAX    = 10;
    public static final double RING_GENERIC_ATTACK_DAMAGE_MIN = 0.2;
    public static final double RING_GENERIC_ATTACK_DAMAGE_MAX = 2;

    // 龙族戒指
    public static final double RING_DRAGON_MAX_HEALTH_MIN    = 12;
    public static final double RING_DRAGON_MAX_HEALTH_MAX    = 20;
    public static final double RING_DRAGON_ATTACK_DAMAGE_MIN = 3;
    public static final double RING_DRAGON_ATTACK_DAMAGE_MAX = 6;
    public static final double RING_DRAGON_HEALTH_REGEN_MIN  = 0.03;   // 0.6 HP/s
    public static final double RING_DRAGON_HEALTH_REGEN_MAX  = 0.07;   // 1.4 HP/s
    public static final double RING_DRAGON_CRIT_CHANCE_MIN   = 0.04;   // 4%
    public static final double RING_DRAGON_CRIT_CHANCE_MAX   = 0.10;   // 10%
    public static final double RING_DRAGON_CRIT_DAMAGE_MIN   = 0.15;   // 15%
    public static final double RING_DRAGON_CRIT_DAMAGE_MAX   = 0.30;   // 30%

    // =========================================================
    // 项链 - Necklace
    // =========================================================

    public static final double NECKLACE_MAX_HEALTH_MIN    = 2;
    public static final double NECKLACE_MAX_HEALTH_MAX    = 12;
    public static final double NECKLACE_ATTACK_DAMAGE_MIN = 0.3;
    public static final double NECKLACE_ATTACK_DAMAGE_MAX = 3;
    public static final double NECKLACE_ARMOR_MIN         = 0.5;
    public static final double NECKLACE_ARMOR_MAX         = 4;
    public static final double NECKLACE_HEALTH_REGEN_MIN  = 0.005;  // 0.1 HP/s
    public static final double NECKLACE_HEALTH_REGEN_MAX  = 0.04;   // 0.8 HP/s

    // =========================================================
    // 手镯 - Bracelet
    // =========================================================

    public static final double BRACELET_MAX_HEALTH_MIN     = 1;
    public static final double BRACELET_MAX_HEALTH_MAX     = 8;
    public static final double BRACELET_ATTACK_DAMAGE_MIN  = 0.2;
    public static final double BRACELET_ATTACK_DAMAGE_MAX  = 2.5;
    public static final double BRACELET_MOVEMENT_SPEED_MIN = 0.005;  // 0.5%
    public static final double BRACELET_MOVEMENT_SPEED_MAX = 0.02;   // 2%
    public static final double BRACELET_LUCK_MIN           = 0.05;
    public static final double BRACELET_LUCK_MAX           = 1;

    // =========================================================
    // 耳环 - Earring
    // =========================================================

    public static final double EARRING_MAX_HEALTH_MIN     = 1;
    public static final double EARRING_MAX_HEALTH_MAX     = 8;
    public static final double EARRING_ATTACK_DAMAGE_MIN  = 0.2;
    public static final double EARRING_ATTACK_DAMAGE_MAX  = 2;
    public static final double EARRING_MOVEMENT_SPEED_MIN = 0.005;  // 0.5%
    public static final double EARRING_MOVEMENT_SPEED_MAX = 0.015;  // 1.5%
    public static final double EARRING_HEALTH_REGEN_MIN   = 0.003;  // 0.06 HP/s
    public static final double EARRING_HEALTH_REGEN_MAX   = 0.03;   // 0.6 HP/s

    // =========================================================
    // 头饰 - Headwear
    // =========================================================

    public static final double HEADWEAR_MAX_HEALTH_MIN    = 2;
    public static final double HEADWEAR_MAX_HEALTH_MAX    = 10;
    public static final double HEADWEAR_ARMOR_MIN         = 0.5;
    public static final double HEADWEAR_ARMOR_MAX         = 5;
    public static final double HEADWEAR_LUCK_MIN          = 0.1;
    public static final double HEADWEAR_LUCK_MAX          = 1.5;
    public static final double HEADWEAR_HEALTH_REGEN_MIN  = 0.005;  // 0.1 HP/s
    public static final double HEADWEAR_HEALTH_REGEN_MAX  = 0.04;   // 0.8 HP/s

    // =========================================================
    // 腰带 - Belt
    // =========================================================

    public static final double BELT_MAX_HEALTH_MIN     = 2;
    public static final double BELT_MAX_HEALTH_MAX     = 10;
    public static final double BELT_ARMOR_MIN          = 0.5;
    public static final double BELT_ARMOR_MAX          = 5;
    public static final double BELT_ATTACK_DAMAGE_MIN  = 0.2;
    public static final double BELT_ATTACK_DAMAGE_MAX  = 2;
    public static final double BELT_MOVEMENT_SPEED_MIN = 0.003;  // 0.3%
    public static final double BELT_MOVEMENT_SPEED_MAX = 0.015;  // 1.5%

    // =========================================================
    // 手套 - Glove
    // =========================================================

    public static final double GLOVE_ATTACK_DAMAGE_MIN      = 0.3;
    public static final double GLOVE_ATTACK_DAMAGE_MAX      = 3;
    public static final double GLOVE_ARMOR_PENETRATION_MIN  = 0.2;
    public static final double GLOVE_ARMOR_PENETRATION_MAX  = 2;
    public static final double GLOVE_CRIT_CHANCE_MIN        = 0.01;  // 1%
    public static final double GLOVE_CRIT_CHANCE_MAX        = 0.1;   // 10%

    // =========================================================
    // 肩饰 - Shoulder
    // =========================================================

    public static final double SHOULDER_ARMOR_MIN            = 0.5;
    public static final double SHOULDER_ARMOR_MAX            = 6;
    public static final double SHOULDER_DAMAGE_REDUCTION_MIN = 0.005;  // 0.5%
    public static final double SHOULDER_DAMAGE_REDUCTION_MAX = 0.08;   // 8%
    public static final double SHOULDER_MAX_HEALTH_MIN       = 1;
    public static final double SHOULDER_MAX_HEALTH_MAX       = 6;

    // =========================================================
    // 脚饰 - Anklet / Boot
    // =========================================================

    public static final double ANKLET_MOVEMENT_SPEED_MIN = 0.005;  // 0.5%
    public static final double ANKLET_MOVEMENT_SPEED_MAX = 0.02;   // 2%
    public static final double ANKLET_ARMOR_MIN          = 0.3;
    public static final double ANKLET_ARMOR_MAX          = 3;
    public static final double ANKLET_LUCK_MIN           = 0.05;
    public static final double ANKLET_LUCK_MAX           = 0.8;

    // =========================================================
    // 通用回退 - Generic Fallback
    // =========================================================

    public static final double GENERIC_MAX_HEALTH_MIN    = 1;
    public static final double GENERIC_MAX_HEALTH_MAX    = 5;
    public static final double GENERIC_ATTACK_DAMAGE_MIN = 0.1;
    public static final double GENERIC_ATTACK_DAMAGE_MAX = 1;

    /**
     * 读取 data/evolutionary_mod/attributes/ranges.json 中的“按物品配置”词条。
     * 若存在该物品配置，返回滚动后的属性；否则返回 null 交由代码常量回退。
     */
    public static AccessoryAttributes rollFromDataDriven(String itemPath, Random random) {
        Map<String, RangeSpec> specs = DATA_DRIVEN_RANGES.get(itemPath);
        if (specs == null || specs.isEmpty()) {
            onFallback(itemPath);
            return null;
        }

        double maxHealth = rollAttr(specs, "max_health", random);
        double attackDamage = rollAttr(specs, "attack_damage", random);
        double armor = rollAttr(specs, "armor", random);
        double movementSpeed = rollAttr(specs, "movement_speed", random);
        double luck = rollAttr(specs, "luck", random);
        double healthRegen = rollAttr(specs, "health_regen", random);
        double armorPenetration = rollAttr(specs, "armor_penetration", random);
        double critChance = rollAttr(specs, "crit_chance", random);
        double critDamage = rollAttr(specs, "crit_damage", random);
        double damageReduction = rollAttr(specs, "damage_reduction", random);

        return new AccessoryAttributes(
                maxHealth, attackDamage, armor, movementSpeed, luck,
                healthRegen, armorPenetration, critChance, critDamage, damageReduction
        );
    }

    private static double rollAttr(Map<String, RangeSpec> specs, String attrName, Random random) {
        RangeSpec spec = specs.get(attrName);
        if (spec == null) return 0;

        double raw = spec.min + random.nextDouble() * (spec.max - spec.min);
        double value = spec.step > 0 ? quantize(raw, spec.step) : round3(raw);
        return spec.perSecond ? round3(value / 20.0) : value;
    }

    private static double quantize(double value, double step) {
        return round3(Math.round(value / step) * step);
    }

    private static double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private static Map<String, Map<String, RangeSpec>> loadDataDrivenRanges() {
        Map<String, Map<String, RangeSpec>> result = new HashMap<>();
        try (InputStream is = AttributeRollRanges.class.getClassLoader().getResourceAsStream(DATA_DRIVEN_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[AttributeRollRanges] 未找到数据配置: {}，将回退代码常量。", DATA_DRIVEN_PATH);
                return result;
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> categoryEntry : root.entrySet()) {
                if (!categoryEntry.getValue().isJsonObject()) continue;
                JsonObject categoryObj = categoryEntry.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> itemEntry : categoryObj.entrySet()) {
                    if (!itemEntry.getValue().isJsonObject()) continue;
                    JsonObject itemObj = itemEntry.getValue().getAsJsonObject();
                    JsonObject attrsObj = itemObj.has("attributes") && itemObj.get("attributes").isJsonObject()
                            ? itemObj.getAsJsonObject("attributes")
                            : null;
                    if (attrsObj == null) {
                        INVALID_ENTRY_COUNT.incrementAndGet();
                        continue;
                    }

                    Map<String, RangeSpec> attrSpecs = new HashMap<>();
                    for (Map.Entry<String, JsonElement> attrEntry : attrsObj.entrySet()) {
                        if (!attrEntry.getValue().isJsonObject()) {
                            INVALID_ENTRY_COUNT.incrementAndGet();
                            continue;
                        }
                        RangeSpec spec = parseRangeSpec(itemEntry.getKey(), attrEntry.getKey(), attrEntry.getValue().getAsJsonObject());
                        if (spec != null) attrSpecs.put(attrEntry.getKey(), spec);
                    }
                    if (!attrSpecs.isEmpty()) {
                        result.put(itemEntry.getKey(), attrSpecs);
                    } else {
                        INVALID_ENTRY_COUNT.incrementAndGet();
                    }
                }
            }
            EvolutionaryMod.LOGGER.info("[AttributeRollRanges] 已加载数据驱动词条配置: {} 项物品，{} 项无效条目。",
                    result.size(), INVALID_ENTRY_COUNT.get());
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[AttributeRollRanges] 加载数据驱动词条配置失败，将回退代码常量。", e);
        }
        return result;
    }

    private static RangeSpec parseRangeSpec(String itemName, String attrName, JsonObject obj) {
        if (!obj.has("min") || !obj.has("max")) {
            EvolutionaryMod.LOGGER.warn("[AttributeRollRanges] {}.{} 缺少 min/max，已忽略。", itemName, attrName);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return null;
        }

        double min = obj.get("min").getAsDouble();
        double max = obj.get("max").getAsDouble();
        if (min > max) {
            EvolutionaryMod.LOGGER.warn("[AttributeRollRanges] {}.{} 的 min > max，已忽略。", itemName, attrName);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return null;
        }

        double step = obj.has("step") ? obj.get("step").getAsDouble() : 0.0;
        if (step < 0) {
            EvolutionaryMod.LOGGER.warn("[AttributeRollRanges] {}.{} 的 step < 0，按 0 处理。", itemName, attrName);
            step = 0.0;
        }
        boolean perSecond = obj.has("per_second") && obj.get("per_second").getAsBoolean();
        return new RangeSpec(min, max, step, perSecond);
    }

    private static void onFallback(String itemPath) {
        FALLBACK_HIT_COUNT.incrementAndGet();
        if (Config.isStrictDataDriven()) {
            throw new IllegalStateException("[STRICT] Attribute data-driven missing for item: " + itemPath
                    + " in " + DATA_DRIVEN_PATH);
        }
        if (FALLBACK_ITEMS_LOGGED.add(itemPath)) {
            EvolutionaryMod.LOGGER.warn("[AttributeRollRanges] 数据未命中，使用代码回退: {}", itemPath);
        }
    }

    public static int getLoadedEntryCount() {
        return DATA_DRIVEN_RANGES.size();
    }

    public static int getInvalidEntryCount() {
        return INVALID_ENTRY_COUNT.get();
    }

    public static int getFallbackHitCount() {
        return FALLBACK_HIT_COUNT.get();
    }

    public static int getFallbackDistinctItemCount() {
        return FALLBACK_ITEMS_LOGGED.size();
    }

    public static boolean hasDataEntry(String itemPath) {
        return DATA_DRIVEN_RANGES.containsKey(itemPath);
    }

    /**
     * 查询某物品在 ranges.json 中某属性的基础区间 [min, max]，返回**存储单位**（每 tick）。
     * 用于锻造强化增量计算（§4.3.1：增量区间 = 基础区间 × 品阶系数）。
     * per_second 属性（health_regen）会换算为每 tick（÷20），与 AccessoryAttributes 存储一致。
     *
     * @return 基础区间（存储单位），或 null（该物品/属性无数据驱动配置）
     */
    public static double[] baseRange(String itemPath, String attrName) {
        Map<String, RangeSpec> specs = DATA_DRIVEN_RANGES.get(itemPath);
        if (specs == null) return null;
        RangeSpec spec = specs.get(attrName);
        if (spec == null) return null;
        if (spec.perSecond()) {
            return new double[]{spec.min() / 20.0, spec.max() / 20.0};
        }
        return new double[]{spec.min(), spec.max()};
    }

    private record RangeSpec(double min, double max, double step, boolean perSecond) {}
}

