package com.muyun.evolutionary_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                .then(Commands.literal("stats")
                        .executes(ctx -> showAccessoryStats(ctx.getSource())))
                .then(Commands.literal("export")
                        .then(Commands.literal("csv")
                                .executes(ctx -> exportDropRatesCSV(ctx.getSource()))))
                .then(Commands.literal("equipped")
                        .executes(ctx -> showEquippedAccessories(ctx.getSource())));

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
        source.sendSuccess(() -> Component.literal("使用 /acc export csv 生成CSV报告"), false);
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

