package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.item.types.Rings;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
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
 * 戒指饰品特效处理器 - Ring Accessory Effects Handler
 * 继承自基础饰品效果处理器，减少重复代码
 */
public class RingEffectsHandler extends AbstractAccessoryEffectHandler<RingEffectsHandler.RingEffect> {

    // 使用 ResourceLocation 代替 UUID 来标识属性修饰符
    private static final ResourceLocation RING_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_max_health");
    private static final ResourceLocation RING_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_attack_damage");
    private static final ResourceLocation RING_ARMOR_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_armor");
    private static final ResourceLocation RING_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_movement_speed");
    private static final ResourceLocation RING_LUCK_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_luck");
    
    // 单例实例
    private static final RingEffectsHandler INSTANCE = new RingEffectsHandler();
    
    /**
     * 获取单例实例
     * @return 戒指效果处理器实例
     */
    public static RingEffectsHandler getInstance() {
        return INSTANCE;
    }
    
    /**
     * 私有构造函数
     */
    private RingEffectsHandler() {
        // 注册戒指效果
        registerRingEffects();
        // 注册到效果计算器
        AccessoryEffectCalculator.registerHandler(this);
    }
    
    /**
     * 注册戒指效果
     */
    private void registerRingEffects() {
        // 破损戒指
        registerEffect(Rings.BROKEN_LIFE_ESSENCE_RING.get(), createEffect().maxHealth(2));
        registerEffect(Rings.BROKEN_BATTLE_POWER_RING.get(), createEffect().attackDamage(0.5));
        registerEffect(Rings.BROKEN_IRON_SHIELD_RING.get(), createEffect().armor(0.5));
        registerEffect(Rings.BROKEN_GALE_RING.get(), createEffect().movementSpeed(0.02));
        registerEffect(Rings.BROKEN_GOOD_FORTUNE_RING.get(), createEffect().luck(0.1));

        // 普通戒指
        registerEffect(Rings.LIFE_ESSENCE_RING.get(), createEffect().maxHealth(4));
        registerEffect(Rings.BATTLE_POWER_RING.get(), createEffect().attackDamage(1));
        registerEffect(Rings.IRON_SHIELD_RING.get(), createEffect().armor(1));
        registerEffect(Rings.GALE_RING.get(), createEffect().movementSpeed(0.05));
        registerEffect(Rings.GOOD_FORTUNE_RING.get(), createEffect().luck(0.25));
        registerEffect(Rings.HEALING_RING.get(), createEffect().healthRegen(0.01)); // 每秒恢复0.2点
        registerEffect(Rings.ARMOR_BREAKER_RING.get(), createEffect().armorPenetration(0.5));

        // 优秀戒指
        registerEffect(Rings.EXCELLENT_LIFE_ESSENCE_RING.get(), createEffect().maxHealth(6).healthRegen(0.015)); // 每秒恢复0.3点
        registerEffect(Rings.EXCELLENT_BATTLE_POWER_RING.get(), createEffect().attackDamage(1.5));
        registerEffect(Rings.EXCELLENT_SHARP_EDGE_RING.get(), createEffect().attackDamage(1.5));
        registerEffect(Rings.EXCELLENT_IRON_SHIELD_RING.get(), createEffect().armor(2));
        registerEffect(Rings.EXCELLENT_GALE_RING.get(), createEffect().movementSpeed(0.08));
        registerEffect(Rings.EXCELLENT_GOOD_FORTUNE_RING.get(), createEffect().luck(0.5));
        registerEffect(Rings.EXCELLENT_HEALING_RING.get(), createEffect().healthRegen(0.02)); // 每秒恢复0.4点
        registerEffect(Rings.EXCELLENT_ARMOR_BREAKER_RING.get(), createEffect().armorPenetration(0.8));

        // 史诗戒指
        registerEffect(Rings.EPIC_LIFE_ESSENCE_RING.get(), createEffect().maxHealth(10).healthRegen(0.025)); // 每秒恢复0.5点
        registerEffect(Rings.EPIC_BATTLE_POWER_RING.get(), createEffect().attackDamage(2.5));
        registerEffect(Rings.EPIC_IRON_SHIELD_RING.get(), createEffect().armor(3));
        registerEffect(Rings.EPIC_GALE_RING.get(), createEffect().movementSpeed(0.12));
        registerEffect(Rings.EPIC_GOOD_FORTUNE_RING.get(), createEffect().luck(1));
        registerEffect(Rings.EPIC_HEALING_RING.get(), createEffect().healthRegen(0.03)); // 每秒恢复0.6点
        registerEffect(Rings.EPIC_ARMOR_BREAKER_RING.get(), createEffect().armorPenetration(1.2));

        // 传说戒指
        registerEffect(Rings.LEGENDARY_LIFE_ESSENCE_RING.get(), createEffect().maxHealth(15).healthRegen(0.04)); // 每秒恢复0.8点
        registerEffect(Rings.LEGENDARY_BATTLE_POWER_RING.get(), createEffect().attackDamage(4));
        registerEffect(Rings.LEGENDARY_IRON_SHIELD_RING.get(), createEffect().armor(5));
        registerEffect(Rings.LEGENDARY_GALE_RING.get(), createEffect().movementSpeed(0.18));
        registerEffect(Rings.LEGENDARY_GOOD_FORTUNE_RING.get(), createEffect().luck(2));
        registerEffect(Rings.LEGENDARY_HEALING_RING.get(), createEffect().healthRegen(0.05)); // 每秒恢复1.0点
        registerEffect(Rings.LEGENDARY_ARMOR_BREAKER_RING.get(), createEffect().armorPenetration(1.8));

        // 神话戒指
        registerEffect(Rings.MYTHIC_LIFE_ESSENCE_RING.get(), createEffect().maxHealth(20).healthRegen(0.06)); // 每秒恢复1.2点
        registerEffect(Rings.MYTHIC_BATTLE_POWER_RING.get(), createEffect().attackDamage(6));
        registerEffect(Rings.MYTHIC_IRON_SHIELD_RING.get(), createEffect().armor(8));
        registerEffect(Rings.MYTHIC_GALE_RING.get(), createEffect().movementSpeed(0.25));
        registerEffect(Rings.MYTHIC_GOOD_FORTUNE_RING.get(), createEffect().luck(3));
        registerEffect(Rings.MYTHIC_HEALING_RING.get(), createEffect().healthRegen(0.075)); // 每秒恢复1.5点
        registerEffect(Rings.MYTHIC_ARMOR_BREAKER_RING.get(), createEffect().armorPenetration(2.5));

        // 龙族套装戒指
        registerEffect(DragonItems.DRAGON_RING.get(), createEffect()
            .maxHealth(16)
            .attackDamage(4.5)
            .healthRegen(0.05)); // 每秒恢复1.0点

        // 注册暴击属性
        registerCritStats();
        
        // 注册伤害减免属性
        registerDamageReductionStats();
    }
    
