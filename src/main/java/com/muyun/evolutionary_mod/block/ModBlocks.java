package com.muyun.evolutionary_mod.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.registry.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, EvolutionaryMod.MODID);

    public static final DeferredHolder<Block, Block> AccessoriesTable =
            registerBlock("accessories_table", () -> new AccessoriesTableBlock(BlockBehaviour.Properties.of().strength(2.0f, 6.0f)));

    private static <T extends Block> Void registerBlockItems(String name, DeferredHolder<Block, T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return null;
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> blockObject = BLOCKS.register(name, block);
        registerBlockItems(name, blockObject);
        return blockObject;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
