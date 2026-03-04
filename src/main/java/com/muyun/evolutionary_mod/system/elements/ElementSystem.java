package com.muyun.evolutionary_mod.system.elements;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 元素系统 - Element System
 * 
 * 管理所有元素的附着、效果和应用。
 * Manages all element attachments, effects, and applications.
 */
public class ElementSystem {
    
    // 存储所有实体的元素附着
    private static final Map<LivingEntity, List<ElementAura>> ELEMENT_AURAS = new ConcurrentHashMap<>();
    
    // 元素效果配置
    private static final Map<ElementType, ElementEffectConfig> ELEMENT_CONFIGS = new HashMap<>();
    
    static {
        initializeElementConfigs();
    }
    
    /**
     * 初始化元素效果配置
     */
    private static void initializeElementConfigs() {
        // 火元素 - 每秒伤害
        ELEMENT_CONFIGS.put(ElementType.FIRE, new ElementEffectConfig()
            .setTickDamage(2.0f, 5.0f)  // 每秒伤害（每20 ticks触发一次）
            .setDuration(100) // 5秒
            .setParticleType(ParticleTypes.FLAME)
            .setSound(SoundEvents.FIRE_AMBIENT));
        
        // 水元素
        ELEMENT_CONFIGS.put(ElementType.WATER, new ElementEffectConfig()
            .setTickHealing(1.0f, 2.0f)
            .setMovementSpeedModifier(-0.10f)
            .setDuration(80) // 4秒
            .setParticleType(ParticleTypes.RAIN)
            .setSound(SoundEvents.WATER_AMBIENT));
        
        // 雷元素 - 每秒伤害
        ELEMENT_CONFIGS.put(ElementType.THUNDER, new ElementEffectConfig()
            .setTickDamage(1.0f, 3.0f)  // 每秒伤害（每20 ticks触发一次）
            .setChainRange(3.0f)
            .setAttackSpeedModifier(-0.20f)
            .setDuration(60) // 3秒
            .setParticleType(ParticleTypes.ELECTRIC_SPARK)
            .setSound(SoundEvents.LIGHTNING_BOLT_THUNDER));
        
        // 土元素
        ELEMENT_CONFIGS.put(ElementType.EARTH, new ElementEffectConfig()
            .setArmorBonus(5.0f)
            .setKnockbackResistance(0.50f)
            .setMovementSpeedModifier(-0.15f)
            .setDuration(160) // 8秒
            .setParticleType(ParticleTypes.ITEM_SLIME)
            .setSound(SoundEvents.STONE_BREAK));
        
        // 风元素
        ELEMENT_CONFIGS.put(ElementType.WIND, new ElementEffectConfig()
            .setMovementSpeedModifier(0.15f)
            .setDiffusionRange(3.0f)
            .setDuration(100) // 5秒
            .setParticleType(ParticleTypes.CLOUD)
            .setSound(SoundEvents.ENDERMAN_TELEPORT));
        
        // 神圣元素
        ELEMENT_CONFIGS.put(ElementType.HOLY, new ElementEffectConfig()
            .setTickHealing(2.0f, 4.0f)
            .setCleanseNegative(true)
            .setUndeadDamageBonus(0.50f)
            .setDuration(120) // 6秒
            .setParticleType(ParticleTypes.END_ROD)
            .setSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
        
        // 精神元素
        ELEMENT_CONFIGS.put(ElementType.PSYCHIC, new ElementEffectConfig()
            .setConfusionChance(0.30f)
            .setDuration(100) // 5秒
            .setParticleType(ParticleTypes.WITCH)
            .setSound(SoundEvents.ELDER_GUARDIAN_CURSE));
        
        // 生命元素
        ELEMENT_CONFIGS.put(ElementType.LIFE, new ElementEffectConfig()
            .setTickHealing(3.0f, 5.0f)
            .setHealthRegenBonus(0.50f)
            .setDuration(120) // 6秒
            .setParticleType(ParticleTypes.HAPPY_VILLAGER)
            .setSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
        
        // 空间元素
        ELEMENT_CONFIGS.put(ElementType.SPACE, new ElementEffectConfig()
            .setTeleportChance(0.20f)
            .setTeleportRange(3.0f, 5.0f)
            .setAreaDamageMultiplier(0.50f)
            .setDuration(80) // 4秒
            .setParticleType(ParticleTypes.PORTAL)
            .setSound(SoundEvents.ENDERMAN_TELEPORT));
        
        // 时间元素
        ELEMENT_CONFIGS.put(ElementType.TIME, new ElementEffectConfig()
            .setMovementSpeedModifier(-0.30f)
            .setAttackSpeedModifier(-0.20f)
            .setDuration(120) // 6秒
            .setParticleType(ParticleTypes.ENCHANT)
            .setSound(SoundEvents.EXPERIENCE_ORB_PICKUP));
        
        // 混沌元素
        ELEMENT_CONFIGS.put(ElementType.CHAOS, new ElementEffectConfig()
            .setRandomEffects(true)
            .setDuration(100) // 5秒
            .setParticleType(ParticleTypes.WITCH)
            .setSound(SoundEvents.EVOKER_CAST_SPELL));
    }
    
    /**
     * 应用元素附着到目标
     */
    public static void applyElement(LivingEntity target, ElementType element, float gauge, int duration) {
        if (target == null || !target.isAlive() || target.level().isClientSide) {
            return;
        }
        
        // 计算持续时间加成（如果目标是玩家且是法师）
        int modifiedDuration = duration;
//        if (target instanceof Player player) {
//            float durationBonus = com.muyun.evolutionary_mod.system.mage.MageAttributeSystem.getInstance().calculateElementDurationBonus(player);
//            modifiedDuration = (int) (duration * (1.0f + durationBonus));
//        }
        
        ElementAura aura = new ElementAura(element, gauge, modifiedDuration, target);
        
        ELEMENT_AURAS.computeIfAbsent(target, k -> new ArrayList<>()).add(aura);
        
        // 播放视觉效果和音效
        playElementEffects(target, element);
    }
    
    /**
     * 处理所有元素的tick效果
     */
    public static void tickElementEffects(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || entity.level().isClientSide) {
            return;
        }
        
        List<ElementAura> auras = ELEMENT_AURAS.get(entity);
        if (auras == null || auras.isEmpty()) {
            return;
        }
        
        // 处理每个元素效果
        Iterator<ElementAura> iterator = auras.iterator();
        while (iterator.hasNext()) {
            ElementAura aura = iterator.next();
            
            if (aura.isExpired()) {
                iterator.remove();
                continue;
            }
            
            // 应用元素效果
            applyElementTickEffect(entity, aura);
            
            // 减少tick
            aura.tick();
        }
        
        // 清理空列表
        if (auras.isEmpty()) {
            ELEMENT_AURAS.remove(entity);
        }
    }
    
    /**
     * 应用元素的tick效果
     */
    private static void applyElementTickEffect(LivingEntity entity, ElementAura aura) {
        ElementEffectConfig config = ELEMENT_CONFIGS.get(aura.element);
        if (config == null) return;
        
        Level level = entity.level();
        
        // 所有效果计数器 - 每秒触发一次（每20 ticks）
        aura.effectTickCounter++;
        boolean shouldTriggerEffect = (aura.effectTickCounter >= 20);
        if (shouldTriggerEffect) {
            aura.effectTickCounter = 0;
        }
        
        // 伤害效果 - 每秒触发一次
        // 注意：混沌元素的伤害在 applyChaosEffect 中处理，不在这里处理
        if (config.hasTickDamage() && aura.element != ElementType.CHAOS && shouldTriggerEffect) {
            float damage = config.getRandomTickDamage() * aura.gauge;
            if (damage > 0) {
                entity.hurt(level.damageSources().magic(), damage);
                
                // 连锁伤害（雷元素）- 在触发伤害时同时触发
                if (aura.element == ElementType.THUNDER && config.chainRange > 0) {
                    applyChainDamage(entity, config.chainRange, damage);
                }
            }
        }
        
        // 治疗效果 - 每秒触发一次
        if (config.hasTickHealing() && entity instanceof Player player && shouldTriggerEffect) {
            float healing = config.getRandomTickHealing() * aura.gauge;
            if (healing > 0) {
                player.heal(healing);
            }
        }
        
        // 移动速度修改 - 每tick应用MobEffect（持续效果，不会造成性能问题）
        if (config.movementSpeedModifier != 0) {
            // 通过MobEffect实现，每tick刷新持续时间
            int amplifier = (int) (Math.abs(config.movementSpeedModifier) * 10);
            MobEffectInstance effect = new MobEffectInstance(
                config.movementSpeedModifier > 0 ? MobEffects.MOVEMENT_SPEED : MobEffects.MOVEMENT_SLOWDOWN,
                40, amplifier, false, false); // 持续时间改为40 ticks（2秒），减少刷新频率
            entity.addEffect(effect);
        }
        
        // 攻击速度修改 - 每tick应用MobEffect（持续效果，不会造成性能问题）
        if (config.attackSpeedModifier != 0) {
            // 通过MobEffect实现，每tick刷新持续时间
            int amplifier = (int) (Math.abs(config.attackSpeedModifier) * 10);
            MobEffectInstance effect = new MobEffectInstance(
                config.attackSpeedModifier > 0 ? MobEffects.DIG_SPEED : MobEffects.DIG_SLOWDOWN,
                40, amplifier, false, false); // 持续时间改为40 ticks（2秒），减少刷新频率
            entity.addEffect(effect);
        }
        
        // 空间传送 - 每秒触发一次
        if (aura.element == ElementType.SPACE && config.teleportChance > 0 && shouldTriggerEffect) {
            if (Math.random() < config.teleportChance * aura.gauge) {
                teleportEntity(entity, config.teleportRangeMin, config.teleportRangeMax);
            }
        }
        
        // 混沌随机效果 - 每秒触发一次
        if (aura.element == ElementType.CHAOS && config.randomEffects && shouldTriggerEffect) {
            applyChaosEffect(entity, aura);
        }
        
        // 净化效果（神圣元素）- 每秒触发一次
        if (aura.element == ElementType.HOLY && config.cleanseNegative && shouldTriggerEffect) {
            cleanseNegativeEffects(entity);
        }
    }
    
    /**
     * 应用连锁伤害（雷元素）
     */
    private static void applyChainDamage(LivingEntity source, float range, float damage) {
        if (damage <= 0) return;
        
        Level level = source.level();
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
            LivingEntity.class,
            source.getBoundingBox().inflate(range),
            e -> e != source && e.isAlive()
        );
        
        for (LivingEntity target : nearbyEntities) {
            target.hurt(level.damageSources().magic(), damage);
        }
    }
    
