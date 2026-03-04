package com.muyun.evolutionary_mod.system.elements;

import net.minecraft.world.entity.LivingEntity;

/**
 * 元素附着 - Element Aura
 * 
 * 表示一个元素附着在实体上的状态。
 * Represents an element attached to an entity.
 */
public class ElementAura {
    public final ElementType element;
    public final float gauge;        // 元素强度 (0.0 - 1.0)
    public final int duration;        // 持续时间（tick）
    public final LivingEntity target; // 附着目标
    public int remainingTicks;       // 剩余tick数
    public int effectTickCounter;    // 效果tick计数器（用于每秒触发所有效果）
    
    public ElementAura(ElementType element, float gauge, int duration, LivingEntity target) {
        this.element = element;
        this.gauge = gauge;
        this.duration = duration;
        this.target = target;
        this.remainingTicks = duration;
        this.effectTickCounter = 0;
    }
    
    /**
     * 减少一个tick
     */
    public void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }
    
    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return remainingTicks <= 0 || target == null || !target.isAlive();
    }
    
    /**
     * 获取剩余时间（秒）
     */
    public float getRemainingSeconds() {
        return remainingTicks / 20.0f;
    }
}

