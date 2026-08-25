package com.muyun.evolutionary_mod.system.task;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务数据容器（客户端内存态）。
 *
 * 当前由 {@link #sample()} 提供演示数据；后续接入真实任务数据源时，
 * 将此类替换为服务端 `SavedData` + 网络包同步的客户端镜像即可，界面代码不动。
 */
public final class TaskData {

    public static final int MAX_LOG = 60;

    private final List<TaskInfo> tasks = new ArrayList<>();
    private final List<TaskLogEntry> logs = new ArrayList<>();
    private boolean autoTrack = true;
    private boolean newTaskNotify = true;
    private int logTimeTick = 0;

    public List<TaskInfo> tasks() { return tasks; }
    public List<TaskLogEntry> logs() { return logs; }
    public boolean autoTrack() { return autoTrack; }
    public boolean newTaskNotify() { return newTaskNotify; }
    public void setAutoTrack(boolean v) { autoTrack = v; }
    public void setNewTaskNotify(boolean v) { newTaskNotify = v; }

    // ------------------------------------------------------------------ 统计
    public long countStatus(TaskStatus status) {
        return tasks.stream().filter(t -> t.status() == status).count();
    }

    public int completionRatePct() {
        long done = countStatus(TaskStatus.COMPLETED);
        return tasks.isEmpty() ? 0 : (int) Math.round(done * 100.0 / tasks.size());
    }

    /** 今日任务：模拟每天重置的日常任务数。 */
    public long countToday() {
        return tasks.stream().filter(t -> t.category() == TaskCategory.DAILY).count();
    }

    /** 推荐列表：全部处于可接取状态的未接取任务（最多 2 个）。 */
    public List<TaskInfo> recommendations() {
        return tasks.stream()
                .filter(t -> t.status() == TaskStatus.AVAILABLE)
                .limit(2)
                .toList();
    }

    /** 最近 n 条日志（新在前）。 */
    public List<TaskLogEntry> recentLogs(int n) {
        int from = Math.max(0, logs.size() - n);
        List<TaskLogEntry> out = new ArrayList<>(logs.subList(from, logs.size()));
        java.util.Collections.reverse(out);
        return out;
    }

    // ------------------------------------------------------------------ 操作
    public void log(String langKey, Object... args) {
        logs.add(new TaskLogEntry(logTimeTick++, Component.translatable(langKey, args)));
        if (logs.size() > MAX_LOG) {
            logs.remove(0);
        }
    }

    public void accept(TaskInfo task) {
        task.accept();
        if (autoTrack) {
            task.setTracking(true);
        }
        log("task.log.accepted", task.name());
    }

    public void track(TaskInfo task, boolean tracking) {
        task.setTracking(tracking);
        log(tracking ? "task.log.tracked" : "task.log.untracked", task.name());
    }

    public void abandon(TaskInfo task) {
        task.abandon();
        log("task.log.abandoned", task.name());
    }

    // ------------------------------------------------------------------ 示例数据
    public static TaskData sample() {
        TaskData data = new TaskData();

        data.tasks.add(new TaskInfo("origin",
                Component.translatable("task.sample.origin.name"),
                Component.translatable("task.sample.origin.desc"),
                TaskCategory.MAIN, TaskStatus.COMPLETED, 3, 3,
                new ItemStack(Items.IRON_SWORD), 50));
        data.tasks.add(new TaskInfo("awaken",
                Component.translatable("task.sample.awaken.name"),
                Component.translatable("task.sample.awaken.desc"),
                TaskCategory.MAIN, TaskStatus.IN_PROGRESS, 2, 5,
                new ItemStack(Items.DIAMOND), 120));
        data.tasks.add(new TaskInfo("destiny",
                Component.translatable("task.sample.destiny.name"),
                Component.translatable("task.sample.destiny.desc"),
                TaskCategory.MAIN, TaskStatus.AVAILABLE, 0, 3,
                new ItemStack(Items.NETHERITE_INGOT), 200));
        data.tasks.add(new TaskInfo("miner",
                Component.translatable("task.sample.miner.name"),
                Component.translatable("task.sample.miner.desc"),
                TaskCategory.SIDE, TaskStatus.IN_PROGRESS, 1, 4,
                new ItemStack(Items.EMERALD), 80));
        data.tasks.add(new TaskInfo("forest",
                Component.translatable("task.sample.forest.name"),
                Component.translatable("task.sample.forest.desc"),
                TaskCategory.SIDE, TaskStatus.AVAILABLE, 0, 2,
                new ItemStack(Items.GOLDEN_APPLE), 60));
        data.tasks.add(new TaskInfo("mineral_daily",
                Component.translatable("task.sample.mineral.name"),
                Component.translatable("task.sample.mineral.desc"),
                TaskCategory.DAILY, TaskStatus.IN_PROGRESS, 5, 16,
                new ItemStack(Items.IRON_INGOT, 2), 30));
        data.tasks.add(new TaskInfo("hunt_daily",
                Component.translatable("task.sample.hunt.name"),
                Component.translatable("task.sample.hunt.desc"),
                TaskCategory.DAILY, TaskStatus.COMPLETED, 10, 10,
                new ItemStack(Items.ROTTEN_FLESH, 4), 40));
        data.tasks.add(new TaskInfo("summer",
                Component.translatable("task.sample.summer.name"),
                Component.translatable("task.sample.summer.desc"),
                TaskCategory.EVENT, TaskStatus.AVAILABLE, 0, 1,
                new ItemStack(Items.FIREWORK_ROCKET, 8), 150));

        data.logs.add(new TaskLogEntry(data.logTimeTick++, Component.translatable("task.log.accepted",
                Component.translatable("task.sample.awaken.name"))));
        data.logs.add(new TaskLogEntry(data.logTimeTick++, Component.translatable("task.log.completed",
                Component.translatable("task.sample.hunt.name"), 40)));
        data.logs.add(new TaskLogEntry(data.logTimeTick++, Component.translatable("task.log.accepted",
                Component.translatable("task.sample.miner.name"))));
        data.logs.add(new TaskLogEntry(data.logTimeTick++, Component.translatable("task.log.completed",
                Component.translatable("task.sample.origin.name"), 50)));

        return data;
    }

    /** 世界动态（右栏）。 */
    public static final List<Component> WORLD_EVENTS = List.of(
            Component.translatable("task.event.1"),
            Component.translatable("task.event.2"),
            Component.translatable("task.event.3")
    );
}
