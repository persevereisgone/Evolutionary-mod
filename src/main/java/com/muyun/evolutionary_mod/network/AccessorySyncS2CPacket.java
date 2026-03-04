package com.muyun.evolutionary_mod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * 饰品同步数据包 - Accessory Sync S2C Packet
 * 
 * 从服务器向客户端同步饰品数据
 * 包含详细的错误处理和日志记录
 * 
 * Synchronizes accessory data from server to client
 * Includes detailed error handling and logging
 */
public class AccessorySyncS2CPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CompoundTag nbt;

    /**
     * 构造饰品同步数据包
     * 
     * @param nbt 饰品数据
     */
    public AccessorySyncS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    /**
     * 编码数据包
     * 
     * @param pkt 数据包
     * @param buf 字节缓冲区
     */
    public static void encode(AccessorySyncS2CPacket pkt, FriendlyByteBuf buf) {
        try {
            buf.writeNbt(pkt.nbt);
        } catch (Exception e) {
            LOGGER.error("Failed to encode accessory sync packet: {}", e.getMessage());
        }
    }

    /**
     * 解码数据包
     * 
     * @param buf 字节缓冲区
     * @return 饰品同步数据包
     */
    public static AccessorySyncS2CPacket decode(FriendlyByteBuf buf) {
        try {
            return new AccessorySyncS2CPacket(buf.readNbt());
        } catch (Exception e) {
            LOGGER.error("Failed to decode accessory sync packet: {}", e.getMessage());
            return new AccessorySyncS2CPacket(new CompoundTag());
        }
    }

    /**
     * 处理数据包
     * 
     * @param pkt 数据包
     * @param ctxSupplier 上下文提供者
     */


    /**
     * 获取饰品数据
     * 
     * @return 饰品数据
     */
    public CompoundTag getNbt() {
        return nbt;
    }
}


