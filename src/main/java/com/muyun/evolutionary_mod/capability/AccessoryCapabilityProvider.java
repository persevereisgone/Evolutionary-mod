package com.muyun.evolutionary_mod.capability;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * 饰品能力相关事件 - Accessory capability related events
 *
 * 目前仅在玩家登录时，把已有的饰品数据同步给客户端。
 * Capability 本身的注册和存储由 {@link PlayerAccessories} 负责。
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AccessoryCapabilityProvider {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        // TODO: NeoForge 1.21.1 的实体能力系统已更换，需要用新的 attachments / EntityCapability API 重写饰品数据同步。
        // 这里先空实现以通过编译，后续可以在完成 PlayerAccessories 的新实现后再同步到客户端。
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        // 占位：目前不做任何同步逻辑
    }
}