    /**
     * 注册暴击属性
     */
    private void registerCritStats() {
        CritSystem.registerCritStats(Rings.BROKEN_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.02).critDamageBonus(0.05));
        CritSystem.registerCritStats(Rings.NORMAL_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.05).critDamageBonus(0.1));
        CritSystem.registerCritStats(Rings.EXCELLENT_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.08).critDamageBonus(0.15));
        CritSystem.registerCritStats(Rings.EPIC_BATTLE_POWER_RING.get(), new CritSystem.CritStats().critChance(0.03));
        CritSystem.registerCritStats(Rings.EPIC_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.12).critDamageBonus(0.25));
        CritSystem.registerCritStats(Rings.LEGENDARY_BATTLE_POWER_RING.get(), new CritSystem.CritStats().critChance(0.05).critDamageBonus(0.3));
        CritSystem.registerCritStats(Rings.LEGENDARY_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.18).critDamageBonus(0.35));
        CritSystem.registerCritStats(Rings.MYTHIC_BATTLE_POWER_RING.get(), new CritSystem.CritStats().critChance(0.08).critDamageBonus(0.5));
        CritSystem.registerCritStats(Rings.MYTHIC_SHARP_EDGE_RING.get(), new CritSystem.CritStats().critChance(0.25).critDamageBonus(0.5));
        
        // 龙族套装戒指暴击属性
        CritSystem.registerCritStats(DragonItems.DRAGON_RING.get(), new CritSystem.CritStats()
            .critChance(0.06)
            .critDamageBonus(0.20));
    }
    
    /**
     * 注册伤害减免属性
     */
    private void registerDamageReductionStats() {
        DamageReductionSystem.registerDamageReductionStats(Rings.EXCELLENT_IRON_SHIELD_RING.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.02));
        DamageReductionSystem.registerDamageReductionStats(Rings.EPIC_IRON_SHIELD_RING.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.05));
        DamageReductionSystem.registerDamageReductionStats(Rings.LEGENDARY_IRON_SHIELD_RING.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.08));
        DamageReductionSystem.registerDamageReductionStats(Rings.MYTHIC_IRON_SHIELD_RING.get(),
            new DamageReductionSystem.DamageReductionStats().damageReduction(0.12));
    }
    
    @Override
    protected String getSlotPrefix() {
        return "RING";
    }
    
    @Override
    protected RingEffect createEffect() {
        return new RingEffect();
    }
    
    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        // 计算所有戒指的总效果
        double totalMaxHealth = 0;
        double totalAttackDamage = 0;
        double totalArmor = 0;
        double totalMovementSpeed = 0;
        double totalLuck = 0;

        // 如果戒指有随机属性，跳过默认效果


        
        // 应用生命值修饰符


        // 应用攻击伤害修饰符


        // 应用护甲修饰符


        // 应用移动速度修饰符


        // 应用幸运修饰符

    }
    
    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        // 重置生命值修饰符
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(RING_MAX_HEALTH_ID) != null) {
            maxHealth.removeModifier(RING_MAX_HEALTH_ID);
        }

        // 重置攻击伤害修饰符
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(RING_ATTACK_DAMAGE_ID) != null) {
            attackDamage.removeModifier(RING_ATTACK_DAMAGE_ID);
        }

        // 重置护甲修饰符
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(RING_ARMOR_ID) != null) {
            armor.removeModifier(RING_ARMOR_ID);
        }

        // 重置移动速度修饰符
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null && moveSpeed.getModifier(RING_MOVEMENT_SPEED_ID) != null) {
            moveSpeed.removeModifier(RING_MOVEMENT_SPEED_ID);
        }

        // 重置幸运修饰符
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null && luck.getModifier(RING_LUCK_ID) != null) {
            luck.removeModifier(RING_LUCK_ID);
        }
    }
    
    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        // 处理生命恢复效果
        double totalHealthRegen = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (slot.name().startsWith("RING")) {
                ItemStack stack = cap.getStack(slot);
                if (!stack.isEmpty()) {
                    // 优先从NBT读取随机属性值

                    // 如果没有随机属性，使用默认效果

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
     * 戒指效果数据类
     */
    public static class RingEffect {
        public double maxHealth = 0;
        public double attackDamage = 0;
        public double armor = 0;
        public double movementSpeed = 0;
        public double luck = 0;
        public double healthRegen = 0; // 每tick值
        public double armorPenetration = 0;
        public double damageReduction = 0; // 百分比

        public RingEffect maxHealth(double value) { this.maxHealth = value; return this; }
        public RingEffect attackDamage(double value) { this.attackDamage = value; return this; }
        public RingEffect armor(double value) { this.armor = value; return this; }
        public RingEffect movementSpeed(double value) { this.movementSpeed = value; return this; }
        public RingEffect luck(double value) { this.luck = value; return this; }
        public RingEffect healthRegen(double value) { this.healthRegen = value; return this; }
        public RingEffect armorPenetration(double value) { this.armorPenetration = value; return this; }
        public RingEffect damageReduction(double value) { this.damageReduction = value; return this; }
    }
    
    /**
     * 应用戒指效果（静态方法，兼容旧代码）
     * @param player 玩家
     */
    public static void applyRingEffects(Player player) {
        getInstance().applyEffects(player);
    }
}
