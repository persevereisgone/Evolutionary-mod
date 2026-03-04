package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Necklace accessories - all quality tiers
 */
public class Necklaces {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality necklaces
    // public static final DeferredHolder<Item, Item> BROKEN_CHAIN_NECKLACE = ITEMS.register("broken_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality necklaces
    // public static final DeferredHolder<Item, Item> SIMPLE_CHAIN_NECKLACE = ITEMS.register("simple_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality necklaces
    // public static final DeferredHolder<Item, Item> SILVER_CHAIN_NECKLACE = ITEMS.register("silver_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality necklaces
    // public static final DeferredHolder<Item, Item> GOLDEN_CHAIN_NECKLACE = ITEMS.register("golden_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality necklaces
    // public static final DeferredHolder<Item, Item> DIAMOND_CHAIN_NECKLACE = ITEMS.register("diamond_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality necklaces
    // public static final DeferredHolder<Item, Item> DIVINE_CHAIN_NECKLACE = ITEMS.register("divine_chain_necklace", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
