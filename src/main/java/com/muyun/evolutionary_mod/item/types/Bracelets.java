package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;

/**
 * 手镯饰品物品 - Bracelet Accessories Items
 *
 * 包含现有手镯饰品，为未来新品质等级预留扩展空间。
 * 现有手镯包括力量、速度、生命、幸运四种类型。
 *
 * Contains existing bracelet accessories, with expansion space reserved for future quality tiers.
 * Current bracelets include strength, speed, health, and luck types.
 */
public class Bracelets {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // Legacy bracelets
    public static final DeferredHolder<Item, Item> BRACELET_STRENGTH = ITEMS.register("strength_bracelet", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BRACELET_SPEED = ITEMS.register("speed_bracelet", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BRACELET_HP = ITEMS.register("hp_bracelet", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BRACELET_GUARD = ITEMS.register("guard_bracelet", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BRACELET_LUCK = ITEMS.register("luck_bracelet", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Future expansion space for new bracelet tiers
    // Broken quality bracelets
    // public static final DeferredHolder<Item, Item> BROKEN_xxx_BRACELET = ITEMS.register("broken_xxx_bracelet", () -> new Item(new Item.Properties()));

    // Normal quality bracelets
    // public static final DeferredHolder<Item, Item> xxx_BRACELET = ITEMS.register("xxx_bracelet", () -> new Item(new Item.Properties()));

    // Excellent quality bracelets
    // public static final DeferredHolder<Item, Item> xxx_BRACELET = ITEMS.register("xxx_bracelet", () -> new Item(new Item.Properties()));

    // Epic quality bracelets
    // public static final DeferredHolder<Item, Item> xxx_BRACELET = ITEMS.register("xxx_bracelet", () -> new Item(new Item.Properties()));

    // Legendary quality bracelets
    // public static final DeferredHolder<Item, Item> xxx_BRACELET = ITEMS.register("xxx_bracelet", () -> new Item(new Item.Properties()));

    // Mythic quality bracelets
    // public static final DeferredHolder<Item, Item> xxx_BRACELET = ITEMS.register("xxx_bracelet", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
