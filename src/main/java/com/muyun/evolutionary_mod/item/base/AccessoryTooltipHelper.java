package com.muyun.evolutionary_mod.item.base;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
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
 */
public class AccessoryTooltipHelper {

    public enum AccessoryRarity {
        BROKEN(ChatFormatting.GRAY),
        NORMAL(ChatFormatting.WHITE),
        EXCELLENT(ChatFormatting.BLUE),
        EPIC(ChatFormatting.DARK_PURPLE),
        LEGENDARY(ChatFormatting.GOLD),
        MYTHIC(ChatFormatting.RED),
        SET(ChatFormatting.AQUA);  // 套装专属品质：青色

        public final ChatFormatting color;
        AccessoryRarity(ChatFormatting color) { this.color = color; }
    }

    public static AccessoryRarity getRarityFromRegistryName(String registryName) {
        if (registryName == null) return AccessoryRarity.NORMAL;
        String n = registryName.toLowerCase();
        if (n.startsWith("mythic_"))    return AccessoryRarity.MYTHIC;
        if (n.startsWith("legendary_")) return AccessoryRarity.LEGENDARY;
        if (n.startsWith("epic_"))      return AccessoryRarity.EPIC;
        if (n.startsWith("excellent_")) return AccessoryRarity.EXCELLENT;
        if (n.startsWith("broken_"))   return AccessoryRarity.BROKEN;
        return AccessoryRarity.NORMAL;
    }

    /** 优先判断套装物品，再按注册名前缀判断品质 */
    public static AccessoryRarity getItemRarity(Item item) {
        if (SetSystem.getSetTypeForItem(item) != null) return AccessoryRarity.SET;
        var key = BuiltInRegistries.ITEM.getKey(item);
        return getRarityFromRegistryName(key == null ? null : key.getPath());
    }

    public static ChatFormatting getItemRarityColor(Item item) {
        return getItemRarity(item).color;
    }

    // -----------------------------------------------------------------------
    // 主入口：传入 ItemStack，优先显示随机词条，再显示静态效果
    // -----------------------------------------------------------------------

    public static void addAccessoryTooltips(List<Component> tooltip, ItemStack itemStack) {
        Item item = itemStack.getItem();
        AccessoryAttributes rolled = itemStack.get(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get());
        boolean hasRolled = rolled != null && !rolled.isEmpty();

        if (hasRolled) {
            // 显示随机词条（DataComponent）
            addRolledTooltip(tooltip, rolled);
        }

        // 无论是否有随机词条，都显示静态特殊效果（如守护手镯抗性）
        addStaticSpecialEffects(tooltip, item);

        // 套装效果
        addSetBonusTooltips(tooltip, item);

        if (!hasRolled) {
            // 没有随机词条时显示静态默认属性
            addStaticAttributeTooltips(tooltip, item);
        }
    }

    /** 传入 Item（无 ItemStack 时的兼容入口） */
    public static void addAccessoryTooltips(List<Component> tooltip, Item item) {
        addStaticAttributeTooltips(tooltip, item);
        addStaticSpecialEffects(tooltip, item);
    }

    // -----------------------------------------------------------------------
    // 随机词条显示（DataComponent）
    // -----------------------------------------------------------------------

    private static void addRolledTooltip(List<Component> tooltip, AccessoryAttributes a) {
        addStatLine(tooltip, "最大生命值",  a.maxHealth(),        "%.0f",   1.0);
        addStatLine(tooltip, "攻击伤害",    a.attackDamage(),     "%.1f",   1.0);
        addStatLine(tooltip, "护甲值",      a.armor(),            "%.1f",   1.0);
        addStatLine(tooltip, "移动速度",    a.movementSpeed(),    "%.1f%%", 100.0);
        addStatLine(tooltip, "幸运值",      a.luck(),             "%.1f",   1.0);
        addStatLine(tooltip, "生命恢复/秒", a.healthRegen(),      "%.2f",   20.0);
        addStatLine(tooltip, "护甲穿透",    a.armorPenetration(), "%.1f",   1.0);
        addStatLine(tooltip, "暴击率",      a.critChance(),       "%.1f%%", 100.0);
        addStatLine(tooltip, "暴击伤害",    a.critDamage(),       "%.0f%%", 100.0);
        addStatLine(tooltip, "伤害减免",    a.damageReduction(),  "%.1f%%", 100.0);
    }

