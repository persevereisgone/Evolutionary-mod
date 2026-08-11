package com.muyun.evolutionary_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.muyun.evolutionary_mod.Config;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.loot.AccessoryDropTable;
import com.muyun.evolutionary_mod.loot.AttributeRollRanges;
import com.muyun.evolutionary_mod.system.sets.SetBalanceConfig;
import com.muyun.evolutionary_mod.system.sets.SetType;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 饰品模组调试命令 - Accessory Mod Debug Commands
 *
 * NeoForge 1.21.1:
 * - @Mod.EventBusSubscriber -> @EventBusSubscriber
 * - RegisterCommandsEvent 包名更新
 * - Capability -> getData()
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AccessoryCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> rootCommand = Commands.literal("accessories")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("help")
                        .executes(ctx -> showHelp(ctx.getSource())))
                .then(Commands.literal("stats")
                        .executes(ctx -> showAccessoryStats(ctx.getSource()))
                        .then(Commands.literal("detail")
                                .executes(ctx -> showDetailedStats(ctx.getSource()))))
                .then(Commands.literal("export")
                        .then(Commands.literal("csv")
                                .executes(ctx -> exportDropRatesCSV(ctx.getSource()))))
                .then(Commands.literal("equipped")
                        .executes(ctx -> showEquippedAccessories(ctx.getSource())))
                .then(Commands.literal("drop")
                        .then(Commands.literal("query")
                                .then(Commands.argument("loot_table_id", ResourceLocationArgument.id())
                                        .executes(ctx -> queryDropConfig(
                                                ctx.getSource(),
                                                ResourceLocationArgument.getId(ctx, "loot_table_id"))))))
                .then(Commands.literal("attr")
                        .then(Commands.literal("query")
                                .then(Commands.argument("item_id", StringArgumentType.string())
                                        .executes(ctx -> queryAttrConfig(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "item_id"))))))
                .then(Commands.literal("set")
                        .then(Commands.literal("query")
                                .then(Commands.argument("set_type", StringArgumentType.word())
                                        .executes(ctx -> querySetConfig(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "set_type"))))))
                .then(Commands.literal("validate")
                        .executes(ctx -> validateDataDrivenConfig(ctx.getSource())));

        LiteralArgumentBuilder<CommandSourceStack> aliasCommand = Commands.literal("acc")
                .requires(source -> source.hasPermission(2))
                .redirect(rootCommand.build());

        dispatcher.register(rootCommand);
        dispatcher.register(aliasCommand);
    }

    private static int showAccessoryStats(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== 饰品模组统计信息 ==="), false);
        source.sendSuccess(() -> Component.literal("随机属性系统：已启用"), false);
        source.sendSuccess(() -> Component.literal("掉落池：普通怪物池、精英怪物池"), false);
        source.sendSuccess(() -> Component.literal("支持品阶：破损、普通、优秀、史诗、传说、至臻"), false);
        source.sendSuccess(() -> Component.literal("数据严格模式： " + (Config.isStrictDataDriven() ? "开启" : "关闭")), false);
        source.sendSuccess(() -> Component.literal("使用 /acc export csv 生成CSV报告"), false);
        source.sendSuccess(() -> Component.literal("使用 /acc stats detail 查看详细统计"), false);
        source.sendSuccess(() -> Component.literal("使用 /acc drop query <loot_table_id> 查询掉落配置"), false);
        source.sendSuccess(() -> Component.literal("使用 /acc attr query <item_id> 查询词条配置命中"), false);
        source.sendSuccess(() -> Component.literal("使用 /acc set query dragon 查询龙套数值配置"), false);
        source.sendSuccess(() -> Component.literal("使用 /acc validate 检查数据配置与回退命中"), false);
        return 1;
    }

    private static int showDetailedStats(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== 详细统计信息 ==="), false);
        source.sendSuccess(() -> Component.literal("严格模式(strictDataDriven): " + (Config.isStrictDataDriven() ? "开启" : "关闭")), false);
        source.sendSuccess(() -> Component.literal("[词条] 已加载: " + AttributeRollRanges.getLoadedEntryCount()
                + "，无效: " + AttributeRollRanges.getInvalidEntryCount()
                + "，回退命中: " + AttributeRollRanges.getFallbackHitCount()
                + "，回退涉及物品: " + AttributeRollRanges.getFallbackDistinctItemCount()), false);
        source.sendSuccess(() -> Component.literal("[掉落] 已加载: " + AccessoryDropTable.getLoadedEntryCount()
                + "，无效: " + AccessoryDropTable.getInvalidEntryCount()
                + "，回退命中: " + AccessoryDropTable.getFallbackHitCount()
                + "，回退涉及表: " + AccessoryDropTable.getFallbackDistinctKeyCount()), false);
        source.sendSuccess(() -> Component.literal("[套装] 已加载: " + SetBalanceConfig.getLoadedEntryCount()
                + "，无效: " + SetBalanceConfig.getInvalidEntryCount()
                + "，回退命中: " + SetBalanceConfig.getFallbackHitCount()), false);
        return 1;
    }

    private static int validateDataDrivenConfig(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== 数据配置校验报告 ==="), false);
        source.sendSuccess(() -> Component.literal("严格模式(strictDataDriven): " + (Config.isStrictDataDriven() ? "开启" : "关闭")), false);
        source.sendSuccess(() -> Component.literal("[词条配置] 已加载: "
                + AttributeRollRanges.getLoadedEntryCount()
                + "，无效条目: " + AttributeRollRanges.getInvalidEntryCount()
                + "，回退命中: " + AttributeRollRanges.getFallbackHitCount()), false);
        source.sendSuccess(() -> Component.literal("[掉落配置] 已加载: "
                + AccessoryDropTable.getLoadedEntryCount()
                + "，无效条目: " + AccessoryDropTable.getInvalidEntryCount()
                + "，回退命中: " + AccessoryDropTable.getFallbackHitCount()), false);
        source.sendSuccess(() -> Component.literal("[套装配置] 已加载: "
                + SetBalanceConfig.getLoadedEntryCount()
                + "，无效条目: " + SetBalanceConfig.getInvalidEntryCount()
                + "，回退命中: " + SetBalanceConfig.getFallbackHitCount()), false);
        source.sendSuccess(() -> Component.literal("若回退命中 > 0，建议补齐 JSON；开发期可开启 strictDataDriven 强制报错。"), false);
        return 1;
    }

    private static int querySetConfig(CommandSourceStack source, String setTypeRaw) {
        SetType setType = SetType.fromConfigKey(setTypeRaw);
        if (setType == null) {
            source.sendFailure(Component.literal("未知套装类型: " + setTypeRaw + "，可用值: dragon"));
            return 0;
        }
        // 当前先落地 dragon，但命令与配置结构已支持按 set_type 扩展
        SetBalanceConfig.DragonConfig c = SetBalanceConfig.getDragon(setType);
        String healthPct = percent(c.twoPieceHealthBonus());
        String fourReducePct = percent(c.fourPieceDamageReduction());
        String shieldChancePct = percent(c.fourPieceShieldChance());
        String breathChancePct = percent(c.sixPieceBreathChance());
        String shieldCdSec = secondsFromTicks(c.fourPieceShieldCooldownTicks());
        String burnSec = secondsFromTicks(c.sixPieceBurnTicks());

        source.sendSuccess(() -> Component.literal("=== 套装配置查询: " + setType.getConfigKey() + " ==="), false);
        source.sendSuccess(() -> Component.literal("[原始数据]"), false);
        source.sendSuccess(() -> Component.literal("2件套 原始: health_bonus=" + c.twoPieceHealthBonus()
                + ", regen_multiplier=" + c.twoPieceRegenMultiplier()), false);
        source.sendSuccess(() -> Component.literal("4件套 原始: armor_bonus=" + c.fourPieceArmorBonus()
                + ", damage_reduction=" + c.fourPieceDamageReduction()
                + ", shield_chance=" + c.fourPieceShieldChance()
                + ", shield_absorb_max=" + c.fourPieceShieldAbsorbMax()
                + ", shield_cooldown_ticks=" + c.fourPieceShieldCooldownTicks()), false);
        source.sendSuccess(() -> Component.literal("6件套 原始: breath_chance=" + c.sixPieceBreathChance()
                + ", breath_range=" + c.sixPieceBreathRange()
                + ", breath_damage=" + c.sixPieceBreathDamage()
                + ", burn_ticks=" + c.sixPieceBurnTicks()), false);

        source.sendSuccess(() -> Component.literal("[效果解释]"), false);
        source.sendSuccess(() -> Component.literal("2件套: 最大生命 +" + healthPct
                + "，生命回复倍率 x" + c.twoPieceRegenMultiplier()), false);
        source.sendSuccess(() -> Component.literal("4件套: 护甲 +" + trim(c.fourPieceArmorBonus())
                + "，减伤 +" + fourReducePct
                + "，受击时 " + shieldChancePct + " 概率触发护盾，最多吸收 "
                + trim(c.fourPieceShieldAbsorbMax()) + " 点伤害，冷却 "
                + c.fourPieceShieldCooldownTicks() + " tick(" + shieldCdSec + "秒)"), false);
        source.sendSuccess(() -> Component.literal("6件套 解释: 攻击时 " + breathChancePct
                + " 概率触发龙息，对前方 " + trim(c.sixPieceBreathRange()) + " 格内目标造成 "
                + trim(c.sixPieceBreathDamage()) + " 点伤害，并点燃 "
                + c.sixPieceBurnTicks() + " tick(" + burnSec + "秒)"), false);
        return 1;
    }

    private static String percent(double value) {
        return trim(value * 100.0) + "%";
    }

    private static String secondsFromTicks(int ticks) {
        return trim(ticks / 20.0);
    }

    private static String trim(double value) {
        if (Math.abs(value - Math.rint(value)) < 1e-9) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private static int queryDropConfig(CommandSourceStack source, ResourceLocation lootTableId) {
        AccessoryDropTable.DropConfig config = AccessoryDropTable.getConfig(lootTableId);
        if (config == null) {
            source.sendFailure(Component.literal("未找到掉落配置: " + lootTableId + "（JSON与回退均未命中）"));
            return 0;
        }
        boolean fromData = AccessoryDropTable.hasDataEntry(lootTableId);
        boolean fromFallback = !fromData && AccessoryDropTable.hasFallbackEntry(lootTableId);
        String rarities = String.join(", ", java.util.Arrays.stream(config.allowedRarities()).map(Enum::name).toList());
        source.sendSuccess(() -> Component.literal("=== 掉落配置查询 ==="), false);
        source.sendSuccess(() -> Component.literal("loot_table_id: " + lootTableId), false);
        source.sendSuccess(() -> Component.literal("来源: " + (fromData ? "JSON(data/evolutionary_mod/loot/drop_table.json)" : (fromFallback ? "回退(Java默认表)" : "未知"))), false);
        source.sendSuccess(() -> Component.literal("drop_chance: " + config.dropChance()), false);
        source.sendSuccess(() -> Component.literal("allowed_rarities: [" + rarities + "]"), false);
        return 1;
    }

    private static int queryAttrConfig(CommandSourceStack source, String itemIdRaw) {
        ResourceLocation itemId = ResourceLocation.tryParse(itemIdRaw);
        if (itemId == null) {
            source.sendFailure(Component.literal("非法物品ID: " + itemIdRaw));
            return 0;
        }
        if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
            source.sendFailure(Component.literal("物品不存在: " + itemId));
            return 0;
        }
        String path = itemId.getPath();
        boolean hasData = AttributeRollRanges.hasDataEntry(path);
        source.sendSuccess(() -> Component.literal("=== 词条配置查询 ==="), false);
        source.sendSuccess(() -> Component.literal("item_id: " + itemId), false);
        source.sendSuccess(() -> Component.literal("item_path(key): " + path), false);
        source.sendSuccess(() -> Component.literal("数据驱动命中: " + (hasData ? "是" : "否（将使用回退逻辑）")), false);
        return 1;
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("=== /acc 帮助 ==="), false);
        source.sendSuccess(() -> Component.literal("/acc stats - 基础统计"), false);
        source.sendSuccess(() -> Component.literal("/acc stats detail - 详细统计（含回退命中）"), false);
        source.sendSuccess(() -> Component.literal("/acc validate - 数据配置校验"), false);
        source.sendSuccess(() -> Component.literal("/acc drop query <loot_table_id> - 查询掉落配置来源与数值"), false);
        source.sendSuccess(() -> Component.literal("/acc attr query <item_id> - 查询词条配置是否命中JSON"), false);
        source.sendSuccess(() -> Component.literal("/acc set query <set_type> - 查询套装数据驱动参数（当前可用: dragon）"), false);
        source.sendSuccess(() -> Component.literal("/acc equipped - 查看已装备饰品"), false);
        source.sendSuccess(() -> Component.literal("/acc export csv - 导出掉率CSV"), false);
        return 1;
    }

    private static int exportDropRatesCSV(CommandSourceStack source) {
        try {
            Path exportDir = Paths.get("accessories_export");
            Files.createDirectories(exportDir);
            Path csvFile = exportDir.resolve("accessory_drop_rates.csv");
            try (FileWriter writer = new FileWriter(csvFile.toFile())) {
                writer.write("饰品名称,品阶,掉落池,权重,基础掉率,实际掉率\n");
                writePoolData(writer, "普通怪物池", 0.03);
                writePoolData(writer, "精英怪物池", 0.015);
            }
            source.sendSuccess(() -> Component.literal("CSV报告已导出到: " + csvFile.toAbsolutePath()), false);
            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.literal("导出失败: " + e.getMessage()));
            return 0;
        }
    }

    private static void writePoolData(FileWriter writer, String poolName, double baseChance) throws IOException {
        Map<String, Integer> pool = new HashMap<>();
        if (poolName.equals("普通怪物池")) {
            pool.put("broken_life_essence_ring", 20); pool.put("broken_battle_power_ring", 20);
            pool.put("broken_iron_shield_ring", 20);  pool.put("broken_gale_ring", 20);
            pool.put("broken_good_fortune_ring", 20); pool.put("broken_sharp_edge_ring", 20);
            pool.put("life_essence_ring", 6);  pool.put("battle_power_ring", 6);
            pool.put("iron_shield_ring", 6);   pool.put("gale_ring", 6);
            pool.put("good_fortune_ring", 6);  pool.put("healing_ring", 6);
            pool.put("normal_sharp_edge_ring", 6); pool.put("armor_breaker_ring", 6);
        } else {
            pool.put("life_essence_ring", 20); pool.put("battle_power_ring", 20);
            pool.put("iron_shield_ring", 20);  pool.put("gale_ring", 20);
            pool.put("good_fortune_ring", 20); pool.put("healing_ring", 20);
            pool.put("normal_sharp_edge_ring", 20); pool.put("armor_breaker_ring", 20);
            pool.put("excellent_life_essence_ring", 2); pool.put("excellent_sharp_edge_ring", 2);
            pool.put("excellent_iron_shield_ring", 2); pool.put("excellent_gale_ring", 2);
            pool.put("excellent_good_fortune_ring", 2); pool.put("excellent_healing_ring", 2);
            pool.put("excellent_armor_breaker_ring", 2);
        }
        int totalWeight = pool.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : pool.entrySet()) {
            String itemName = formatItemName(entry.getKey());
            String rarity = getRarityFromName(entry.getKey());
            double itemChance = (double) entry.getValue() / totalWeight;
            double actualChance = baseChance * itemChance;
            writer.write(String.format("%s,%s,%s,%d,%.4f,%.6f\n",
                    itemName, rarity, poolName, entry.getValue(), itemChance, actualChance));
        }
    }

    private static String formatItemName(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("_")) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    private static String getRarityFromName(String name) {
        if (name.startsWith("broken_"))    return "破损";
        if (name.startsWith("excellent_")) return "优秀";
        if (name.startsWith("epic_"))      return "史诗";
        if (name.startsWith("legendary_")) return "传说";
        if (name.startsWith("mythic_"))    return "至臻";
        return "普通";
    }

    /**
     * 显示玩家当前装备的饰品。
     * NeoForge 1.21.1: player.getData() 替代 getCapability()。
     */
    private static int showEquippedAccessories(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("此命令只能由玩家执行"));
            return 0;
        }
        PlayerAccessories accessories = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        source.sendSuccess(() -> Component.literal("=== 当前装备的饰品 ==="), false);
        boolean hasAny = false;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = accessories.getStack(slot);
            if (!stack.isEmpty()) {
                hasAny = true;
                String slotName = getSlotDisplayName(slot);
                String itemName = stack.getDisplayName().getString();
                source.sendSuccess(() -> Component.literal(slotName + ": " + itemName), false);
            }
        }
        if (!hasAny) source.sendSuccess(() -> Component.literal("当前没有装备任何饰品"), false);
        return 1;
    }

    private static String getSlotDisplayName(AccessorySlot slot) {
        return switch (slot) {
            case HEAD -> "头部";
            case EARRING_1, EARRING_2 -> "耳环";
            case NECKLACE -> "项链";
            case GLOVE_1, GLOVE_2 -> "手套";
            case BRACELET_1, BRACELET_2 -> "手镯";
            case RING_1, RING_2, RING_3, RING_4 -> "戒指";
            case BELT -> "腰带";
            case BOOT_1, BOOT_2 -> "靴饰";
            case ACCESSORY_1, ACCESSORY_2, ACCESSORY_3 -> "配饰";
        };
    }
}

