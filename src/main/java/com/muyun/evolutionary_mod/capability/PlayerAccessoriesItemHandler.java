package com.muyun.evolutionary_mod.capability;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.core.AccessoryRules;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;

/**
 * 玩家饰品物品处理器 - Player Accessories Item Handler
 * 
 * 实现IItemHandlerModifiable接口，用于处理饰品栏的物品操作。
 * Implements IItemHandlerModifiable interface for handling item operations in accessory slots.
 */
public class PlayerAccessoriesItemHandler implements IItemHandlerModifiable {
    private final PlayerAccessories cap;

    public PlayerAccessoriesItemHandler(PlayerAccessories cap) {
        this.cap = cap;
    }

    @Override
    public int getSlots() {
        return AccessorySlot.count();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return cap.getStack(AccessorySlot.values()[slot]);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        AccessorySlot aslot = AccessorySlot.values()[slot];
        if (!AccessoryRules.isValidForSlot(aslot, stack)) {
            return stack;
        }
        if (simulate) return ItemStack.EMPTY;
        cap.setStack(aslot, stack.copy());
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        AccessorySlot aslot = AccessorySlot.values()[slot];
        return AccessoryRules.isValidForSlot(aslot, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack cur = getStackInSlot(slot);
        if (cur.isEmpty()) return ItemStack.EMPTY;
        if (simulate) return cur.copy();
        cap.setStack(AccessorySlot.values()[slot], ItemStack.EMPTY);
        return cur.copy();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        AccessorySlot aslot = AccessorySlot.values()[slot];
        if (!AccessoryRules.isValidForSlot(aslot, stack)) {
            // ignore invalid assignments
            return;
        }
        cap.setStack(aslot, stack.copy());
    }
}

