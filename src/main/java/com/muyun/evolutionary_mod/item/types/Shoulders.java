package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Shoulder accessories - all quality tiers
 */
public class Shoulders {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality shoulders
    // public static final DeferredHolder<Item, Item> BROKEN_CLOAK = ITEMS.register("broken_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality shoulders
    // public static final DeferredHolder<Item, Item> SIMPLE_CLOAK = ITEMS.register("simple_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality shoulders
    // public static final DeferredHolder<Item, Item> SILVER_CLOAK = ITEMS.register("silver_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality shoulders
    // public static final DeferredHolder<Item, Item> GOLDEN_CLOAK = ITEMS.register("golden_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality shoulders
    // public static final DeferredHolder<Item, Item> DIAMOND_CLOAK = ITEMS.register("diamond_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality shoulders
    // public static final DeferredHolder<Item, Item> DIVINE_CLOAK = ITEMS.register("divine_cloak", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
