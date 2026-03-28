package com.muyun.evolutionary_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessoryRules;
import com.muyun.evolutionary_mod.core.AccessorySlot;

/**
 * C2S 数据包：客户端请求自动装备主手物品到第一个合适的空饰品槽位。
 */
public record AccessoryAutoEquipC2SPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AccessoryAutoEquipC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "auto_equip_c2s"));

    public static final StreamCodec<FriendlyByteBuf, AccessoryAutoEquipC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> { /* 无数据 */ },
                    buf -> new AccessoryAutoEquipC2SPayload()
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            ItemStack mainhand = serverPlayer.getMainHandItem();
            if (mainhand.isEmpty()) return;

            PlayerAccessories data = serverPlayer.getData(EvolutionaryMod.PLAYER_ACCESSORIES);

            // 找到第一个合适的空槽位
            for (AccessorySlot slot : AccessorySlot.values()) {
                if (AccessoryRules.isValidForSlot(slot, mainhand) && data.getStack(slot).isEmpty()) {
                    data.setStack(slot, mainhand.copy());
                    serverPlayer.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    break;
                }
            }
        });
    }
}
