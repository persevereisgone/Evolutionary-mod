package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 基础饰品效果处理器抽象类
 * 提取所有饰品效果类的共同逻辑，减少重复代码
 */
public abstract class AbstractAccessoryEffectHandler<T> {
    
    // 效果映射表
    protected final Map<Item, T> effects = new HashMap<>();
    
    // 装备变化缓存
    protected final Map<UUID, String> equipmentCache = new HashMap<>();
    
    // 抽象方法，子类需要实现
    protected abstract String getSlotPrefix();
    protected abstract void calculateAndApplyEffects(Player player, PlayerAccessories cap);
    protected abstract void resetModifiersWithoutHealthAdjust(Player player);
    protected abstract T createEffect();
    
    /**
     * 应用饰品效果
     * 智能检测装备变化，只在必要时更新属性
     * @param player 玩家
     */
    public void applyEffects(Player player) {

    }
    
    /**
     * 更新饰品属性修饰符
     * 只在装备变化时调用，避免每tick重复计算
     * @param player 玩家
     */
    public void updateAttributes(Player player) {

    }
    
    /**
     * 处理每tick需要执行的效果（如生命恢复）
     * @param player 玩家
     * @param cap 玩家饰品能力
     */
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 默认实现为空，子类可根据需要重写
    }
    
    /**
     * 清理玩家缓存
     * @param playerId 玩家UUID
     */
    public void clearCache(UUID playerId) {
        equipmentCache.remove(playerId);
    }
    
    /**
     * 检查物品是否有效果
     * @param item 物品
     * @return 是否有效果
     */
    public boolean hasEffect(Item item) {
        return effects.containsKey(item);
    }
    
    /**
     * 获取物品的效果
     * @param item 物品
     * @return 效果数据
     */
    public T getEffect(Item item) {
        return effects.get(item);
    }
    
    /**
     * 注册效果
     * @param item 物品
     * @param effect 效果
     */
    protected void registerEffect(Item item, T effect) {
        effects.put(item, effect);
    }
}
