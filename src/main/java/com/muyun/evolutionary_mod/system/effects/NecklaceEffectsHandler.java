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
 * 项链饰品特效处理器 - Necklace Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class NecklaceEffectsHandler extends AbstractAccessoryEffectHandler<NecklaceEffectsHandler.NecklaceEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation NECKLACE_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_max_health");
    private static final ResourceLocation NECKLACE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_attack_damage");
    private static final ResourceLocation NECKLACE_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_movement_speed");
    private static final ResourceLocation NECKLACE_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_luck");
    private static final ResourceLocation NECKLACE_ARMOR_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_armor");
    
    // 单例实例
    private static final NecklaceEffectsHandler INSTANCE = new NecklaceEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 项链效果处理器实例
     */
    public static NecklaceEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private NecklaceEffectsHandler() {
        // 注册项链效果
        registerNecklaceEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册项链效果
     */
    private void registerNecklaceEffects() {
        // 龙族套装 - Dragon Set
        // 龙族项链：生命+18，护甲+5，伤害减免+3%，生命恢复+0.4/秒
        registerEffect(DragonItems.DRAGON_NECKLACE.get(), createEffect()
            .maxHealth(18)
            .armor(5)
            .healthRegen(0.02)); // 每秒恢复0.4点 (0.4/20)

        // 龙族项链：伤害减免+3%
        DamageReductionSystem.registerDamageReductionStats(DragonItems.DRAGON_NECKLACE.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.03));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "NECKLACE";
    }
    
    @Override
    protected NecklaceEffect createEffect() {
        return new NecklaceEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有项链的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;
        double totalArmor = 0;

        // 只检查项链槽位
        ItemStack stack = cap.getStack(AccessorySlot.NECKLACE);
        if (!stack.isEmpty()) {
            // 如果项链有随机属性，跳过默认效果

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
        if (maxHealth != null && maxHealth.getModifier(NECKLACE_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(NECKLACE_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(NECKLACE_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(NECKLACE_ATTACK_DAMAGE_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(NECKLACE_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(NECKLACE_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(NECKLACE_LUCK_ID) != null) {
            luck.removeModifier(NECKLACE_LUCK_ID);
        }

        // 重置护甲修饰符
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(NECKLACE_ARMOR_ID) != null) {
            armor.removeModifier(NECKLACE_ARMOR_ID);
        }
    }
    
    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 处理生命恢复效果


        // 只检查项链槽位

                // 优先从NBT读取随机属性值


        // 应用生命恢复效果

    }
    
    /**
     * 项链效果数据类
     */
    public static class NecklaceEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public double armor = 0;
        public double healthRegen = 0; // per tick (每秒值需要除以20)

        public NecklaceEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public NecklaceEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public NecklaceEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public NecklaceEffect luck(double value) { this.luck = value; return this; }
        public NecklaceEffect armor(double value) { this.armor = value; return this; }
        public NecklaceEffect healthRegen(double value) { this.healthRegen = value; return this; }
    }
    
    /**
     * 应用项链效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyNecklaceEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
