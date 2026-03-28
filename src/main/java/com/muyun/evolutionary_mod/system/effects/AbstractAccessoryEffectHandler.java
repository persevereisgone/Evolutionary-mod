package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 基础饰品效果处理器抽象类
 *
 * NeoForge 1.21.1: 使用 getData() 替代 getCapability()。
 */
public abstract class AbstractAccessoryEffectHandler<T> {

    protected final Map<Item, T> effects = new HashMap<>();
    protected final Map<UUID, String> equipmentCache = new HashMap<>();

    protected abstract String getSlotPrefix();
    protected abstract void calculateAndApplyEffects(Player player, PlayerAccessories cap);
    protected abstract void resetModifiersWithoutHealthAdjust(Player player);
    protected abstract T createEffect();

    /**
     * 应用饰品效果（直接使用 getData() 获取 PlayerAccessories）。
     */
    public void applyEffects(Player player) {
        if (player == null || player.level().isClientSide) return;
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);

        // 检查装备是否变化（利用缓存避免每 tick 重复计算）
        String currentEquipment = buildEquipmentSignature(cap);
        String cachedEquipment = equipmentCache.get(player.getUUID());
        if (currentEquipment.equals(cachedEquipment)) {
            // 装备没有变化，仍需要处理每 tick 效果（如生命恢复）
            handlePerTickEffects(player, cap);
            return;
        }

        // 装备发生变化，重新计算
        equipmentCache.put(player.getUUID(), currentEquipment);
        resetModifiersWithoutHealthAdjust(player);
        calculateAndApplyEffects(player, cap);
        handlePerTickEffects(player, cap);
    }

    /**
     * 强制更新属性（忽略缓存）。
     */
    public void updateAttributes(Player player) {
        if (player == null || player.level().isClientSide) return;
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        equipmentCache.remove(player.getUUID());
        resetModifiersWithoutHealthAdjust(player);
        calculateAndApplyEffects(player, cap);
    }

    /**
     * 处理每 tick 效果（如生命恢复）。子类可重写。
     */
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 默认为空
    }

    public void clearCache(UUID playerId) {
        equipmentCache.remove(playerId);
    }

    public boolean hasEffect(Item item) {
        return effects.containsKey(item);
    }

    public T getEffect(Item item) {
        return effects.get(item);
    }

    /**
     * 从 ItemStack 上读取随机词条（DataComponent）。
     * 若物品未经过随机滚动则返回 AccessoryAttributes.EMPTY。
     */
    public static AccessoryAttributes getRolledAttributes(ItemStack stack) {
        if (stack.isEmpty()) return AccessoryAttributes.EMPTY;
        AccessoryAttributes attrs = stack.get(EvolutionaryMod.ACCESSORY_ATTRIBUTES.get());
        return attrs != null ? attrs : AccessoryAttributes.EMPTY;
    }

    protected void registerEffect(Item item, T effect) {
        effects.put(item, effect);
    }

    /**
     * 根据当前装备槽位内容生成签名字符串，用于检测装备变化。
     * 同时包含 DataComponent 内容，确保随机词条变化时缓存失效。
     */
    private String buildEquipmentSignature(PlayerAccessories cap) {
        StringBuilder sb = new StringBuilder();
        for (com.muyun.evolutionary_mod.core.AccessorySlot slot : com.muyun.evolutionary_mod.core.AccessorySlot.values()) {
            if (slot.name().startsWith(getSlotPrefix())) {
                ItemStack stack = cap.getStack(slot);
                if (stack.isEmpty()) {
                    sb.append("empty,");
                } else {
                    sb.append(stack.getItem().toString());
                    // 加入随机词条哈希，确保词条变化时缓存失效
                    AccessoryAttributes attrs = stack.get(com.muyun.evolutionary_mod.EvolutionaryMod.ACCESSORY_ATTRIBUTES.get());
                    if (attrs != null) sb.append('#').append(attrs.hashCode());
                    sb.append(',');
                }
            }
        }
        return sb.toString();
    }
}
