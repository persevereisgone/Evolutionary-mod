package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * 耳环饰品特效处理器 - Earring Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class EarringEffectsHandler extends AbstractAccessoryEffectHandler<EarringEffectsHandler.EarringEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation EARRING_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_max_health");
    private static final ResourceLocation EARRING_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_attack_damage");
    private static final ResourceLocation EARRING_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_movement_speed");
    private static final ResourceLocation EARRING_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_luck");
    
    // 单例实例
    private static final EarringEffectsHandler INSTANCE = new EarringEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 耳环效果处理器实例
     */
    public static EarringEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private EarringEffectsHandler() {
        // 注册耳环效果
        registerEarringEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册耳环效果
     */
    private void registerEarringEffects() {
        // 龙族套装 - Dragon Set
        // 龙族耳环：幸运+3，生命恢复+0.8/秒，生命+10，暴击率+3%
        registerEffect(DragonItems.DRAGON_EARRING.get(), createEffect()
            .luck(3)
            .healthRegen(0.04) // 每秒恢复0.8点 (0.8/20)
            .maxHealth(10));

        // 龙族耳环：暴击率+3%
        CritSystem.registerCritStats(DragonItems.DRAGON_EARRING.get(), new CritSystem.CritStats()
            .critChance(0.03));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "EARRING";
    }
    
    @Override
    protected EarringEffect createEffect() {
        return new EarringEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有耳环的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (slot.name().startsWith("EARRING")) {
                ItemStack stack = cap.getStack(slot);
                if (!stack.isEmpty()) {
                    // 如果耳环有随机属性，跳过默认效果

                }
            }
        }
        
        // 应用生命值修饰符
        if (totalMaxHealth > 0) {

        }

        // 应用攻击伤害修饰符
        if (totalAttackDamage > 0) {

        }

        // 应用移动速度修饰符
        if (totalMovementSpeed > 0) {

        }

        // 应用幸运修饰符
        if (totalLuck > 0) {

        }
    }
    
    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        // 重置生命值修饰符
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(EARRING_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(EARRING_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(EARRING_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(EARRING_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(EARRING_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(EARRING_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(EARRING_LUCK_ID) != null) {
            luck.removeModifier(EARRING_LUCK_ID);
        }
    }
    
    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 处理生命恢复效果
        double totalHealthRegen = 0;

        for (AccessorySlot slot : AccessorySlot.values()) {
            if (slot.name().startsWith("EARRING")) {
                ItemStack stack = cap.getStack(slot);
                if (!stack.isEmpty()) {
                    // 如果耳环有随机属性，跳过默认效果

                            // 优先从NBT读取随机属性值


                }
            }
        }

        // 应用生命恢复效果
        if (totalHealthRegen > 0) {
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            if (currentHealth < maxHealth) {
                player.heal((float)totalHealthRegen);
            }
        }
    }
    
    /**
     * 耳环效果数据类
     */
    public static class EarringEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public double healthRegen = 0; // per tick (每秒值需要除以20)

        public EarringEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public EarringEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public EarringEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public EarringEffect luck(double value) { this.luck = value; return this; }
        public EarringEffect healthRegen(double value) { this.healthRegen = value; return this; }
    }
    
    /**
     * 应用耳环效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyEarringEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
