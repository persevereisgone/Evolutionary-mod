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
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 雷云术士 - Storm Mage
 * 雷元素女巫：召唤闪电，反弹近战伤害，死亡雷暴
 */
public class StormMageEntity extends Witch {

    private int lightningCooldown = 0;

    public StormMageEntity(EntityType<? extends StormMageEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Witch.createAttributes()
                .add(Attributes.MAX_HEALTH, 55.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 30.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        lightningCooldown++;
        if (lightningCooldown >= 100 && this.getTarget() != null) {
            lightningCooldown = 0;
            LivingEntity target = this.getTarget();
            if (this.level() instanceof ServerLevel sl) {
                net.minecraft.world.entity.LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
                if (bolt != null) {
                    bolt.moveTo(target.getX(), target.getY(), target.getZ());
                    bolt.setVisualOnly(false);
                    sl.addFreshEntity(bolt);
                }
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX(), target.getY() + 1, target.getZ(), 20, 0.5, 1.0, 0.5, 0.1);
            }
        }

        if (this.level() instanceof ServerLevel sl && this.tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    this.getX(), this.getY() + 1.5, this.getZ(), 2, 0.4, 0.4, 0.4, 0.02);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hit = super.hurt(source, amount);
        // 受到近战攻击时反弹雷击
        if (hit && source.getEntity() instanceof LivingEntity attacker
                && attacker.distanceTo(this) < 4.0
                && this.level() instanceof ServerLevel sl) {
            attacker.hurt(this.level().damageSources().lightningBolt(), amount * 0.2f);
        }
        return hit;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!(this.level() instanceof ServerLevel sl)) return;
        // 死亡雷暴：6格内随机 5 道闪电
        for (int i = 0; i < 5; i++) {
            double ox = (this.getRandom().nextDouble() - 0.5) * 12;
            double oz = (this.getRandom().nextDouble() - 0.5) * 12;
            net.minecraft.world.entity.LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
            if (bolt != null) {
                bolt.moveTo(this.getX() + ox, this.getY(), this.getZ() + oz);
                sl.addFreshEntity(bolt);
            }
        }
    }
}

