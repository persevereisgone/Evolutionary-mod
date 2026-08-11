package com.muyun.evolutionary_mod.system.weight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "evolutionary_mod")
public class WeightEventHandler {
    // 用于追踪是否已发送警告消息
    private static boolean warningSent = false;
    private static boolean overweightWarningSent = false;

    /**
     * 玩家Tick事件 - 应用速度和饱食惩罚
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || player.isDeadOrDying()) {
            return;
        }

        WeightSystem.WeightStatus status = WeightSystem.getWeightStatus(player);

        // 应用速度惩罚
        applySpeedPenalty(player);

        // 应用饱食惩罚
        applyHungerPenalty(player);

        // 发送状态警告
        sendStatusWarning(player, status);
    }

    /**
     * 应用速度惩罚
     */
    private static void applySpeedPenalty(Player player) {
        double penalty = WeightSystem.getSpeedPenalty(player);
        if (penalty > 0) {
            // 使用属性修饰符应用速度惩罚
            float baseSpeed = 0.1f; // 玩家基础行走速度
            float penaltySpeed = baseSpeed * (float)(1 - penalty);
            player.setSpeed(penaltySpeed);
        }
    }

    /**
     * 应用饱食惩罚
     */
    private static void applyHungerPenalty(Player player) {
        double multiplier = WeightSystem.getHungerMultiplier(player);
        if (multiplier > 1 && player.getRandom().nextFloat() < 0.02 * multiplier) {
            // 随机减少饱食度，超重时概率增加
            if (player.getFoodData().getFoodLevel() > 0) {
                player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 1);
            }
        }
    }

    /**
     * 发送状态警告消息
     */
    private static void sendStatusWarning(Player player, WeightSystem.WeightStatus status) {
        switch (status) {
            case HEAVY:
                if (!warningSent) {
                    player.sendSystemMessage(Component.translatable("message.evolutionary_mod.weight.heavy"));
                    warningSent = true;
                    overweightWarningSent = false;
                }
                break;
            case OVERWEIGHT:
                if (!overweightWarningSent) {
                    player.sendSystemMessage(Component.translatable("message.evolutionary_mod.weight.overweight"));
                    overweightWarningSent = true;
                }
                break;
            case OVERLOADED:
                player.sendSystemMessage(Component.translatable("message.evolutionary_mod.weight.overloaded"));
                break;
            case NORMAL:
                warningSent = false;
                overweightWarningSent = false;
                break;
        }
    }

    /**
     * 玩家加入世界事件 - 初始化
     */
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 可以在这里添加初始化逻辑
        }
    }
}
