package com.muyun.evolutionary_mod.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.item.base.GeneralAccessoryItem;

/**
 * 饰品规则 - Accessory Rules
 *
 * 定义饰品槽位的验证规则，判断物品是否可以放入指定槽位。
 * 所有槽位判断均使用枚举直接比较，不依赖字符串匹配或反射。
 */
public class AccessoryRules {

    public static boolean isValidForSlot(AccessorySlot slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        // 获取注册名（仅本 Mod 物品）
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        boolean isOurMod = id != null && EvolutionaryMod.MODID.equals(id.getNamespace());
        String path = isOurMod ? id.getPath() : "";

        return switch (slot) {
            // ---- 头饰槽 ----
            // 接受本 Mod 含 "headwear" 的饰品，或原版头盔（EquipmentSlot.HEAD）
            case HEAD -> {
                if (isOurMod && path.contains("headwear")) yield true;
                if (stack.getItem() instanceof ArmorItem armor) {
                    yield armor.getEquipmentSlot() == EquipmentSlot.HEAD;
                }
                yield false;
            }

            // ---- 戒指槽（4个）----
            case RING_1, RING_2, RING_3, RING_4 ->
                    isOurMod && path.endsWith("_ring");

            // ---- 手镯槽（2个）----
            case BRACELET_1, BRACELET_2 ->
                    isOurMod && path.endsWith("_bracelet");

            // ---- 耳环槽（2个）----
            case EARRING_1, EARRING_2 ->
                    isOurMod && path.contains("earring");

            // ---- 手套槽（2个）----
            case GLOVE_1, GLOVE_2 ->
                    isOurMod && (path.contains("glove") || path.contains("gauntlet"));

            // ---- 靴子槽（2个）----
            case BOOT_1, BOOT_2 ->
                    isOurMod && (path.contains("boot") || path.contains("shoe") || path.contains("anklet"));

            // ---- 项链槽 ----
            case NECKLACE ->
                    isOurMod && path.contains("necklace");

            // ---- 腰带槽 ----
            case BELT ->
                    isOurMod && path.contains("belt");

            // ---- 配饰槽（3个）----
            case ACCESSORY_1, ACCESSORY_2, ACCESSORY_3 ->
                    stack.getItem() instanceof GeneralAccessoryItem;
        };
    }
}
