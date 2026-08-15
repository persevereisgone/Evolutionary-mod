package com.muyun.evolutionary_mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.items.ItemStackHandler;

import com.muyun.evolutionary_mod.menu.ForgeTableMenu;

/**
 * 锻造台方块实体 - Forge Table Block Entity
 *
 * 持有 6 个操作槽：装备 / 类型精华 / 品阶碎片 / 重锻石 / 锁定石 / 属性精华。
 * 槽位内容通过 ForgeTableMenu 与客户端同步；实际操作在服务端由 C2S payload 驱动。
 */
public class ForgeTableBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_EQUIPMENT = 0;
    public static final int SLOT_ESSENCE = 1;        // 类型精华（accessory_essence）
    public static final int SLOT_SHARD = 2;          // 品阶碎片（rank_shard_*）
    public static final int SLOT_REROLL_STONE = 3;   // 普通/高级重锻石
    public static final int SLOT_LOCK_STONE = 4;     // 锁定重锻石
    public static final int SLOT_ATTR_ESSENCE = 5;   // 属性精华（attribute_essence_*）
    public static final int SLOT_COUNT = 6;

    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public ForgeTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGE_TABLE.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.evolutionary_mod.forge_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ForgeTableMenu(id, playerInventory, inventory);
    }

    // ------------------------------------------------------------------
    // NBT 持久化
    // ------------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }
}
