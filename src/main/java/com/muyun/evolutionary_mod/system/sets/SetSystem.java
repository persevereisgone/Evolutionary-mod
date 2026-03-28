package com.muyun.evolutionary_mod.system.sets;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.system.sets.handlers.DragonSetEffectHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.*;

/**
 * 套装系统 - Set System
 */
public class SetSystem {

    private static final Map<SetType, Set<Item>> SET_ITEMS = new HashMap<>();
    private static final Map<UUID, Map<SetType, List<SetBonus>>> ACTIVE_BONUSES = new HashMap<>();
    private static final Map<UUID, Double> HEALTH_REGEN_MULTIPLIERS = new HashMap<>();
    private static final Map<UUID, Double> SET_DAMAGE_REDUCTION = new HashMap<>();
    private static final Map<SetType, SetEffectHandler> EFFECT_HANDLERS = new HashMap<>();

    public static void initialize() {
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_RING.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_NECKLACE.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_BRACELET.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_EARRING.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_HEADWEAR.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_BELT.get());
        registerEffectHandler(new DragonSetEffectHandler());
    }

    public static void registerEffectHandler(SetEffectHandler handler) {
        EFFECT_HANDLERS.put(handler.getSetType(), handler);
    }

    public static void registerSetItem(SetType setType, Item item) {
        SET_ITEMS.computeIfAbsent(setType, k -> new HashSet<>()).add(item);
    }

    public static SetType getSetTypeForItem(Item item) {
        for (Map.Entry<SetType, Set<Item>> entry : SET_ITEMS.entrySet()) {
            if (entry.getValue().contains(item)) return entry.getKey();
        }
        return null;
    }

    /**
     * 检测玩家装备的套装件数。
     * NeoForge 1.21.1: 使用 getData() 替代 getCapability()。
     */
    public static int getEquippedPieceCount(Player player, SetType setType) {
        Set<Item> setItems = SET_ITEMS.get(setType);
        if (setItems == null || setItems.isEmpty()) return 0;

        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        int count = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (!stack.isEmpty() && setItems.contains(stack.getItem())) count++;
        }
        return count;
    }

    public static void applySetBonuses(Player player) {
        if (player.level().isClientSide) return;

        UUID playerUUID = player.getUUID();
        Map<SetType, List<SetBonus>> currentActiveBonuses = new HashMap<>();

        for (SetType setType : SetType.values()) {
            int pieceCount = getEquippedPieceCount(player, setType);
            if (pieceCount > 0) {
                List<SetBonus> activeBonuses = setType.getActiveBonuses(pieceCount);
                if (!activeBonuses.isEmpty()) {
                    currentActiveBonuses.put(setType, activeBonuses);
                    for (SetBonus bonus : activeBonuses) bonus.apply(player, pieceCount);
                }
            }
        }

        // 移除不再激活的效果
        Map<SetType, List<SetBonus>> previousActiveBonuses = ACTIVE_BONUSES.get(playerUUID);
        if (previousActiveBonuses != null) {
            for (Map.Entry<SetType, List<SetBonus>> entry : previousActiveBonuses.entrySet()) {
                List<SetBonus> currentBonuses = currentActiveBonuses.get(entry.getKey());
                if (currentBonuses == null || currentBonuses.isEmpty()) {
                    for (SetBonus bonus : entry.getValue()) bonus.remove(player);
                } else {
                    for (SetBonus bonus : entry.getValue()) {
                        if (!currentBonuses.contains(bonus)) bonus.remove(player);
                    }
                }
            }
        }

        if (currentActiveBonuses.isEmpty()) {
            ACTIVE_BONUSES.remove(playerUUID);
            HEALTH_REGEN_MULTIPLIERS.remove(playerUUID);
            SET_DAMAGE_REDUCTION.remove(playerUUID);
        } else {
            ACTIVE_BONUSES.put(playerUUID, currentActiveBonuses);

            boolean hasDragonBloodline = currentActiveBonuses
                    .getOrDefault(SetType.DRAGON, Collections.emptyList())
                    .stream().anyMatch(b -> b instanceof DragonSetBonus.DragonBloodline);
            if (hasDragonBloodline) HEALTH_REGEN_MULTIPLIERS.put(playerUUID, 1.5);
            else HEALTH_REGEN_MULTIPLIERS.remove(playerUUID);

            boolean hasDragonScale = currentActiveBonuses
                    .getOrDefault(SetType.DRAGON, Collections.emptyList())
                    .stream().anyMatch(b -> b instanceof DragonSetBonus.DragonScaleProtection);
            if (hasDragonScale) SET_DAMAGE_REDUCTION.put(playerUUID, 0.15);
            else SET_DAMAGE_REDUCTION.remove(playerUUID);
        }

        applyHealthRegenBonus(player);
    }

    private static void applyHealthRegenBonus(Player player) {
        Double multiplier = HEALTH_REGEN_MULTIPLIERS.get(player.getUUID());
        if (multiplier != null && multiplier > 1.0) {
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            if (currentHealth < maxHealth) {
                float bonusRegen = maxHealth * 0.0005f * (float)(multiplier - 1.0);
                if (bonusRegen > 0) player.heal(bonusRegen);
            }
        }
    }

    public static double getSetDamageReduction(Player player) {
        return SET_DAMAGE_REDUCTION.getOrDefault(player.getUUID(), 0.0);
    }

    public static double getHealthRegenMultiplier(Player player) {
        return HEALTH_REGEN_MULTIPLIERS.getOrDefault(player.getUUID(), 1.0);
    }

    public static boolean hasSetBonus(Player player, SetType setType, int requiredPieces) {
        return getEquippedPieceCount(player, setType) >= requiredPieces;
    }

    public static Map<SetType, List<SetBonus>> getActiveBonuses(Player player) {
        return ACTIVE_BONUSES.getOrDefault(player.getUUID(), Collections.emptyMap());
    }

    public static void cleanupPlayer(Player player) {
        UUID playerUUID = player.getUUID();
        Map<SetType, List<SetBonus>> activeBonuses = ACTIVE_BONUSES.remove(playerUUID);
        if (activeBonuses != null) {
            for (List<SetBonus> bonuses : activeBonuses.values()) {
                for (SetBonus bonus : bonuses) bonus.remove(player);
            }
        }
    }

    /**
     * 处理玩家受到伤害时的事件。
     * NeoForge 1.21.1: 事件类型改为 LivingIncomingDamageEvent。
     */
    public static void handlePlayerHurt(Player player, LivingIncomingDamageEvent event) {
        for (SetType setType : SetType.values()) {
            SetEffectHandler handler = EFFECT_HANDLERS.get(setType);
            if (handler != null) {
                int pieceCount = getEquippedPieceCount(player, setType);
                if (pieceCount > 0) handler.onPlayerHurt(player, event, pieceCount);
            }
        }
    }

    /**
     * 处理玩家造成伤害时的事件。
     * NeoForge 1.21.1: 事件类型改为 LivingIncomingDamageEvent。
     */
    public static void handlePlayerAttack(Player player, LivingIncomingDamageEvent event, LivingEntity target) {
        for (SetType setType : SetType.values()) {
            SetEffectHandler handler = EFFECT_HANDLERS.get(setType);
            if (handler != null) {
                int pieceCount = getEquippedPieceCount(player, setType);
                if (pieceCount > 0) handler.onPlayerAttack(player, event, target, pieceCount);
            }
        }
    }
}
