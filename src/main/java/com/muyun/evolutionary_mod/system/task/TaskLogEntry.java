package com.muyun.evolutionary_mod.system.task;

import net.minecraft.network.chat.Component;

/**
 * 一条任务日志。timeTick 为记录时的游戏刻（仅用于顺序展示，界面只显示消息）。
 */
public record TaskLogEntry(int timeTick, Component message) {
}
