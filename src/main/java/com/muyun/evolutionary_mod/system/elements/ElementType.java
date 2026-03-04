package com.muyun.evolutionary_mod.system.elements;

/**
 * 元素类型枚举 - Element Type Enum
 * 
 * 定义了所有可用的元素类型，每个元素都有独特的颜色和标识符。
 * Defines all available element types, each with unique color and identifier.
 */
public enum ElementType {
    FIRE("火", "fire", 0xFF4500),           // 火焰 - 持续伤害
    WATER("水", "water", 0x0066FF),        // 水 - 治疗/减速
    THUNDER("雷", "thunder", 0xFFD700),    // 雷电 - 连锁伤害/麻痹
    EARTH("土", "earth", 0x8B4513),        // 土 - 防御/控制
    WIND("风", "wind", 0xE0E0E0),          // 风 - 移动速度/扩散
    HOLY("神圣", "holy", 0xFFD700),        // 神圣 - 治疗/净化/对邪恶加成
    SHADOW("暗影", "shadow", 0x1A1A1A),    // 暗影 - 持续伤害/削弱
    PSYCHIC("精神", "psychic", 0x9370DB),  // 精神 - 控制/混乱
    LIFE("生命", "life", 0x32CD32),        // 生命 - 治疗/恢复
    NECROTIC("亡灵", "necrotic", 0x696969), // 亡灵 - 持续伤害/削弱
    SPACE("空间", "space", 0x4B0082),      // 空间 - 传送/范围伤害
    TIME("时间", "time", 0xFFD700),        // 时间 - 加速/减速/冷却
    CHAOS("混沌", "chaos", 0xFF00FF);      // 混沌 - 随机效果/高伤害
    
    public final String name;
    public final String id;
    public final int color;
    
    ElementType(String name, String id, int color) {
        this.name = name;
        this.id = id;
        this.color = color;
    }
    
    /**
     * 根据ID获取元素类型
     */
    public static ElementType fromId(String id) {
        for (ElementType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}

