package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.item.types.Bracelets;
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
 * 手镯饰品特效处理器 - Bracelet Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class BraceletEffectsHandler extends AbstractAccessoryEffectHandler<BraceletEffectsHandler.BraceletEffect> {
    
    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation BRACELET_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "bracelet_max_health");
    private static final ResourceLocation BRACELET_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "bracelet_attack_damage");
    private static final ResourceLocation BRACELET_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "bracelet_movement_speed");
    private static final ResourceLocation BRACELET_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "bracelet_luck");
    
    // 单例实例
    private static final BraceletEffectsHandler INSTANCE = new BraceletEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 手镯效果处理器实例
     */
    public static BraceletEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private BraceletEffectsHandler() {
        // 注册手镯效果
        registerBraceletEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册手镯效果
     */
    private void registerBraceletEffects() {
        // 现有手镯
        registerEffect(Bracelets.BRACELET_STRENGTH.get(), createEffect().attackDamage(5.0));
        registerEffect(Bracelets.BRACELET_SPEED.get(), createEffect().movementSpeed(0.3));
        registerEffect(Bracelets.BRACELET_HP.get(), createEffect().maxHealth(10.0));
        registerEffect(Bracelets.BRACELET_GUARD.get(), createEffect().resistanceEffect(true));
        registerEffect(Bracelets.BRACELET_LUCK.get(), createEffect().luck(5.0));

        // 龙族套装 - Dragon Set
        // 龙族手镯：攻击+5.5，移速+18%，暴击率+4%
        registerEffect(DragonItems.DRAGON_BRACELET.get(), createEffect()
            .attackDamage(5.5)
            .movementSpeed(0.18));

        // 龙族手镯：暴击率+4%
        CritSystem.registerCritStats(DragonItems.DRAGON_BRACELET.get(), new CritSystem.CritStats()
            .critChance(0.04));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "BRACELET";
    }
    
    @Override
    protected BraceletEffect createEffect() {
        return new BraceletEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有手镯的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (slot.name().startsWith("BRACELET")) {
                ItemStack stack = cap.getStack(slot);
                if (!stack.isEmpty()) {
                    // 如果手镯有随机属性，跳过默认效果

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
        if (maxHealth != null && maxHealth.getModifier(BRACELET_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(BRACELET_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(BRACELET_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(BRACELET_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(BRACELET_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(BRACELET_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(BRACELET_LUCK_ID) != null) {
            luck.removeModifier(BRACELET_LUCK_ID);
        }
    }
    
    /**
     * 手镯效果数据类
     */
    public static class BraceletEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public boolean hasResistanceEffect = false;

        public BraceletEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public BraceletEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public BraceletEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public BraceletEffect luck(double value) { this.luck = value; return this; }
        public BraceletEffect resistanceEffect(boolean hasEffect) { this.hasResistanceEffect = hasEffect; return this; }
    }
    
    /**
     * 应用手镯效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyBraceletEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
