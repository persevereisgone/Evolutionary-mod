package com.muyun.evolutionary_mod.capability;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.network.AccessorySyncS2CPayload;

/**
 * 饰品数据相关事件监听器
 *
 * NeoForge 1.21.1：使用 AttachmentType + getData() 替代旧版 Capability 体系。
 * 玩家登录/重生时同步饰品数据到客户端。
 * 玩家死亡重生时通过 PlayerCloneEvent 保留饰品数据。
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AccessoryCapabilityProvider {

    /**
     * 玩家登录时，将服务端饰品数据同步到客户端。
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        syncToClient(serverPlayer);
    }

    /**
     * 玩家重生时重新同步（例如死亡后复活）。
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        syncToClient(serverPlayer);
    }

    /**
     * 玩家死亡重生时复制饰品数据（保留物品）。
     */
    @SubscribeEvent
    public static void onPlayerClone(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // 从旧玩家实体复制饰品数据到新玩家实体
            PlayerAccessories oldData = event.getOriginal().getData(EvolutionaryMod.PLAYER_ACCESSORIES);
            event.getEntity().getData(EvolutionaryMod.PLAYER_ACCESSORIES).copyFrom(oldData);
        }
    }

    /**
     * 向客户端同步饰品 NBT 数据。
     */
    private static void syncToClient(ServerPlayer serverPlayer) {
        try {
            PlayerAccessories data = serverPlayer.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
            net.minecraft.nbt.CompoundTag nbt = data.serializeNBT(serverPlayer.registryAccess());
            PacketDistributor.sendToPlayer(serverPlayer, new AccessorySyncS2CPayload(nbt));
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("Failed to sync accessory data to client: {}", e.getMessage());
        }
    }
}
