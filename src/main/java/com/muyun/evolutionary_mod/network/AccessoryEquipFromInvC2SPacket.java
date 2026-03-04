package com.muyun.evolutionary_mod.network;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.core.AccessoryRules;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;

import java.util.function.Supplier;

public class AccessoryEquipFromInvC2SPacket {
    private final int accessorySlotIndex;
    private final int invIndex;

    public AccessoryEquipFromInvC2SPacket(int accessorySlotIndex, int invIndex) {
        this.accessorySlotIndex = accessorySlotIndex;
        this.invIndex = invIndex;
    }

    public static void encode(AccessoryEquipFromInvC2SPacket pkt, net.minecraft.network.FriendlyByteBuf buf) {
        buf.writeInt(pkt.accessorySlotIndex);
        buf.writeInt(pkt.invIndex);
    }

    public static AccessoryEquipFromInvC2SPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new AccessoryEquipFromInvC2SPacket(buf.readInt(), buf.readInt());
    }

}


