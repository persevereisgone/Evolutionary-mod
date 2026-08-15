package com.muyun.evolutionary_mod.system.forge;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import com.muyun.evolutionary_mod.loot.AccessoryDropRarity;
import com.muyun.evolutionary_mod.loot.AttributeRollRanges;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 锻造台服务端核心逻辑 - Forge System
 *
 * 对应策划案 §4 强化 / §4.5 属性精华 / §5 重锻 / §6 粉碎。
 * 所有数值来自 ForgeConfig（JSON 数据驱动，回退常量）。
 *
 * 核心原则：
 * - 强化层独立 DataComponent（FORGE_ENHANCEMENT），基础词条在 ACCESSORY_ATTRIBUTES
 * - 强化方向池 = 物品已有属性集合（§4.3，V1.14）
 * - 强化增量 = ranges.json 基础区间 × 品阶每级系数（§4.3.1）
 * - 普通重锻保留强化层；高级重锻重滚基础 + 强化（偏高）；锁定石搭配使用
 * - 粉碎：品阶碎片 50% 返还、类型精华 25%/份、重锻石不返
 */
public class ForgeSystem {

    public static final Random RANDOM = new Random();

    /** 操作结果：携带返回给 UI 的反馈信息。returns 为粉碎返还物品（item id -> count）。 */
    public record ForgeResult(boolean success, String messageKey, ItemStack output,
                              java.util.Map<String, Integer> returns) {
        public ForgeResult(boolean success, String messageKey, ItemStack output) {
            this(success, messageKey, output, java.util.Map.of());
        }
    }

    // =========================================================
    // 强化方向键 ↔ AccessoryAttributes 字段读写
    // =========================================================

    public static final List<String> ALL_DIRECTIONS = List.of(
            "max_health", "attack_damage", "armor", "crit_chance", "crit_damage",
            "damage_reduction", "armor_penetration", "movement_speed", "health_regen", "luck");

    public static double getAttr(AccessoryAttributes a, String key) {
        return switch (key) {
            case "max_health" -> a.maxHealth();
            case "attack_damage" -> a.attackDamage();
            case "armor" -> a.armor();
            case "movement_speed" -> a.movementSpeed();
            case "luck" -> a.luck();
            case "health_regen" -> a.healthRegen();
            case "armor_penetration" -> a.armorPenetration();
            case "crit_chance" -> a.critChance();
            case "crit_damage" -> a.critDamage();
            case "damage_reduction" -> a.damageReduction();
            default -> 0;
        };
    }

    public static AccessoryAttributes setAttr(AccessoryAttributes a, String key, double v) {
        return switch (key) {
            case "max_health" -> a.withMaxHealth(v);
            case "attack_damage" -> a.withAttackDamage(v);
            case "armor" -> a.withArmor(v);
            case "movement_speed" -> a.withMovementSpeed(v);
            case "luck" -> a.withLuck(v);
            case "health_regen" -> a.withHealthRegen(v);
            case "armor_penetration" -> a.withArmorPenetration(v);
            case "crit_chance" -> a.withCritChance(v);
            case "crit_damage" -> a.withCritDamage(v);
            case "damage_reduction" -> a.withDamageReduction(v);
            default -> a;
        };
    }

    // =========================================================
    // 强化（§4）
    // =========================================================

    /**
     * 单次强化。校验后消耗材料并掷骰，无论成败都记入 spent_materials（§4.2）。
     *
     * @param stack       目标装备
     * @param essenceType 类型精华 item id（如 accessory_essence），V1 仅饰品精华可用
     * @param shardId     品阶碎片 item id（rank_shard_*）
     * @param shardCount  同种碎片数量 [1,10]
     * @return 结果（output 为修改后的装备或原装备）
     */
    public static ForgeResult enhance(ItemStack stack, String essenceType, String shardId, int shardCount) {
        if (stack.isEmpty() || !(stack.getItem() instanceof AccessoryItem)) {
            return fail("forge.message.not_accessory");
        }
        // V1 仅饰品精华可用（§3.1 武器/护甲精华挂「未开放」）
        if (!"evolutionary_mod:accessory_essence".equals(essenceType)) {
            return fail("forge.message.essence_unavailable");
        }
        AccessoryAttributes base = stack.getOrDefault(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), AccessoryAttributes.EMPTY);
        // 强化方向池 = 物品已有属性（§4.3）：无基础词条时强化不可用
        List<String> activeDirs = activeDirections(base);
        if (activeDirs.isEmpty()) {
            return fail("forge.message.no_base_attrs");
        }
        ForgeEnhancement fe = stack.getOrDefault(EvolutionaryMod.FORGE_ENHANCEMENT.get(), ForgeEnhancement.EMPTY);
        int maxLevel = ForgeConfig.enhanceCosts().maxLevel();
        if (fe.enhanceLevel() >= maxLevel) {
            return fail("forge.message.max_level");
        }
        if (shardCount < 1 || shardCount > ForgeConfig.enhanceCosts().maxShardsPerTry()) {
            return fail("forge.message.bad_shard_count");
        }