    private static void addStatLine(List<Component> tooltip, String name, double value, String fmt, double scale) {
        if (value == 0) return;
        String display = fmt.endsWith("%%")
                ? String.format(fmt.replace("%%", ""), value * scale) + "%"
                : String.format(fmt, value * scale);
        tooltip.add(Component.literal("  " + name + ": +" + display)
                .withStyle(ChatFormatting.GRAY));
    }

    // -----------------------------------------------------------------------
    // 静态属性显示（无随机词条时的回退）
    // -----------------------------------------------------------------------

    private static void addStaticAttributeTooltips(List<Component> tooltip, Item item) {
        addEffectTooltip(tooltip, RingEffectsHandler.getInstance().getEffect(item), "ring");
        addEffectTooltip(tooltip, BraceletEffectsHandler.getInstance().getEffect(item), "bracelet");
        addEffectTooltip(tooltip, NecklaceEffectsHandler.getInstance().getEffect(item), "necklace");
        addEffectTooltip(tooltip, EarringEffectsHandler.getInstance().getEffect(item), "earring");
        addEffectTooltip(tooltip, HeadwearEffectsHandler.getInstance().getEffect(item), "headwear");
        addEffectTooltip(tooltip, BeltEffectsHandler.getInstance().getEffect(item), "belt");
        addEffectTooltip(tooltip, CritSystem.getCritStats(item), "crit");
        addEffectTooltip(tooltip, DamageReductionSystem.getDamageReductionStats(item), "damage_reduction");
    }

    /** 静态特殊效果（不依赖数值，如守护手镯描述文字） */
    private static void addStaticSpecialEffects(List<Component> tooltip, Item item) {
        BraceletEffectsHandler.BraceletEffect be = BraceletEffectsHandler.getInstance().getEffect(item);
        if (be != null && be.hasResistanceEffect) {
            tooltip.add(Component.translatable("item.evolutionary_mod.bracelet_guard.description"));
        }
    }

    // -----------------------------------------------------------------------
    // 静态效果 Tooltip（旧路径）
    // -----------------------------------------------------------------------

    private static void addEffectTooltip(List<Component> tooltip, Object effect, String effectType) {
        if (effect == null) return;
        switch (effectType) {
            case "ring"             -> addRingTooltip(tooltip, (RingEffectsHandler.RingEffect) effect);
            case "bracelet"         -> addBraceletTooltip(tooltip, (BraceletEffectsHandler.BraceletEffect) effect);
            case "necklace"         -> addNecklaceTooltip(tooltip, (NecklaceEffectsHandler.NecklaceEffect) effect);
            case "earring"          -> addEarringTooltip(tooltip, (EarringEffectsHandler.EarringEffect) effect);
            case "headwear"         -> addHeadwearTooltip(tooltip, (HeadwearEffectsHandler.HeadwearEffect) effect);
            case "belt"             -> addBeltTooltip(tooltip, (BeltEffectsHandler.BeltEffect) effect);
            case "crit"             -> addCritTooltip(tooltip, (CritSystem.CritStats) effect);
            case "damage_reduction" -> addDamageReductionTooltip(tooltip, (DamageReductionSystem.DamageReductionStats) effect);
        }
    }

    private static void addRingTooltip(List<Component> tooltip, RingEffectsHandler.RingEffect e) {
        addStat(tooltip, "max_health",       e.maxHealth,          "%.0f");
        addStat(tooltip, "attack_damage",    e.attackDamage,       "%.1f");
        addStat(tooltip, "armor",            e.armor,              "%.1f");
        addStat(tooltip, "movement_speed",   e.movementSpeed*100,  "%.1f%%");
        addStat(tooltip, "luck",             e.luck,               "%.2f");
        addStat(tooltip, "health_regen",     e.healthRegen*20,     "%.1f");
        addStat(tooltip, "armor_penetration",e.armorPenetration,   "%.1f");
        addStat(tooltip, "damage_reduction", e.damageReduction*100,"%.1f%%");
    }

    private static void addBraceletTooltip(List<Component> tooltip, BraceletEffectsHandler.BraceletEffect e) {
        addStat(tooltip, "max_health",     e.maxHealth,         "%.0f");
        addStat(tooltip, "attack_damage",  e.attackDamage,      "%.1f");
        addStat(tooltip, "movement_speed", e.movementSpeed*100, "%.1f%%");
        addStat(tooltip, "luck",           e.luck,              "%.2f");
    }

