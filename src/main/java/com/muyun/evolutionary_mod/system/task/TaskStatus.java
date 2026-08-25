package com.muyun.evolutionary_mod.system.task;

import net.minecraft.network.chat.Component;

/**
 * 任务状态。
 * 设计稿配色：可接取(灰) / 进行中(蓝青) / 已完成(绿) / 失败(红，预留)。
 */
public enum TaskStatus {

    AVAILABLE("task.status.available"),
    IN_PROGRESS("task.status.in_progress"),
    COMPLETED("task.status.completed"),
    FAILED("task.status.failed");

    private final String langKey;

    TaskStatus(String langKey) {
        this.langKey = langKey;
    }

    public Component displayName() {
        return Component.translatable(this.langKey);
    }
}
