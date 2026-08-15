package com.muyun.evolutionary_mod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import com.muyun.evolutionary_mod.ModMenus;
import com.muyun.evolutionary_mod.block.ForgeTableBlockEntity;
import com.muyun.evolutionary_mod.item.forge.ForgeMaterials;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import com.muyun.evolutionary_mod.system.forge.ForgeSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 锻造台菜单 - Forge Table Menu
 *
 * 槽位布局（与 ForgeTableBlockEntity 槽位索引一致）：
 *   0 装备 / 1 类型精华 / 2 品阶碎片 / 3 重锻石 / 4 锁定石 / 5 属性精华
 *
 * 操作（强化/属性精华/重锻/粉碎）通过 C2S payload 触发，
 * 服务端读取槽位并调用 ForgeSystem，槽位变化经 AbstractContainerMenu 自动同步。
 */
public class ForgeTableMenu extends AbstractContainerMenu {

    private static final int FORGE_SLOT_START = 0;
    private static final int FORGE_SLOT_END = ForgeTableBlockEntity.SLOT_COUNT; // exclusive

    private final IItemHandler forgeInventory;
    private final Player player;

    public ForgeTableMenu(int id, Inventory playerInventory, IItemHandler forgeInventory) {
        super(ModMenus.FORGE_TABLE_MENU.get(), id);
        this.forgeInventory = forgeInventory;
        this.player = playerInventory.player;

        // 锻造台操作槽
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_EQUIPMENT, 62, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof AccessoryItem;
            }
        });
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_ESSENCE, 80, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ForgeMaterials.ACCESSORY_ESSENCE.get());
            }
        });
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_SHARD, 98, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ForgeSystem.isRankShard(stack.getItemHolder()
                        .unwrapKey().map(k -> k.location().toString()).orElse(""));
            }
        });
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_REROLL_STONE, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ForgeMaterials.REROLL_STONE.get())
                        || stack.is(ForgeMaterials.REROLL_STONE_ADVANCED.get());
            }
        });
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_LOCK_STONE, 98, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ForgeMaterials.REROLL_STONE_LOCK.get());
            }
        });
        this.addSlot(new SlotItemHandler(forgeInventory, ForgeTableBlockEntity.SLOT_ATTR_ESSENCE, 116, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                String id = stack.getItemHolder().unwrapKey().map(k -> k.location().toString()).orElse("");
                return id.startsWith("evolutionary_mod:attribute_essence_");
            }
        });

        // 玩家背包（3 行 × 9 列）
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    /** 客户端构造（由 MenuType 工厂调用）：使用空容器渲染槽位，内容由服务端同步。 */
    public ForgeTableMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, new net.neoforged.neoforge.items.ItemStackHandler(ForgeTableBlockEntity.SLOT_COUNT));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index >= FORGE_SLOT_START && index < FORGE_SLOT_END) {
                // 锻造台槽位 → 玩家背包
                if (!this.moveItemStackTo(stack, FORGE_SLOT_END, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 玩家背包 → 锻造台对应槽位
                if (!this.moveItemStackTo(stack, FORGE_SLOT_START, FORGE_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    /** 获取锻造台某槽物品（服务端操作读取）。 */
    public ItemStack getForgeSlot(int slot) {
        return forgeInventory.getStackInSlot(slot);
    }

    /** 设置锻造台某槽物品。 */
    public void setForgeSlot(int slot, ItemStack stack) {
        forgeInventory.extractItem(slot, forgeInventory.getStackInSlot(slot).getCount(), false);
        forgeInventory.insertItem(slot, stack, false);
    }

    public Player getPlayer() {
        return player;
    }
}
