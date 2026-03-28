package com.muyun.evolutionary_mod;

import com.muyun.evolutionary_mod.entity.ModEntities;
import com.muyun.evolutionary_mod.entity.monster.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端入口 - 仅在客户端加载。
 */
@Mod(value = EvolutionaryMod.MODID, dist = Dist.CLIENT)
public class EvolutionaryModClient {

    public EvolutionaryModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        EvolutionaryMod.LOGGER.info("[EvolutionaryMod] Client init. Player: {}",
                Minecraft.getInstance().getUser().getName());
    }

    @EventBusSubscriber(modid = EvolutionaryMod.MODID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            // 烬焰骷髅 - 复用原版骷髅渲染器
            event.registerEntityRenderer(ModEntities.CINDER_SKELETON.get(),    ctx -> new SkeletonRenderer(ctx));
            // 雷霆蜘蛛 - 复用原版蜘蛛渲染器
            event.registerEntityRenderer(ModEntities.THUNDER_SPIDER.get(),     ctx -> new SpiderRenderer(ctx));
            // 暗影潜行者 - 复用原版末影人渲染器
            event.registerEntityRenderer(ModEntities.SHADOW_STALKER.get(),     ctx -> new EndermanRenderer(ctx));
            // 冰霜尸鬼 - 复用原版僵尸渲染器
            event.registerEntityRenderer(ModEntities.FROST_GHOUL.get(),        ctx -> new ZombieRenderer(ctx));
            // 土甲战士
            event.registerEntityRenderer(ModEntities.IRONCLAD_WARRIOR.get(),   ctx -> new ZombieRenderer(ctx));
            // 新增10种精英怪渲染器
            event.registerEntityRenderer(ModEntities.LAVA_SHOOTER.get(),       ctx -> new SkeletonRenderer(ctx));
            event.registerEntityRenderer(ModEntities.STORM_MAGE.get(),         ctx -> new WitchRenderer(ctx));
            event.registerEntityRenderer(ModEntities.ABYSS_LURKER.get(),       ctx -> new ZombieRenderer(ctx));
            event.registerEntityRenderer(ModEntities.HOLY_KNIGHT.get(),        ctx -> new ZombieRenderer(ctx));
            event.registerEntityRenderer(ModEntities.CHAOS_SHIFTER.get(),      ctx -> new SlimeRenderer(ctx));
            event.registerEntityRenderer(ModEntities.TIME_WARPER.get(),        ctx -> new EndermanRenderer(ctx));
            event.registerEntityRenderer(ModEntities.SOUL_NECROMANCER.get(),   ctx -> new WitherSkeletonRenderer(ctx));
            event.registerEntityRenderer(ModEntities.EARTH_GUARDIAN.get(),     ctx -> new IronGolemRenderer(ctx));
            event.registerEntityRenderer(ModEntities.WIND_BLADE_HUNTER.get(),  ctx -> new PillagerRenderer(ctx));
        }
    }
}
