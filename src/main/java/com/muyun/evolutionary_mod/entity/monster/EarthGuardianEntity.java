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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 大地守卫 - Earth Guardian
 * 土元素铁傀儡：超高血量护甲，跺脚击退，护甲分阶段，死亡生成石块
 */
public class EarthGuardianEntity extends IronGolem {

    private int stompCooldown = 0;
    private int armorStage = 3; // 3=满甲, 2=破损, 1=严重破损, 0=无甲

    public EarthGuardianEntity(EntityType<? extends EarthGuardianEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && stompCooldown <= 0) {
            stompCooldown = 60;
            // 跺脚击退范围效果
            AABB box = this.getBoundingBox().inflate(4);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
            for (Player p : players) {
                p.hurt(this.level().damageSources().mobAttack(this), 4.0f);
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                double dx = p.getX() - this.getX();
                double dz = p.getZ() - this.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > 0) {
                    p.push(dx / dist * 1.5, 0.4, dz / dist * 1.5);
                }
            }
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION,
                        this.getX(), this.getY(), this.getZ(), 5, 1.0, 0.1, 1.0, 0.1);
            }
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hit = super.hurt(source, amount);
        if (hit) {
            float pct = this.getHealth() / this.getMaxHealth();
            int newStage = pct > 0.66f ? 3 : pct > 0.33f ? 2 : pct > 0.0f ? 1 : 0;
            if (newStage < armorStage) {
                armorStage = newStage;
                double newArmor = switch (armorStage) {
                    case 2 -> 14.0;
                    case 1 -> 7.0;
                    default -> 0.0;
                };
                this.getAttribute(Attributes.ARMOR).setBaseValue(newArmor);
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.EXPLOSION,
                            this.getX(), this.getY() + 1, this.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (stompCooldown > 0) stompCooldown--;
        if (this.level() instanceof ServerLevel sl && this.tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.POOF,
                    this.getX(), this.getY() + 1.0, this.getZ(), 3, 0.5, 0.5, 0.5, 0.02);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel)) return;
        BlockPos center = this.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                if (this.level().getBlockState(pos).isAir()) {
                    if (this.getRandom().nextFloat() < 0.4f) {
                        this.level().setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
