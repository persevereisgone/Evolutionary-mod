package com.muyun.evolutionary_mod.loot;

/**
 * 随机属性掉落系统（旧 Forge 版本实现）
 *
 * 注意：1.21.1 后 ItemStack NBT / Data Components 与 Loot 体系都有较大改动，
 * 旧版用 getOrCreateTag/hasTag 的实现已经不兼容。为保证项目先能正常编译、运行，
 * 这里暂时移除随机属性注入逻辑，后续可以基于 Data Components + 1.21.1 的 Loot API 重新设计。
 */
public class AttributeRollerEvents {
    // TODO: 以后如果要恢复随机属性掉落，请按 1.21.1 的 Data Components/Loot API 重新实现。
}

