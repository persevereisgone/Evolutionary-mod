package com.muyun.evolutionary_mod.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.core.AccessorySlot;

/**
 * 玩家饰品能力 - Player Accessories Capability
 * 
 * Simple capability implementation that stores accessory ItemStacks per slot.
 * 简单的能力实现，按槽位存储饰品ItemStack。
 */
public class PlayerAccessories implements INBTSerializable<CompoundTag> {
    /**
     * NeoForge 1.21.1 使用新的能力系统（EntityCapability / Attachments）。
     * 这里先声明一个 EntityCapability 入口，后续需要在 {@code RegisterCapabilitiesEvent} 中为 Player 注册 provider。
     *
     * 注意：旧的 Forge CapabilityManager/CapabilityToken/LazyOptional 已不可用。
     */
    public static final EntityCapability<PlayerAccessories, Void> PLAYER_ACCESSORIES =
            EntityCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "player_accessories"),
                    PlayerAccessories.class
            );

    private final ItemStack[] stacks = new ItemStack[AccessorySlot.count()];

    public PlayerAccessories() {
        for (int i = 0; i < stacks.length; i++) stacks[i] = ItemStack.EMPTY;
    }

    public ItemStack getStack(AccessorySlot slot) {
        return stacks[slot.ordinal()];
    }

    public void setStack(AccessorySlot slot, ItemStack stack) {
        stacks[slot.ordinal()] = stack;
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
            // 1.21+: ItemStack 需要通过 HolderLookup.Provider 来序列化
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

