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
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 暗影潜行者 - Shadow Stalker
 *
 * 暗影元素精英末影人：
 * - 近战攻击造成虚弱+失明效果
 * - 传送冷却比普通末影人短（更频繁）
 * - 攻击时身上涌现暗影粒子
 * - 低血量时进入隐身状态并短暂传送脱离
 */
public class ShadowStalkerEntity extends EnderMan {

    private static final float SHADOW_THRESHOLD = 0.4f;
    private boolean shadowEscape = false;
    private int escapeTimer = 0;

    public ShadowStalkerEntity(EntityType<? extends ShadowStalkerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0f));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,     100, 1));
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,     60, 0));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,  80, 0));
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.PORTAL,
                        living.getX(), living.getY() + 1.0, living.getZ(),
                        16, 0.3, 0.5, 0.3, 0.1);
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // 低血量触发暗影逃脱
        if (!shadowEscape && this.getHealth() / this.getMaxHealth() < SHADOW_THRESHOLD) {
            shadowEscape = true;
            escapeTimer = 60;
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 80, 0));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 2));
            // 随机传送逃脱
            for (int i = 0; i < 16; i++) {
                double tx = this.getX() + (this.getRandom().nextDouble() - 0.5) * 16;
                double ty = this.getY() + (this.getRandom().nextInt(3) - 1);
                double tz = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 16;
                if (this.randomTeleport(tx, ty, tz, true)) break;
            }
        }

        if (escapeTimer > 0) escapeTimer--;
        if (escapeTimer == 0 && shadowEscape) shadowEscape = false;

        // 持续暗影粒子
        if (this.level() instanceof ServerLevel sl && this.tickCount % 6 == 0) {
            sl.sendParticles(ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.5, this.getZ(),
                    3, 0.3, 0.5, 0.3, 0.02);
        }
    }
}

