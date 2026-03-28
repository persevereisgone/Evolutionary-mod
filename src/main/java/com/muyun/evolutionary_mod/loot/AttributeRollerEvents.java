package com.muyun.evolutionary_mod.loot;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Random;

/**
 * 随机属性掉落系统 - Attribute Roller Events
 *
 * NeoForge 1.21.1: 使用 DataComponentType 替代旧版 ItemStack NBT (getTag/hasTag)。
 * 监听 LivingDropsEvent，在饰品掉落时为其随机滚动属性词条并写入 DataComponent。
 *
 * 词条滚动规则：
 * - 每件饰品固定拥有 2 个主词条（由物品类型决定）
 * - 每件饰品额外随机获得 0~2 个次要词条
 * - 每个词条的数值在 [base * 0.8, base * 1.2] 范围内随机浮动
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AttributeRollerEvents {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        for (net.minecraft.world.entity.item.ItemEntity itemEntity : event.getDrops()) {
            ItemStack stack = itemEntity.getItem();
            if (!(stack.getItem() instanceof AccessoryItem)) continue;
            // 若已有词条（如玩家手动给予的物品），跳过
            if (stack.has(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get())) continue;

            AccessoryAttributes rolled = rollAttributes(stack);
            if (!rolled.isEmpty()) {
                stack.set(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get(), rolled);
            }
        }
    }

    /**
     * 根据物品注册名判断饰品类型并随机滚动词条。
     * 所有区间常量定义在 {@link AttributeRollRanges}。
     */
    public static AccessoryAttributes rollAttributes(ItemStack stack) {
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(stack.getItem()).getPath();
        String lower = path.toLowerCase();

        double maxHealth = 0, attackDamage = 0, armor = 0;
        double movementSpeed = 0, luck = 0, healthRegen = 0;
        double armorPenetration = 0, critChance = 0, critDamage = 0, damageReduction = 0;

        // ---- 戒指 ----
        if (lower.contains("_ring")) {
            if (lower.contains("life") || lower.contains("healing")) {
                maxHealth   = randRange(AttributeRollRanges.RING_LIFE_MAX_HEALTH_MIN,   AttributeRollRanges.RING_LIFE_MAX_HEALTH_MAX);
                healthRegen = randRange(AttributeRollRanges.RING_LIFE_HEALTH_REGEN_MIN, AttributeRollRanges.RING_LIFE_HEALTH_REGEN_MAX);
            } else if (lower.contains("battle") || lower.contains("sharp")) {
                attackDamage = randRange(AttributeRollRanges.RING_BATTLE_ATTACK_DAMAGE_MIN, AttributeRollRanges.RING_BATTLE_ATTACK_DAMAGE_MAX);
                critChance   = randRange(AttributeRollRanges.RING_BATTLE_CRIT_CHANCE_MIN,   AttributeRollRanges.RING_BATTLE_CRIT_CHANCE_MAX);
                critDamage   = randRange(AttributeRollRanges.RING_BATTLE_CRIT_DAMAGE_MIN,   AttributeRollRanges.RING_BATTLE_CRIT_DAMAGE_MAX);
            } else if (lower.contains("iron") || lower.contains("shield")) {
                armor           = randRange(AttributeRollRanges.RING_SHIELD_ARMOR_MIN,            AttributeRollRanges.RING_SHIELD_ARMOR_MAX);
                damageReduction = randRange(AttributeRollRanges.RING_SHIELD_DAMAGE_REDUCTION_MIN, AttributeRollRanges.RING_SHIELD_DAMAGE_REDUCTION_MAX);
            } else if (lower.contains("gale")) {
                movementSpeed = randRange(AttributeRollRanges.RING_GALE_MOVEMENT_SPEED_MIN, AttributeRollRanges.RING_GALE_MOVEMENT_SPEED_MAX);
            } else if (lower.contains("fortune")) {
                luck = randRange(AttributeRollRanges.RING_FORTUNE_LUCK_MIN, AttributeRollRanges.RING_FORTUNE_LUCK_MAX);
            } else if (lower.contains("armor_breaker")) {
                armorPenetration = randRange(AttributeRollRanges.RING_BREAKER_ARMOR_PENETRATION_MIN, AttributeRollRanges.RING_BREAKER_ARMOR_PENETRATION_MAX);
                attackDamage     = randRange(AttributeRollRanges.RING_BREAKER_ATTACK_DAMAGE_MIN,     AttributeRollRanges.RING_BREAKER_ATTACK_DAMAGE_MAX);
            } else if (lower.contains("dragon")) {
                maxHealth    = randRange(AttributeRollRanges.RING_DRAGON_MAX_HEALTH_MIN,    AttributeRollRanges.RING_DRAGON_MAX_HEALTH_MAX);
                attackDamage = randRange(AttributeRollRanges.RING_DRAGON_ATTACK_DAMAGE_MIN, AttributeRollRanges.RING_DRAGON_ATTACK_DAMAGE_MAX);
                healthRegen  = randRange(AttributeRollRanges.RING_DRAGON_HEALTH_REGEN_MIN,  AttributeRollRanges.RING_DRAGON_HEALTH_REGEN_MAX);
                critChance   = randRange(AttributeRollRanges.RING_DRAGON_CRIT_CHANCE_MIN,   AttributeRollRanges.RING_DRAGON_CRIT_CHANCE_MAX);
                critDamage   = randRange(AttributeRollRanges.RING_DRAGON_CRIT_DAMAGE_MIN,   AttributeRollRanges.RING_DRAGON_CRIT_DAMAGE_MAX);
            } else {
                maxHealth    = randRange(AttributeRollRanges.RING_GENERIC_MAX_HEALTH_MIN,    AttributeRollRanges.RING_GENERIC_MAX_HEALTH_MAX);
                attackDamage = randRange(AttributeRollRanges.RING_GENERIC_ATTACK_DAMAGE_MIN, AttributeRollRanges.RING_GENERIC_ATTACK_DAMAGE_MAX);
            }
        }
        // ---- 项链 ----
        else if (lower.contains("necklace")) {
            maxHealth    = randRange(AttributeRollRanges.NECKLACE_MAX_HEALTH_MIN,    AttributeRollRanges.NECKLACE_MAX_HEALTH_MAX);
            attackDamage = randRange(AttributeRollRanges.NECKLACE_ATTACK_DAMAGE_MIN, AttributeRollRanges.NECKLACE_ATTACK_DAMAGE_MAX);
            armor        = randRange(AttributeRollRanges.NECKLACE_ARMOR_MIN,         AttributeRollRanges.NECKLACE_ARMOR_MAX);
            healthRegen  = randRange(AttributeRollRanges.NECKLACE_HEALTH_REGEN_MIN,  AttributeRollRanges.NECKLACE_HEALTH_REGEN_MAX);
        }
        // ---- 手镯 ----
        else if (lower.contains("bracelet")) {
            maxHealth     = randRange(AttributeRollRanges.BRACELET_MAX_HEALTH_MIN,     AttributeRollRanges.BRACELET_MAX_HEALTH_MAX);
            attackDamage  = randRange(AttributeRollRanges.BRACELET_ATTACK_DAMAGE_MIN,  AttributeRollRanges.BRACELET_ATTACK_DAMAGE_MAX);
            movementSpeed = randRange(AttributeRollRanges.BRACELET_MOVEMENT_SPEED_MIN, AttributeRollRanges.BRACELET_MOVEMENT_SPEED_MAX);
            luck          = randRange(AttributeRollRanges.BRACELET_LUCK_MIN,           AttributeRollRanges.BRACELET_LUCK_MAX);
        }
        // ---- 耳环 ----
        else if (lower.contains("earring")) {
            maxHealth     = randRange(AttributeRollRanges.EARRING_MAX_HEALTH_MIN,     AttributeRollRanges.EARRING_MAX_HEALTH_MAX);
            attackDamage  = randRange(AttributeRollRanges.EARRING_ATTACK_DAMAGE_MIN,  AttributeRollRanges.EARRING_ATTACK_DAMAGE_MAX);
            movementSpeed = randRange(AttributeRollRanges.EARRING_MOVEMENT_SPEED_MIN, AttributeRollRanges.EARRING_MOVEMENT_SPEED_MAX);
            healthRegen   = randRange(AttributeRollRanges.EARRING_HEALTH_REGEN_MIN,   AttributeRollRanges.EARRING_HEALTH_REGEN_MAX);
        }
        // ---- 头饰 ----
        else if (lower.contains("headwear")) {
            maxHealth   = randRange(AttributeRollRanges.HEADWEAR_MAX_HEALTH_MIN,   AttributeRollRanges.HEADWEAR_MAX_HEALTH_MAX);
            armor       = randRange(AttributeRollRanges.HEADWEAR_ARMOR_MIN,        AttributeRollRanges.HEADWEAR_ARMOR_MAX);
            luck        = randRange(AttributeRollRanges.HEADWEAR_LUCK_MIN,         AttributeRollRanges.HEADWEAR_LUCK_MAX);
            healthRegen = randRange(AttributeRollRanges.HEADWEAR_HEALTH_REGEN_MIN, AttributeRollRanges.HEADWEAR_HEALTH_REGEN_MAX);
        }
        // ---- 腰带 ----
        else if (lower.contains("belt")) {
            maxHealth     = randRange(AttributeRollRanges.BELT_MAX_HEALTH_MIN,     AttributeRollRanges.BELT_MAX_HEALTH_MAX);
            armor         = randRange(AttributeRollRanges.BELT_ARMOR_MIN,          AttributeRollRanges.BELT_ARMOR_MAX);
            attackDamage  = randRange(AttributeRollRanges.BELT_ATTACK_DAMAGE_MIN,  AttributeRollRanges.BELT_ATTACK_DAMAGE_MAX);
            movementSpeed = randRange(AttributeRollRanges.BELT_MOVEMENT_SPEED_MIN, AttributeRollRanges.BELT_MOVEMENT_SPEED_MAX);
        }
        // ---- 手套 ----
        else if (lower.contains("glove") || lower.contains("gauntlet")) {
            attackDamage     = randRange(AttributeRollRanges.GLOVE_ATTACK_DAMAGE_MIN,     AttributeRollRanges.GLOVE_ATTACK_DAMAGE_MAX);
            armorPenetration = randRange(AttributeRollRanges.GLOVE_ARMOR_PENETRATION_MIN, AttributeRollRanges.GLOVE_ARMOR_PENETRATION_MAX);
            critChance       = randRange(AttributeRollRanges.GLOVE_CRIT_CHANCE_MIN,       AttributeRollRanges.GLOVE_CRIT_CHANCE_MAX);
        }
        // ---- 肩饰 ----
        else if (lower.contains("shoulder") || lower.contains("pauldron")) {
            armor           = randRange(AttributeRollRanges.SHOULDER_ARMOR_MIN,            AttributeRollRanges.SHOULDER_ARMOR_MAX);
            damageReduction = randRange(AttributeRollRanges.SHOULDER_DAMAGE_REDUCTION_MIN, AttributeRollRanges.SHOULDER_DAMAGE_REDUCTION_MAX);
            maxHealth       = randRange(AttributeRollRanges.SHOULDER_MAX_HEALTH_MIN,       AttributeRollRanges.SHOULDER_MAX_HEALTH_MAX);
        }
        // ---- 脚饰 ----
        else if (lower.contains("anklet") || lower.contains("boot") || lower.contains("shoe")) {
            movementSpeed = randRange(AttributeRollRanges.ANKLET_MOVEMENT_SPEED_MIN, AttributeRollRanges.ANKLET_MOVEMENT_SPEED_MAX);
            armor         = randRange(AttributeRollRanges.ANKLET_ARMOR_MIN,          AttributeRollRanges.ANKLET_ARMOR_MAX);
            luck          = randRange(AttributeRollRanges.ANKLET_LUCK_MIN,           AttributeRollRanges.ANKLET_LUCK_MAX);
        }
        // ---- 通用回退 ----
        else {
            maxHealth    = randRange(AttributeRollRanges.GENERIC_MAX_HEALTH_MIN,    AttributeRollRanges.GENERIC_MAX_HEALTH_MAX);
            attackDamage = randRange(AttributeRollRanges.GENERIC_ATTACK_DAMAGE_MIN, AttributeRollRanges.GENERIC_ATTACK_DAMAGE_MAX);
        }

        return new AccessoryAttributes(
                maxHealth, attackDamage, armor, movementSpeed, luck,
                healthRegen, armorPenetration, critChance, critDamage, damageReduction);
    }

    /** 在 [min, max] 范围内随机取一个 double，保留 3 位有效小数 */
    private static double randRange(double min, double max) {
        double raw = min + RANDOM.nextDouble() * (max - min);
        return Math.round(raw * 1000.0) / 1000.0;
    }
}
