package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Belt accessories - all quality tiers
 */
public class Belts {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality belts
    // public static final DeferredHolder<Item, Item> BROKEN_LEATHER_BELT = ITEMS.register("broken_leather_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality belts
    // public static final DeferredHolder<Item, Item> SIMPLE_LEATHER_BELT = ITEMS.register("simple_leather_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality belts
    // public static final DeferredHolder<Item, Item> IRON_BUCKLE_BELT = ITEMS.register("iron_buckle_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality belts
    // public static final DeferredHolder<Item, Item> GOLDEN_BUCKLE_BELT = ITEMS.register("golden_buckle_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality belts
    // public static final DeferredHolder<Item, Item> DIAMOND_BUCKLE_BELT = ITEMS.register("diamond_buckle_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality belts
    // public static final DeferredHolder<Item, Item> DIVINE_BUCKLE_BELT = ITEMS.register("divine_buckle_belt", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
