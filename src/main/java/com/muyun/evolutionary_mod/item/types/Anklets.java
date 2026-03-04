package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Anklet accessories - all quality tiers
 */
public class Anklets {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality anklets
    // public static final DeferredHolder<Item, Item> BROKEN_CHAIN_ANKLET = ITEMS.register("broken_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality anklets
    // public static final DeferredHolder<Item, Item> SIMPLE_CHAIN_ANKLET = ITEMS.register("simple_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality anklets
    // public static final DeferredHolder<Item, Item> SILVER_CHAIN_ANKLET = ITEMS.register("silver_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality anklets
    // public static final DeferredHolder<Item, Item> GOLDEN_CHAIN_ANKLET = ITEMS.register("golden_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality anklets
    // public static final DeferredHolder<Item, Item> DIAMOND_CHAIN_ANKLET = ITEMS.register("diamond_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality anklets
    // public static final DeferredHolder<Item, Item> DIVINE_CHAIN_ANKLET = ITEMS.register("divine_chain_anklet", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
