package com.muyun.evolutionary_mod.entity;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.entity.monster.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, EvolutionaryMod.MODID);

    /** 烬焰骷髅 - 火元素骷髅，射出火焰箭，低血量自爆 */
    public static final DeferredHolder<EntityType<?>, EntityType<CinderSkeletonEntity>> CINDER_SKELETON =
            ENTITY_TYPES.register("cinder_skeleton",
                    () -> EntityType.Builder.<CinderSkeletonEntity>of(CinderSkeletonEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.99f)
                            .clientTrackingRange(8)
                            .build("cinder_skeleton"));

    /** 雷霆蜘蛛 - 雷元素蜘蛛，跳跃释放雷击范围伤害 */
    public static final DeferredHolder<EntityType<?>, EntityType<ThunderSpiderEntity>> THUNDER_SPIDER =
            ENTITY_TYPES.register("thunder_spider",
                    () -> EntityType.Builder.<ThunderSpiderEntity>of(ThunderSpiderEntity::new, MobCategory.MONSTER)
                            .sized(1.4f, 0.9f)
                            .clientTrackingRange(8)
                            .build("thunder_spider"));

    /** 暗影潜行者 - 暗影末影人，隐身接近，近战造成虚弱 */
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowStalkerEntity>> SHADOW_STALKER =
            ENTITY_TYPES.register("shadow_stalker",
                    () -> EntityType.Builder.<ShadowStalkerEntity>of(ShadowStalkerEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 2.9f)
                            .clientTrackingRange(8)
                            .build("shadow_stalker"));

    /** 冰霜尸鬼 - 冰元素僵尸，攻击减速，死亡留下冰陷阱 */
    public static final DeferredHolder<EntityType<?>, EntityType<FrostGhoulEntity>> FROST_GHOUL =
            ENTITY_TYPES.register("frost_ghoul",
                    () -> EntityType.Builder.<FrostGhoulEntity>of(FrostGhoulEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("frost_ghoul"));

    /** 土甲战士 - 土元素僵尸猪灵，超高防御，需破甲才能有效伤害 */
    public static final DeferredHolder<EntityType<?>, EntityType<IroncladWarriorEntity>> IRONCLAD_WARRIOR =
            ENTITY_TYPES.register("ironclad_warrior",
                    () -> EntityType.Builder.<IroncladWarriorEntity>of(IroncladWarriorEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("ironclad_warrior"));

    /** 熔岩射手 - 火元素骷髅，射出熔岩弹，死亡引爆岩浆 */
    public static final DeferredHolder<EntityType<?>, EntityType<LavaShooterEntity>> LAVA_SHOOTER =
            ENTITY_TYPES.register("lava_shooter",
                    () -> EntityType.Builder.<LavaShooterEntity>of(LavaShooterEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.99f)
                            .clientTrackingRange(8)
                            .build("lava_shooter"));

    /** 雷云术士 - 雷元素女巫，召唤闪电，反弹近战伤害，死亡雷暴 */
    public static final DeferredHolder<EntityType<?>, EntityType<StormMageEntity>> STORM_MAGE =
            ENTITY_TYPES.register("storm_mage",
                    () -> EntityType.Builder.<StormMageEntity>of(StormMageEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("storm_mage"));

    /** 深渊潜伏者 - 水+暗影溺尸，水中强化，毒液攻击，潜地突袭 */
    public static final DeferredHolder<EntityType<?>, EntityType<AbyssLurkerEntity>> ABYSS_LURKER =
            ENTITY_TYPES.register("abyss_lurker",
                    () -> EntityType.Builder.<AbyssLurkerEntity>of(AbyssLurkerEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("abyss_lurker"));

    /** 圣焰骑士 - 神圣元素僵尸猪灵，护盾，灼烧攻击，死亡光柱 */
    public static final DeferredHolder<EntityType<?>, EntityType<HolyKnightEntity>> HOLY_KNIGHT =
            ENTITY_TYPES.register("holy_knight",
                    () -> EntityType.Builder.<HolyKnightEntity>of(HolyKnightEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(8)
                            .build("holy_knight"));

    /** 混沌变形体 - 混沌元素大史莱姆，随机切换形态，死亡分裂 */
    public static final DeferredHolder<EntityType<?>, EntityType<ChaosShifterEntity>> CHAOS_SHIFTER =
            ENTITY_TYPES.register("chaos_shifter",
                    () -> EntityType.Builder.<ChaosShifterEntity>of(ChaosShifterEntity::new, MobCategory.MONSTER)
                            .sized(2.04f, 2.04f)
                            .clientTrackingRange(8)
                            .build("chaos_shifter"));

    /** 时间扭曲者 - 时间+空间末影人，攻击缓慢，受击闪现，时间领域 */
    public static final DeferredHolder<EntityType<?>, EntityType<TimeWarperEntity>> TIME_WARPER =
            ENTITY_TYPES.register("time_warper",
                    () -> EntityType.Builder.<TimeWarperEntity>of(TimeWarperEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 2.9f)
                            .clientTrackingRange(8)
                            .build("time_warper"));

    /** 亡魂法师 - 亡灵+精神凋灵骷髅，召唤随从，凋零攻击，死亡回血 */
    public static final DeferredHolder<EntityType<?>, EntityType<SoulNecromancerEntity>> SOUL_NECROMANCER =
            ENTITY_TYPES.register("soul_necromancer",
                    () -> EntityType.Builder.<SoulNecromancerEntity>of(SoulNecromancerEntity::new, MobCategory.MONSTER)
                            .sized(0.7f, 2.4f)
                            .clientTrackingRange(8)
                            .build("soul_necromancer"));

    /** 大地守卫 - 土元素铁傀儡，超高血量护甲，跺脚击退，护甲分阶段 */
    public static final DeferredHolder<EntityType<?>, EntityType<EarthGuardianEntity>> EARTH_GUARDIAN =
            ENTITY_TYPES.register("earth_guardian",
                    () -> EntityType.Builder.<EarthGuardianEntity>of(EarthGuardianEntity::new, MobCategory.MONSTER)
                            .sized(1.4f, 2.7f)
                            .clientTrackingRange(10)
                            .build("earth_guardian"));

    /** 风刃猎手 - 风元素掠夺者，穿透箭，极速，低血量疾风模式 */
    public static final DeferredHolder<EntityType<?>, EntityType<WindBladeHunterEntity>> WIND_BLADE_HUNTER =
            ENTITY_TYPES.register("wind_blade_hunter",
                    () -> EntityType.Builder.<WindBladeHunterEntity>of(WindBladeHunterEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("wind_blade_hunter"));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}

