package com.muyun.evolutionary_mod.system.weight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = "evolutionary_mod")
public class WeightConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ====================
    // 基础设置
    // ====================

    private static final ModConfigSpec.DoubleValue BASE_MAX_WEIGHT = BUILDER
            .comment("玩家基础最大负重（重量单位），推荐值: 120")
            .defineInRange("baseMaxWeight", 120.0, 50.0, 500.0);

    private static final ModConfigSpec.DoubleValue SAFE_THRESHOLD = BUILDER
            .comment("安全负重阈值比例（0.0-1.0），超过此值开始减速，推荐值: 0.6")
            .defineInRange("safeThreshold", 0.6, 0.0, 1.0);

    private static final ModConfigSpec.DoubleValue WARNING_THRESHOLD = BUILDER
            .comment("警戒负重阈值比例（0.0-1.0），超过此值开始加速消耗饱食，推荐值: 0.85")
            .defineInRange("warningThreshold", 0.85, 0.0, 1.0);

    private static final ModConfigSpec.DoubleValue SPEED_PENALTY_MULTIPLIER = BUILDER
            .comment("速度惩罚倍率（0.0-1.0），值越大惩罚越严重，推荐值: 0.5")
            .defineInRange("speedPenaltyMultiplier", 0.5, 0.0, 1.0);

    private static final ModConfigSpec.DoubleValue HUNGER_PENALTY_MULTIPLIER = BUILDER
            .comment("饱食消耗倍率，超过警戒阈值时生效，推荐值: 2.0")
            .defineInRange("hungerPenaltyMultiplier", 2.0, 1.0, 10.0);

    // ====================
    // 等级加成设置
    // ====================

    private static final ModConfigSpec.DoubleValue LEVEL_WEIGHT_BONUS_PER_LEVEL = BUILDER
            .comment("每级增加的负重上限，推荐值: 2.0")
            .defineInRange("levelWeightBonusPerLevel", 2.0, 0.5, 10.0);

    // ====================
    // 物品重量设置（按材质分类）
    // ====================

    // 工具武器
    private static final ModConfigSpec.DoubleValue WOODEN_TOOL_WEIGHT = BUILDER
            .comment("木质/石质工具的基础重量")
            .defineInRange("woodenToolWeight", 1.5, 0.5, 5.0);

    private static final ModConfigSpec.DoubleValue IRON_TOOL_WEIGHT = BUILDER
            .comment("铁质工具的基础重量")
            .defineInRange("ironToolWeight", 2.5, 1.0, 8.0);

    private static final ModConfigSpec.DoubleValue DIAMOND_TOOL_WEIGHT = BUILDER
            .comment("钻石工具的基础重量")
            .defineInRange("diamondToolWeight", 4.0, 2.0, 10.0);

    private static final ModConfigSpec.DoubleValue NETHERITE_TOOL_WEIGHT = BUILDER
            .comment("下界合金工具的基础重量")
            .defineInRange("netheriteToolWeight", 5.0, 3.0, 15.0);

    // 护甲（单件）
    private static final ModConfigSpec.DoubleValue LEATHER_ARMOR_WEIGHT = BUILDER
            .comment("皮革护甲单件重量")
            .defineInRange("leatherArmorWeight", 2.0, 0.5, 5.0);

    private static final ModConfigSpec.DoubleValue IRON_ARMOR_WEIGHT = BUILDER
            .comment("铁护甲单件重量")
            .defineInRange("ironArmorWeight", 4.0, 1.0, 10.0);

    private static final ModConfigSpec.DoubleValue DIAMOND_ARMOR_WEIGHT = BUILDER
            .comment("钻石护甲单件重量")
            .defineInRange("diamondArmorWeight", 6.0, 3.0, 15.0);

    private static final ModConfigSpec.DoubleValue NETHERITE_ARMOR_WEIGHT = BUILDER
            .comment("下界合金护甲单件重量")
            .defineInRange("netheriteArmorWeight", 8.0, 4.0, 20.0);

    // 资源材料
    private static final ModConfigSpec.DoubleValue BUILDING_MATERIAL_WEIGHT = BUILDER
            .comment("建筑材料基础重量（石头、木板等）")
            .defineInRange("buildingMaterialWeight", 0.3, 0.1, 1.0);

    private static final ModConfigSpec.DoubleValue ORE_WEIGHT = BUILDER
            .comment("矿石基础重量（铁矿、煤矿等）")
            .defineInRange("oreWeight", 0.5, 0.2, 2.0);

    private static final ModConfigSpec.DoubleValue PRECIOUS_MATERIAL_WEIGHT = BUILDER
            .comment("珍贵材料基础重量（钻石、绿宝石等）")
            .defineInRange("preciousMaterialWeight", 1.0, 0.5, 5.0);

    // 食物
    private static final ModConfigSpec.DoubleValue BASIC_FOOD_WEIGHT = BUILDER
            .comment("基础食物重量（面包、生肉等）")
            .defineInRange("basicFoodWeight", 0.2, 0.05, 1.0);

    private static final ModConfigSpec.DoubleValue ADVANCED_FOOD_WEIGHT = BUILDER
            .comment("高级食物重量（熟肉、金苹果等）")
            .defineInRange("advancedFoodWeight", 0.4, 0.1, 2.0);

    // 药水杂物
    private static final ModConfigSpec.DoubleValue POTION_WEIGHT = BUILDER
            .comment("药水重量")
            .defineInRange("potionWeight", 0.5, 0.2, 2.0);

    private static final ModConfigSpec.DoubleValue ARROW_WEIGHT = BUILDER
            .comment("箭矢/弹药重量")
            .defineInRange("arrowWeight", 0.05, 0.01, 0.2);

    private static final ModConfigSpec.DoubleValue REDSTONE_WEIGHT = BUILDER
            .comment("红石/荧石等小型材料重量")
            .defineInRange("redstoneWeight", 0.1, 0.02, 0.5);

    // 家具机械
    private static final ModConfigSpec.DoubleValue FURNITURE_WEIGHT = BUILDER
            .comment("基础家具重量（工作台、箱子等）")
            .defineInRange("furnitureWeight", 5.0, 2.0, 15.0);

    private static final ModConfigSpec.DoubleValue MACHINE_WEIGHT = BUILDER
            .comment("重型机械重量（熔炉、铁砧等）")
            .defineInRange("machineWeight", 10.0, 5.0, 30.0);

    // ====================
    // 自定义物品重量映射
    // ====================

    private static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_ITEM_WEIGHTS = BUILDER
            .comment("自定义物品重量映射，格式: 物品ID=重量，例如: minecraft:diamond=1.0")
            .defineListAllowEmpty("customItemWeights", List.of(), WeightConfig::validateItemWeightEntry);

    public static final ModConfigSpec SPEC = BUILDER.build();

    // ====================
    // 运行时配置值
    // ====================

    // 基础设置
    public static double baseMaxWeight;
    public static double safeThreshold;
    public static double warningThreshold;
    public static double speedPenaltyMultiplier;
    public static double hungerPenaltyMultiplier;

    // 等级加成
    public static double levelWeightBonusPerLevel;

    // 物品重量
    public static double woodenToolWeight;
    public static double ironToolWeight;
    public static double diamondToolWeight;
    public static double netheriteToolWeight;

    public static double leatherArmorWeight;
    public static double ironArmorWeight;
    public static double diamondArmorWeight;
    public static double netheriteArmorWeight;

    public static double buildingMaterialWeight;
    public static double oreWeight;
    public static double preciousMaterialWeight;

    public static double basicFoodWeight;
    public static double advancedFoodWeight;

    public static double potionWeight;
    public static double arrowWeight;
    public static double redstoneWeight;

    public static double furnitureWeight;
    public static double machineWeight;

    // 自定义物品重量映射
    public static final Map<Item, Double> CUSTOM_WEIGHTS = new HashMap<>();

    private static boolean validateItemWeightEntry(final Object obj) {
        if (!(obj instanceof String entry)) {
            return false;
        }
        String[] parts = entry.split("=");
        if (parts.length != 2) {
            return false;
        }
        try {
            ResourceLocation.tryParse(parts[0]);
            Double.parseDouble(parts[1]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // 本模组有多份 COMMON 配置，只处理自己的 Spec
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        // 基础设置
        baseMaxWeight = BASE_MAX_WEIGHT.get();
        safeThreshold = SAFE_THRESHOLD.get();
        warningThreshold = WARNING_THRESHOLD.get();
        speedPenaltyMultiplier = SPEED_PENALTY_MULTIPLIER.get();
        hungerPenaltyMultiplier = HUNGER_PENALTY_MULTIPLIER.get();

        // 等级加成
        levelWeightBonusPerLevel = LEVEL_WEIGHT_BONUS_PER_LEVEL.get();

        // 物品重量
        woodenToolWeight = WOODEN_TOOL_WEIGHT.get();
        ironToolWeight = IRON_TOOL_WEIGHT.get();
        diamondToolWeight = DIAMOND_TOOL_WEIGHT.get();
        netheriteToolWeight = NETHERITE_TOOL_WEIGHT.get();

        leatherArmorWeight = LEATHER_ARMOR_WEIGHT.get();
        ironArmorWeight = IRON_ARMOR_WEIGHT.get();
        diamondArmorWeight = DIAMOND_ARMOR_WEIGHT.get();
        netheriteArmorWeight = NETHERITE_ARMOR_WEIGHT.get();

        buildingMaterialWeight = BUILDING_MATERIAL_WEIGHT.get();
        oreWeight = ORE_WEIGHT.get();
        preciousMaterialWeight = PRECIOUS_MATERIAL_WEIGHT.get();

        basicFoodWeight = BASIC_FOOD_WEIGHT.get();
        advancedFoodWeight = ADVANCED_FOOD_WEIGHT.get();

        potionWeight = POTION_WEIGHT.get();
        arrowWeight = ARROW_WEIGHT.get();
        redstoneWeight = REDSTONE_WEIGHT.get();

        furnitureWeight = FURNITURE_WEIGHT.get();
        machineWeight = MACHINE_WEIGHT.get();

        // 解析自定义物品重量
        CUSTOM_WEIGHTS.clear();
        for (String entry : CUSTOM_ITEM_WEIGHTS.get()) {
            String[] parts = entry.split("=");
            if (parts.length == 2) {
                ResourceLocation itemId = ResourceLocation.tryParse(parts[0]);
                double weight = Double.parseDouble(parts[1]);
                Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item != null) {
                    CUSTOM_WEIGHTS.put(item, weight);
                }
            }
        }

        // 清理缓存
        WeightSystem.invalidateCache();
    }
}