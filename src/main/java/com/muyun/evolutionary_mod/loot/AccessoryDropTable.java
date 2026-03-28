package com.muyun.evolutionary_mod.loot;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * 饰品掉落配置表 - Accessory Drop Table
 *
 * 集中定义哪些战利品表来源（怪物/宝箱）可以掉落饰品，
 * 以及每个来源的掉落概率和可掉品质范围。
 *
 * 修改掉落规则只需修改本文件的 static 块，无需改动掉落逻辑。
 */
public class AccessoryDropTable {

    /**
     * 单条掉落配置
     *
     * @param dropChance     触发掉落的概率 [0.0, 1.0]
     * @param allowedRarities 可掉落的品质范围（按权重随机选）
     */
    public record DropConfig(float dropChance, AccessoryDropRarity[] allowedRarities) {}

    private static final Map<ResourceLocation, DropConfig> TABLE = new HashMap<>();

    static {
        // ---------------------------------------------------------------
        // 普通怪物 - Common Mobs (3% 概率，掉破损/普通)
        // ---------------------------------------------------------------
        register("minecraft:entities/zombie",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/zombie_villager", 0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/skeleton",        0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/creeper",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/spider",          0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/cave_spider",     0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/drowned",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/husk",            0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/stray",           0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/phantom",         0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/slime",           0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/magma_cube",      0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/ghast",           0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/zombified_piglin",0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/hoglin",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/zoglin",          0.03f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/silverfish",      0.01f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/endermite",       0.01f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:entities/vex",             0.02f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));

        // ---------------------------------------------------------------
        // 精英怪物 - Elite Mobs
        // ---------------------------------------------------------------
        register("minecraft:entities/witch",           0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/vindicator",      0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/pillager",        0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/ravager",         0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/evoker",          0.08f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:entities/blaze",           0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/wither_skeleton", 0.08f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:entities/piglin_brute",    0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/piglin",          0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/enderman",        0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/guardian",        0.05f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/shulker",         0.06f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/bogged",          0.04f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:entities/breeze",          0.07f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:entities/iron_golem",      0.04f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));

        // ---------------------------------------------------------------
        // 自定义精英怪物 - Custom Elite Mobs
        // ---------------------------------------------------------------
        register("evolutionary_mod:entities/cinder_skeleton",  0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/thunder_spider",   0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/shadow_stalker",   0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/frost_ghoul",      0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/ironclad_warrior", 0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/lava_shooter",     0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/storm_mage",       0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/abyss_lurker",     0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/holy_knight",      0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/chaos_shifter",    0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/time_warper",      0.18f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/soul_necromancer", 0.18f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/earth_guardian",   0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/wind_blade_hunter",0.12f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("evolutionary_mod:entities/life_leecher",     0.15f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));

        // ---------------------------------------------------------------
        // Boss 怪物 - Boss Mobs (必掉高品质)
        // ---------------------------------------------------------------
        register("minecraft:entities/elder_guardian",  0.80f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:entities/wither",          1.00f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        register("minecraft:entities/ender_dragon",    1.00f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY, AccessoryDropRarity.MYTHIC));

        // ---------------------------------------------------------------
        // 普通宝箱 - Common Chests
        // ---------------------------------------------------------------
        register("minecraft:chests/simple_dungeon",         0.30f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:chests/abandoned_mineshaft",    0.25f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_armorer",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_butcher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_cartographer",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_fisher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_fletcher",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_mason",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_shepherd",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_tannery",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_temple",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_toolsmith",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/village/village_weaponsmith",0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/igloo_chest",            0.20f, rarity(AccessoryDropRarity.BROKEN, AccessoryDropRarity.NORMAL));
        register("minecraft:chests/desert_pyramid",        0.35f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:chests/jungle_temple",         0.35f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:chests/buried_treasure",         0.4f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:chests/pillager_outpost",         0.3f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));
        register("minecraft:chests/trial_chambers/reward",         0.3f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT));

        // ---------------------------------------------------------------
        // 进阶宝箱 - Advanced Chests
        // ---------------------------------------------------------------
        register("minecraft:chests/stronghold_corridor",   0.40f, rarity(AccessoryDropRarity.NORMAL, AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:chests/stronghold_library",    0.50f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:chests/nether_bridge",         0.40f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));
        register("minecraft:chests/bastion_treasure",      0.60f, rarity(AccessoryDropRarity.EXCELLENT, AccessoryDropRarity.EPIC));

        // ---------------------------------------------------------------
        // 稀有宝箱 - Rare Chests
        // ---------------------------------------------------------------
        register("minecraft:chests/end_city_treasure",     0.50f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        register("minecraft:chests/woodland_mansion",      0.55f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
        register("minecraft:chests/ancient_city",          0.20f, rarity(AccessoryDropRarity.EPIC, AccessoryDropRarity.LEGENDARY));
    }

    // -----------------------------------------------------------------------
    // 工具方法
    // -----------------------------------------------------------------------

    private static void register(String path, float chance, AccessoryDropRarity[] rarities) {
        TABLE.put(ResourceLocation.parse(path), new DropConfig(chance, rarities));
    }

    /** 便捷构造品质数组 */
    public static AccessoryDropRarity[] rarity(AccessoryDropRarity... rarities) {
        return rarities;
    }

    /**
     * 根据战利品表 ID 查询掉落配置。
     *
     * @param lootTableId 战利品表 ResourceLocation
     * @return 对应配置，若不在表中则返回 null
     */
    public static DropConfig getConfig(ResourceLocation lootTableId) {
        return TABLE.get(lootTableId);
    }
}

