package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.network.AccessoryOpenMenuC2SPayload;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 客户端输入事件 - Client Input Events
 *
 * NeoForge 1.21.1:
 * - TickEvent.ClientTickEvent (Phase.END) -> ClientTickEvent.Post
 * - NetworkHandler.CHANNEL.sendToServer() -> PacketDistributor.sendToServer()
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID, value = Dist.CLIENT)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 打开饰品容器界面
        if (ClientHandlers.OPEN_ACCESSORIES != null && ClientHandlers.OPEN_ACCESSORIES.consumeClick()) {
            PacketDistributor.sendToServer(new AccessoryOpenMenuC2SPayload());
        }

        // 打开玩家属性面板（纯客户端界面）
        if (ClientHandlers.OPEN_PLAYER_ATTRIBUTES != null && ClientHandlers.OPEN_PLAYER_ATTRIBUTES.consumeClick()) {
            mc.setScreen(new PlayerAttributesScreen());
        }
    }
}
