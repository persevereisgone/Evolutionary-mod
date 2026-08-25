package com.muyun.evolutionary_mod.item.forge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;

/**
 * 锻造材料物品注册 - Forge Materials Registry
 *
 * 对应策划案 §3 材料体系，共 23 种：
 * 3 类型精华 + 7 品阶碎片 + 3 重锻石 + 10 属性精华。
 *
 * 物品均为普通可堆叠材料，非 AccessoryItem，不参与饰品槽位/掉落池。
 */
public class ForgeMaterials {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, EvolutionaryMod.MODID);

    // =====================
    // 3 类型精华（§3.1）
    // =====================
    public static final DeferredHolder<Item, Item> ACCESSORY_ESSENCE =
            ITEMS.register("accessory_essence", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> WEAPON_ESSENCE =
            ITEMS.register("weapon_essence", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ARMOR_ESSENCE =
            ITEMS.register("armor_essence", () -> new Item(new Item.Properties()));

    // =====================
    // 7 品阶碎片（§3.2）
    // =====================
    public static final DeferredHolder<Item, Item> RANK_SHARD_BROKEN =
            ITEMS.register("rank_shard_broken", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_NORMAL =
            ITEMS.register("rank_shard_normal", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_FINE =
            ITEMS.register("rank_shard_fine", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_EXCELLENT =
            ITEMS.register("rank_shard_excellent", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_EPIC =
            ITEMS.register("rank_shard_epic", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_LEGENDARY =
            ITEMS.register("rank_shard_legendary", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> RANK_SHARD_MYTHIC =
            ITEMS.register("rank_shard_mythic", () -> new Item(new Item.Properties()));

    // =====================
    // 3 重锻石（§3.3）
    // =====================
    public static final DeferredHolder<Item, Item> REROLL_STONE =
            ITEMS.register("reroll_stone", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> REROLL_STONE_ADVANCED =
            ITEMS.register("reroll_stone_advanced", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> REROLL_STONE_LOCK =
            ITEMS.register("reroll_stone_lock", () -> new Item(new Item.Properties()));

    // =====================
    // 10 属性精华（§3.5）
    // =====================
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_LIFE =
            ITEMS.register("attribute_essence_life", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_ATTACK =
            ITEMS.register("attribute_essence_attack", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_ARMOR =
            ITEMS.register("attribute_essence_armor", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_SPEED =
            ITEMS.register("attribute_essence_speed", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_LUCK =
            ITEMS.register("attribute_essence_luck", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_REGEN =
            ITEMS.register("attribute_essence_regen", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_PENETRATION =
            ITEMS.register("attribute_essence_penetration", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_CRIT_CHANCE =
            ITEMS.register("attribute_essence_crit_chance", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_CRIT_DAMAGE =
            ITEMS.register("attribute_essence_crit_damage", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ATTRIBUTE_ESSENCE_REDUCTION =
            ITEMS.register("attribute_essence_reduction", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}