package com.muyun.evolutionary_mod.system.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

/**
 * 强化层 Data Component - Forge Enhancement
 *
 * 对应策划案 §4.1：锻造台对物品写入独立强化层，与基础词条（ACCESSORY_ATTRIBUTES）分离。
 *
 * 字段：
 * - enhanceLevel：强化阶 0~10（属性精华添加也 +1，共用上限，见 §4.5）
 * - essenceAdded：属性精华添加次数（UI 展示 / 重锻判断，见实装注意事项 9）
 * - bonus：累计强化加成（键与基础词条一致，最终属性 = 基础 + bonus，见 §4.1.2）
 * - spentShards：历史消耗的品阶碎片（item id -> count），供粉碎 50% 返还（§6.1）
 * - spentEssence：历史消耗的类型精华总数，供粉碎 25%/份 独立返还（§6.2）
 */
public record ForgeEnhancement(
        int enhanceLevel,
        int essenceAdded,
        AccessoryAttributes bonus,
        Map<String, Integer> spentShards,
        int spentEssence
) {

    public static final ForgeEnhancement EMPTY = new ForgeEnhancement(
            0, 0, AccessoryAttributes.EMPTY, new HashMap<>(), 0);

    public static final Codec<ForgeEnhancement> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("enhance_level", 0).forGetter(ForgeEnhancement::enhanceLevel),
            Codec.INT.optionalFieldOf("essence_added", 0).forGetter(ForgeEnhancement::essenceAdded),
            AccessoryAttributes.CODEC.optionalFieldOf("bonus", AccessoryAttributes.EMPTY).forGetter(ForgeEnhancement::bonus),
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .optionalFieldOf("spent_shards", new HashMap<>()).forGetter(ForgeEnhancement::spentShards),
            Codec.INT.optionalFieldOf("spent_essence", 0).forGetter(ForgeEnhancement::spentEssence)
    ).apply(inst, ForgeEnhancement::new));

    public static final StreamCodec<FriendlyByteBuf, ForgeEnhancement> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ForgeEnhancement decode(FriendlyByteBuf buf) {
                    int level = buf.readVarInt();
                    int essenceAdded = buf.readVarInt();
                    AccessoryAttributes bonus = AccessoryAttributes.STREAM_CODEC.decode(buf);
                    Map<String, Integer> shards = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readVarInt);
                    int spentEssence = buf.readVarInt();
                    return new ForgeEnhancement(level, essenceAdded, bonus, shards, spentEssence);
                }

                @Override
                public void encode(FriendlyByteBuf buf, ForgeEnhancement f) {
                    buf.writeVarInt(f.enhanceLevel());
                    buf.writeVarInt(f.essenceAdded());
                    AccessoryAttributes.STREAM_CODEC.encode(buf, f.bonus());
                    buf.writeMap(f.spentShards(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeVarInt);
                    buf.writeVarInt(f.spentEssence());
                }
            };

    public boolean isEmpty() {
        return enhanceLevel == 0 && essenceAdded == 0 && bonus.isEmpty()
                && spentShards.isEmpty() && spentEssence == 0;
    }

    public boolean hasSpentMaterials() {
        return !spentShards.isEmpty() || spentEssence > 0;
    }

    // -----------------------------------------------------------------------
    // 变更方法（不可变 copy）
    // -----------------------------------------------------------------------

    public ForgeEnhancement withEnhanceLevel(int level) {
        return new ForgeEnhancement(level, essenceAdded, bonus, spentShards, spentEssence);
    }

    public ForgeEnhancement withEssenceAdded(int count) {
        return new ForgeEnhancement(enhanceLevel, count, bonus, spentShards, spentEssence);
    }

    public ForgeEnhancement withBonus(AccessoryAttributes newBonus) {
        return new ForgeEnhancement(enhanceLevel, essenceAdded, newBonus, spentShards, spentEssence);
    }

    public ForgeEnhancement withSpentShards(Map<String, Integer> shards) {
        return new ForgeEnhancement(enhanceLevel, essenceAdded, bonus, shards, spentEssence);
    }

    public ForgeEnhancement withSpentEssence(int count) {
        return new ForgeEnhancement(enhanceLevel, essenceAdded, bonus, spentShards, count);
    }
}
