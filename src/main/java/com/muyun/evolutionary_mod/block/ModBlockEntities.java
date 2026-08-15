package com.muyun.evolutionary_mod.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EvolutionaryMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForgeTableBlockEntity>> FORGE_TABLE =
            BLOCK_ENTITIES.register("forge_table",
                    () -> BlockEntityType.Builder.of(ForgeTableBlockEntity::new,
                            ModBlocks.AccessoriesTable.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
