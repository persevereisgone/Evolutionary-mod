package com.muyun.evolutionary_mod.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.muyun.evolutionary_mod.system.combat.CritSystem;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import com.muyun.evolutionary_mod.system.effects.BeltEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BraceletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.EarringEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.HeadwearEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.NecklaceEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.RingEffectsHandler;
import com.muyun.evolutionary_mod.system.sets.SetBonus;
import com.muyun.evolutionary_mod.system.sets.SetSystem;
import com.muyun.evolutionary_mod.system.sets.SetType;

import java.util.List;

/**
 * 饰品工具提示助手类 - Accessory Tooltip Helper Class
 *
 * 提供统一的工具提示显示逻辑，所有饰品类型都可以复用。
 * Provides unified tooltip display logic that all accessory types can reuse.
 */
public class AccessoryTooltipHelper {

    /**
     * 饰品品阶枚举 - Accessory Rarity Enum
     */
    public enum AccessoryRarity {
        BROKEN(ChatFormatting.GRAY),      // 破损 - 淡灰色
        NORMAL(ChatFormatting.WHITE),     // 普通 - 白色
        EXCELLENT(ChatFormatting.BLUE),   // 优秀 - 蓝色
        EPIC(ChatFormatting.DARK_PURPLE), // 史诗 - 紫色
        LEGENDARY(ChatFormatting.GOLD),   // 传说 - 金色
        MYTHIC(ChatFormatting.RED);       // 至臻 - 亮红色

        public final ChatFormatting color;

        AccessoryRarity(ChatFormatting color) {
            this.color = color;
        }
    }

    /**
     * 从物品注册名解析品阶 - Parse rarity from item registry name
     *
     * @param registryName 注册名 / Registry name
     * @return 对应的品阶，如果无法解析则返回NORMAL / Corresponding rarity, returns NORMAL if cannot parse
     */
    public static AccessoryRarity getRarityFromRegistryName(String registryName) {
        if (registryName == null) return AccessoryRarity.NORMAL;

        String lowerName = registryName.toLowerCase();

        if (lowerName.startsWith("mythic_")) return AccessoryRarity.MYTHIC;
        if (lowerName.startsWith("legendary_")) return AccessoryRarity.LEGENDARY;
        if (lowerName.startsWith("epic_")) return AccessoryRarity.EPIC;
        if (lowerName.startsWith("excellent_")) return AccessoryRarity.EXCELLENT;
        if (lowerName.startsWith("broken_")) return AccessoryRarity.BROKEN;

        return AccessoryRarity.NORMAL;
    }

    /**
     * 获取物品的品阶颜色 - Get item's rarity color
     *
     * @param item 物品 / Item
     * @return 对应的颜色 / Corresponding color
     */
    public static ChatFormatting getItemRarityColor(Item item) {
        var key = BuiltInRegistries.ITEM.getKey(item);
        String registryPath = key == null ? null : key.getPath();
        AccessoryRarity rarity = getRarityFromRegistryName(registryPath);
        return rarity.color;
    }

    /**
     * 为饰品添加工具提示 - Add tooltips for accessories
     *
     * @param tooltip 工具提示列表 / The tooltip list to add to
     * @param item 要显示工具提示的物品 / The item to display tooltips for
     */
    public static void addAccessoryTooltips(List<Component> tooltip, Item item) {
        // 检查各种效果并添加相应的工具提示 - Check various effects and add corresponding tooltips
        addEffectTooltip(tooltip, RingEffectsHandler.getInstance().getEffect(item), "ring");
        addEffectTooltip(tooltip, BraceletEffectsHandler.getInstance().getEffect(item), "bracelet");
        addEffectTooltip(tooltip, NecklaceEffectsHandler.getInstance().getEffect(item), "necklace");
        addEffectTooltip(tooltip, EarringEffectsHandler.getInstance().getEffect(item), "earring");
        addEffectTooltip(tooltip, HeadwearEffectsHandler.getInstance().getEffect(item), "headwear");
        addEffectTooltip(tooltip, BeltEffectsHandler.getInstance().getEffect(item), "belt");
        addEffectTooltip(tooltip, CritSystem.getCritStats(item), "crit");
        addEffectTooltip(tooltip, DamageReductionSystem.getDamageReductionStats(item), "damage_reduction");

        // 如果没有任何效果，显示无效果提示 - If no effects, show no effects message
        if (RingEffectsHandler.getInstance().getEffect(item) == null &&
            BraceletEffectsHandler.getInstance().getEffect(item) == null &&
            NecklaceEffectsHandler.getInstance().getEffect(item) == null &&
            EarringEffectsHandler.getInstance().getEffect(item) == null &&
            HeadwearEffectsHandler.getInstance().getEffect(item) == null &&
            BeltEffectsHandler.getInstance().getEffect(item) == null &&
            CritSystem.getCritStats(item) == null &&
            DamageReductionSystem.getDamageReductionStats(item) == null) {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod.no_effects"));
        }
    }

