package com.muyun.evolutionary_mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;

/**
 * 锻造台方块 - Forge Table Block
 *
 * 交互入口：右键打开锻造台菜单（强化 / 重锻 / 粉碎）。
 * 通过 S2C 包通知客户端打开，服务端同时创建菜单。
 */
public class AccessoriesTableBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(
        0.0D, 0.0D, 0.0D, // 左下角 (x1, y1, z1)
        1.0D, 0.9D, 1.0D  // 右上角 (x2, y2, z2) - 0.9D 是为了让碰撞箱略低于方块顶部
    );

    public AccessoriesTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ForgeTableBlockEntity forgeTable) {
            if (player instanceof ServerPlayer sp) {
                sp.openMenu(forgeTable);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ForgeTableBlockEntity forgeTable) {
                // 掉落方块内物品（保持锻造台数据不丢失，仅掉落槽位内容）
                var inv = forgeTable.getInventory();
                for (int i = 0; i < inv.getSlots(); i++) {
                    net.minecraft.world.item.ItemStack stack = inv.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
