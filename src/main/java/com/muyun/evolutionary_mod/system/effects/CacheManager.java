package com.muyun.evolutionary_mod.system.effects;


import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import com.muyun.evolutionary_mod.EvolutionaryMod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 缓存管理器
 * 统一管理所有饰品效果处理器的缓存，解决内存泄漏问题
 */
public class CacheManager {
    
    // 所有饰品效果处理器列表
    private static final List<AbstractAccessoryEffectHandler<?>> handlers = new ArrayList<>();
    
    /**
     * 注册效果处理器
     * @param handler 效果处理器
     */
    public static void registerHandler(AbstractAccessoryEffectHandler<?> handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }
    
    /**
     * 清理玩家缓存
     * @param playerId 玩家UUID
     */
    public static void clearPlayerCache(UUID playerId) {
        for (AbstractAccessoryEffectHandler<?> handler : handlers) {
            handler.clearCache(playerId);
        }
    }
    
    /**
     * 事件监听，玩家离开时清理缓存
     * @param event 玩家离开事件
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        clearPlayerCache(event.getEntity().getUUID());
    }
    
    /**
     * 事件监听，玩家死亡时清理缓存
     * @param event 生物死亡事件
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player) {
            clearPlayerCache(event.getEntity().getUUID());
        }
    }
    
    /**
     * 获取所有已注册的处理器
     * @return 处理器列表
     */
    public static List<AbstractAccessoryEffectHandler<?>> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
