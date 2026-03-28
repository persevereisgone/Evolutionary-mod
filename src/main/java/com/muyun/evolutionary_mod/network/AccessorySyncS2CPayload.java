package com.muyun.evolutionary_mod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * S2C 数据包：服务端向客户端同步饰品数据。
 */
public record AccessorySyncS2CPayload(CompoundTag nbt) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AccessorySyncS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "sync_accessories_s2c"));

    public static final StreamCodec<FriendlyByteBuf, AccessorySyncS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeNbt(payload.nbt()),
                    buf -> new AccessorySyncS2CPayload(buf.readNbt())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 客户端处理：将收到的 NBT 反序列化到本地玩家的饰品数据中。
     */
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            try {
                player.getData(EvolutionaryMod.PLAYER_ACCESSORIES)
                        .deserializeNBT(player.registryAccess(), this.nbt());
            } catch (Exception e) {
                EvolutionaryMod.LOGGER.error("Failed to deserialize accessory sync packet: {}", e.getMessage());
            }
        });
    }
}

