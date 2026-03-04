package com.muyun.evolutionary_mod.system.sets;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * 龙族套装效果 - Dragon Set Bonus
 * 
 * 定义龙族套装的所有套装效果：
 * - 2件套：龙之血脉 - 最大生命值 +20%，生命恢复速度 +50%
 * - 4件套：龙鳞护体 - 护甲 +15，伤害减免 +15%，受到伤害时有30%概率生成吸收10点伤害的护盾（冷却10秒）
 * - 6件套：龙息爆发 - 攻击时有25%概率触发龙息，对前方5格内的敌人造成10攻击伤害的火焰伤害
 * 
 * Defines all set bonuses for the Dragon Set:
 * - 2-piece: Dragon Bloodline - Max health +20%, Health regeneration speed +50%
 * - 4-piece: Dragon Scale Protection - Armor +15, Damage reduction +15%, 30% chance to generate a 10-damage-absorbing shield when taking damage (10s cooldown)
 * - 6-piece: Dragon Breath Burst - 25% chance to trigger dragon breath on attack, dealing 10 attack damage as fire damage to enemies within 5 blocks in front
 */
public class DragonSetBonus {
    
    // UUIDs for attribute modifiers
    public static final UUID DRAGON_BLOODLINE_HEALTH_UUID = UUID.fromString("d5f2a8f2-6c6a-4f7a-9b2e-2a5c4b9e2001");
    public static final UUID DRAGON_SCALE_ARMOR_UUID = UUID.fromString("d5f2a8f2-6c6a-4f7a-9b2e-2a5c4b9e2002");
    
    /**
     * 2件套效果：龙之血脉
     * 效果：最大生命值 +20%，生命恢复速度 +50%
     * 描述："龙族的血脉在你体内流淌，赋予你强大的生命力"
     */
    public static class DragonBloodline extends SetBonus {
        public DragonBloodline() {
            super("龙之血脉", "龙族的血脉在你体内流淌，赋予你强大的生命力", 2);
        }
        
        @Override
        public void apply(Player player, int pieceCount) {
            // 应用最大生命值 +20% 的效果
            // 注意：百分比加成应该基于"基础值+所有数值增加"，而不是只基于基础值
            // Note: Percentage bonus should be based on "base value + all ADDITION modifiers", not just base value
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                // 先移除旧的修饰符（如果存在），避免重复应用
                // Remove old modifier first (if exists) to avoid duplicate application

                
                // 记录当前生命值，用于后续保持
                // Record current health to keep it unchanged
                float currentHealth = player.getHealth();
                
                // 使用 MULTIPLY_TOTAL 操作来实现百分比加成
                // 注意：MULTIPLY_TOTAL 会基于当前总值（基础值 + 所有ADDITION修饰符）计算
                // 由于 AccessoryEffectCalculator 先应用所有单个饰品的ADDITION修饰符，
                // 然后再应用套装效果，所以这里的百分比加成会基于"基础值+所有数值增加"
                // Use MULTIPLY_TOTAL operation to implement percentage bonus
                // Note: MULTIPLY_TOTAL is based on current total value (base + all ADDITION modifiers)
                // Since AccessoryEffectCalculator applies all individual accessory ADDITION modifiers first,
                // then applies set bonuses, the percentage bonus here will be based on "base + all numerical additions"
                // 
                // 计算方式：total * (1 + multiplier) = total * 1.20，所以 multiplier = 0.20
                // Calculation: total * (1 + multiplier) = total * 1.20, so multiplier = 0.20

                
                // 计算新的最大生命值
                // Calculate new max health
                float newMaxHealth = player.getMaxHealth();
                
                // 当生命值上限增加时，保持当前生命值数值不变（不按比例调整）
                // 这样避免玩家通过反复装备/卸下饰品来刷血量的漏洞
                // When max health increases, keep current health value unchanged (not proportionally adjusted)
                // This prevents players from exploiting by repeatedly equipping/unequipping accessories to gain health
                if (currentHealth <= newMaxHealth) {
                    // 如果当前生命值不超过新的上限，保持原值
                    // If current health doesn't exceed new max, keep original value
                    player.setHealth(currentHealth);
                } else {
                    // 如果当前生命值超过新的上限（理论上不应该发生），调整到上限
                    // If current health exceeds new max (shouldn't happen in theory), adjust to max
                    player.setHealth(newMaxHealth);
                }
            }
            
            // 生命恢复速度 +50% 的效果通过标记玩家来实现（在 SetSystem 中处理）
            // Health regeneration speed +50% is handled by marking the player (processed in SetSystem)
        }
        
        @Override
        public void remove(Player player) {
            // 移除最大生命值加成
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                // 记录当前生命值
                // Record current health
                float currentHealth = player.getHealth();
                
                // 移除修饰符
                // Remove modifier

                
                // 计算新的最大生命值
                // Calculate new max health
                float newMaxHealth = player.getMaxHealth();
                
                // 当生命值上限减少时，如果当前生命值超过新的上限，才需要调整
                // 否则保持当前生命值不变（不按比例调整）
                // 这样避免玩家通过反复装备/卸下饰品来刷血量的漏洞
                // When max health decreases, only adjust if current health exceeds new max
                // Otherwise keep current health unchanged (not proportionally adjusted)
                // This prevents players from exploiting by repeatedly equipping/unequipping accessories to gain health
                if (currentHealth > newMaxHealth) {
                    // 如果当前生命值超过新的上限，调整到上限
                    // If current health exceeds new max, adjust to max
                    player.setHealth(newMaxHealth);
                }
                // 如果当前生命值不超过新的上限，保持原值不变
                // If current health doesn't exceed new max, keep original value unchanged
            }
        }
    }
    
    /**
     * 4件套效果：龙鳞护体
     * 效果：护甲 +15，伤害减免 +15%
     * 触发：受到伤害时有30%概率生成吸收10点伤害的护盾（冷却10秒）
     * 描述："龙鳞覆盖你的身体，提供强大的防护"
     */
    public static class DragonScaleProtection extends SetBonus {
        public DragonScaleProtection() {
            super("龙鳞护体", "龙鳞覆盖你的身体，提供强大的防护", 4);
        }
        
        @Override
        public void apply(Player player, int pieceCount) {
            // 应用护甲 +15 的效果

            
            // 伤害减免 +15% 通过 DamageReductionSystem 处理（在 SetSystem 中注册）
            // Damage reduction +15% is handled by DamageReductionSystem (registered in SetSystem)
            
            // 护盾触发逻辑在 AccessoryEvents 中处理
            // Shield trigger logic is handled in AccessoryEvents
        }
        
        @Override
        public void remove(Player player) {
            // 移除护甲加成

        }
    }
    
    /**
     * 6件套效果：龙息爆发
     * 效果：攻击时有25%概率触发龙息，对前方5格内的敌人造成10攻击伤害的火焰伤害
     * 描述："龙息从你体内爆发，焚烧一切敌人"
     */
    public static class DragonBreathBurst extends SetBonus {
        public DragonBreathBurst() {
            super("龙息爆发", "龙息从你体内爆发，焚烧一切敌人", 6);
        }
        
        @Override
        public void apply(Player player, int pieceCount) {
            // 龙息触发逻辑在 AccessoryEvents 中处理
            // Dragon breath trigger logic is handled in AccessoryEvents
        }
        
        @Override
        public void remove(Player player) {
            // 无需清理，事件处理器会自动检查
            // No cleanup needed, event handler will check automatically
        }
    }
}

