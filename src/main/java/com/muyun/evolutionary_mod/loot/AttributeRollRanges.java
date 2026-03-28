package com.muyun.evolutionary_mod.loot;

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
}

