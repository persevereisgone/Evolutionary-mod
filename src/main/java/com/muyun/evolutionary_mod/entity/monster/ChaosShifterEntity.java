package com.muyun.evolutionary_mod.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 混沌变形体 - Chaos Shifter
 * 混沌元素大史莱姆：随机切换形态，死亡分裂，攻击附加随机元素
 */
public class ChaosShifterEntity extends Slime {

    private int morphCooldown = 0;
    private int currentMode = 0; // 0=速度, 1=防御, 2=攻击
    private static final String[] MODE_NAMES = {"speed", "defense", "attack"};

    public ChaosShifterEntity(EntityType<? extends ChaosShifterEntity> type, Level level) {
        super(type, level);
        this.setSize(3, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 4.0);
    }

    private void applyMode(int mode) {
        currentMode = mode;
        switch (mode) {
            case 0 -> { // 速度型
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,  200, 2, false, false));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, false, false));
            }
            case 1 -> { // 防御型
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2, false, false));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION,      200, 1, false, false));
            }
            case 2 -> { // 攻击型
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,      200, 2, false, false));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,    200, 0, false, false));
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        morphCooldown++;
        if (morphCooldown >= 200) {
            morphCooldown = 0;
            int newMode = this.getRandom().nextInt(3);
            applyMode(newMode);
        }

        // 粒子颜色随形态变化
        if (this.level() instanceof ServerLevel sl && this.tickCount % 5 == 0) {
            var particle = switch (currentMode) {
                case 0 -> ParticleTypes.ELECTRIC_SPARK;
                case 1 -> ParticleTypes.ENCHANT;
                default -> ParticleTypes.FLAME;
            };
            sl.sendParticles(particle, this.getX(), this.getY() + 0.5, this.getZ(), 3, 0.5, 0.3, 0.5, 0.02);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            // 随机附加效果
            int effect = this.getRandom().nextInt(4);
            switch (effect) {
                case 0 -> living.addEffect(new MobEffectInstance(MobEffects.POISON,    80, 1));
                case 1 -> living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
                case 2 -> living.igniteForSeconds(3);
                case 3 -> living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,   80, 1));
            }
        }
        return hit;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel sl)) return;
        // 50% 概率分裂为2个中型混沌变形体
        if (this.getRandom().nextFloat() < 0.5f) {
            for (int i = 0; i < 2; i++) {
                ChaosShifterEntity mini = new ChaosShifterEntity(
                        com.muyun.evolutionary_mod.entity.ModEntities.CHAOS_SHIFTER.get(), sl);
                mini.setSize(1, true);
                mini.moveTo(this.getX() + (i == 0 ? 1 : -1), this.getY(), this.getZ());
                mini.setHealth(20);
                sl.addFreshEntity(mini);
            }
        }
    }
}