    private static void addNecklaceTooltip(List<Component> tooltip, NecklaceEffectsHandler.NecklaceEffect e) {
        addStat(tooltip, "max_health",     e.maxHealth,         "%.0f");
        addStat(tooltip, "attack_damage",  e.attackDamage,      "%.1f");
        addStat(tooltip, "movement_speed", e.movementSpeed*100, "%.1f%%");
        addStat(tooltip, "luck",           e.luck,              "%.2f");
        addStat(tooltip, "armor",          e.armor,             "%.0f");
        addStat(tooltip, "health_regen",   e.healthRegen*20,    "%.1f");
    }

    private static void addEarringTooltip(List<Component> tooltip, EarringEffectsHandler.EarringEffect e) {
        addStat(tooltip, "max_health",     e.maxHealth,         "%.0f");
        addStat(tooltip, "attack_damage",  e.attackDamage,      "%.1f");
        addStat(tooltip, "movement_speed", e.movementSpeed*100, "%.1f%%");
        addStat(tooltip, "luck",           e.luck,              "%.2f");
        addStat(tooltip, "health_regen",   e.healthRegen*20,    "%.1f");
    }

    private static void addHeadwearTooltip(List<Component> tooltip, HeadwearEffectsHandler.HeadwearEffect e) {
        addStat(tooltip, "max_health",     e.maxHealth,         "%.0f");
        addStat(tooltip, "attack_damage",  e.attackDamage,      "%.1f");
        addStat(tooltip, "movement_speed", e.movementSpeed*100, "%.1f%%");
        addStat(tooltip, "luck",           e.luck,              "%.2f");
        addStat(tooltip, "armor",          e.armor,             "%.0f");
        addStat(tooltip, "health_regen",   e.healthRegen*20,    "%.1f");
    }

    private static void addBeltTooltip(List<Component> tooltip, BeltEffectsHandler.BeltEffect e) {
        addStat(tooltip, "max_health",     e.maxHealth,         "%.0f");
        addStat(tooltip, "attack_damage",  e.attackDamage,      "%.1f");
        addStat(tooltip, "movement_speed", e.movementSpeed*100, "%.1f%%");
        addStat(tooltip, "luck",           e.luck,              "%.2f");
        addStat(tooltip, "armor",          e.armor,             "%.0f");
    }

    private static void addCritTooltip(List<Component> tooltip, CritSystem.CritStats s) {
        addStat(tooltip, "crit_chance",  s.critChance*100,      "%.1f%%");
        addStat(tooltip, "crit_damage",  s.critDamageBonus*100, "%.0f%%");
    }

    private static void addDamageReductionTooltip(List<Component> tooltip, DamageReductionSystem.DamageReductionStats s) {
        addStat(tooltip, "damage_reduction", s.damageReduction*100, "%.1f%%");
    }

    private static void addStat(List<Component> tooltip, String key, double value, String format) {
        if (value == 0) return;
        tooltip.add(Component.translatable("tooltip.evolutionary_mod." + key,
                String.format(format, value)));
    }

    // -----------------------------------------------------------------------
    // 套装效果 Tooltip
    // -----------------------------------------------------------------------

    private static void addSetBonusTooltips(List<Component> tooltip, Item item) {
        SetType setType = SetSystem.getSetTypeForItem(item);
        if (setType == null) return;
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("set." + setType.name().toLowerCase() + ".name")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        for (SetBonus bonus : setType.getBonuses()) {
            addSetBonusTooltip(tooltip, bonus, setType);
        }
    }

    private static void addSetBonusTooltip(List<Component> tooltip, SetBonus bonus, SetType setType) {
        String key = getSetBonusTranslationKey(bonus, setType);
        int pieces = bonus.getRequiredPieces();
        Component name = Component.translatable(key + ".name").withStyle(ChatFormatting.YELLOW);
        Component req  = Component.translatable("tooltip.evolutionary_mod.set_bonus.pieces", pieces).withStyle(ChatFormatting.GRAY);
        tooltip.add(name.copy().append(" ").append(req));
        tooltip.add(Component.literal("  ").append(Component.translatable(key + ".description").withStyle(ChatFormatting.GRAY)));
    }

    private static String getSetBonusTranslationKey(SetBonus bonus, SetType setType) {
        String className  = bonus.getClass().getSimpleName();
        String setName    = setType.name().toLowerCase();
        if (className.contains("$")) {
            String[] parts = className.split("\\$");
            if (parts.length == 2) {
                String snake  = parts[1].replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                String prefix = setName + "_";
                if (snake.startsWith(prefix)) snake = snake.substring(prefix.length());
                return "set_bonus." + setName + "." + snake;
            }
        }
        String snake  = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        String prefix = setName + "_";
        if (snake.startsWith(prefix)) snake = snake.substring(prefix.length());
        return "set_bonus." + setName + "." + snake;
    }
}
