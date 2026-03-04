package com.muyun.evolutionary_mod.item.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    public static void register(IEventBus eventBus) {
        // Use the new organized registry system
        AccessoryRegistry.registerAll(eventBus);

        // Legacy registration for backward compatibility
        ITEMS.register(eventBus);
    }
}
