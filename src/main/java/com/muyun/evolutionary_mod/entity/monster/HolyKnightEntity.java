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
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 圣焰骑士 - Holy Knight
 * 神圣元素僵尸猪灵：神圣护盾，死亡光柱，对玩家灼烧
 */
public class HolyKnightEntity extends ZombifiedPiglin {

    private int shieldCooldown = 0;
    private boolean shieldActive = false;
    private int shieldTimer = 0;

    public HolyKnightEntity(EntityType<? extends HolyKnightEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ZombifiedPiglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 70.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
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
            living.igniteForSeconds(4);
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.END_ROD,
                        living.getX(), living.getY() + 1, living.getZ(), 8, 0.3, 0.5, 0.3, 0.05);
            }
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 护盾激活时减免50%伤害
        if (shieldActive) amount *= 0.5f;
        boolean hit = super.hurt(source, amount);
        // 受击触发护盾（20秒冷却）
        if (hit && !shieldActive && shieldCooldown <= 0) {
            shieldActive = true;
            shieldTimer = 60;
            shieldCooldown = 400;
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (shieldCooldown > 0) shieldCooldown--;
            if (shieldTimer > 0) shieldTimer--;
            else shieldActive = false;
        }
        if (this.level() instanceof ServerLevel sl && this.tickCount % 6 == 0) {
            sl.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 1.5, this.getZ(), 2, 0.3, 0.4, 0.3, 0.02);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel sl)) return;
        // 死亡光柱：持续伤害周围敌人
        AABB box = this.getBoundingBox().inflate(4);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
        for (Player p : players) {
            p.hurt(this.level().damageSources().magic(), 6.0f);
            p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        }
        sl.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 60, 0.5, 2.0, 0.5, 0.1);
    }
}

