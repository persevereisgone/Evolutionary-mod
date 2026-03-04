package com.muyun.evolutionary_mod.system.sets.handlers;

import com.muyun.evolutionary_mod.system.sets.SetEffectHandler;
import com.muyun.evolutionary_mod.system.sets.SetType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 龙族套装效果事件处理器 - Dragon Set Effect Event Handler
 * 
 * 处理龙族套装的事件触发效果：
 * - 4件套：龙鳞护体 - 护盾触发（30%概率，冷却10秒）
 * - 6件套：龙息爆发 - 攻击触发（25%概率）
 */
public class DragonSetEffectHandler implements SetEffectHandler {
    
    // 4件套护盾冷却时间记录（玩家UUID -> 上次触发时间（tick））
    private static final Map<UUID, Integer> DRAGON_SCALE_SHIELD_COOLDOWN = new HashMap<>();
    private static final int DRAGON_SCALE_SHIELD_COOLDOWN_TICKS = 200; // 10秒 = 200 tick
    
    @Override
    public SetType getSetType() {
        return SetType.DRAGON;
    }
    
    /**
     * 处理4件套效果：龙鳞护体 - 护盾触发
     * 受到伤害时有30%概率生成吸收10点伤害的护盾（冷却10秒）
     */

    
    /**
     * 处理6件套效果：龙息爆发
     * 攻击时有25%概率触发龙息，对前方5格内的敌人造成10攻击伤害的火焰伤害
     */

}

