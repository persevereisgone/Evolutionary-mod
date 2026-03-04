package com.muyun.evolutionary_mod.system.sets;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 套装效果事件处理器接口 - Set Effect Event Handler Interface
 * 
 * 定义套装效果在事件中的处理逻辑。
 * 每个套装可以实现此接口来处理其特殊的事件触发效果（如护盾、爆发等）。
 * 
 * Defines event handling logic for set bonuses.
 * Each set can implement this interface to handle its special event-triggered effects (e.g., shields, bursts, etc.).
 */
public interface SetEffectHandler {
    
    /**
     * 处理玩家受到伤害时的事件
     * 用于实现防御性套装效果（如护盾、伤害减免等）
     * 
     * @param player 受到伤害的玩家
     * @param event 伤害事件
     * @param pieceCount 当前装备的套装件数
     */

    
    /**
     * 处理玩家造成伤害时的事件
     * 用于实现攻击性套装效果（如爆发伤害、额外效果等）
     * 
     * @param player 造成伤害的玩家
     * @param event 伤害事件
     * @param target 被攻击的目标
     * @param pieceCount 当前装备的套装件数
     */

    
    /**
     * 获取此处理器对应的套装类型
     * 
     * @return 套装类型
     */
    SetType getSetType();
}

