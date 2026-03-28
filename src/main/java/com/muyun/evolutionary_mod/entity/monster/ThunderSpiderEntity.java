package com.muyun.evolutionary_mod.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 雷霆蜘蛛 - Thunder Spider
 *
 * 雷元素精英蜘蛛：
 * - 跳跃攻击时释放雷击范围伤害，击中周围所有敌人
 * - 命中目标造成短暂麻痹（缓慢+虚弱）
 * - 每隔一段时间释放一次雷击脉冲
 * - 自身免疫雷击伤害（闪电）
 */
public class ThunderSpiderEntity extends Spider {

    private static final int PULSE_COOLDOWN = 60; // 3秒一次脉冲
    private int pulseTick = 0;

    public ThunderSpiderEntity(EntityType<? extends ThunderSpiderEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4f));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            // 麻痹效果
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
            // 命中雷击粒子
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        living.getX(), living.getY() + 1.0, living.getZ(),
                        12, 0.3, 0.5, 0.3, 0.05);
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        pulseTick++;
        if (pulseTick >= PULSE_COOLDOWN) {
            pulseTick = 0;
            thunderPulse();
        }

        // 持续粒子
        if (this.level() instanceof ServerLevel sl && this.tickCount % 5 == 0) {
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    2, 0.4, 0.3, 0.4, 0.01);
        }
    }

    /** 释放雷击脉冲，伤害并麻痹范围内所有玩家 */
    private void thunderPulse() {
        if (!(this.level() instanceof ServerLevel sl)) return;
        double range = 4.0;
        AABB box = this.getBoundingBox().inflate(range);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
        for (Player p : players) {
            p.hurt(this.level().damageSources().mobAttack(this), 3.0f);
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
        }
        sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                this.getX(), this.getY() + 1.0, this.getZ(),
                30, range * 0.5, 0.5, range * 0.5, 0.1);
        this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5f, 1.8f);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 免疫闪电伤害
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)) return false;
        return super.hurt(source, amount);
    }
}

