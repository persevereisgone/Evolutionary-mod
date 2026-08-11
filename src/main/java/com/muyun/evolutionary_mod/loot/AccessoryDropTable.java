package com.muyun.evolutionary_mod.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyun.evolutionary_mod.Config;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 饰品掉落配置表 - Accessory Drop Table
 *
 * 规则优先级：
 * 1) data/evolutionary_mod/loot/drop_table.json（数据驱动）
 * 2) 本类内置 defaults（代码回退）
 */
public class AccessoryDropTable {

    public record DropConfig(float dropChance, AccessoryDropRarity[] allowedRarities) {}

    private static final String DATA_DRIVEN_PATH = "data/evolutionary_mod/loot/drop_table.json";
    private static final AtomicInteger INVALID_ENTRY_COUNT = new AtomicInteger(0);
    private static final AtomicInteger FALLBACK_HIT_COUNT = new AtomicInteger(0);
    private static final Set<ResourceLocation> FALLBACK_KEYS_LOGGED = ConcurrentHashMap.newKeySet();

    private static final Map<ResourceLocation, DropConfig> FALLBACK_TABLE = new HashMap<>();
    private static final Map<ResourceLocation, DropConfig> DATA_TABLE = loadDataDrivenTable();

    static {
        loadFallbackDefaults();
    }

    private static void loadFallbackDefaults() {
        // 普通怪物
        registerFallback("minecraft:entities/zombie",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/zombie_villager", 0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/skeleton",        0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/creeper",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/spider",          0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/cave_spider",     0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/drowned",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/husk",            0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/stray",           0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/phantom",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/slime",           0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/magma_cube",      0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/ghast",           0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/zombified_piglin",0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/hoglin",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/zoglin",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/silverfish",      0.01f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/endermite",       0.01f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:entities/vex",             0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));

