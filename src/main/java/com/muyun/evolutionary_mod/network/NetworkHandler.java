package com.muyun.evolutionary_mod.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络包注册处理器 - Network Handler
 *
 * NeoForge 1.21.1 使用 CustomPacketPayload + RegisterPayloadHandlersEvent 注册网络包。
 * 在 EvolutionaryMod 主类构造函数中通过 modEventBus.addListener(NetworkHandler::registerPayloads) 挂载。
 */
public class NetworkHandler {

    /**
     * 注册所有网络 Payload。由 mod 事件总线调用。
     */
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // S2C: 服务端同步饰品数据到客户端
        registrar.playToClient(
                AccessorySyncS2CPayload.TYPE,
                AccessorySyncS2CPayload.STREAM_CODEC,
                AccessorySyncS2CPayload::handle
        );

        // C2S: 客户端请求打开饰品菜单
        registrar.playToServer(
                AccessoryOpenMenuC2SPayload.TYPE,
                AccessoryOpenMenuC2SPayload.STREAM_CODEC,
                AccessoryOpenMenuC2SPayload::handle
        );

        // C2S: 客户端请求修改饰品槽位
        registrar.playToServer(
                AccessorySlotModifyC2SPayload.TYPE,
                AccessorySlotModifyC2SPayload.STREAM_CODEC,
                AccessorySlotModifyC2SPayload::handle
        );

        // C2S: 客户端请求自动装备主手物品
        registrar.playToServer(
                AccessoryAutoEquipC2SPayload.TYPE,
                AccessoryAutoEquipC2SPayload.STREAM_CODEC,
                AccessoryAutoEquipC2SPayload::handle
        );

        // C2S: 客户端请求从背包装备饰品
        registrar.playToServer(
                AccessoryEquipFromInvC2SPayload.TYPE,
                AccessoryEquipFromInvC2SPayload.STREAM_CODEC,
                AccessoryEquipFromInvC2SPayload::handle
        );
    }

    /**
     * 向指定玩家发送饰品同步数据包。
     */
    public static void sendAccessorySyncToPlayer(net.minecraft.server.level.ServerPlayer serverPlayer,
                                                  net.minecraft.nbt.CompoundTag nbt) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                serverPlayer, new AccessorySyncS2CPayload(nbt));
    }
}
