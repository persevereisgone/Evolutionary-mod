package com.muyun.evolutionary_mod.system.sets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyun.evolutionary_mod.Config;
import com.muyun.evolutionary_mod.EvolutionaryMod;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 套装平衡参数（数据驱动优先，代码回退）。
 */
public final class SetBalanceConfig {
    private SetBalanceConfig() {}

    private static final String SETS_BASE_PATH = "data/evolutionary_mod/sets/";
    private static final AtomicInteger FALLBACK_HIT_COUNT = new AtomicInteger(0);
    private static final AtomicInteger INVALID_ENTRY_COUNT = new AtomicInteger(0);

    private static final DragonConfig FALLBACK_DRAGON = new DragonConfig(
            0.20, 1.5, 15.0, 0.15,
            0.30f, 10.0f, 200,
            0.25f, 5.0, 10.0f, 100
    );

    private static final Map<SetType, DragonConfig> DRAGON_CONFIGS = loadSetConfigs();

    public static DragonConfig dragon() {
        return DRAGON_CONFIGS.get(SetType.DRAGON);
    }

    public static DragonConfig getDragon(SetType setType) {
        return DRAGON_CONFIGS.getOrDefault(setType, dragon());
    }

    public static int getFallbackHitCount() {
        return FALLBACK_HIT_COUNT.get();
    }

    public static int getInvalidEntryCount() {
        return INVALID_ENTRY_COUNT.get();
    }

    public static int getLoadedEntryCount() {
        return DRAGON_CONFIGS.size();
    }

    private static Map<SetType, DragonConfig> loadSetConfigs() {
        Map<SetType, DragonConfig> map = new EnumMap<>(SetType.class);
        for (SetType type : SetType.values()) {
            map.put(type, loadDragonConfig(type));
        }
        return map;
    }

    private static DragonConfig loadDragonConfig(SetType setType) {
        String path = SETS_BASE_PATH + setType.getConfigKey() + "_set.json";
        try (InputStream is = SetBalanceConfig.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return useFallback(setType, path, "未找到配置文件");
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            return new DragonConfig(
                    readDouble(root, "two_piece_health_bonus", FALLBACK_DRAGON.twoPieceHealthBonus()),
                    readDouble(root, "two_piece_regen_multiplier", FALLBACK_DRAGON.twoPieceRegenMultiplier()),
                    readDouble(root, "four_piece_armor_bonus", FALLBACK_DRAGON.fourPieceArmorBonus()),
                    readDouble(root, "four_piece_damage_reduction", FALLBACK_DRAGON.fourPieceDamageReduction()),
                    (float) readDouble(root, "four_piece_shield_chance", FALLBACK_DRAGON.fourPieceShieldChance()),
                    (float) readDouble(root, "four_piece_shield_absorb_max", FALLBACK_DRAGON.fourPieceShieldAbsorbMax()),
                    readInt(root, "four_piece_shield_cooldown_ticks", FALLBACK_DRAGON.fourPieceShieldCooldownTicks()),
                    (float) readDouble(root, "six_piece_breath_chance", FALLBACK_DRAGON.sixPieceBreathChance()),
                    readDouble(root, "six_piece_breath_range", FALLBACK_DRAGON.sixPieceBreathRange()),
                    (float) readDouble(root, "six_piece_breath_damage", FALLBACK_DRAGON.sixPieceBreathDamage()),
                    readInt(root, "six_piece_burn_ticks", FALLBACK_DRAGON.sixPieceBurnTicks())
            );
        } catch (Exception e) {
            EvolutionaryMod.LOGGER.error("[SetBalanceConfig] 加载套装配置失败: {}", setType.name(), e);
            return useFallback(setType, path, "解析异常");
        }
    }

    private static DragonConfig useFallback(SetType setType, String path, String reason) {
        FALLBACK_HIT_COUNT.incrementAndGet();
        if (Config.isStrictDataDriven()) {
            throw new IllegalStateException("[STRICT] Set config fallback triggered: " + setType.name()
                    + ", reason=" + reason + ", path=" + path);
        }
        EvolutionaryMod.LOGGER.warn("[SetBalanceConfig] 套装配置回退默认值: {} ({})", path, reason);
        return FALLBACK_DRAGON;
    }

    private static double readDouble(JsonObject root, String key, double fallback) {
        if (!root.has(key)) {
            INVALID_ENTRY_COUNT.incrementAndGet();
            return fallback;
        }
        try {
            return root.get(key).getAsDouble();
        } catch (Exception e) {
            INVALID_ENTRY_COUNT.incrementAndGet();
            return fallback;
        }
    }

    private static int readInt(JsonObject root, String key, int fallback) {
        if (!root.has(key)) {
            INVALID_ENTRY_COUNT.incrementAndGet();
            return fallback;
        }
        try {
            return root.get(key).getAsInt();
        } catch (Exception e) {
            INVALID_ENTRY_COUNT.incrementAndGet();
            return fallback;
        }
    }

    public record DragonConfig(
            double twoPieceHealthBonus,
            double twoPieceRegenMultiplier,
            double fourPieceArmorBonus,
            double fourPieceDamageReduction,
            float fourPieceShieldChance,
            float fourPieceShieldAbsorbMax,
            int fourPieceShieldCooldownTicks,
            float sixPieceBreathChance,
            double sixPieceBreathRange,
            float sixPieceBreathDamage,
            int sixPieceBurnTicks
    ) {}
}

