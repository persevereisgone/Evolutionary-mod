package com.muyun.evolutionary_mod.system.sets;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.item.sets.DragonItems;
import com.muyun.evolutionary_mod.system.sets.handlers.DragonSetEffectHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


import java.util.*;

/**
 * 套装系统 - Set System
 * 
 * 管理所有套装的检测、效果应用和移除。
 * 负责检测玩家装备的套装件数，并应用相应的套装效果。
 * 
 * Manages detection, effect application, and removal of all sets.
 * Responsible for detecting the number of set pieces equipped by players and applying corresponding set bonuses.
 */
public class SetSystem {
    
    /**
     * 存储每个套装类型对应的饰品物品列表
     * 键：套装类型，值：属于该套装的饰品物品集合
     */
    private static final Map<SetType, Set<Item>> SET_ITEMS = new HashMap<>();
    
    /**
     * 存储每个玩家当前激活的套装效果
     * 键：玩家UUID，值：当前激活的套装效果映射（套装类型 -> 激活的效果列表）
     */
    private static final Map<UUID, Map<SetType, List<SetBonus>>> ACTIVE_BONUSES = new HashMap<>();
    
    /**
     * 存储玩家生命恢复速度加成倍数
     * 键：玩家UUID，值：生命恢复速度加成倍数（1.0 = 无加成，1.5 = +50%）
     */
    private static final Map<UUID, Double> HEALTH_REGEN_MULTIPLIERS = new HashMap<>();
    
    /**
     * 存储玩家伤害减免加成（来自套装效果）
     * 键：玩家UUID，值：伤害减免百分比（0.0 - 1.0）
     */
    private static final Map<UUID, Double> SET_DAMAGE_REDUCTION = new HashMap<>();
    
    /**
     * 存储每个套装类型对应的事件处理器
     * 键：套装类型，值：事件处理器
     */
    private static final Map<SetType, SetEffectHandler> EFFECT_HANDLERS = new HashMap<>();
    
    /**
     * 初始化套装系统
     * 注册每个套装类型对应的饰品物品和事件处理器
     */
    public static void initialize() {
        // 注册龙族套装的饰品物品
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_RING.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_NECKLACE.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_BRACELET.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_EARRING.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_HEADWEAR.get());
        registerSetItem(SetType.DRAGON, DragonItems.DRAGON_BELT.get());
        
