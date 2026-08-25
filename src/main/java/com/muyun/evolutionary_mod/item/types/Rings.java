package com.muyun.evolutionary_mod.item.types;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;

/**
 * 戒指饰品物品 - Ring Accessories Items
 *
 * 包含所有品质等级的戒指饰品。品阶共 7 档（残破 / 普通 / 优秀 / 精良 / 史诗 / 传说 / 至臻），
 * 其中残破档仅 6 个系列（愈合 healing、破甲 armor_breaker 无残破档），
 * 普通及以后各档均为 8 个系列。
 *
 * Contains ring accessories of all quality tiers, from broken to mythic (7 tiers total).
 * Broken tier only covers 6 series (healing & armor breaker have no broken tier);
 * from Normal up, all 8 series are present.
 */
public class Rings {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, EvolutionaryMod.MODID);

    // Broken quality rings
    public static final DeferredHolder<Item, Item> BROKEN_LIFE_ESSENCE_RING = ITEMS.register("broken_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BROKEN_BATTLE_POWER_RING = ITEMS.register("broken_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BROKEN_IRON_SHIELD_RING = ITEMS.register("broken_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BROKEN_GALE_RING = ITEMS.register("broken_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BROKEN_GOOD_FORTUNE_RING = ITEMS.register("broken_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BROKEN_SHARP_EDGE_RING = ITEMS.register("broken_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Normal quality rings
    public static final DeferredHolder<Item, Item> LIFE_ESSENCE_RING = ITEMS.register("life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> BATTLE_POWER_RING = ITEMS.register("battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> IRON_SHIELD_RING = ITEMS.register("iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GALE_RING = ITEMS.register("gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> GOOD_FORTUNE_RING = ITEMS.register("good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HEALING_RING = ITEMS.register("healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> SHARP_EDGE_RING = ITEMS.register("sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARMOR_BREAKER_RING = ITEMS.register("armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Fine quality rings
    public static final DeferredHolder<Item, Item> FINE_LIFE_ESSENCE_RING = ITEMS.register("fine_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_BATTLE_POWER_RING = ITEMS.register("fine_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_IRON_SHIELD_RING = ITEMS.register("fine_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_GALE_RING = ITEMS.register("fine_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_GOOD_FORTUNE_RING = ITEMS.register("fine_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_HEALING_RING = ITEMS.register("fine_healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_SHARP_EDGE_RING = ITEMS.register("fine_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> FINE_ARMOR_BREAKER_RING = ITEMS.register("fine_armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Excellent quality rings
    public static final DeferredHolder<Item, Item> EXCELLENT_LIFE_ESSENCE_RING = ITEMS.register("excellent_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_BATTLE_POWER_RING = ITEMS.register("excellent_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_SHARP_EDGE_RING = ITEMS.register("excellent_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_IRON_SHIELD_RING = ITEMS.register("excellent_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_GALE_RING = ITEMS.register("excellent_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_GOOD_FORTUNE_RING = ITEMS.register("excellent_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_HEALING_RING = ITEMS.register("excellent_healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EXCELLENT_ARMOR_BREAKER_RING = ITEMS.register("excellent_armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Epic quality rings
    public static final DeferredHolder<Item, Item> EPIC_LIFE_ESSENCE_RING = ITEMS.register("epic_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_BATTLE_POWER_RING = ITEMS.register("epic_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_IRON_SHIELD_RING = ITEMS.register("epic_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_GALE_RING = ITEMS.register("epic_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_GOOD_FORTUNE_RING = ITEMS.register("epic_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_HEALING_RING = ITEMS.register("epic_healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_SHARP_EDGE_RING = ITEMS.register("epic_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> EPIC_ARMOR_BREAKER_RING = ITEMS.register("epic_armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Legendary quality rings
    public static final DeferredHolder<Item, Item> LEGENDARY_LIFE_ESSENCE_RING = ITEMS.register("legendary_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_BATTLE_POWER_RING = ITEMS.register("legendary_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_IRON_SHIELD_RING = ITEMS.register("legendary_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_GALE_RING = ITEMS.register("legendary_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_GOOD_FORTUNE_RING = ITEMS.register("legendary_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_HEALING_RING = ITEMS.register("legendary_healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_SHARP_EDGE_RING = ITEMS.register("legendary_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> LEGENDARY_ARMOR_BREAKER_RING = ITEMS.register("legendary_armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));

    // Mythic quality rings
    public static final DeferredHolder<Item, Item> MYTHIC_LIFE_ESSENCE_RING = ITEMS.register("mythic_life_essence_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_BATTLE_POWER_RING = ITEMS.register("mythic_battle_power_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_IRON_SHIELD_RING = ITEMS.register("mythic_iron_shield_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_GALE_RING = ITEMS.register("mythic_gale_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_GOOD_FORTUNE_RING = ITEMS.register("mythic_good_fortune_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_HEALING_RING = ITEMS.register("mythic_healing_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_SHARP_EDGE_RING = ITEMS.register("mythic_sharp_edge_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> MYTHIC_ARMOR_BREAKER_RING = ITEMS.register("mythic_armor_breaker_ring", () -> new AccessoryItem(new Item.Properties().stacksTo(1)));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
