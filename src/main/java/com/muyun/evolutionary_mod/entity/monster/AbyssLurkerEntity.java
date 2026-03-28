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
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 深渊潜伏者 - Abyss Lurker
 * 水+暗影元素溺尸：水中强化，毒液三叉戟，潜地突袭
 */
public class AbyssLurkerEntity extends Drowned {

    private int ambushCooldown = 0;
    private boolean isAmbushing = false;
    private int ambushTimer = 0;

    public AbyssLurkerEntity(EntityType<? extends AbyssLurkerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Drowned.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
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
            living.addEffect(new MobEffectInstance(MobEffects.POISON,   100, 1));
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.BUBBLE_POP,
                        living.getX(), living.getY() + 1, living.getZ(), 10, 0.3, 0.5, 0.3, 0.05);
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // 水中强化
        if (this.isInWater()) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,  20, 1, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,    20, 1, false, false));
        }

        // 潜地突袭
        ambushCooldown++;
        if (!isAmbushing && ambushCooldown >= 300 && this.getTarget() != null) {
            ambushCooldown = 0;
            isAmbushing = true;
            ambushTimer = 60;
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 80, 0));
            this.setInvisible(true);
        }
        if (isAmbushing) {
            ambushTimer--;
            if (ambushTimer <= 0) {
                isAmbushing = false;
                this.setInvisible(false);
                // 传送到目标旁突袭
                if (this.getTarget() != null) {
                    LivingEntity t = this.getTarget();
                    this.teleportTo(t.getX(), t.getY(), t.getZ());
                    this.doHurtTarget(t);
                }
            }
        }

        if (this.level() instanceof ServerLevel sl && this.tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.3, 0.4, 0.3, 0.02);
        }
    }
}