        // 注册套装事件处理器
        registerEffectHandler(new DragonSetEffectHandler());
    }
    
    /**
     * 注册套装事件处理器
     * 
     * @param handler 事件处理器
     */
    public static void registerEffectHandler(SetEffectHandler handler) {
        EFFECT_HANDLERS.put(handler.getSetType(), handler);
    }
    
    /**
     * 注册套装物品
     * 
     * @param setType 套装类型
     * @param item 属于该套装的饰品物品
     */
    public static void registerSetItem(SetType setType, Item item) {
        SET_ITEMS.computeIfAbsent(setType, k -> new HashSet<>()).add(item);
    }
    
    /**
     * 获取物品所属的套装类型
     * 
     * @param item 物品
     * @return 套装类型，如果物品不属于任何套装则返回null
     */
    public static SetType getSetTypeForItem(Item item) {
        for (Map.Entry<SetType, Set<Item>> entry : SET_ITEMS.entrySet()) {
            if (entry.getValue().contains(item)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * 检测玩家装备的套装件数
     * 
     * @param player 玩家
     * @param setType 套装类型
     * @return 装备的套装件数
     */
    public static int getEquippedPieceCount(Player player, SetType setType) {
        return 0;
    }
    
    /**
     * 应用所有套装的套装效果
     * 在玩家Tick事件中调用
     * 
     * @param player 玩家
     */
    public static void applySetBonuses(Player player) {
        if (player.level().isClientSide) {
            return; // 只在服务端运行
        }
        
        UUID playerUUID = player.getUUID();
        Map<SetType, List<SetBonus>> currentActiveBonuses = new HashMap<>();
        
        // 遍历所有套装类型
        for (SetType setType : SetType.values()) {
            int pieceCount = getEquippedPieceCount(player, setType);
            
            if (pieceCount > 0) {
                // 获取应该激活的效果
                List<SetBonus> activeBonuses = setType.getActiveBonuses(pieceCount);
                
                if (!activeBonuses.isEmpty()) {
                    currentActiveBonuses.put(setType, activeBonuses);
                    
                    // 应用每个激活的效果
                    for (SetBonus bonus : activeBonuses) {
                        bonus.apply(player, pieceCount);
                    }
                }
            }
        }
        
        // 移除不再激活的效果
        Map<SetType, List<SetBonus>> previousActiveBonuses = ACTIVE_BONUSES.get(playerUUID);
        if (previousActiveBonuses != null) {
            for (Map.Entry<SetType, List<SetBonus>> entry : previousActiveBonuses.entrySet()) {
                SetType setType = entry.getKey();
                List<SetBonus> previousBonuses = entry.getValue();
                List<SetBonus> currentBonuses = currentActiveBonuses.get(setType);
                
                // 找出需要移除的效果
                if (currentBonuses == null || currentBonuses.isEmpty()) {
                    // 该套装的所有效果都需要移除
                    for (SetBonus bonus : previousBonuses) {
                        bonus.remove(player);
                    }
                } else {
                    // 找出不再激活的效果并移除
                    for (SetBonus bonus : previousBonuses) {
                        if (!currentBonuses.contains(bonus)) {
                            bonus.remove(player);
                        }
                    }
                }
            }
        }
        
        // 更新激活的效果记录
        if (currentActiveBonuses.isEmpty()) {
            ACTIVE_BONUSES.remove(playerUUID);
            HEALTH_REGEN_MULTIPLIERS.remove(playerUUID);
            SET_DAMAGE_REDUCTION.remove(playerUUID);
        } else {
            ACTIVE_BONUSES.put(playerUUID, currentActiveBonuses);
            
            // 更新生命恢复速度加成（2件套效果）
            boolean hasDragonBloodline = currentActiveBonuses.getOrDefault(SetType.DRAGON, Collections.emptyList())
                .stream()
                .anyMatch(bonus -> bonus instanceof DragonSetBonus.DragonBloodline);
            if (hasDragonBloodline) {
                HEALTH_REGEN_MULTIPLIERS.put(playerUUID, 1.5); // +50%
            } else {
                HEALTH_REGEN_MULTIPLIERS.remove(playerUUID);
            }
            
            // 更新伤害减免加成（4件套效果）
            boolean hasDragonScale = currentActiveBonuses.getOrDefault(SetType.DRAGON, Collections.emptyList())
                .stream()
                .anyMatch(bonus -> bonus instanceof DragonSetBonus.DragonScaleProtection);
            if (hasDragonScale) {
                SET_DAMAGE_REDUCTION.put(playerUUID, 0.15); // +15%
            } else {
                SET_DAMAGE_REDUCTION.remove(playerUUID);
            }
        }
        
        // 应用生命恢复速度加成（每tick处理）
        applyHealthRegenBonus(player);
    }
    
    /**
     * 应用生命恢复速度加成
     * 在玩家Tick事件中调用，增强玩家的生命恢复速度
     * 
     * 这个方法会增强所有来自饰品的生命恢复效果（包括戒指、项链、耳环、头饰等）
     * This method enhances all health regeneration effects from accessories (rings, necklaces, earrings, headwear, etc.)
     */
    private static void applyHealthRegenBonus(Player player) {

    }
    
    /**
     * 获取玩家的套装伤害减免加成
     * 
     * @param player 玩家
     * @return 伤害减免百分比（0.0 - 1.0）
     */
    public static double getSetDamageReduction(Player player) {
        return SET_DAMAGE_REDUCTION.getOrDefault(player.getUUID(), 0.0);
    }
    
    /**
     * 获取玩家的生命恢复速度加成倍数
     * 
     * @param player 玩家
     * @return 生命恢复速度加成倍数（1.0 = 无加成）
     */
    public static double getHealthRegenMultiplier(Player player) {
        return HEALTH_REGEN_MULTIPLIERS.getOrDefault(player.getUUID(), 1.0);
    }
    
    /**
     * 检查玩家是否装备了指定套装的指定件数
     * 
     * @param player 玩家
     * @param setType 套装类型
     * @param requiredPieces 需要的件数
     * @return 是否满足条件
     */
    public static boolean hasSetBonus(Player player, SetType setType, int requiredPieces) {
        int pieceCount = getEquippedPieceCount(player, setType);
        return pieceCount >= requiredPieces;
    }
    
    /**
     * 获取玩家当前激活的套装效果
     * 
     * @param player 玩家
     * @return 激活的套装效果映射
     */
    public static Map<SetType, List<SetBonus>> getActiveBonuses(Player player) {
        return ACTIVE_BONUSES.getOrDefault(player.getUUID(), Collections.emptyMap());
    }
    
    /**
     * 清理玩家数据（当玩家离线时调用）
     * 
     * @param player 玩家
     */
    public static void cleanupPlayer(Player player) {
        UUID playerUUID = player.getUUID();
        Map<SetType, List<SetBonus>> activeBonuses = ACTIVE_BONUSES.remove(playerUUID);
        
        if (activeBonuses != null) {
            // 移除所有激活的效果
            for (List<SetBonus> bonuses : activeBonuses.values()) {
                for (SetBonus bonus : bonuses) {
                    bonus.remove(player);
                }
            }
        }
    }
    
    /**
     * 处理玩家受到伤害时的事件
     * 由 AccessoryEvents 调用，委托给对应的套装事件处理器
     * 
     * @param player 受到伤害的玩家
     * @param event 伤害事件
     */

    
    /**
     * 处理玩家造成伤害时的事件
     * 由 AccessoryEvents 调用，委托给对应的套装事件处理器
     * 
     * @param player 造成伤害的玩家
     * @param event 伤害事件
     * @param target 被攻击的目标
     */

}