    /**
     * 为饰品添加工具提示（支持随机属性） - Add tooltips for accessories (with random attributes support)
     *
     * @param tooltip 工具提示列表 / The tooltip list to add to
     * @param itemStack 要显示工具提示的物品堆栈 / The item stack to display tooltips for
     */
    public static void addAccessoryTooltips(List<Component> tooltip, ItemStack itemStack) {
        Item item = itemStack.getItem();
        //CompoundTag nbt = itemStack.getTag();

        // 检查是否有随机属性数据 - Check if there are random attribute data
        //boolean hasRandomAttributes = nbt != null && nbt.getBoolean("accessory_attrs_rolled");

        //if (hasRandomAttributes) {
            // 使用随机属性显示工具提示 - Use random attributes for tooltips
            addRandomAttributeTooltips(tooltip, itemStack);
        //} else {
            // 使用默认静态效果 - Use default static effects
            addAccessoryTooltips(tooltip, item);
        //}
        
        // 添加套装效果工具提示 - Add set bonus tooltips
        //addSetBonusTooltips(tooltip, item);
    }

    /**
     * 从ItemStack的NBT读取属性值 - Read attribute value from ItemStack NBT
     *
     * @param itemStack 物品堆栈 / Item stack
     * @param attributeName 属性名 / Attribute name
     * @param defaultValue 默认值 / Default value
     * @return 属性值 / Attribute value
     */
    private static double getAttributeValue(ItemStack itemStack, String attributeName, double defaultValue) {
        //CompoundTag nbt = itemStack.getTag();
//        if (nbt != null && nbt.contains("accessory_attr_" + attributeName)) {
//            return nbt.getDouble("accessory_attr_" + attributeName);
//        }
        return defaultValue;
    }

    /**
     * 添加随机属性工具提示 - Add random attribute tooltips
     */
    private static void addRandomAttributeTooltips(List<Component> tooltip, ItemStack itemStack) {
        Item item = itemStack.getItem();

        // 检查并添加戒指效果 - Check and add ring effects
        RingEffectsHandler.RingEffect ringEffect = RingEffectsHandler.getInstance().getEffect(item);
        if (ringEffect != null) {
            addRandomRingTooltip(tooltip, itemStack, ringEffect);
        }

        // 检查并添加手镯效果 - Check and add bracelet effects
        BraceletEffectsHandler.BraceletEffect braceletEffect = BraceletEffectsHandler.getInstance().getEffect(item);
        if (braceletEffect != null) {
            addRandomBraceletTooltip(tooltip, itemStack, braceletEffect);
        }

        // 检查并添加项链效果 - Check and add necklace effects
        NecklaceEffectsHandler.NecklaceEffect necklaceEffect = NecklaceEffectsHandler.getInstance().getEffect(item);
        if (necklaceEffect != null) {
            addRandomNecklaceTooltip(tooltip, itemStack, necklaceEffect);
        }

        // 检查并添加耳环效果 - Check and add earring effects
        EarringEffectsHandler.EarringEffect earringEffect = EarringEffectsHandler.getInstance().getEffect(item);
        if (earringEffect != null) {
            addRandomEarringTooltip(tooltip, itemStack, earringEffect);
        }

        // 检查并添加头饰效果 - Check and add headwear effects
        HeadwearEffectsHandler.HeadwearEffect headwearEffect = HeadwearEffectsHandler.getInstance().getEffect(item);
        if (headwearEffect != null) {
            addRandomHeadwearTooltip(tooltip, itemStack, headwearEffect);
        }

        // 检查并添加腰带效果 - Check and add belt effects
        BeltEffectsHandler.BeltEffect beltEffect = BeltEffectsHandler.getInstance().getEffect(item);
        if (beltEffect != null) {
            addRandomBeltTooltip(tooltip, itemStack, beltEffect);
        }

        // 检查并添加暴击效果 - Check and add crit effects
        CritSystem.CritStats critStats = CritSystem.getCritStats(item);
        if (critStats != null) {
            addRandomCritTooltip(tooltip, itemStack, critStats);
        }

        // 检查并添加伤害减免效果 - Check and add damage reduction effects
        DamageReductionSystem.DamageReductionStats damageReductionStats = DamageReductionSystem.getDamageReductionStats(item);
        if (damageReductionStats != null) {
            addRandomDamageReductionTooltip(tooltip, itemStack, damageReductionStats);
        }
    }

