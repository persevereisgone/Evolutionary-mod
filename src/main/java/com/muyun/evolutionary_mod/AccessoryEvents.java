package com.muyun.evolutionary_mod;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.types.Bracelets;
import com.muyun.evolutionary_mod.menu.AccessoryMenu;
import com.muyun.evolutionary_mod.system.combat.ArmorPenetrationSystem;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import com.muyun.evolutionary_mod.system.effects.AccessoryEffectCalculator;
import com.muyun.evolutionary_mod.system.sets.SetSystem;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 饰品事件处理器 - Accessory Events Handler
 *
 * NeoForge 1.21.1:
 * - TickEvent.PlayerTickEvent (Phase.END) -> PlayerTickEvent.Post
 * - LivingHurtEvent -> LivingIncomingDamageEvent
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AccessoryEvents {

    private static boolean hasItem(Player player, Item item) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }

    /**
     * 每 Tick 末尾：应用所有饰品属性效果（仅服务端，菜单关闭时）。
     */
    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (player.containerMenu instanceof AccessoryMenu) return;
        AccessoryEffectCalculator.calculateAndApplyAllEffects(player);
    }

    /**
     * 伤害事件：防御（伤害减免）+ 进攻（暴击）。
     * NeoForge 1.21.1 使用 LivingIncomingDamageEvent 替代旧版 LivingHurtEvent。
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        // 防御方：玩家受伤
        if (event.getEntity() instanceof Player player) {
            if (player.level().isClientSide) return;

            // 守护手镯效果
            if (hasItem(player, Bracelets.BRACELET_GUARD.get())) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, false, true));
            }

            // 套装触发效果
            SetSystem.handlePlayerHurt(player, event);

            // 饰品伤害减免
            float reduced = DamageReductionSystem.applyDamageReduction(player, event.getAmount());
            event.setAmount(reduced);
        }

        // 进攻方：玩家造成伤害
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.level().isClientSide) return;

            // 套装攻击触发效果
            SetSystem.handlePlayerAttack(player, event, event.getEntity());

            // 暴击处理
            float processed = CritSystem.processCriticalHit(player, event.getAmount(), event.getEntity());
            event.setAmount(processed);

            // 护甲穿透处理
            float penetrated = ArmorPenetrationSystem.applyArmorPenetration(player, event.getAmount(), event.getEntity());
            event.setAmount(penetrated);
        }
    }
}
