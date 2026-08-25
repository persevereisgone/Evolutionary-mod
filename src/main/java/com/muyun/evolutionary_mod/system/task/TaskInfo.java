package com.muyun.evolutionary_mod.system.task;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * 单个任务信息。可变类，运行时由 TaskScreen 修改状态/进度/追踪。
 */
public final class TaskInfo {

    private final String id;
    private final Component name;
    private final Component description;
    private final TaskCategory category;
    private final ItemStack reward;
    private final int xpReward;
    private final int target;
    private TaskStatus status;
    private int progress;
    private boolean tracking;

    public TaskInfo(String id, Component name, Component description,
                    TaskCategory category, TaskStatus status,
                    int progress, int target, ItemStack reward, int xpReward) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
        this.progress = progress;
        this.target = target;
        this.reward = reward;
        this.xpReward = xpReward;
        this.tracking = false;
    }

    // --- getters ---
    public String id() { return id; }
    public Component name() { return name; }
    public Component description() { return description; }
    public TaskCategory category() { return category; }
    public TaskStatus status() { return status; }
    public int progress() { return progress; }
    public int target() { return target; }
    public ItemStack reward() { return reward; }
    public int xpReward() { return xpReward; }
    public boolean tracking() { return tracking; }

    public void setStatus(TaskStatus status) { this.status = status; }
    public void setProgress(int progress) { this.progress = Math.min(progress, target); }
    public void setTracking(boolean tracking) { this.tracking = tracking; }

    public float progressRatio() {
        return target <= 0 ? 0 : Math.min(1f, (float) progress / target);
    }

    /** 是否可用于按钮交互。 */
    public boolean canAccept() { return status == TaskStatus.AVAILABLE; }
    public boolean canTrack() { return status == TaskStatus.IN_PROGRESS; }
    public boolean canAbandon() {
        return status == TaskStatus.IN_PROGRESS || status == TaskStatus.FAILED;
    }

    /** 接取 → 进行中。 */
    public void accept() {
        this.status = TaskStatus.IN_PROGRESS;
        this.progress = 0;
    }

    /** 放弃 → 可接取，进度归零，取消追踪。 */
    public void abandon() {
        this.status = TaskStatus.AVAILABLE;
        this.progress = 0;
        this.tracking = false;
    }

    @Override
    public String toString() {
        return "TaskInfo{" + id + " " + status + " " + progress + "/" + target + "}";
    }
}