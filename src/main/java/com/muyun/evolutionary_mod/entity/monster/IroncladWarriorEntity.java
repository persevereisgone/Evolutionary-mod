package com.muyun.evolutionary_mod.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 土甲战士 - Ironclad Warrior
 *
 * 土元素精英僵尸猪灵：
 * - 极高护甲，受到攻击时会触发格挡（小概率减免大量伤害）
 * - 受到足够伤害后进入破甲状态，护甲降低
 * - 攻击造成击退抗性降低效果（踩碎地面）
 * - 死亡时释放土块粒子
 */
public class IroncladWarriorEntity extends ZombifiedPiglin {

    private static final float ARMOR_BREAK_THRESHOLD = 0.5f;
    private static final float BLOCK_CHANCE = 0.25f;
    private boolean armorBroken = false;

    public IroncladWarriorEntity(EntityType<? extends IroncladWarriorEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ZombifiedPiglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 20.0);
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
    public boolean hurt(DamageSource source, float amount) {
        // 破甲检测
        if (!armorBroken && this.getHealth() / this.getMaxHealth() < ARMOR_BREAK_THRESHOLD) {
            armorBroken = true;
            // 破甲后护甲大幅降低
            var armorAttr = this.getAttribute(Attributes.ARMOR);
            if (armorAttr != null) {
                armorAttr.setBaseValue(4.0);
            }
            var toughnessAttr = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttr != null) {
                toughnessAttr.setBaseValue(0.0);
            }
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0));
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION,
                        this.getX(), this.getY() + 1.5, this.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1);
            }
        }

        // 未破甲时有概率格挡
        if (!armorBroken && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)
                && this.getRandom().nextFloat() < BLOCK_CHANCE) {
            amount *= 0.15f; // 格挡减免 85%
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        6, 0.2, 0.3, 0.2, 0.05);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof net.minecraft.world.entity.LivingEntity living) {
            // 踩踏效果：短暂降低击退抗性（方便后续攻击击退）
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel sl && this.tickCount % 10 == 0) {
            // 土块粒子
            sl.sendParticles(
                    new net.minecraft.core.particles.BlockParticleOption(
                            ParticleTypes.FALLING_DUST,
                            net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState()),
                    this.getX(), this.getY() + 0.1, this.getZ(),
                    2, 0.4, 0.1, 0.4, 0.01);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    1, 0, 0, 0, 0);
        }
    }
}

