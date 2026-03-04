package com.muyun.evolutionary_mod.item.tabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.block.ModBlocks;
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


public class ModCreativeModelTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EvolutionaryMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ACCESSORIES_TAB =
            CREATIVE_MODE_TAB.register("accessories_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Bracelets.BRACELET_STRENGTH.get()))
                    .title(Component.translatable("itemGroup.accessories_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // 手镯：目前没有分品阶，保持原顺序
                        pOutput.accept(Bracelets.BRACELET_STRENGTH.get());
                        pOutput.accept(Bracelets.BRACELET_SPEED.get());
                        pOutput.accept(Bracelets.BRACELET_HP.get());
                        pOutput.accept(Bracelets.BRACELET_GUARD.get());
                        pOutput.accept(Bracelets.BRACELET_LUCK.get());

                        // 戒指：按“种类 → 品阶”排序，类似附魔书：
                        // 例如：生命精华戒指：残破 → 普通 → 精良 → 史诗 → 传说 → 至臻

                        // 生命精华 Life Essence
                        pOutput.accept(Rings.BROKEN_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.EPIC_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.MYTHIC_LIFE_ESSENCE_RING.get());

                        // 战斗力 Battle Power
                        pOutput.accept(Rings.BROKEN_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.EXCELLENT_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.EPIC_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.LEGENDARY_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.MYTHIC_BATTLE_POWER_RING.get());

                        // 铁壁 Iron Shield
                        pOutput.accept(Rings.BROKEN_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.EXCELLENT_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.EPIC_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.LEGENDARY_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.MYTHIC_IRON_SHIELD_RING.get());

                        // 疾风 Gale
                        pOutput.accept(Rings.BROKEN_GALE_RING.get());
                        pOutput.accept(Rings.GALE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_GALE_RING.get());
                        pOutput.accept(Rings.EPIC_GALE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_GALE_RING.get());
                        pOutput.accept(Rings.MYTHIC_GALE_RING.get());

                        // 好运 Good Fortune
                        pOutput.accept(Rings.BROKEN_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.EPIC_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.MYTHIC_GOOD_FORTUNE_RING.get());

                        // 治疗 Healing（没有残破品质）
                        pOutput.accept(Rings.HEALING_RING.get());
                        pOutput.accept(Rings.EXCELLENT_HEALING_RING.get());
                        pOutput.accept(Rings.EPIC_HEALING_RING.get());
                        pOutput.accept(Rings.LEGENDARY_HEALING_RING.get());
                        pOutput.accept(Rings.MYTHIC_HEALING_RING.get());

                        // 利刃 Sharp Edge（普通品质是 NORMAL_SHARP_EDGE_RING）
                        pOutput.accept(Rings.BROKEN_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.NORMAL_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.EPIC_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.MYTHIC_SHARP_EDGE_RING.get());

                        // 破甲 Armor Breaker（没有残破品质）
                        pOutput.accept(Rings.ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.EXCELLENT_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.EPIC_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.LEGENDARY_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.MYTHIC_ARMOR_BREAKER_RING.get());

                        // 饰品工作台
                        pOutput.accept(ModBlocks.AccessoriesTable.get());
                    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RINGS_TAB =
            CREATIVE_MODE_TAB.register("rings_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Rings.EXCELLENT_LIFE_ESSENCE_RING.get()))
                    .title(Component.translatable("itemGroup.rings_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // 戒指专用标签页，同样按“种类 → 品阶”排列

                        // 生命精华 Life Essence
                        pOutput.accept(Rings.BROKEN_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.EPIC_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_LIFE_ESSENCE_RING.get());
                        pOutput.accept(Rings.MYTHIC_LIFE_ESSENCE_RING.get());

                        // 战斗力 Battle Power
                        pOutput.accept(Rings.BROKEN_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.EXCELLENT_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.EPIC_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.LEGENDARY_BATTLE_POWER_RING.get());
                        pOutput.accept(Rings.MYTHIC_BATTLE_POWER_RING.get());

                        // 铁壁 Iron Shield
                        pOutput.accept(Rings.BROKEN_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.EXCELLENT_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.EPIC_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.LEGENDARY_IRON_SHIELD_RING.get());
                        pOutput.accept(Rings.MYTHIC_IRON_SHIELD_RING.get());

                        // 疾风 Gale
                        pOutput.accept(Rings.BROKEN_GALE_RING.get());
                        pOutput.accept(Rings.GALE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_GALE_RING.get());
                        pOutput.accept(Rings.EPIC_GALE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_GALE_RING.get());
                        pOutput.accept(Rings.MYTHIC_GALE_RING.get());

                        // 好运 Good Fortune
                        pOutput.accept(Rings.BROKEN_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.EPIC_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_GOOD_FORTUNE_RING.get());
                        pOutput.accept(Rings.MYTHIC_GOOD_FORTUNE_RING.get());

                        // 治疗 Healing
                        pOutput.accept(Rings.HEALING_RING.get());
                        pOutput.accept(Rings.EXCELLENT_HEALING_RING.get());
                        pOutput.accept(Rings.EPIC_HEALING_RING.get());
                        pOutput.accept(Rings.LEGENDARY_HEALING_RING.get());
                        pOutput.accept(Rings.MYTHIC_HEALING_RING.get());

                        // 利刃 Sharp Edge
                        pOutput.accept(Rings.BROKEN_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.NORMAL_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.EXCELLENT_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.EPIC_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.LEGENDARY_SHARP_EDGE_RING.get());
                        pOutput.accept(Rings.MYTHIC_SHARP_EDGE_RING.get());

                        // 破甲 Armor Breaker
                        pOutput.accept(Rings.ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.EXCELLENT_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.EPIC_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.LEGENDARY_ARMOR_BREAKER_RING.get());
                        pOutput.accept(Rings.MYTHIC_ARMOR_BREAKER_RING.get());
                    }).withTabsBefore(ACCESSORIES_TAB.getKey()).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SETS_TAB =
            CREATIVE_MODE_TAB.register("sets_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(DragonItems.DRAGON_RING.get()))
                    .title(Component.translatable("itemGroup.sets_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // 套装饰品专用标签页
                        // Set Accessories Tab

                        // 龙族套装 Dragon Set
                        pOutput.accept(DragonItems.DRAGON_RING.get());
                        pOutput.accept(DragonItems.DRAGON_NECKLACE.get());
                        pOutput.accept(DragonItems.DRAGON_BRACELET.get());
                        pOutput.accept(DragonItems.DRAGON_EARRING.get());
                        pOutput.accept(DragonItems.DRAGON_HEADWEAR.get());
                        pOutput.accept(DragonItems.DRAGON_BELT.get());

                        // 未来可以在这里添加更多套装
                        // Future sets can be added here
                    }).withTabsBefore(RINGS_TAB.getKey()).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
