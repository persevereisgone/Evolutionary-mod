package com.muyun.evolutionary_mod.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 锻造材料全局战利品修改器 - Forge Material Global Loot Modifier
 *
 * 按 material_drops.json 档位独立判定追加掉落：
 * - 三档材料：类型精华 / 品阶碎片 / 属性精华（独立判定，比例 6:2:1）
 * - 三种重锻石：普通 / 高级 / 锁定（独立判定）
 * - 子类按权重细分：类型精华 1:1:1、碎片 6/5/4/3/2/1、属性精华按方向权重
 *
 * 规则要点（§8.2.1）：
 * - 幸运加成：开箱取 THIS_ENTITY、击杀取 KILLER_ENTITY；原版普通怪/精英向固定概率不吃幸运
 * - 不受 Looting 影响
 * - 重锻石数量：非 Boss 默认 1 个（10% 概率额外 +1）；Boss 2~4（由 JSON count 控制）
 */
public class ForgeMaterialLootModifier extends LootModifier {

    private static final Random RANDOM = new Random();
    private static final double BONUS_EXTRA_CHANCE = 0.10;

    private static final String REROLL_STONE_NORMAL = "evolutionary_mod:reroll_stone";
    private static final String REROLL_STONE_ADVANCED = "evolutionary_mod:reroll_stone_advanced";
    private static final String REROLL_STONE_LOCK = "evolutionary_mod:reroll_stone_lock";

    public static final MapCodec<ForgeMaterialLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .apply(inst, ForgeMaterialLootModifier::new));

    public ForgeMaterialLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation tableId = context.getQueriedLootTableId();
        ForgeMaterialDrops.TierConfig tier = ForgeMaterialDrops.getTier(tableId);
        if (tier == null) return generatedLoot;

        boolean boss = isBossTable(tableId);
        float luck = resolveLuck(context);

        // 三档材料独立判定
        rollMaterial(generatedLoot, tier.essenceChance(), tier.essenceCountMin(), tier.essenceCountMax(),
                ForgeMaterialDrops.getWeights().essenceWeights(), luck, boss);
        rollMaterial(generatedLoot, tier.shardChance(), tier.shardCountMin(), tier.shardCountMax(),
                ForgeMaterialDrops.getWeights().shardWeights(), luck, false);
        rollMaterial(generatedLoot, tier.attributeChance(), tier.attributeCountMin(), tier.attributeCountMax(),
                ForgeMaterialDrops.getWeights().attributeWeights(), luck, false);

        // 三种重锻石独立判定
        rollStone(generatedLoot, tier.normalStone(), REROLL_STONE_NORMAL, luck, boss);
        rollStone(generatedLoot, tier.advancedStone(), REROLL_STONE_ADVANCED, luck, boss);
        rollStone(generatedLoot, tier.lockStone(), REROLL_STONE_LOCK, luck, boss);

        return generatedLoot;
    }

    /** Boss 判定（用于材料数量 1~3 与重锻石数量 2~4；count 由 JSON 指定）。 */
    private static boolean isBossTable(ResourceLocation tableId) {
        return tableId.getNamespace().equals("minecraft")
                && (tableId.getPath().equals("entities/elder_guardian")
                    || tableId.getPath().equals("entities/wither")
                    || tableId.getPath().equals("entities/ender_dragon"));
    }

    /**
     * 幸运值解析：
     * - 开箱场景：THIS_ENTITY 为玩家
     * - 击杀场景：THIS_ENTITY 为被击杀怪物，需取 LAST_DAMAGE_PLAYER
     *   （1.21.1 中击杀幸运参数，对应策划案 §8.2.1 规则 2 的 KILLER_ENTITY 意图）
     */
    private static float resolveLuck(LootContext context) {
        Player player = null;
        if (context.hasParam(LootContextParams.THIS_ENTITY)
                && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player p) {
            player = p;
        }
        if (player == null && context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
            player = context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
        }
        if (player == null) return 0f;
        return (float) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.LUCK) * 0.01f;
    }

    private void rollMaterial(ObjectArrayList<ItemStack> out, double chance, int min, int max,
                              Map<String, Integer> weights, float luckBonus, boolean boss) {
        if (chance <= 0 || max <= 0) return;
        double finalChance = Math.min(1.0, chance + luckBonus);
        if (RANDOM.nextDouble() > finalChance) return;
        int count = randInt(min, max);
        String pick = pickWeighted(weights);
        if (pick == null) return;
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(pick));
        if (item == null) return;
        out.add(new ItemStack(item, count));
    }

    private void rollStone(ObjectArrayList<ItemStack> out, ForgeMaterialDrops.StoneConfig stone,
                           String itemId, float luckBonus, boolean boss) {
        if (stone == null || stone.chance() <= 0) return;
        // fixed=true（原版普通怪/精英向）不吃幸运与 Looting；其余吃幸运
        double finalChance = stone.fixed() ? stone.chance() : Math.min(1.0, stone.chance() + luckBonus);
        if (RANDOM.nextDouble() > finalChance) return;
        int count = randInt(stone.countMin(), stone.countMax());
        // 非 Boss 默认 1 个，10% 概率额外 +1；原版怪（fixed）固定 1 个不加成
        if (!boss && !stone.fixed() && count == 1 && RANDOM.nextDouble() < BONUS_EXTRA_CHANCE) {
            count = 2;
        }
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item == null) return;
        out.add(new ItemStack(item, count));
    }

    private static int randInt(int min, int max) {
        if (max <= min) return min;
        return min + RANDOM.nextInt(max - min + 1);
    }

    private static String pickWeighted(Map<String, Integer> weights) {
        if (weights == null || weights.isEmpty()) return null;
        List<String> keys = new ArrayList<>(weights.keySet());
        int total = keys.stream().mapToInt(weights::get).sum();
        if (total <= 0) return null;
        int roll = RANDOM.nextInt(total);
        for (String k : keys) {
            roll -= weights.get(k);
            if (roll < 0) return k;
        }
        return keys.get(keys.size() - 1);
    }
}