    /**
     * 通用效果工具提示添加方法 - Generic effect tooltip addition method
     *
     * @param tooltip 工具提示列表 / The tooltip list
     * @param effect 效果数据 / The effect data
     * @param effectType 效果类型 ("ring", "bracelet", "crit", "damage_reduction") / Effect type
     */
    private static void addEffectTooltip(List<Component> tooltip, Object effect, String effectType) {
        if (effect == null) return;

        switch (effectType) {
            case "ring":
                addRingTooltip(tooltip, (RingEffectsHandler.RingEffect) effect);
                break;
            case "bracelet":
                addBraceletTooltip(tooltip, (BraceletEffectsHandler.BraceletEffect) effect);
                break;
            case "necklace":
                addNecklaceTooltip(tooltip, (NecklaceEffectsHandler.NecklaceEffect) effect);
                break;
            case "earring":
                addEarringTooltip(tooltip, (EarringEffectsHandler.EarringEffect) effect);
                break;
            case "headwear":
                addHeadwearTooltip(tooltip, (HeadwearEffectsHandler.HeadwearEffect) effect);
                break;
            case "belt":
                addBeltTooltip(tooltip, (BeltEffectsHandler.BeltEffect) effect);
                break;
            case "crit":
                addCritTooltip(tooltip, (CritSystem.CritStats) effect);
                break;
            case "damage_reduction":
                addDamageReductionTooltip(tooltip, (DamageReductionSystem.DamageReductionStats) effect);
                break;
        }
    }

    /**
     * 添加戒指效果工具提示 - Add Ring Effect Tooltip
     */
    private static void addRingTooltip(List<Component> tooltip, RingEffectsHandler.RingEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "armor", effect.armor, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");
        addStatTooltip(tooltip, "health_regen", effect.healthRegen * 20, "%.1f");
        addStatTooltip(tooltip, "armor_penetration", effect.armorPenetration, "%.1f");
        addStatTooltip(tooltip, "damage_reduction", effect.damageReduction * 100, "%.1f%%");
    }

    /**
     * 添加随机属性戒指工具提示 - Add Random Attribute Ring Tooltip
     */
    private static void addRandomRingTooltip(List<Component> tooltip, ItemStack itemStack, RingEffectsHandler.RingEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomStatTooltip(tooltip, itemStack, "armor", "%.1f", "护甲值");
        // movement_speed is stored as decimal; display as percent
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");
        // health_regen is stored as per-tick value; display as per-second (x20)
        addRandomScaledStatTooltip(tooltip, itemStack, "health_regen", "%.1f", "生命恢复/秒", 20.0);
        addRandomStatTooltip(tooltip, itemStack, "armor_penetration", "%.1f", "护甲穿透");
        addRandomPercentStatTooltip(tooltip, itemStack, "damage_reduction", "%.1f%%", "伤害减免");
    }

    /**
     * 添加手镯效果工具提示 - Add Bracelet Effect Tooltip
     */
    private static void addBraceletTooltip(List<Component> tooltip, BraceletEffectsHandler.BraceletEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");

        if (effect.hasResistanceEffect) {
            tooltip.add(Component.translatable("item.evolutionary_mod.bracelet_guard.description"));
        }
    }

    /**
     * 添加随机属性手镯工具提示 - Add Random Attribute Bracelet Tooltip
     */
    private static void addRandomBraceletTooltip(List<Component> tooltip, ItemStack itemStack, BraceletEffectsHandler.BraceletEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");

        if (effect.hasResistanceEffect) {
            tooltip.add(Component.translatable("item.evolutionary_mod.bracelet_guard.description"));
        }
    }

    /**
     * 添加项链效果工具提示 - Add Necklace Effect Tooltip
     */
    private static void addNecklaceTooltip(List<Component> tooltip, NecklaceEffectsHandler.NecklaceEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");
        addStatTooltip(tooltip, "armor", effect.armor, "%.0f");
        addStatTooltip(tooltip, "health_regen", effect.healthRegen * 20, "%.1f");
    }

