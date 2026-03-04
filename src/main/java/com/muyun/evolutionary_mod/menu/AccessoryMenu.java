package com.muyun.evolutionary_mod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import com.muyun.evolutionary_mod.ModMenus;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.capability.PlayerAccessoriesItemHandler;
import com.muyun.evolutionary_mod.client.LayoutConfig;
import com.muyun.evolutionary_mod.core.AccessoryRules;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.system.effects.AccessoryEffectCalculator;

public class AccessoryMenu extends AbstractContainerMenu {
    private final Player player;

    public AccessoryMenu(int id, Inventory playerInventory, PlayerAccessories cap) {
        super(ModMenus.ACCESSORY_MENU.get(), id);
        this.player = playerInventory.player;

        IItemHandler handler = new PlayerAccessoriesItemHandler(cap);

        // Load layout: use bundled assets layout to ensure consistent server-side slot positions
        LayoutConfig.Layout layout = LayoutConfig.defaultLayout();
        try {
            try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("assets/evolutionary_mod/layout/accessories_layout.json")) {
                if (is != null) {
                    String bundledJson = new String(is.readAllBytes());
                    layout = LayoutConfig.loadFromString(bundledJson);
                }
            }
        } catch (Exception ignored) {}

        // small global pixel offset applied at runtime (does not change JSON)
        final int pixelOffsetX = 1;
        final int pixelOffsetY = 1;

        for (int i = 0; i < AccessorySlot.count(); i++) {
            AccessorySlot aslot = AccessorySlot.values()[i];
            int[] pos = layout.slots.get(aslot);
            int x = 8;
            int y = 18;
            if (pos != null && pos.length >= 2) {
                x = pos[0];
                y = pos[1];
            }
            // apply runtime offset so JSON remains unchanged
            this.addSlot(new SlotItemHandler(handler, i, x + pixelOffsetX, y + pixelOffsetY));
        }

        // add player inventory slots (layout-driven) using layout cellSize and gap
        int startX = layout.inventoryStartX;
        int startY = layout.inventoryStartY;
        int step = layout.cellSize + layout.gap;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, col + row * 9 + 9, startX + col * step + pixelOffsetX, startY + row * step + pixelOffsetY));
            }
        }
        // hotbar
        int hotbarY = startY + 3 * step + pixelOffsetY;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new net.minecraft.world.inventory.Slot(playerInventory, col, startX + col * step + pixelOffsetX, hotbarY));
        }
    }


    // client-side constructor used by MenuType factory (reads nothing from buffer)
//    public AccessoryMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
//        this(id, playerInventory);
//    }

    // Two-arg constructor used by MenuType factory in some mappings
    //public AccessoryMenu(int id, Inventory playerInventory) {
        //this(id, playerInventory);
    //}

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    /**
     * 当菜单关闭时，重新计算并应用所有饰品属性
     * When menu is closed, recalculate and apply all accessory attributes
     */
    @Override
    public void removed(Player player) {
        super.removed(player);

        // 只在服务端执行属性重新计算
        // Only recalculate attributes on server side
        if (!player.level().isClientSide) {
            recalculateAllAccessoryEffects(player);
        }
    }

    /**
     * 重新计算并应用所有饰品属性效果
     * Recalculate and apply all accessory attribute effects
     * 
     * 这个方法会：
     * 1. 重置所有饰品属性修饰符
     * 2. 根据当前装备的饰品重新计算并应用所有属性
     * 3. 应用套装效果
     * 
     * This method will:
     * 1. Reset all accessory attribute modifiers
     * 2. Recalculate and apply all attributes based on currently equipped accessories
     * 3. Apply set bonuses
     */
    private static void recalculateAllAccessoryEffects(Player player) {
        // 使用统一的饰品效果计算器来重新计算并应用所有效果
        // Use unified accessory effect calculator to recalculate and apply all effects
        AccessoryEffectCalculator.calculateAndApplyAllEffects(player);
    }

    /**
     * Get the slot type prefix for an item (e.g., "RING", "BRACELET", "HEAD", etc.)
     * Returns null if the item doesn't match any slot type
     */
    private String getSlotTypePrefix(ItemStack stack) {
        if (stack.isEmpty()) return null;

        // Check each slot type to find which type this item belongs to
        for (AccessorySlot slotType : AccessorySlot.values()) {
            if (AccessoryRules.isValidForSlot(slotType, stack)) {
                // Extract the prefix (e.g., "RING" from "RING_1", "HEAD" from "HEAD")
                String slotName = slotType.name();
                if (slotName.contains("_")) {
                    return slotName.split("_")[0]; // "RING", "BRACELET", etc.
                } else {
                    return slotName; // "HEAD", "NECKLACE", "BELT"
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        net.minecraft.world.inventory.Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int accessoryCount = AccessorySlot.count();
            // if clicked slot is accessory slot -> move to player inventory
            if (index < accessoryCount) {
                if (!this.moveItemStackTo(stack, accessoryCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.setChanged();
            } else {
                // clicked in player inventory: try to move to first appropriate empty accessory slot
                // First, determine which slot type this item belongs to
                String slotTypePrefix = getSlotTypePrefix(stack);
                if (slotTypePrefix == null) {
                    // Item doesn't match any slot type, cannot be added
                    return ItemStack.EMPTY;
                }

                // Use layout order to find slots (matching visual order in GUI)
                AccessorySlot[] slotOrder = {
                    AccessorySlot.HEAD,
                    AccessorySlot.EARRING_1,
                    AccessorySlot.EARRING_2,
                    AccessorySlot.NECKLACE,
                    AccessorySlot.GLOVE_1,
                    AccessorySlot.GLOVE_2,
                    AccessorySlot.BRACELET_1,
                    AccessorySlot.BRACELET_2,
                    AccessorySlot.RING_1,
                    AccessorySlot.RING_2,
                    AccessorySlot.RING_3,
                    AccessorySlot.RING_4,
                    AccessorySlot.BELT,
                    AccessorySlot.BOOT_1,
                    AccessorySlot.BOOT_2,
                    AccessorySlot.ACCESSORY_1,
                    AccessorySlot.ACCESSORY_2,
                    AccessorySlot.ACCESSORY_3
                };

                boolean moved = false;

                // Only search within slots of the matching type
                for (AccessorySlot slotType : slotOrder) {
                    // Check if this slot matches the item's type
                    String currentSlotPrefix = slotType.name().contains("_")
                        ? slotType.name().split("_")[0]
                        : slotType.name();

                    if (currentSlotPrefix.equals(slotTypePrefix)) {
                        int slotIndex = slotType.ordinal();
                        if (slotIndex < accessoryCount) {
                            net.neoforged.neoforge.items.SlotItemHandler accSlot = (net.neoforged.neoforge.items.SlotItemHandler) this.slots.get(slotIndex);
                            if (accSlot.mayPlace(stack) && accSlot.getItem().isEmpty()) {
                                accSlot.set(stack.copy());
                                slot.set(ItemStack.EMPTY);
                                moved = true;
                                break;
                            }
                        }
                    }
                }

                // If no empty slot of the matching type was found, cannot add
                if (!moved) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}


