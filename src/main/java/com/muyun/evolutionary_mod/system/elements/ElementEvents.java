package com.muyun.evolutionary_mod.system.elements;

/**
 * 元素系统事件（旧实现占位）
 *
 * 原先这里监听 Tick 和伤害事件来驱动元素系统。由于 NeoForge 1.21.1 的
 * Tick / Living 事件 API 与旧版差异较大，且元素逻辑目前尚未完全迁移，
 * 为保证项目先能正常编译，这里先去掉旧版事件实现，仅保留空类占位。
 */
public class ElementEvents {
    // TODO: 以后按 NeoForge 1.21.1 的事件体系重写元素 Tick / 伤害处理逻辑。
}
