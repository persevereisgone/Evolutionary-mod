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
 * C2S 数据包：客户端请求修改某个饰品槽位（装备/卸下）。
 */
public record AccessorySlotModifyC2SPayload(int slotIndex, byte op) implements CustomPacketPayload {

    public static final byte OP_SET_FROM_MAINHAND = 0;
    public static final byte OP_CLEAR_TO_MAINHAND = 1;

    public static final CustomPacketPayload.Type<AccessorySlotModifyC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "slot_modify_c2s"));

    public static final StreamCodec<FriendlyByteBuf, AccessorySlotModifyC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.slotIndex());
                        buf.writeByte(payload.op());
                    },
                    buf -> new AccessorySlotModifyC2SPayload(buf.readInt(), buf.readByte())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            if (slotIndex() < 0 || slotIndex() >= AccessorySlot.count()) return;

            AccessorySlot slot = AccessorySlot.values()[slotIndex()];
            PlayerAccessories data = serverPlayer.getData(EvolutionaryMod.PLAYER_ACCESSORIES);

            if (op() == OP_SET_FROM_MAINHAND) {
                ItemStack mainhand = serverPlayer.getMainHandItem();
                if (mainhand.isEmpty()) return;
                if (!AccessoryRules.isValidForSlot(slot, mainhand)) return;
                ItemStack existing = data.getStack(slot);
                data.setStack(slot, mainhand.copy());
                serverPlayer.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                        existing.isEmpty() ? ItemStack.EMPTY : existing.copy());
            } else if (op() == OP_CLEAR_TO_MAINHAND) {
                ItemStack slotStack = data.getStack(slot);
                if (slotStack.isEmpty()) return;
                if (serverPlayer.getMainHandItem().isEmpty()) {
                    serverPlayer.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, slotStack.copy());
                    data.setStack(slot, ItemStack.EMPTY);
                } else {
                    // 背包没有空间则直接丢出
                    if (!serverPlayer.getInventory().add(slotStack.copy())) {
                        serverPlayer.drop(slotStack.copy(), false);
                    }
                    data.setStack(slot, ItemStack.EMPTY);
                }
            }
        });
    }
}

