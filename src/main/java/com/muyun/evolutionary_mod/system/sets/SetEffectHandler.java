package com.muyun.evolutionary_mod.system.sets;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 套装效果事件处理器接口 - Set Effect Event Handler Interface
 *
 * NeoForge 1.21.1: LivingHurtEvent -> LivingIncomingDamageEvent
 */
public interface SetEffectHandler {

    /**
     * 处理玩家受到伤害时的事件（防御性套装效果）。
     */
    default void onPlayerHurt(Player player, LivingIncomingDamageEvent event, int pieceCount) {
        // 默认实现为空，子类可以重写
    }

    /**
     * 处理玩家造成伤害时的事件（攻击性套装效果）。
     */
    default void onPlayerAttack(Player player, LivingIncomingDamageEvent event, LivingEntity target, int pieceCount) {
        // 默认实现为空，子类可以重写
    }

    /**
     * 获取此处理器对应的套装类型。
     */
    SetType getSetType();
}
