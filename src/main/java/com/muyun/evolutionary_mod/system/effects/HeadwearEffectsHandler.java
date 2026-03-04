package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * 头饰饰品特效处理器 - Headwear Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class HeadwearEffectsHandler extends AbstractAccessoryEffectHandler<HeadwearEffectsHandler.HeadwearEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation HEADWEAR_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "headwear_max_health");
    private static final ResourceLocation HEADWEAR_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "headwear_attack_damage");
    private static final ResourceLocation HEADWEAR_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "headwear_movement_speed");
    private static final ResourceLocation HEADWEAR_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "headwear_luck");
    private static final ResourceLocation HEADWEAR_ARMOR_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "headwear_armor");
    
    // 单例实例
    private static final HeadwearEffectsHandler INSTANCE = new HeadwearEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 头饰效果处理器实例
     */
    public static HeadwearEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private HeadwearEffectsHandler() {
        // 注册头饰效果
        registerHeadwearEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册头饰效果
     */
    private void registerHeadwearEffects() {
        // 龙族套装 - Dragon Set
        // 龙族头饰：护甲+6，生命+12，伤害减免+4%，生命恢复+0.6/秒
        registerEffect(DragonItems.DRAGON_HEADWEAR.get(), createEffect()
            .armor(6)
            .maxHealth(12)
            .healthRegen(0.03)); // 每秒恢复0.6点 (0.6/20)

        // 龙族头饰：伤害减免+4%
        DamageReductionSystem.registerDamageReductionStats(DragonItems.DRAGON_HEADWEAR.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.04));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "HEAD";
    }
    
    @Override
    protected HeadwearEffect createEffect() {
        return new HeadwearEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有头饰的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        double totalArmor = 0;
        
        // 只检查头饰槽位
        ItemStack stack = cap.getStack(AccessorySlot.HEAD);
        if (!stack.isEmpty()) {
            // 如果头饰有随机属性，跳过默认效果

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

        // 应用护甲修饰符
        if (totalArmor > 0) {

        }
    }
    
    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        // 重置生命值修饰符
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(HEADWEAR_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(HEADWEAR_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(HEADWEAR_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(HEADWEAR_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(HEADWEAR_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(HEADWEAR_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(HEADWEAR_LUCK_ID) != null) {
            luck.removeModifier(HEADWEAR_LUCK_ID);
        }

        // 重置护甲修饰符
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(HEADWEAR_ARMOR_ID) != null) {
            armor.removeModifier(HEADWEAR_ARMOR_ID);
        }
    }
    
    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 处理生命恢复效果
        double totalHealthRegen = 0;

        // 只检查头饰槽位
        ItemStack stack = cap.getStack(AccessorySlot.HEAD);
        if (!stack.isEmpty()) {
            // 如果头饰有随机属性，跳过默认效果

                // 优先从NBT读取随机属性值

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
     * 头饰效果数据类
     */
    public static class HeadwearEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public double armor = 0;
        public double healthRegen = 0; // per tick (每秒值需要除以20)

        public HeadwearEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public HeadwearEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public HeadwearEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public HeadwearEffect luck(double value) { this.luck = value; return this; }
        public HeadwearEffect armor(double value) { this.armor = value; return this; }
        public HeadwearEffect healthRegen(double value) { this.healthRegen = value; return this; }
    }
    
    /**
     * 应用头饰效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyHeadwearEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
