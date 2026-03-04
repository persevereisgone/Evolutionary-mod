package com.muyun.evolutionary_mod.network;

/**
 * C2S 数据包占位：请求打开饰品菜单
 *
 * 旧实现依赖 SimpleChannel + NetworkEvent + NetworkHooks.openScreen，
 * 这些在 NeoForge 1.21.1 中的用法已发生较大变化。
 *
 * 当前先保留一个空的占位类，以便后续基于 CustomPacketPayload 和
 * 1.21.1 的菜单打开流程重新实现。
 */
public class AccessoryOpenMenuC2SPacket {
    // TODO: 使用新的网络 API 重写：客户端发送 payload，服务端打开 AccessoryMenu。
}

