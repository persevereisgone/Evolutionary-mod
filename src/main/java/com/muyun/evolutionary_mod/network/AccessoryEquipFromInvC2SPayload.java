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
 * C2S 数据包：客户端请求从背包特定格子装备饰品到指定槽位。
 */
public record AccessoryEquipFromInvC2SPayload(int accessorySlotIndex, int invIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AccessoryEquipFromInvC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "equip_from_inv_c2s"));

    public static final StreamCodec<FriendlyByteBuf, AccessoryEquipFromInvC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.accessorySlotIndex());
                        buf.writeInt(payload.invIndex());
                    },
                    buf -> new AccessoryEquipFromInvC2SPayload(buf.readInt(), buf.readInt())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            if (accessorySlotIndex() < 0 || accessorySlotIndex() >= AccessorySlot.count()) return;
            if (invIndex() < 0 || invIndex() >= serverPlayer.getInventory().getContainerSize()) return;

            AccessorySlot slot = AccessorySlot.values()[accessorySlotIndex()];
            ItemStack invStack = serverPlayer.getInventory().getItem(invIndex());
            if (invStack.isEmpty()) return;
            if (!AccessoryRules.isValidForSlot(slot, invStack)) return;

            PlayerAccessories data = serverPlayer.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
            ItemStack existing = data.getStack(slot);

            // 交换槽位物品与背包物品
            data.setStack(slot, invStack.copy());
            serverPlayer.getInventory().setItem(invIndex(), existing.copy());
        });
    }
}

