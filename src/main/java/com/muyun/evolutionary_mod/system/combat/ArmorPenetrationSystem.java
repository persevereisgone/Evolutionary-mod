package com.muyun.evolutionary_mod.system.combat;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.system.effects.AbstractAccessoryEffectHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 护甲穿透系统 - Armor Penetration System
 *
 * 护甲穿透通过临时降低目标护甲值来实现：
 * 每点护甲穿透直接减少目标 1 点有效护甲（不低于 0）。
 */
public class ArmorPenetrationSystem {

    private static final Map<Item, Double> GLOBAL_ARMOR_PENETRATION = new HashMap<>();

    public static void registerArmorPenetration(Item item, double value) {
        GLOBAL_ARMOR_PENETRATION.put(item, value);
    }

    /**
     * 获取玩家全部饰品的总护甲穿透值。
     */
    public static double getTotalArmorPenetration(Player player) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        double total = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            // 静态词条
            Double base = GLOBAL_ARMOR_PENETRATION.get(stack.getItem());
            if (base != null) total += base;
            // 随机词条
            AccessoryAttributes rolled = AbstractAccessoryEffectHandler.getRolledAttributes(stack);
            total += rolled.armorPenetration();
        }
        return total;
    }

    /**
     * 应用护甲穿透：临时降低目标护甲，计算伤害后恢复。
     * 返回应用穿透后的实际伤害量。
     */
    public static float applyArmorPenetration(Player attacker, float baseDamage, LivingEntity target) {
        double penetration = getTotalArmorPenetration(attacker);
        if (penetration <= 0) return baseDamage;

        AttributeInstance armorAttr = target.getAttribute(Attributes.ARMOR);
        if (armorAttr == null) return baseDamage;

        // 临时降低目标护甲
        double originalArmor = armorAttr.getValue();
        double reducedArmor = Math.max(0, originalArmor - penetration);
        double armorDiff = originalArmor - reducedArmor;

        if (armorDiff <= 0) return baseDamage;

        // 根据护甲差值估算额外伤害
        // 原版护甲减伤公式：reduction = armor / (armor + 4)
        // 穿透后减伤：reducedReduction = reducedArmor / (reducedArmor + 4)
        double originalReduction = originalArmor / (originalArmor + 4.0);
        double reducedReduction  = reducedArmor  / (reducedArmor  + 4.0);
        double reductionDiff = originalReduction - reducedReduction;

        // 补偿因护甲减少而多造成的伤害
        return baseDamage + (float)(baseDamage * reductionDiff);
    }

    public static double getArmorPenetration(Item item) {
        return GLOBAL_ARMOR_PENETRATION.getOrDefault(item, 0.0);
    }
}

