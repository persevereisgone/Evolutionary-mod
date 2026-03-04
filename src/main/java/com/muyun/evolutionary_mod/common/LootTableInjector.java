package com.muyun.evolutionary_mod.common;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.types.Rings;

import java.util.Set;

/**
 * 在加载原版实体掉落表时注入自定义饰品掉落池。
 * Inject accessory loot pools into vanilla entity loot tables when they are loaded.
 *
 * 注意：LootTableLoadEvent 在未来版本中可能会调整为数据生成/JSON 配置，
 * 如果遇到兼容问题，可以改为纯数据驱动的 loot table 注入方式。
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class LootTableInjector {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 默认掉落概率 3%（测试用：99.99%）
     * 注意：使用 0.9999f 而不是 1.0f，因为某些实现中 1.0f 可能不工作
     */
    private static final float DEFAULT_DROP_CHANCE = 0.03f;

    /**
     * 排除强力精英怪（后续你设计高级饰品掉落时再单独处理）
     */
    private static final Set<String> EXCLUDED_ENTITY_IDS = Set.of(
            "ender_dragon",
            "wither",
            "warden",
            "elder_guardian"
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        
        // 调试：记录所有实体相关的战利品表加载（使用info级别确保能看到）
        if (name != null && name.getPath() != null && name.getPath().startsWith("entities/")) {
            LOGGER.info("[饰品掉落] 检测到实体战利品表: {}", name);
        }

        if (!isVanillaEntityLootTable(name)) {
            return;
        }

        String entityId = getEntityIdFromLootTable(name);
        if (entityId == null) {
            LOGGER.info("[饰品掉落] 无法从战利品表路径提取实体ID: {}", name);
            return;
        }

        LOGGER.info("[饰品掉落] 提取到实体ID: {}", entityId);

        // 只对敌对生物（怪物）注入，不对被动生物/村民等注入
        if (!isHostileMob(entityId)) {
            LOGGER.info("[饰品掉落] 实体 {} 不是敌对生物（类别检查失败），跳过", entityId);
            return;
        }

        // 排除末影龙、凋零等强力精英怪，后续单独设计高级饰品掉落
        if (EXCLUDED_ENTITY_IDS.contains(entityId)) {
            LOGGER.info("[饰品掉落] 实体 {} 在排除列表中，跳过", entityId);
            return;
        }

        LOGGER.info("[饰品掉落] 正在为实体 {} 注入饰品掉落池，掉落概率: {}%", entityId, DEFAULT_DROP_CHANCE * 100);

        // 直接在代码中构建掉落池，而不是引用 JSON 文件
        // 因为 LootTableReference 在 LootTableLoadEvent 时可能无法正确解析
        LootPool.Builder poolBuilder = LootPool.lootPool()
                .name("evolutionary_mod_all_inject")
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                // 默认掉落概率 3%
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(DEFAULT_DROP_CHANCE))
                // 残破品质（权重20）
                .add(LootItem.lootTableItem(Rings.BROKEN_LIFE_ESSENCE_RING.get()).setWeight(20))
                .add(LootItem.lootTableItem(Rings.BROKEN_BATTLE_POWER_RING.get()).setWeight(20))
                .add(LootItem.lootTableItem(Rings.BROKEN_IRON_SHIELD_RING.get()).setWeight(20))
                .add(LootItem.lootTableItem(Rings.BROKEN_GALE_RING.get()).setWeight(20))
                .add(LootItem.lootTableItem(Rings.BROKEN_GOOD_FORTUNE_RING.get()).setWeight(20))
                .add(LootItem.lootTableItem(Rings.BROKEN_SHARP_EDGE_RING.get()).setWeight(20))
                // 普通品质（权重6）
                .add(LootItem.lootTableItem(Rings.LIFE_ESSENCE_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.BATTLE_POWER_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.IRON_SHIELD_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.GALE_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.GOOD_FORTUNE_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.HEALING_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.NORMAL_SHARP_EDGE_RING.get()).setWeight(6))
                .add(LootItem.lootTableItem(Rings.ARMOR_BREAKER_RING.get()).setWeight(6))
                // 优秀品质（权重2）
                .add(LootItem.lootTableItem(Rings.EXCELLENT_LIFE_ESSENCE_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_BATTLE_POWER_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_IRON_SHIELD_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_GALE_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_GOOD_FORTUNE_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_HEALING_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_SHARP_EDGE_RING.get()).setWeight(2))
                .add(LootItem.lootTableItem(Rings.EXCELLENT_ARMOR_BREAKER_RING.get()).setWeight(2));
        
        LootPool injectedPool = poolBuilder.build();
        event.getTable().addPool(injectedPool);
        
        LOGGER.info("[饰品掉落] 已成功注入饰品掉落池到实体 {} 的战利品表，池名称: {}", 
                entityId, injectedPool.getName());
    }

    private static boolean isVanillaEntityLootTable(ResourceLocation name) {
        if (name == null) return false;
        if (!"minecraft".equals(name.getNamespace())) return false;
        return name.getPath() != null && name.getPath().startsWith("entities/");
    }

    /**
     * 根据实体 ID 判断是否为敌对生物（Monster 类别）。
     */
    private static boolean isHostileMob(String entityId) {
        ResourceLocation entityKey = ResourceLocation.fromNamespaceAndPath("minecraft", entityId);
        EntityType<?> type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(entityKey);
        if (type == null) {
            return false;
        }
        return type.getCategory() == MobCategory.MONSTER;
    }

    private static String getEntityIdFromLootTable(ResourceLocation name) {
        if (name == null) return null;
        String path = name.getPath();
        if (path == null) return null;
        if (!path.startsWith("entities/")) return null;
        // e.g. "entities/zombie" -> "zombie"
        return path.substring("entities/".length());
    }
}


