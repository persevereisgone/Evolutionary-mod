package com.muyun.evolutionary_mod.network;

/**
 * 网络处理器占位 - Network Handler Placeholder
 *
 * 旧版基于 Forge / NeoForge SimpleChannel 的实现在 1.21.1 中已经不兼容，
 * 推荐改用 1.21+ 的 CustomPacketPayload / 内置网络系统。
 *
 * 目前为了通过编译，先保留一个空的占位类，不做任何实际网络注册与发送。
 * 以后如果要恢复饰品同步、菜单打开等网络功能，可以在这里按 1.21.1 官方范例重写。
 */
public class NetworkHandler {
    // TODO: 使用 NeoForge 1.21.1 推荐的 CustomPacketPayload / PayloadChannel 重新实现网络通信。
}

