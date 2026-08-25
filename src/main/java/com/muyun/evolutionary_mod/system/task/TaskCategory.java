package com.muyun.evolutionary_mod.system.task;

import net.minecraft.network.chat.Component;

/**
 * 任务分类。
 * 页签与任务过滤共用：全部(ALL 由 UI 层实现) + MAIN/SIDE/DAILY/EVENT。
 */
public enum TaskCategory {

    MAIN("task.tab.main"),
    SIDE("task.tab.side"),
    DAILY("task.tab.daily"),
    EVENT("task.tab.event");

    private final String langKey;

    TaskCategory(String langKey) {
        this.langKey = langKey;
    }

    public Component displayName() {
        return Component.translatable(this.langKey);
    }
}