    /**
     * 传送实体（空间元素）
     */
    private static void teleportEntity(LivingEntity entity, float minRange, float maxRange) {
        if (entity.level().isClientSide) return;
        
        double angle = Math.random() * Math.PI * 2;
        double distance = minRange + Math.random() * (maxRange - minRange);
        double x = entity.getX() + Math.cos(angle) * distance;
        double z = entity.getZ() + Math.sin(angle) * distance;
        double y = entity.getY();
        
        entity.teleportTo(x, y, z);
    }
    
    /**
     * 应用混沌效果 - 每秒触发一次（每20 ticks）
     * 注意：计数器已在 applyElementTickEffect 中处理
     */
    private static void applyChaosEffect(LivingEntity entity, ElementAura aura) {
        // 计数器已在 applyElementTickEffect 中处理，这里直接应用效果
        
        double rand = Math.random();
        
        if (rand < 0.40) {
            // 造成伤害 - 每秒伤害
            float damage = (float) (3 + Math.random() * 3) * aura.gauge;
            entity.hurt(entity.level().damageSources().magic(), damage);
        } else if (rand < 0.60 && entity instanceof Player player) {
            // 恢复生命值
            float healing = (float) (2 + Math.random() * 2) * aura.gauge;
            player.heal(healing);
        } else if (rand < 0.75) {
            // 移动速度变化
            int amplifier = (int) (Math.random() * 5);
            MobEffectInstance effect = new MobEffectInstance(
                Math.random() < 0.5 ? MobEffects.MOVEMENT_SPEED : MobEffects.MOVEMENT_SLOWDOWN,
                20, amplifier, false, false);
            entity.addEffect(effect);
        }
    }
    
