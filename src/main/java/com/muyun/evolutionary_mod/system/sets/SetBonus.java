package com.muyun.evolutionary_mod.system.sets;

import net.minecraft.world.entity.player.Player;

/**
 * 套装效果类 - Set Bonus Class
 * 
 * 定义套装效果的接口和基础实现。
 * 每个套装效果包含效果描述、触发条件和应用逻辑。
 * 
 * Defines the interface and base implementation for set bonuses.
 * Each set bonus contains effect description, trigger conditions, and application logic.
 */
public class SetBonus {
    
    /**
     * 套装效果名称
     */
    private final String name;
    
    /**
     * 套装效果描述
     */
    private final String description;
    
    /**
     * 需要的套装件数
     */
    private final int requiredPieces;
    
    /**
     * 构造函数
     * 
     * @param name 效果名称
     * @param description 效果描述
     * @param requiredPieces 需要的套装件数
     */
    public SetBonus(String name, String description, int requiredPieces) {
        this.name = name;
        this.description = description;
        this.requiredPieces = requiredPieces;
    }
    
    /**
     * 获取效果名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取效果描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取需要的套装件数
     */
    public int getRequiredPieces() {
        return requiredPieces;
    }
    
    /**
     * 应用套装效果
     * 子类需要重写此方法来实现具体的效果逻辑
     * 
     * @param player 玩家
     * @param pieceCount 当前装备的套装件数
     */
    public void apply(Player player, int pieceCount) {
        // 默认实现为空，具体效果由子类实现
        // Default implementation is empty, specific effects are implemented by subclasses
    }
    
    /**
     * 移除套装效果
     * 子类需要重写此方法来清理效果
     * 
     * @param player 玩家
     */
    public void remove(Player player) {
        // 默认实现为空，具体清理由子类实现
        // Default implementation is empty, specific cleanup is implemented by subclasses
    }
    
    /**
     * 检查效果是否应该被激活
     * 
     * @param pieceCount 当前装备的套装件数
     * @return 是否应该激活
     */
    public boolean shouldActivate(int pieceCount) {
        return pieceCount >= requiredPieces;
    }
}

