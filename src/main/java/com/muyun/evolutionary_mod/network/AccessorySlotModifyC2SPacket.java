package com.muyun.evolutionary_mod.network;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.core.AccessoryRules;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class AccessorySlotModifyC2SPacket {
    public static final byte OP_SET_FROM_MAINHAND = 0;
    public static final byte OP_CLEAR_TO_MAINHAND = 1;

    private final int slotIndex;
    private final byte op;

    public AccessorySlotModifyC2SPacket(int slotIndex, byte op) {
        this.slotIndex = slotIndex;
        this.op = op;
    }

    public static void encode(AccessorySlotModifyC2SPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.slotIndex);
        buf.writeByte(pkt.op);
    }

    public static AccessorySlotModifyC2SPacket decode(FriendlyByteBuf buf) {
        return new AccessorySlotModifyC2SPacket(buf.readInt(), buf.readByte());
    }

}


