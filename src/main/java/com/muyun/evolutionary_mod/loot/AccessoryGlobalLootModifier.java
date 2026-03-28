package com.muyun.evolutionary_mod.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import java.util.Random;

/**
 * 饰品全局战利品修改器 - Accessory Global Loot Modifier
 *
 * 在符合条件的战利品表（怪物/宝箱）触发时，
 * 按配置的概率和品质权重向掉落列表中追加一件饰品，
 * 并通过 AttributeRollerEvents.rollAttributes 为其滚动随机词条。
 *
 * 掉落规则配置见 AccessoryDropTable，品质权重见 AccessoryDropRarity。
 */
public class AccessoryGlobalLootModifier extends LootModifier {

    private static final Random RANDOM = new Random();

    /**
     * Codec：用于从 JSON 反序列化此修改器（NeoForge 1.21.1 要求）。
     * 本修改器无额外字段，只需继承 LootModifier 的 conditions。
     */
    public static final MapCodec<AccessoryGlobalLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .apply(inst, AccessoryGlobalLootModifier::new));

    public AccessoryGlobalLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 1. 根据战利品表 ID 查找掉落配置
        ResourceLocation tableId = context.getQueriedLootTableId();
        AccessoryDropTable.DropConfig config = AccessoryDropTable.getConfig(tableId);
        if (config == null) return generatedLoot;

        // 2. 获取玩家幸运值，计算最终掉落概率
        //    每点幸运值提升 1% 掉落概率，可通过后续扩展调整系数
        float luckBonus = 0f;
        if (context.hasParam(LootContextParams.THIS_ENTITY)
                && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player player) {
            luckBonus = (float) player.getAttributeValue(Attributes.LUCK) * 0.01f;
        }
        float finalChance = Math.min(config.dropChance() + luckBonus, 1.0f);

        // 3. 概率判定
        if (RANDOM.nextFloat() > finalChance) return generatedLoot;

        // 4. 按权重随机选品质
        AccessoryDropRarity rarity = AccessoryDropRarity.rollRarity(config.allowedRarities(), RANDOM);

        // 5. 从该品质物品池随机选一件饰品
        ItemStack accessory = pickRandomAccessory(rarity);
        if (accessory.isEmpty()) return generatedLoot;

        // 6. 滚动随机词条（复用现有逻辑）
        AccessoryAttributes attrs = AttributeRollerEvents.rollAttributes(accessory);
        if (!attrs.isEmpty()) {
            accessory.set(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), attrs);
        }

        // 7. 加入掉落列表
        generatedLoot.add(accessory);
        return generatedLoot;
    }

    /**
     * 从注册表中收集指定品质的所有饰品，随机返回一件。
     *
     * 筛选条件：
     * - 物品为 AccessoryItem 实例
     * - 注册名命名空间为 evolutionary_mod
     * - 注册名路径符合品质前缀规则（NORMAL 品质无前缀但不得以其他品质前缀开头）
     */
    private static ItemStack pickRandomAccessory(AccessoryDropRarity rarity) {
        String prefix = rarity.registryPrefix();
        List<Item> candidates = new ArrayList<>();

        // 其他品质的前缀，用于排除 NORMAL 品质时过滤掉有前缀的物品
        String[] otherPrefixes = {"broken_", "excellent_", "epic_", "legendary_", "mythic_"};

        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof AccessoryItem)) continue;
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (!EvolutionaryMod.MODID.equals(id.getNamespace())) continue;
            String path = id.getPath();

            if (rarity == AccessoryDropRarity.NORMAL) {
                // NORMAL 品质：无品质前缀，但也不能是其他品质前缀开头
                boolean hasOtherPrefix = false;
                for (String p : otherPrefixes) {
                    if (path.startsWith(p)) { hasOtherPrefix = true; break; }
                }
                if (!hasOtherPrefix) candidates.add(item);
            } else {
                // 其他品质：以对应前缀开头
                if (path.startsWith(prefix)) candidates.add(item);
            }
        }

        if (candidates.isEmpty()) return ItemStack.EMPTY;
        return new ItemStack(candidates.get(RANDOM.nextInt(candidates.size())));
    }
}
