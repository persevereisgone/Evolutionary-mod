package com.muyun.evolutionary_mod.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.registries.DeferredHolder;

import com.muyun.evolutionary_mod.item.base.GeneralAccessoryItem;

/**
 * 饰品规则 - Accessory Rules
 * 
 * 定义饰品槽位的验证规则，判断物品是否可以放入指定槽位。
 * 
 * Defines validation rules for accessory slots, determining whether items can be placed in specific slots.
 */
public class AccessoryRules {
    public static boolean isValidForSlot(AccessorySlot slot, ItemStack stack) {
        if (stack == null) return false;
        if (stack.isEmpty()) return false;

        // HEAD: accept helmet armor or headwear accessories
        if (slot == AccessorySlot.HEAD) {
            // First check if it's a helmet armor
            if (stack.getItem() instanceof ArmorItem armor) {
                try {
                    // Use reflection to support different mappings
                    try {
                        java.lang.reflect.Method m = ArmorItem.class.getMethod("getEquipmentSlot");
                        Object res = m.invoke(armor);
                        if (res instanceof EquipmentSlot) return res == EquipmentSlot.HEAD;
                    } catch (NoSuchMethodException ignored) {}

                    try {
                        java.lang.reflect.Method m2 = ArmorItem.class.getMethod("getSlot");
                        Object res2 = m2.invoke(armor);
                        if (res2 instanceof EquipmentSlot) return res2 == EquipmentSlot.HEAD;
                    } catch (NoSuchMethodException ignored) {}
                } catch (Exception ignored) {}
            }
            // Also accept headwear accessories (items with "headwear" in their name)
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id != null) {
                String name = id.getPath().toLowerCase();
                if (name.contains("headwear") && "evolutionary_mod".equals(id.getNamespace())) {
                    return true;
                }
            }
            return false;
        }

        // For ring/bracelet/glove/earring slots, accept items whose registry name contains matching keywords
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null) {
            String name = id.getPath().toLowerCase();
            if (slot.name().startsWith("BRACELET")) {
                if (name.endsWith("_bracelet") && "evolutionary_mod".equals(id.getNamespace())) return true;
            } else if (slot.name().startsWith("RING")) {
                // All registered rings from this mod are valid for ring slots
                if (name.endsWith("_ring") && "evolutionary_mod".equals(id.getNamespace())) return true;
            } else if (slot.name().startsWith("GLOVE")) {
                if ((name.contains("glove") || name.contains("gauntlet")) && "evolutionary_mod".equals(id.getNamespace())) return true;
            } else if (slot.name().startsWith("EARRING")) {
                if (name.contains("earring") && "evolutionary_mod".equals(id.getNamespace())) return true;
            } else if (slot.name().startsWith("BOOT")) {
                if ((name.contains("shoe") || name.contains("boot")) && "evolutionary_mod".equals(id.getNamespace())) return true;
            } else if (slot.name().startsWith("ACCESSORY")) {
                // 配饰槽位只接受类型为配饰的物品
                if (stack.getItem() instanceof GeneralAccessoryItem) return true;
            } else {
                // for other unique slots, accept if name contains slot id
                if (name.contains("necklace") && slot == AccessorySlot.NECKLACE && "evolutionary_mod".equals(id.getNamespace())) return true;
                if (name.contains("belt") && slot == AccessorySlot.BELT && "evolutionary_mod".equals(id.getNamespace())) return true;
            }
        }

        // No fallback - strict type matching only
        return false;
    }
}

