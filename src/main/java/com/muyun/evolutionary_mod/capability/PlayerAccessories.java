package com.muyun.evolutionary_mod.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.common.util.INBTSerializable;

import com.muyun.evolutionary_mod.core.AccessorySlot;

/**
 * 玩家饰品数据 - Player Accessories Data
 *
 * 存储玩家的饰品槽位数据。
 * NeoForge 1.21.1 使用 AttachmentType 系统（而非旧版 Capability），
 * AttachmentType 的注册在 {@link com.muyun.evolutionary_mod.EvolutionaryMod} 主类中完成。
 */
public class PlayerAccessories implements INBTSerializable<CompoundTag> {

    private final ItemStack[] stacks = new ItemStack[AccessorySlot.count()];

    public PlayerAccessories() {
        for (int i = 0; i < stacks.length; i++) stacks[i] = ItemStack.EMPTY;
    }

    public ItemStack getStack(AccessorySlot slot) {
        return stacks[slot.ordinal()];
    }

    public void setStack(AccessorySlot slot, ItemStack stack) {
        stacks[slot.ordinal()] = stack == null ? ItemStack.EMPTY : stack;
    }

    public void copyFrom(PlayerAccessories other) {
        for (int i = 0; i < stacks.length; i++) {
            this.stacks[i] = other.stacks[i].copy();
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStack stack : stacks) {
            Tag stackTag = stack.saveOptional(provider);
            if (stackTag == null) {
                stackTag = new CompoundTag();
            }
            list.add(stackTag);
        }
        nbt.put("stacks", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag list = nbt.getList("stacks", Tag.TAG_COMPOUND);
        for (int i = 0; i < stacks.length; i++) {
            if (i < list.size()) {
                stacks[i] = ItemStack.parseOptional(provider, list.getCompound(i));
            } else {
                stacks[i] = ItemStack.EMPTY;
            }
        }
    }
}
