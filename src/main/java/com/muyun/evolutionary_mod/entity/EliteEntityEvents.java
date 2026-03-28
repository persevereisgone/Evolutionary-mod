package com.muyun.evolutionary_mod.entity;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.entity.monster.*;

import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

/**
 * 精英怪物事件处理器 - Elite Entity Events
 *
 * 负责：
 * 1. 注册自定义实体的属性（EntityAttributeCreationEvent）
 * 2. 注册生成位置规则（RegisterSpawnPlacementsEvent）
 */
public class EliteEntityEvents {

    @EventBusSubscriber(modid = EvolutionaryMod.MODID)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(ModEntities.CINDER_SKELETON.get(),   CinderSkeletonEntity.createAttributes().build());
            event.put(ModEntities.THUNDER_SPIDER.get(),    ThunderSpiderEntity.createAttributes().build());
            event.put(ModEntities.SHADOW_STALKER.get(),    ShadowStalkerEntity.createAttributes().build());
            event.put(ModEntities.FROST_GHOUL.get(),       FrostGhoulEntity.createAttributes().build());
            event.put(ModEntities.IRONCLAD_WARRIOR.get(),  IroncladWarriorEntity.createAttributes().build());
            // 新增10种精英怪
            event.put(ModEntities.LAVA_SHOOTER.get(),      LavaShooterEntity.createAttributes().build());
            event.put(ModEntities.STORM_MAGE.get(),        StormMageEntity.createAttributes().build());
            event.put(ModEntities.ABYSS_LURKER.get(),      AbyssLurkerEntity.createAttributes().build());
            event.put(ModEntities.HOLY_KNIGHT.get(),       HolyKnightEntity.createAttributes().build());
            event.put(ModEntities.CHAOS_SHIFTER.get(),     ChaosShifterEntity.createAttributes().build());
            event.put(ModEntities.TIME_WARPER.get(),       TimeWarperEntity.createAttributes().build());
            event.put(ModEntities.SOUL_NECROMANCER.get(),  SoulNecromancerEntity.createAttributes().build());
            event.put(ModEntities.EARTH_GUARDIAN.get(),    EarthGuardianEntity.createAttributes().build());
            event.put(ModEntities.WIND_BLADE_HUNTER.get(), WindBladeHunterEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            // 烬焰骷髅：地面生成，夜间
            event.register(ModEntities.CINDER_SKELETON.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 雷霆蜘蛛：地面生成，低光照
            event.register(ModEntities.THUNDER_SPIDER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 暗影潜行者：地面生成，夜间
            event.register(ModEntities.SHADOW_STALKER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 冰霜尸鬼：地面生成，夜间
            event.register(ModEntities.FROST_GHOUL.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 土甲战士：地面生成，低光照
            event.register(ModEntities.IRONCLAD_WARRIOR.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 熔岩射手：地面生成，夜间/低光照
            event.register(ModEntities.LAVA_SHOOTER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 雷云术士：地面生成，夜间
            event.register(ModEntities.STORM_MAGE.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 深渊潜伏者：地面生成，夜间
            event.register(ModEntities.ABYSS_LURKER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 圣焰骑士：地面生成，低光照
            event.register(ModEntities.HOLY_KNIGHT.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 混沌变形体：地面生成，任何光照
            event.register(ModEntities.CHAOS_SHIFTER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 时间扭曲者：地面生成，夜间
            event.register(ModEntities.TIME_WARPER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            (level.getSkyDarken() > 4 && level.getMaxLocalRawBrightness(pos) <= 7),
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 亡魂法师：地面生成，低光照
            event.register(ModEntities.SOUL_NECROMANCER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 大地守卫：地面生成，低光照
            event.register(ModEntities.EARTH_GUARDIAN.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

            // 风刃猎手：地面生成，任何光照
            event.register(ModEntities.WIND_BLADE_HUNTER.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnType, pos, random) ->
                            spawnType == MobSpawnType.SPAWNER ||
                            level.getMaxLocalRawBrightness(pos) <= 7,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE);

        }
    }
}
