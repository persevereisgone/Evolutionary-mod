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
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 时间扭曲者 - Time Warper
 * 时间+空间元素末影人：攻击缓慢，受击闪现，时间减缓领域
 */
public class TimeWarperEntity extends EnderMan {

    private int domainCooldown = 0;
    private int teleportCooldown = 0;

    public TimeWarperEntity(EntityType<? extends TimeWarperEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.MAX_HEALTH, 65.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,      100, 1));
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hit = super.hurt(source, amount);
        // 受击闪现到附近随机位置
        if (hit && teleportCooldown <= 0) {
            teleportCooldown = 40;
            double tx = this.getX() + (this.getRandom().nextDouble() - 0.5) * 10;
            double tz = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 10;
            this.teleportTo(tx, this.getY(), tz);
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.PORTAL, tx, this.getY() + 1, tz, 20, 0.5, 1.0, 0.5, 0.1);
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        if (teleportCooldown > 0) teleportCooldown--;
        domainCooldown++;

        // 每30秒释放时间减缓领域
        if (domainCooldown >= 600 && this.getTarget() != null) {
            domainCooldown = 0;
            AABB box = this.getBoundingBox().inflate(5);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
            for (Player p : players) {
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                p.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,      100, 2));
            }
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        this.getX(), this.getY() + 1, this.getZ(), 80, 5.0, 1.0, 5.0, 0.05);
            }
        }

        if (this.level() instanceof ServerLevel sl && this.tickCount % 6 == 0) {
            sl.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    this.getX(), this.getY() + 2.0, this.getZ(), 3, 0.4, 0.5, 0.4, 0.02);
        }
    }
}

