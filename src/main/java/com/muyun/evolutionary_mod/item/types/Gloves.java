package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;


/**
 * Glove accessories - all quality tiers
 */
public class Gloves {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality gloves
    // public static final DeferredHolder<Item, Item> BROKEN_LEATHER_GLOVES = ITEMS.register("broken_leather_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality gloves
    // public static final DeferredHolder<Item, Item> SIMPLE_LEATHER_GLOVES = ITEMS.register("simple_leather_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality gloves
    // public static final DeferredHolder<Item, Item> IRON_REINFORCED_GLOVES = ITEMS.register("iron_reinforced_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality gloves
    // public static final DeferredHolder<Item, Item> GOLDEN_GLOVES = ITEMS.register("golden_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality gloves
    // public static final DeferredHolder<Item, Item> DIAMOND_GLOVES = ITEMS.register("diamond_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality gloves
    // public static final DeferredHolder<Item, Item> DIVINE_GLOVES = ITEMS.register("divine_gloves", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