    /**
     * 清除负面效果（神圣元素）
     */
    private static void cleanseNegativeEffects(LivingEntity entity) {

    }
    
    /**
     * 播放元素视觉效果和音效
     */
    private static void playElementEffects(LivingEntity entity, ElementType element) {
        ElementEffectConfig config = ELEMENT_CONFIGS.get(element);
        if (config == null) return;
        
        Level level = entity.level();
        if (level.isClientSide) return;
        
        // 播放粒子效果
        if (config.particleType != null) {
            Vec3 pos = entity.position().add(0, entity.getBbHeight() / 2, 0);
            for (int i = 0; i < 10; i++) {
                double offsetX = (Math.random() - 0.5) * 0.5;
                double offsetY = (Math.random() - 0.5) * 0.5;
                double offsetZ = (Math.random() - 0.5) * 0.5;
                level.addParticle(config.particleType,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    0, 0, 0);
            }
        }
        
        // 播放音效
        if (config.sound != null) {
            level.playSound(null, entity.blockPosition(),
                config.sound, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
    }
    
    /**
     * 计算元素伤害加成
     */
    public static float calculateElementDamage(float baseDamage, ElementType element, Player player) {
        if (player == null) return baseDamage;
        
        // 从饰品获取元素伤害加成
        float elementBonus = getElementDamageBonus(player, element);
        
        // 从法师属性系统获取加成
//        float mageBonus = com.muyun.evolutionary_mod.system.mage.MageAttributeSystem.getInstance().calculateElementDamageBonus(player, element);
        
//        return baseDamage * (1.0f + elementBonus + mageBonus);
        return 0;
    }
    
    /**
     * 获取玩家的元素伤害加成
     */
    public static float getElementDamageBonus(Player player, ElementType element) {
        // TODO: 从饰品系统获取元素伤害加成
        // 这里需要与饰品系统集成
        return 0.0f;
    }
    
    /**
     * 获取目标的所有元素附着
     */
    public static List<ElementAura> getElementAuras(LivingEntity entity) {
        return ELEMENT_AURAS.getOrDefault(entity, Collections.emptyList());
    }
    
    /**
     * 清除目标的所有元素附着
     */
    public static void clearElementAuras(LivingEntity entity) {
        ELEMENT_AURAS.remove(entity);
    }
    
    /**
     * 元素效果配置类
     */
    private static class ElementEffectConfig {
        // 伤害
        public float tickDamageMin = 0;
        public float tickDamageMax = 0;
        
        // 治疗
        public float tickHealingMin = 0;
        public float tickHealingMax = 0;
        
        // 属性修改
        public float movementSpeedModifier = 0;
        public float attackSpeedModifier = 0;
        public float attackDamageModifier = 0;
        public float armorBonus = 0;
        public float knockbackResistance = 0;
        public float attributeReduction = 0;
        
        // 特殊效果
        public float chainRange = 0;
        public float diffusionRange = 0;
        public float teleportChance = 0;
        public float teleportRangeMin = 0;
        public float teleportRangeMax = 0;
        public float areaDamageMultiplier = 0;
        public float confusionChance = 0;
        public float healthRegenBonus = 0;
        public float undeadDamageBonus = 0;
        public boolean cleanseNegative = false;
        public boolean randomEffects = false;
        
        // 视觉效果
        public net.minecraft.core.particles.ParticleOptions particleType = null;
        public net.minecraft.sounds.SoundEvent sound = null;
        
        // 持续时间
        public int duration = 100;
        
        // Builder方法
        public ElementEffectConfig setTickDamage(float min, float max) {
            this.tickDamageMin = min;
            this.tickDamageMax = max;
            return this;
        }
        
        public ElementEffectConfig setTickHealing(float min, float max) {
            this.tickHealingMin = min;
            this.tickHealingMax = max;
            return this;
        }
        
        public ElementEffectConfig setMovementSpeedModifier(float modifier) {
            this.movementSpeedModifier = modifier;
            return this;
        }
        
        public ElementEffectConfig setAttackSpeedModifier(float modifier) {
            this.attackSpeedModifier = modifier;
            return this;
        }
        
        public ElementEffectConfig setAttackDamageModifier(float modifier) {
            this.attackDamageModifier = modifier;
            return this;
        }
        
        public ElementEffectConfig setArmorBonus(float bonus) {
            this.armorBonus = bonus;
            return this;
        }
        
        public ElementEffectConfig setKnockbackResistance(float resistance) {
            this.knockbackResistance = resistance;
            return this;
        }
        
        public ElementEffectConfig setChainRange(float range) {
            this.chainRange = range;
            return this;
        }
        
        public ElementEffectConfig setDiffusionRange(float range) {
            this.diffusionRange = range;
            return this;
        }
        
        public ElementEffectConfig setTeleportChance(float chance) {
            this.teleportChance = chance;
            return this;
        }
        
        public ElementEffectConfig setTeleportRange(float min, float max) {
            this.teleportRangeMin = min;
            this.teleportRangeMax = max;
            return this;
        }
        
        public ElementEffectConfig setAreaDamageMultiplier(float multiplier) {
            this.areaDamageMultiplier = multiplier;
            return this;
        }
        
        public ElementEffectConfig setConfusionChance(float chance) {
            this.confusionChance = chance;
            return this;
        }
        
        public ElementEffectConfig setHealthRegenBonus(float bonus) {
            this.healthRegenBonus = bonus;
            return this;
        }
        
        public ElementEffectConfig setUndeadDamageBonus(float bonus) {
            this.undeadDamageBonus = bonus;
            return this;
        }
        
        public ElementEffectConfig setCleanseNegative(boolean cleanse) {
            this.cleanseNegative = cleanse;
            return this;
        }
        
        public ElementEffectConfig setRandomEffects(boolean random) {
            this.randomEffects = random;
            return this;
        }
        
        public ElementEffectConfig setAttributeReduction(float reduction) {
            this.attributeReduction = reduction;
            return this;
        }
        
        public ElementEffectConfig setDuration(int ticks) {
            this.duration = ticks;
            return this;
        }
        
        public ElementEffectConfig setParticleType(net.minecraft.core.particles.ParticleOptions particle) {
            this.particleType = particle;
            return this;
        }
        
        public ElementEffectConfig setSound(net.minecraft.sounds.SoundEvent sound) {
            this.sound = sound;
            return this;
        }
        
        public boolean hasTickDamage() {
            return tickDamageMax > 0;
        }
        
        public boolean hasTickHealing() {
            return tickHealingMax > 0;
        }
        
        public float getRandomTickDamage() {
            return tickDamageMin + (float) Math.random() * (tickDamageMax - tickDamageMin);
        }
        
        public float getRandomTickHealing() {
            return tickHealingMin + (float) Math.random() * (tickHealingMax - tickHealingMin);
        }
    }
}

