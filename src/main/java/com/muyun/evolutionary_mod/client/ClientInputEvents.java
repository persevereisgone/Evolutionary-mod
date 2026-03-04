package com.muyun.evolutionary_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.ModMenus;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.menu.AccessoryMenu;

/**
 * 客户端输入事件（旧实现占位）
 *
 * 原本这里通过 ClientTick 事件轮询按键状态并发送网络包。
 * 目前键位注册和界面打开逻辑主要集中在 ClientHandlers / EvolutionaryModClient 中，
 * 为避免依赖 NeoForge 1.21.1 中尚未完全确认的 TickEvent 客户端事件签名，
 * 暂时移除旧实现，只保留空类占位。
 */
public class ClientInputEvents {
    // TODO: 如果未来需要在客户端 tick 中做额外逻辑，可按新事件 API 单独添加。
}