        // 品阶判定
        AccessoryDropRarity itemRarity = rarityOf(stack);
        AccessoryDropRarity shardRarity = rarityOfShard(shardId);
        if (shardRarity == null) return fail("forge.message.bad_shard");
        int delta = itemRarity.ordinal() - shardRarity.ordinal();
        double perShard = ForgeConfig.enhanceCosts().successBonus().getOrDefault(delta, 0.0);
        double success = Math.min(1.0, shardCount * perShard);

        // 消耗：1× 类型精华 + 全部碎片，记入 spent（§4.2.3）
        ForgeEnhancement spent = fe
                .withSpentEssence(fe.spentEssence() + ForgeConfig.enhanceCosts().essencePerTry())
                .withSpentShards(addShard(fe.spentShards(), shardId, shardCount));

        boolean won = RANDOM.nextDouble() < success;
        if (!won) {
            stack.set(EvolutionaryMod.FORGE_ENHANCEMENT.get(), spent);
            return new ForgeResult(false, "forge.message.enhance_fail", stack);
        }

        // 成功：+1 阶，抽 1~2 条方向（§4.2.5）
        int newLevel = fe.enhanceLevel() + 1;
        AccessoryAttributes bonus = rollBonus(stack, base, spent.bonus(), itemRarity);
        ForgeEnhancement out = spent.withEnhanceLevel(newLevel).withBonus(bonus);
        stack.set(EvolutionaryMod.FORGE_ENHANCEMENT.get(), out);
        return new ForgeResult(true, "forge.message.enhance_success", stack);
    }

    /** 从基础词条中提取非 0 字段（强化方向池，§4.3）。 */
    public static List<String> activeDirections(AccessoryAttributes base) {
        List<String> dirs = new ArrayList<>();
        for (String key : ALL_DIRECTIONS) {
            if (getAttr(base, key) != 0) dirs.add(key);
        }
        return dirs;
    }

    /**
     * 抽取强化加成：75% 1 条 / 25% 2 条；方向按权重（§4.3），数值 = 基础区间 × 品阶系数（§4.3.1）。
     */
    private static AccessoryAttributes rollBonus(ItemStack stack, AccessoryAttributes base,
                                                 AccessoryAttributes current, AccessoryDropRarity rarity) {
        AccessoryAttributes result = current == null ? AccessoryAttributes.EMPTY : current;
        List<String> dirs = activeDirections(base);
        if (dirs.isEmpty()) return result;
        double factor = ForgeConfig.enhanceRolls().rarityFactor().getOrDefault(rarity.name(), 0.05);
        double roll = RANDOM.nextDouble();
        int count = roll < ForgeConfig.enhanceRolls().rollOneChance() ? 1 : 2;

        for (int i = 0; i < count && !dirs.isEmpty(); i++) {
            String dir = pickWeightedDirection(dirs, stack);
            double[] baseRange = AttributeRollRanges.baseRange(itemPath(stack), dir);
            double min, max;
            if (baseRange == null) {
                // 该物品无此属性数据驱动配置 → 按品阶标准区间
                double[] std = ForgeConfig.essenceRange(dir, rarity);
                if (std == null) continue;
                min = std[0] * factor; max = std[1] * factor;
            } else {
                min = baseRange[0] * factor; max = baseRange[1] * factor;
            }
            double inc = round3(min + RANDOM.nextDouble() * (max - min));
            result = setAttr(result, dir, round3(getAttr(result, dir) + inc));
        }
        return result;
    }

    private static String pickWeightedDirection(List<String> dirs, ItemStack stack) {
        Map<String, Integer> weights = ForgeConfig.enhanceRolls().directionWeights();
        int total = 0;
        for (String d : dirs) total += weights.getOrDefault(d, 1);
        int roll = RANDOM.nextInt(total);
        for (String d : dirs) {
            roll -= weights.getOrDefault(d, 1);
            if (roll < 0) return d;
        }
        return dirs.get(dirs.size() - 1);
    }

    // =========================================================
    // 属性精华（§4.5）
    // =========================================================

    /**
     * 属性精华操作：给「没有该属性」的物品添加该属性基础词条；必成功；占用强化次数。
     *
     * @param stack     目标装备
     * @param attrKey   属性字段名（max_health / attack_damage / ...）
     * @return 结果
     */
    public static ForgeResult addAttributeEssence(ItemStack stack, String attrKey) {
        if (stack.isEmpty() || !(stack.getItem() instanceof AccessoryItem)) {
            return fail("forge.message.not_accessory");
        }
        AccessoryAttributes base = stack.getOrDefault(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), AccessoryAttributes.EMPTY);
        if (getAttr(base, attrKey) != 0) {
            return fail("forge.message.attr_exists");
        }
        ForgeEnhancement fe = stack.getOrDefault(EvolutionaryMod.FORGE_ENHANCEMENT.get(), ForgeEnhancement.EMPTY);
        if (fe.enhanceLevel() >= ForgeConfig.enhanceCosts().maxLevel()) {
            return fail("forge.message.max_level");
        }
        AccessoryDropRarity rarity = rarityOf(stack);
        double[] std = ForgeConfig.essenceRange(attrKey, rarity);
        if (std == null) return fail("forge.message.no_essence_range");
        double value = round3(std[0] + RANDOM.nextDouble() * (std[1] - std[0]));

        AccessoryAttributes newBase = setAttr(base, attrKey, value);
        stack.set(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), newBase);
        // 强化次数 +1，并记 essence_added（§4.5 / 实装注意事项 9）
        ForgeEnhancement out = fe
                .withEnhanceLevel(fe.enhanceLevel() + 1)
                .withEssenceAdded(fe.essenceAdded() + 1);
        stack.set(EvolutionaryMod.FORGE_ENHANCEMENT.get(), out);
        return new ForgeResult(true, "forge.message.attr_essence_added", stack);
    }

    // =========================================================
    // 重锻（§5）
    // =========================================================

    /**
     * 重锻：普通石重滚基础词条（保留维度与强化层）；高级石重滚基础 + 强化（偏高）。
     * 锁定石搭配使用，被锁条目原样保留。
     *
     * @param stack      目标装备
     * @param stoneId    普通/高级石 item id
     * @param lockKeys   锁定词条（基础字段名，高级石时可含强化方向键）
     * @param lockCount  锁定石数量（<= maxLockStones）
     * @return 结果
     */
    public static ForgeResult reroll(ItemStack stack, String stoneId, List<String> lockKeys, int lockCount) {
        if (stack.isEmpty() || !(stack.getItem() instanceof AccessoryItem)) {
            return fail("forge.message.not_accessory");
        }
        boolean advanced = "evolutionary_mod:reroll_stone_advanced".equals(stoneId);
        boolean normal = "evolutionary_mod:reroll_stone".equals(stoneId);
        if (!normal && !advanced) return fail("forge.message.bad_stone");
        if (lockCount > ForgeConfig.rerollRules().maxLockStones()) {
            return fail("forge.message.too_many_locks");
        }
        if (lockCount > 0 && lockKeys.size() != lockCount) {
            return fail("forge.message.lock_mismatch");
        }
        AccessoryAttributes base = stack.getOrDefault(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), AccessoryAttributes.EMPTY);
        if (base.isEmpty()) return fail("forge.message.no_base_attrs");

        // 锁定基础词条
        AccessoryAttributes newBase = base;
        for (String key : ALL_DIRECTIONS) {
            if (lockKeys.contains(key)) continue;
            newBase = setAttr(newBase, key, rollValueForKey(stack, key));
        }
        stack.set(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), newBase);

        // 高级石：重滚强化加成（偏高），保留 enhance_level / spent_materials（§5.2）
        if (advanced) {
            ForgeEnhancement fe = stack.getOrDefault(EvolutionaryMod.FORGE_ENHANCEMENT.get(), ForgeEnhancement.EMPTY);
            AccessoryAttributes bonus = AccessoryAttributes.EMPTY;
            for (String key : ALL_DIRECTIONS) {
                if (lockKeys.contains(key)) {
                    bonus = setAttr(bonus, key, getAttr(fe.bonus(), key));
                    continue;
                }
                bonus = setAttr(bonus, key, rollBonusForKey(stack, key, fe.enhanceLevel()));
            }
            stack.set(EvolutionaryMod.FORGE_ENHANCEMENT.get(), fe.withBonus(bonus));
        }
        return new ForgeResult(true, advanced ? "forge.message.reroll_advanced" : "forge.message.reroll_normal", stack);
    }

    /** 重锻基础词条取值：物品自身 ranges.json 有该属性 → 标准滚动；否则品阶标准区间（§5.2.1）。 */
    private static double rollValueForKey(ItemStack stack, String key) {
        AccessoryDropRarity rarity = rarityOf(stack);
        double[] baseRange = AttributeRollRanges.baseRange(itemPath(stack), key);
        if (baseRange != null) {
            return round3(baseRange[0] + RANDOM.nextDouble() * (baseRange[1] - baseRange[0]));
        }
        double[] std = ForgeConfig.essenceRange(key, rarity);
        if (std != null) return round3(std[0] + RANDOM.nextDouble() * (std[1] - std[0]));
        return 0;
    }

    /** 高级重锻强化加成：按当前强化阶每级增量偏高重抽（§5.2 有效区间下沿上移 bias）。 */
    private static double rollBonusForKey(ItemStack stack, String key, int enhanceLevel) {
        if (enhanceLevel <= 0) return 0;
        AccessoryDropRarity rarity = rarityOf(stack);
        double factor = ForgeConfig.enhanceRolls().rarityFactor().getOrDefault(rarity.name(), 0.05);
        double[] baseRange = AttributeRollRanges.baseRange(itemPath(stack), key);
        double min, max;
        if (baseRange != null) {
            min = baseRange[0] * factor; max = baseRange[1] * factor;
        } else {
            double[] std = ForgeConfig.essenceRange(key, rarity);
            if (std == null) return 0;
            min = std[0] * factor; max = std[1] * factor;
        }
        double bias = ForgeConfig.rerollRules().advancedBiasShift();
        double span = max - min;
        double low = min + span * bias;
        return round3(low + RANDOM.nextDouble() * (max - low));
    }

    // =========================================================
    // 粉碎（§6）
    // =========================================================

    /**
     * 粉碎返还：销毁装备，按 spent_materials 结算返还。
     * 返还物品通过 result.returns()（item id -> count）返回，由调用方发放。
     *
     * @return 结果（output 为空表示装备已销毁）
     */
    public static ForgeResult smash(ItemStack stack) {
        if (stack.isEmpty()) return fail("forge.message.no_item");
        ForgeEnhancement fe = stack.getOrDefault(EvolutionaryMod.FORGE_ENHANCEMENT.get(), ForgeEnhancement.EMPTY);
        if (!fe.hasSpentMaterials() && fe.enhanceLevel() == 0) {
            return fail("forge.message.not_enhanced");
        }
        // 品阶碎片 50% 返还（§6.1）：floor(spent/2) 必返 + 奇数 50% 再 +1
        Map<String, Integer> returns = new HashMap<>();
        for (Map.Entry<String, Integer> e : fe.spentShards().entrySet()) {
            int spent = e.getValue();
            int total = spent / 2;
            if (spent % 2 == 1 && RANDOM.nextDouble() < 0.5) total += 1;
            if (total > 0) returns.put(e.getKey(), total);
        }
        // 类型精华 25%/份 独立返还（§6.2）
        for (int i = 0; i < fe.spentEssence(); i++) {
            if (RANDOM.nextDouble() < ForgeConfig.rerollRules().smashEssenceRate()) {
                returns.merge("evolutionary_mod:accessory_essence", 1, Integer::sum);
            }
        }
        return new ForgeResult(true, "forge.message.smash", stack, returns);
    }

    // =========================================================
    // 工具
    // =========================================================

    private static ForgeResult fail(String key) {
        return new ForgeResult(false, key, ItemStack.EMPTY);
    }

    private static Map<String, Integer> addShard(Map<String, Integer> map, String id, int count) {
        Map<String, Integer> copy = new HashMap<>(map);
        copy.merge(id, count, Integer::sum);
        return copy;
    }

    public static String itemPath(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    /** 从物品注册名前缀判断品阶。 */
    public static AccessoryDropRarity rarityOf(ItemStack stack) {
        String path = itemPath(stack).toLowerCase();
        if (path.startsWith("broken_")) return AccessoryDropRarity.BROKEN;
        if (path.startsWith("excellent_")) return AccessoryDropRarity.EXCELLENT;
        if (path.startsWith("epic_")) return AccessoryDropRarity.EPIC;
        if (path.startsWith("legendary_")) return AccessoryDropRarity.LEGENDARY;
        if (path.startsWith("mythic_")) return AccessoryDropRarity.MYTHIC;
        return AccessoryDropRarity.NORMAL;
    }

    /** 品阶碎片 → 品阶（rank_shard_*）。 */
    public static AccessoryDropRarity rarityOfShard(String shardId) {
        return switch (shardId) {
            case "evolutionary_mod:rank_shard_broken" -> AccessoryDropRarity.BROKEN;
            case "evolutionary_mod:rank_shard_normal" -> AccessoryDropRarity.NORMAL;
            case "evolutionary_mod:rank_shard_excellent" -> AccessoryDropRarity.EXCELLENT;
            case "evolutionary_mod:rank_shard_epic" -> AccessoryDropRarity.EPIC;
            case "evolutionary_mod:rank_shard_legendary" -> AccessoryDropRarity.LEGENDARY;
            case "evolutionary_mod:rank_shard_mythic" -> AccessoryDropRarity.MYTHIC;
            default -> null;
        };
    }

    public static boolean isRankShard(String itemId) {
        return rarityOfShard(itemId) != null;
    }

    public static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
