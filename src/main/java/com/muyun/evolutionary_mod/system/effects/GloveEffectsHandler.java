package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * 手套饰品特效处理器 - Glove Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class GloveEffectsHandler extends AbstractAccessoryEffectHandler<GloveEffectsHandler.GloveEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation GLOVE_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "glove_max_health");
    private static final ResourceLocation GLOVE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "glove_attack_damage");
    private static final ResourceLocation GLOVE_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "glove_movement_speed");
    private static final ResourceLocation GLOVE_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "glove_luck");
    
    // 单例实例
    private static final GloveEffectsHandler INSTANCE = new GloveEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 手套效果处理器实例
     */
    public static GloveEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private GloveEffectsHandler() {
        // 注册手套效果
        registerGloveEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册手套效果
     */
    private void registerGloveEffects() {
        // 目前没有实现具体的手套物品，预留注册位置
    }
    
    @Override
    protected String getSlotPrefix() {
        return "GLOVE";
    }
    
    @Override
    protected GloveEffect createEffect() {
        return new GloveEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有手套的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (slot.name().startsWith("GLOVE")) {
                ItemStack stack = cap.getStack(slot);
                if (!stack.isEmpty()) {
                    // 如果手套有随机属性，跳过默认效果

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
        if (maxHealth != null && maxHealth.getModifier(GLOVE_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(GLOVE_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(GLOVE_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(GLOVE_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(GLOVE_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(GLOVE_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(GLOVE_LUCK_ID) != null) {
            luck.removeModifier(GLOVE_LUCK_ID);
        }
    }
    
    /**
     * 手套效果数据类
     */
    public static class GloveEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;

        public GloveEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public GloveEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public GloveEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public GloveEffect luck(double value) { this.luck = value; return this; }
    }
    
    /**
     * 应用手套效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyGloveEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
