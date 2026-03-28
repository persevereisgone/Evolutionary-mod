package com.muyun.evolutionary_mod.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 烬焰骷髅 - Cinder Skeleton
 *
 * 火元素精英骷髅：
 * - 射出火焰箭，命中后燃烧目标
 * - 生命值低于 30% 时进入狂暴状态，射速加快
 * - 自身免疫火焰伤害
 * - 死亡时释放一圈火焰粒子
 */
public class CinderSkeletonEntity extends AbstractSkeleton {

    private static final float BERSERK_THRESHOLD = 0.3f;
    private boolean isBerserk = false;

    public CinderSkeletonEntity(EntityType<? extends CinderSkeletonEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 20, 15.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack bowStack = this.getProjectile(this.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND));
        AbstractArrow arrow = this.getArrow(bowStack, distanceFactor, null);
        if (arrow instanceof Arrow fireArrow) {
            fireArrow.igniteForSeconds(5);
        }
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, isBerserk ? 1.8f : 1.6f, 12.0f);
        this.level().addFreshEntity(arrow);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // 低血量进入狂暴
        if (!this.level().isClientSide && !isBerserk
                && this.getHealth() / this.getMaxHealth() < BERSERK_THRESHOLD) {
            isBerserk = true;
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
        }
        // 服务端粒子效果
        if (this.level() instanceof ServerLevel sl && this.tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.FLAME,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    1, 0.2, 0.2, 0.2, 0.02);
        }
    }

    @Override
    public boolean isOnFire() { return false; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 免疫火焰伤害
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.LAVA,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1);
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }
}

