package com.muyun.evolutionary_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.menu.AccessoryMenu;

/**
 * C2S 数据包：客户端请求服务端打开饰品菜单。
 */
public record AccessoryOpenMenuC2SPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AccessoryOpenMenuC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "open_accessory_menu_c2s"));

    public static final StreamCodec<FriendlyByteBuf, AccessoryOpenMenuC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> { /* 无数据 */ },
                    buf -> new AccessoryOpenMenuC2SPayload()
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 服务端处理：在服务端为该玩家打开饰品菜单。
     */
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.evolutionary_mod.accessories");
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    PlayerAccessories data = serverPlayer.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
                    return new AccessoryMenu(syncId, inv, data);
                }
            });
        });
    }
}

