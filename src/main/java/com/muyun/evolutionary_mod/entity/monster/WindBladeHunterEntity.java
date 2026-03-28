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
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 风刃猎手 - Wind Blade Hunter
 * 风元素掠夺者：穿透箭，极速，跳跃风压波，低血量疾风模式
 */
public class WindBladeHunterEntity extends Pillager {

    private boolean furyMode = false;

    public WindBladeHunterEntity(EntityType<? extends WindBladeHunterEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pillager.createAttributes()
                .add(Attributes.MAX_HEALTH, 45.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 35.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedCrossbowAttackGoal<>(this, 1.0, 20.0f));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0f));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // 低血量进入疾风模式
        float pct = this.getHealth() / this.getMaxHealth();
        if (!furyMode && pct < 0.3f) {
            furyMode = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6);
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 1, false, false));
        }

        // 跳跃时释放风压波
        if (!this.onGround() && this.getDeltaMovement().y < -0.1) {
            if (this.level() instanceof ServerLevel sl && this.tickCount % 4 == 0) {
                sl.sendParticles(ParticleTypes.CLOUD,
                        this.getX(), this.getY(), this.getZ(), 5, 0.5, 0.1, 0.5, 0.05);
            }
        }

        if (this.level() instanceof ServerLevel sl && this.tickCount % 4 == 0) {
            sl.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY() + 1.0, this.getZ(), 1, 0.2, 0.2, 0.2, 0.01);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        // 穿透箭：射向目标，命中后继续飞行
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        Arrow arrow = new Arrow(this.level(), this, arrowStack, null);
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx*dx + dz*dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, furyMode ? 3.0f : 2.0f, 8.0f);
        arrow.pickup = net.minecraft.world.entity.projectile.AbstractArrow.Pickup.DISALLOWED;
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putByte("PierceLevel", (byte) 3);
        arrow.readAdditionalSaveData(tag);
        this.level().addFreshEntity(arrow);
    }
}