    /**
     * 添加随机属性项链工具提示 - Add Random Attribute Necklace Tooltip
     */
    private static void addRandomNecklaceTooltip(List<Component> tooltip, ItemStack itemStack, NecklaceEffectsHandler.NecklaceEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");
        addRandomStatTooltip(tooltip, itemStack, "armor", "%.0f", "护甲值");
        addRandomScaledStatTooltip(tooltip, itemStack, "health_regen", "%.1f", "生命恢复/秒", 20.0);
    }

    /**
     * 添加耳环效果工具提示 - Add Earring Effect Tooltip
     */
    private static void addEarringTooltip(List<Component> tooltip, EarringEffectsHandler.EarringEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");
        addStatTooltip(tooltip, "health_regen", effect.healthRegen * 20, "%.1f");
    }

    /**
     * 添加随机属性耳环工具提示 - Add Random Attribute Earring Tooltip
     */
    private static void addRandomEarringTooltip(List<Component> tooltip, ItemStack itemStack, EarringEffectsHandler.EarringEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");
        addRandomScaledStatTooltip(tooltip, itemStack, "health_regen", "%.1f", "生命恢复/秒", 20.0);
    }

    /**
     * 添加头饰效果工具提示 - Add Headwear Effect Tooltip
     */
    private static void addHeadwearTooltip(List<Component> tooltip, HeadwearEffectsHandler.HeadwearEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");
        addStatTooltip(tooltip, "armor", effect.armor, "%.0f");
        addStatTooltip(tooltip, "health_regen", effect.healthRegen * 20, "%.1f");
    }

    /**
     * 添加随机属性头饰工具提示 - Add Random Attribute Headwear Tooltip
     */
    private static void addRandomHeadwearTooltip(List<Component> tooltip, ItemStack itemStack, HeadwearEffectsHandler.HeadwearEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");
        addRandomStatTooltip(tooltip, itemStack, "armor", "%.0f", "护甲值");
        addRandomScaledStatTooltip(tooltip, itemStack, "health_regen", "%.1f", "生命恢复/秒", 20.0);
    }

    /**
     * 添加腰带效果工具提示 - Add Belt Effect Tooltip
     */
    private static void addBeltTooltip(List<Component> tooltip, BeltEffectsHandler.BeltEffect effect) {
        addStatTooltip(tooltip, "max_health", effect.maxHealth, "%.0f");
        addStatTooltip(tooltip, "attack_damage", effect.attackDamage, "%.1f");
        addStatTooltip(tooltip, "movement_speed", effect.movementSpeed * 100, "%.1f%%");
        addStatTooltip(tooltip, "luck", effect.luck, "%.2f");
        addStatTooltip(tooltip, "armor", effect.armor, "%.0f");
    }

    /**
     * 添加随机属性腰带工具提示 - Add Random Attribute Belt Tooltip
     */
    private static void addRandomBeltTooltip(List<Component> tooltip, ItemStack itemStack, BeltEffectsHandler.BeltEffect effect) {
        addRandomStatTooltip(tooltip, itemStack, "max_health", "%.0f", "最大生命值");
        addRandomStatTooltip(tooltip, itemStack, "attack_damage", "%.1f", "攻击伤害");
        addRandomPercentStatTooltip(tooltip, itemStack, "movement_speed", "%.1f%%", "移动速度");
        addRandomStatTooltip(tooltip, itemStack, "luck", "%.2f", "幸运值");
        addRandomStatTooltip(tooltip, itemStack, "armor", "%.0f", "护甲值");
    }

    /**
     * 添加暴击效果工具提示 - Add Critical Hit Effect Tooltip
     */
    private static void addCritTooltip(List<Component> tooltip, CritSystem.CritStats critStats) {
        addStatTooltip(tooltip, "crit_chance", critStats.critChance * 100, "%.1f%%");
        addStatTooltip(tooltip, "crit_damage", critStats.critDamageBonus * 100, "%.0f%%");
    }

    /**
     * 添加随机属性暴击工具提示 - Add Random Attribute Crit Tooltip
     */
    private static void addRandomCritTooltip(List<Component> tooltip, ItemStack itemStack, CritSystem.CritStats critStats) {
        // crit values are stored as decimals (e.g. 0.015 = 1.5%), so multiply by 100 for display
        addRandomPercentStatTooltip(tooltip, itemStack, "crit_chance", "%.1f%%", "暴击率");
        addRandomPercentStatTooltip(tooltip, itemStack, "crit_damage", "%.0f%%", "暴击伤害");
    }

