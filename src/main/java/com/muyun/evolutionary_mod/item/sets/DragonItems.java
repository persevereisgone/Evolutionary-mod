package com.muyun.evolutionary_mod.item.sets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;


/**
 * 龙族套装饰品物品 - Dragon Set Accessories Items
 *
 * 包含龙族套装的所有部位饰品，用于激活套装效果。
 * 套装包含：戒指、项链、手镯、耳环、头饰、腰带
 *
 * Contains all pieces of the Dragon Set accessories for activating set bonuses.
 * Set includes: Ring, Necklace, Bracelet, Earring, Headwear, Belt
 */
public class DragonItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // 龙族套装饰品 - Dragon Set Accessories
    public static final DeferredHolder<Item, Item> DRAGON_RING = ITEMS.register("dragon_ring", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    
    public static final DeferredHolder<Item, Item> DRAGON_NECKLACE = ITEMS.register("dragon_necklace", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    
    public static final DeferredHolder<Item, Item> DRAGON_BRACELET = ITEMS.register("dragon_bracelet", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    
    public static final DeferredHolder<Item, Item> DRAGON_EARRING = ITEMS.register("dragon_earring", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    
    public static final DeferredHolder<Item, Item> DRAGON_HEADWEAR = ITEMS.register("dragon_headwear", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    
    public static final DeferredHolder<Item, Item> DRAGON_BELT = ITEMS.register("dragon_belt", 
        () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

