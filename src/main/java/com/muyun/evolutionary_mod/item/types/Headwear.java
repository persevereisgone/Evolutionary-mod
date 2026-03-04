package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Headwear accessories - all quality tiers
 */
public class Headwear {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality headwear
    // public static final DeferredHolder<Item, Item> BROKEN_HEADBAND = ITEMS.register("broken_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality headwear
    // public static final DeferredHolder<Item, Item> SIMPLE_HEADBAND = ITEMS.register("simple_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality headwear
    // public static final DeferredHolder<Item, Item> SILVER_HEADBAND = ITEMS.register("silver_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality headwear
    // public static final DeferredHolder<Item, Item> GOLDEN_HEADBAND = ITEMS.register("golden_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality headwear
    // public static final DeferredHolder<Item, Item> DIAMOND_HEADBAND = ITEMS.register("diamond_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality headwear
    // public static final DeferredHolder<Item, Item> DIVINE_HEADBAND = ITEMS.register("divine_headband", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