    /**
     * 添加伤害减免工具提示 - Add Damage Reduction Tooltip
     */
    private static void addDamageReductionTooltip(List<Component> tooltip, DamageReductionSystem.DamageReductionStats stats) {
        addStatTooltip(tooltip, "damage_reduction", stats.damageReduction * 100, "%.1f%%");
    }

    /**
     * 添加随机属性伤害减免工具提示 - Add Random Attribute Damage Reduction Tooltip
     */
    private static void addRandomDamageReductionTooltip(List<Component> tooltip, ItemStack itemStack, DamageReductionSystem.DamageReductionStats stats) {
        // stored as decimal (e.g. 0.005 = 0.5%), use %.1f to show one decimal place for accuracy
        addRandomPercentStatTooltip(tooltip, itemStack, "damage_reduction", "%.1f%%", "伤害减免");
    }

    /**
     * 通用属性工具提示添加方法 - Generic stat tooltip addition method
     *
     * @param tooltip 工具提示列表 / Tooltip list
     * @param key 翻译键 / Translation key
     * @param value 属性值 / Stat value
     * @param format 格式化字符串 / Format string
     */
    private static void addStatTooltip(List<Component> tooltip, String key, double value, String format) {
        if (value != 0) {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod." + key,
                String.format(format, value)));
        }
    }

    /**
     * 添加随机属性工具提示（显示实际值和范围） - Add random attribute tooltip (showing actual value and range)
     *
     * @param tooltip 工具提示列表 / Tooltip list
     * @param itemStack 物品堆栈 / Item stack
     * @param attributeName 属性名 / Attribute name
     * @param format 格式化字符串 / Format string
     * @param displayName 显示名称 / Display name
     */
    private static void addRandomStatTooltip(List<Component> tooltip, ItemStack itemStack, String attributeName, String format, String displayName) {
        double actualValue = getAttributeValue(itemStack, attributeName, 0);
        if (actualValue == 0) return;

        // 获取范围信息 - Get range information
        double[] range = getAttributeRange(itemStack, attributeName);
        if (range != null) {
            // 显示实际值和范围 - Show actual value and range
            tooltip.add(Component.translatable("tooltip.evolutionary_mod.random_stat",
                displayName,
                String.format(format, actualValue),
                String.format(format, range[0]),
                String.format(format, range[1])));
        } else {
            // 如果没有范围信息，回退到普通显示 - Fallback to normal display if no range info
            tooltip.add(Component.translatable("tooltip.evolutionary_mod." + attributeName,
                String.format(format, actualValue)));
        }
    }

    /**
     * 带倍率的随机属性工具提示（例如生命恢复按每秒显示） - Scaled random stat tooltip (e.g. regen per-second)
     */
    private static void addRandomScaledStatTooltip(List<Component> tooltip, ItemStack itemStack, String attributeName, String format, String displayName, double scale) {
        double actualValue = getAttributeValue(itemStack, attributeName, 0);
        if (actualValue == 0) return;

        double[] range = getAttributeRange(itemStack, attributeName);
        if (range != null) {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod.random_stat",
                displayName,
                String.format(format, actualValue * scale),
                String.format(format, range[0] * scale),
                String.format(format, range[1] * scale)));
        } else {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod." + attributeName,
                String.format(format, actualValue * scale)));
        }
    }

    /**
     * 百分比类随机属性工具提示（NBT里存小数，显示时乘100） - Percent-based random stat tooltip (stored as decimal, display * 100)
     */
    private static void addRandomPercentStatTooltip(List<Component> tooltip, ItemStack itemStack, String attributeName, String format, String displayName) {
        double actualValue = getAttributeValue(itemStack, attributeName, 0);
        if (actualValue == 0) return;

        double[] range = getAttributeRange(itemStack, attributeName);
        if (range != null) {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod.random_stat",
                displayName,
                String.format(format, actualValue * 100),
                String.format(format, range[0] * 100),
                String.format(format, range[1] * 100)));
        } else {
            tooltip.add(Component.translatable("tooltip.evolutionary_mod." + attributeName,
                String.format(format, actualValue * 100)));
        }
    }

    /**
     * 获取物品的属性范围 - Get attribute range for item
     *
     * @param itemStack 物品堆栈 / Item stack
     * @param attributeName 属性名 / Attribute name
     * @return 范围数组 [min, max] 或 null / Range array [min, max] or null
     */
    private static double[] getAttributeRange(ItemStack itemStack, String attributeName) {
        // Prefer ranges stored on the ItemStack (written at roll time)
        //CompoundTag nbt = itemStack.getTag();
//        if (nbt != null) {
//            String minKey = "accessory_attr_" + attributeName + "_min";
//            String maxKey = "accessory_attr_" + attributeName + "_max";
//            if (nbt.contains(minKey) && nbt.contains(maxKey)) {
//                double min = nbt.getDouble(minKey);
//                double max = nbt.getDouble(maxKey);
//                return new double[]{min, max};
//            }
//        }

        // Fallback: if no stored range, do not show range (use actual value only)
        return null;
    }
    
    /**
     * 添加套装效果工具提示 - Add set bonus tooltips
     * 
     * @param tooltip 工具提示列表 / Tooltip list
     * @param item 物品 / Item
     */
    private static void addSetBonusTooltips(List<Component> tooltip, Item item) {
        SetType setType = SetSystem.getSetTypeForItem(item);
        if (setType == null) {
            return; // 不是套装物品 / Not a set item
        }
        
        // 添加空行分隔 / Add empty line separator
        tooltip.add(Component.empty());
        
        // 添加套装名称 / Add set name
        String setNameKey = "set." + setType.name().toLowerCase() + ".name";
        Component setName = Component.translatable(setNameKey)
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        tooltip.add(setName);
        
        // 添加所有套装效果 / Add all set bonuses
        List<SetBonus> bonuses = setType.getBonuses();
        for (SetBonus bonus : bonuses) {
            addSetBonusTooltip(tooltip, bonus, setType);
        }
    }
    
    /**
     * 添加单个套装效果工具提示 - Add single set bonus tooltip
     * 
     * @param tooltip 工具提示列表 / Tooltip list
     * @param bonus 套装效果 / Set bonus
     * @param setType 套装类型 / Set type
     */
    private static void addSetBonusTooltip(List<Component> tooltip, SetBonus bonus, SetType setType) {
        int requiredPieces = bonus.getRequiredPieces();
        
        // 构建效果名称，包含件数要求 / Build effect name with piece requirement
        String bonusKey = getSetBonusTranslationKey(bonus, setType);
        Component name = Component.translatable(bonusKey + ".name")
            .withStyle(ChatFormatting.YELLOW);
        
        // 添加件数要求 / Add piece requirement
        Component pieceRequirement = Component.translatable("tooltip.evolutionary_mod.set_bonus.pieces", requiredPieces)
            .withStyle(ChatFormatting.GRAY);
        Component fullName = name.copy().append(" ").append(pieceRequirement);
        tooltip.add(fullName);
        
        // 添加效果描述 / Add effect description
        Component description = Component.translatable(bonusKey + ".description")
            .withStyle(ChatFormatting.GRAY);
        tooltip.add(Component.literal("  ").append(description));
    }
    
    /**
     * 获取套装效果的翻译键 - Get set bonus translation key
     * 
     * @param bonus 套装效果 / Set bonus
     * @param setType 套装类型 / Set type
     * @return 翻译键 / Translation key
     */
    private static String getSetBonusTranslationKey(SetBonus bonus, SetType setType) {
        // 根据套装效果类名生成翻译键 / Generate translation key from set bonus class name
        String className = bonus.getClass().getSimpleName();
        String setTypeName = setType.name().toLowerCase();
        
        // 处理内部类名（如 DragonSetBonus$DragonBloodline -> dragon.bloodline）
        if (className.contains("$")) {
            String[] parts = className.split("\\$");
            if (parts.length == 2) {
                String innerClass = parts[1];
                // 将驼峰命名转换为下划线命名 / Convert camelCase to snake_case
                // DragonBloodline -> dragon_bloodline
                String snakeCase = innerClass.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                // 移除套装类型前缀（如 dragon_bloodline -> bloodline）
                String prefix = setTypeName + "_";
                if (snakeCase.startsWith(prefix)) {
                    snakeCase = snakeCase.substring(prefix.length());
                }
                return "set_bonus." + setTypeName + "." + snakeCase;
            }
        }
        
        // 回退：尝试从类名中提取 / Fallback: try to extract from class name
        String simpleName = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        String prefix = setTypeName + "_";
        if (simpleName.startsWith(prefix)) {
            simpleName = simpleName.substring(prefix.length());
        }
        return "set_bonus." + setTypeName + "." + simpleName;
    }
}
