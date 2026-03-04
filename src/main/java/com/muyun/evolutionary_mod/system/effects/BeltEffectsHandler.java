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
 * 腰带饰品特效处理器 - Belt Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class BeltEffectsHandler extends AbstractAccessoryEffectHandler<BeltEffectsHandler.BeltEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation BELT_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_max_health");
    private static final ResourceLocation BELT_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_attack_damage");
    private static final ResourceLocation BELT_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_movement_speed");
    private static final ResourceLocation BELT_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_luck");
    private static final ResourceLocation BELT_ARMOR_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_armor");
    
    // 单例实例
    private static final BeltEffectsHandler INSTANCE = new BeltEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 腰带效果处理器实例
     */
    public static BeltEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private BeltEffectsHandler() {
        // 注册腰带效果
        registerBeltEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册腰带效果
     */
    private void registerBeltEffects() {
        // 龙族套装 - Dragon Set
        // 龙族腰带：攻击+4，护甲+4，移速+12%，幸运+2，暴击伤害+15%
        registerEffect(DragonItems.DRAGON_BELT.get(), createEffect()
            .attackDamage(4)
            .armor(4)
            .movementSpeed(0.12)
            .luck(2));

        // 龙族腰带：暴击伤害+15%
        CritSystem.registerCritStats(DragonItems.DRAGON_BELT.get(), new CritSystem.CritStats()
            .critDamageBonus(0.15));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "BELT";
    }
    
    @Override
    protected BeltEffect createEffect() {
        return new BeltEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有腰带的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        double totalArmor = 0;
        
        // 只检查腰带槽位
        ItemStack stack = cap.getStack(AccessorySlot.BELT);
        if (!stack.isEmpty()) {
            // 如果腰带有随机属性，跳过默认效果

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
        if (maxHealth != null && maxHealth.getModifier(BELT_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(BELT_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(BELT_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(BELT_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(BELT_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(BELT_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(BELT_LUCK_ID) != null) {
            luck.removeModifier(BELT_LUCK_ID);
        }

        // 重置护甲修饰符
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(BELT_ARMOR_ID) != null) {
            armor.removeModifier(BELT_ARMOR_ID);
        }
    }
    
    /**
     * 腰带效果数据类
     */
    public static class BeltEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public double armor = 0;

        public BeltEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public BeltEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public BeltEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public BeltEffect luck(double value) { this.luck = value; return this; }
        public BeltEffect armor(double value) { this.armor = value; return this; }
    }
    
    /**
     * 应用腰带效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyBeltEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
