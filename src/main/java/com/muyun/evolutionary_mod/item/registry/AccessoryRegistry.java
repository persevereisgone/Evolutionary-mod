package com.muyun.evolutionary_mod.item.registry;

import net.neoforged.bus.api.IEventBus;

import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.item.types.Anklets;
import com.muyun.evolutionary_mod.item.types.Belts;
import com.muyun.evolutionary_mod.item.types.Bracelets;
import com.muyun.evolutionary_mod.item.types.Earrings;
import com.muyun.evolutionary_mod.item.types.Gloves;
import com.muyun.evolutionary_mod.item.types.Headwear;
import com.muyun.evolutionary_mod.item.types.Necklaces;
import com.muyun.evolutionary_mod.item.types.Rings;
import com.muyun.evolutionary_mod.item.types.Shoulders;

/**
 * 饰品物品统一注册器 - Unified Accessory Items Registry
 *
 * 统一管理所有饰品物品的注册，按照部位分类组织注册流程。
 * 提供统一的注册接口，便于扩展新的饰品部位。
 *
 * Unified registry for all accessory items, organizing registration process by accessory type.
 * Provides unified registration interface for easy extension of new accessory types.
 */
public class AccessoryRegistry {

    /**
     * Register all accessory items with the event bus
     */
    public static void registerAll(IEventBus eventBus) {
        // Register bracelets
        Bracelets.register(eventBus);

        // Register rings
        Rings.register(eventBus);

        // Register necklaces
        Necklaces.register(eventBus);

        // Register earrings
        Earrings.register(eventBus);

        // Register headwear
        Headwear.register(eventBus);

        // Register belts
        Belts.register(eventBus);

        // Register gloves
        Gloves.register(eventBus);

        // Register shoulders
        Shoulders.register(eventBus);

        // Register anklets
        Anklets.register(eventBus);

        // Register dragon set items
        DragonItems.register(eventBus);

        // Future accessory types can be added here:
        // Shoes.register(eventBus);
        // etc.
    }

    /**
     * Get the total number of registered accessory items
     */
    public static int getTotalAccessoryCount() {
        return Bracelets.ITEMS.getEntries().size() +
               Rings.ITEMS.getEntries().size() +
               Necklaces.ITEMS.getEntries().size() +
               Earrings.ITEMS.getEntries().size() +
               Headwear.ITEMS.getEntries().size() +
               Belts.ITEMS.getEntries().size() +
               Gloves.ITEMS.getEntries().size() +
               Shoulders.ITEMS.getEntries().size() +
               Anklets.ITEMS.getEntries().size();
    }
}
