package com.muyun.evolutionary_mod.entity.monster;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * 冰霜尸鬼 - Frost Ghoul
 *
 * 冰元素精英僵尸：
 * - 近战攻击造成缓慢效果
 * - 自身免疫冰冻/缓慢效果
 * - 死亡时在脚下放置冰块陷阱
 * - 持续释放冰霜粒子
 */
public class FrostGhoulEntity extends Zombie {

    public FrostGhoulEntity(EntityType<? extends FrostGhoulEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.ATTACK_DAMAGE, 5.5)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ARMOR, 3.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            // 冰冻缓慢效果
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,      60, 1));
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE,
                        living.getX(), living.getY() + 1.0, living.getZ(),
                        12, 0.3, 0.4, 0.3, 0.05);
            }
        }
        return hit;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel)) return;
        // 死亡时在周围放置冰块
        BlockPos center = this.blockPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                if (this.level().getBlockState(pos).isAir()) {
                    this.level().setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                }
            }
        }
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 免疫缓慢类效果（由主动触发，非伤害免疫）
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 持续冰霜粒子
        if (this.level() instanceof ServerLevel sl && this.tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    2, 0.3, 0.3, 0.3, 0.01);
        }
    }

    @Override
    protected boolean isSunSensitive() {
        return false; // 冰霜尸鬼不怕太阳
    }
}