        // 精英怪物
        registerFallback("minecraft:entities/witch",           0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/vindicator",      0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/pillager",        0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/ravager",         0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/evoker",          0.08f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:entities/blaze",           0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/wither_skeleton", 0.08f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:entities/piglin_brute",    0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/piglin",          0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/enderman",        0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/guardian",        0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/shulker",         0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/bogged",          0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:entities/breeze",          0.07f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:entities/iron_golem",      0.04f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));

        // 自定义精英怪
        registerFallback("evolutionary_mod:entities/cinder_skeleton",  0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/thunder_spider",   0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/shadow_stalker",   0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/frost_ghoul",      0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/ironclad_warrior", 0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/lava_shooter",     0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/storm_mage",       0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/abyss_lurker",     0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/holy_knight",      0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/chaos_shifter",    0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/time_warper",      0.18f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/soul_necromancer", 0.18f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/earth_guardian",   0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/wind_blade_hunter",0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("evolutionary_mod:entities/life_leecher",     0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));

        // Boss
        registerFallback("minecraft:entities/elder_guardian",  0.80f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:entities/wither",          1.00f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        registerFallback("minecraft:entities/ender_dragon",    1.00f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY, AccessoryDropRarity.MYTHIC));

        // 宝箱
        registerFallback("minecraft:chests/simple_dungeon",         0.30f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/abandoned_mineshaft",    0.25f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_armorer",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_butcher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_cartographer",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_fisher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_fletcher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_mason",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_shepherd",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_tannery",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_temple",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_toolsmith",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/village/village_weaponsmith",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/igloo_chest",            0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        registerFallback("minecraft:chests/desert_pyramid",         0.35f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/jungle_temple",          0.35f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/buried_treasure",        0.40f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/pillager_outpost",       0.30f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/trial_chambers/reward",  0.30f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        registerFallback("minecraft:chests/stronghold_corridor",    0.40f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:chests/stronghold_library",     0.50f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:chests/nether_bridge",          0.40f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:chests/bastion_treasure",       0.60f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        registerFallback("minecraft:chests/end_city_treasure",      0.50f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        registerFallback("minecraft:chests/woodland_mansion",       0.55f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        registerFallback("minecraft:chests/ancient_city",           0.20f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
    }

    private static void registerFallback(String path, float chance, AccessoryDropRarity[] rarities) {
        FALLBACK_TABLE.put(ResourceLocation.parse(path), new DropConfig(chance, rarities));
    }

    public static AccessoryDropRarity[] rarity(AccessoryDropRarity... rarities) {
        return rarities;
    }

    public static DropConfig getConfig(ResourceLocation lootTableId) {
        DropConfig fromData = DATA_TABLE.get(lootTableId);
        if (fromData != null) return fromData;

        DropConfig fallback = FALLBACK_TABLE.get(lootTableId);
        if (fallback != null) {
            onFallback(lootTableId);
        }
        return fallback;
    }

    private static Map<ResourceLocation, DropConfig> loadDataDrivenTable() {
        Map<ResourceLocation, DropConfig> table = new HashMap<>();
        try (InputStream is = AccessoryDropTable.class.getClassLoader().getResourceAsStream(DATA_DRIVEN_PATH)) {
            if (is == null) {
                EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] 未找到数据配置: {}，将使用代码回退。", DATA_DRIVEN_PATH);
                return table;
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray entries = root.has("entries") && root.get("entries").isJsonArray()
                    ? root.getAsJsonArray("entries")
                    : new JsonArray();

            for (JsonElement element : entries) {
                if (!element.isJsonObject()) continue;
                parseEntry(table, element.getAsJsonObject());
            }
            EvolutionaryMod.LOGGER.info("[AccessoryDropTable] 已加载数据驱动掉落配置: {} 条，{} 条无效条目。",
                    table.size(), INVALID_ENTRY_COUNT.get());
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[AccessoryDropTable] 加载数据驱动掉落配置失败，将使用代码回退。", e);
        }
        return table;
    }

    private static void parseEntry(Map<ResourceLocation, DropConfig> table, JsonObject obj) {
        if (!obj.has("loot_table_id") || !obj.has("drop_chance") || !obj.has("allowed_rarities")) {
            EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] 条目缺少必填字段，已忽略: {}", obj);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return;
        }

        String id = obj.get("loot_table_id").getAsString();
        ResourceLocation rl;
        try {
            rl = ResourceLocation.parse(id);
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] 非法 loot_table_id: {}", id);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return;
        }

        float chance = obj.get("drop_chance").getAsFloat();
        if (chance < 0f || chance > 1f) {
            EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] drop_chance 超出范围 [0,1]: {} ({})", id, chance);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return;
        }

        JsonArray raritiesJson = obj.getAsJsonArray("allowed_rarities");
        AccessoryDropRarity[] rarities = new AccessoryDropRarity[raritiesJson.size()];
        for (int i = 0; i < raritiesJson.size(); i++) {
            try {
                rarities[i] = AccessoryDropRarity.valueOf(raritiesJson.get(i).getAsString().toUpperCase());
            } catch (Exception e) {
                EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] 非法品质枚举: {} ({})", id, raritiesJson.get(i));
                INVALID_ENTRY_COUNT.incrementAndGet();
                return;
            }
        }
        if (rarities.length == 0) {
            EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] allowed_rarities 为空: {}", id);
            INVALID_ENTRY_COUNT.incrementAndGet();
            return;
        }
        table.put(rl, new DropConfig(chance, rarities));
    }

    private static void onFallback(ResourceLocation lootTableId) {
        FALLBACK_HIT_COUNT.incrementAndGet();
        if (Config.isStrictDataDriven()) {
            throw new IllegalStateException("[STRICT] Drop table data missing for loot_table_id: " + lootTableId
                    + " in " + DATA_DRIVEN_PATH);
        }
        if (FALLBACK_KEYS_LOGGED.add(lootTableId)) {
            EvolutionaryMod.LOGGER.warn("[AccessoryDropTable] 数据未命中，使用代码回退: {}", lootTableId);
        }
    }

    public static int getLoadedEntryCount() {
        return DATA_TABLE.size();
    }

    public static int getInvalidEntryCount() {
        return INVALID_ENTRY_COUNT.get();
    }

    public static int getFallbackHitCount() {
        return FALLBACK_HIT_COUNT.get();
    }

    public static int getFallbackDistinctKeyCount() {
        return FALLBACK_KEYS_LOGGED.size();
    }

    public static boolean hasDataEntry(ResourceLocation lootTableId) {
        return DATA_TABLE.containsKey(lootTableId);
    }

    public static boolean hasFallbackEntry(ResourceLocation lootTableId) {
        return FALLBACK_TABLE.containsKey(lootTableId);
    }
}

