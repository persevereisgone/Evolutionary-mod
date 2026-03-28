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
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 亡魂法师 - Soul Necromancer
 * 亡灵+精神元素凋灵骷髅：召唤随从，凋零攻击，死亡回血
 */
public class SoulNecromancerEntity extends WitherSkeleton {

    private int summonCooldown = 0;

    public SoulNecromancerEntity(EntityType<? extends SoulNecromancerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return WitherSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 70.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 30.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER,   100, 1));
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,  60, 0));
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        summonCooldown++;
        // 每20秒召唤2只随从
        if (summonCooldown >= 400 && this.getTarget() != null) {
            summonCooldown = 0;
            if (this.level() instanceof ServerLevel sl) {
                for (int i = 0; i < 2; i++) {
                    double ox = (this.getRandom().nextDouble() - 0.5) * 4;
                    double oz = (this.getRandom().nextDouble() - 0.5) * 4;
                    net.minecraft.world.entity.monster.Skeleton skeleton =
                            EntityType.SKELETON.create(sl);
                    if (skeleton != null) {
                        skeleton.moveTo(this.getX() + ox, this.getY(), this.getZ() + oz,
                                this.getYRot(), 0);
                        skeleton.setHealth(20);
                        sl.addFreshEntity(skeleton);
                    }
                }
                sl.sendParticles(ParticleTypes.SOUL,
                        this.getX(), this.getY() + 1, this.getZ(), 20, 1.0, 0.5, 1.0, 0.05);
            }
        }

        if (this.level() instanceof ServerLevel sl && this.tickCount % 6 == 0) {
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY() + 1.5, this.getZ(), 2, 0.3, 0.4, 0.3, 0.02);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel sl)) return;
        // 死亡时为周围所有怪物恢复50%生命
        AABB box = this.getBoundingBox().inflate(8);
        List<LivingEntity> mobs = this.level().getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity mob : mobs) {
            if (mob instanceof Player || mob == this) continue;
            mob.heal(mob.getMaxHealth() * 0.5f);
        }
        sl.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1, this.getZ(),
                60, 3.0, 1.0, 3.0, 0.1);
    }
}

