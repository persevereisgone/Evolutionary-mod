package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * Earring accessories - all quality tiers
 */
public class Earrings {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Broken quality earrings
    // public static final DeferredHolder<Item, Item> BROKEN_STUD_EARRING = ITEMS.register("broken_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    // Normal quality earrings
    // public static final DeferredHolder<Item, Item> SIMPLE_STUD_EARRING = ITEMS.register("simple_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    // Excellent quality earrings
    // public static final DeferredHolder<Item, Item> SILVER_STUD_EARRING = ITEMS.register("silver_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    // Epic quality earrings
    // public static final DeferredHolder<Item, Item> GOLDEN_STUD_EARRING = ITEMS.register("golden_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    // Legendary quality earrings
    // public static final DeferredHolder<Item, Item> DIAMOND_STUD_EARRING = ITEMS.register("diamond_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    // Mythic quality earrings
    // public static final DeferredHolder<Item, Item> DIVINE_STUD_EARRING = ITEMS.register("divine_stud_earring", () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
