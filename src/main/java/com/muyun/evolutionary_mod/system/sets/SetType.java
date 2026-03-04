package com.muyun.evolutionary_mod.system.sets;

import java.util.ArrayList;
import java.util.List;

/**
 * 套装类型枚举 - Set Type Enum
 * 
 * 定义所有可用的套装类型及其效果配置。
 * 
 * Defines all available set types and their effect configurations.
 */
public enum SetType {
    /**
     * 龙族套装 - Dragon Set
     */
    DRAGON("龙族套装", "Dragon Set", List.of(
        new DragonSetBonus.DragonBloodline(),      // 2件套
        new DragonSetBonus.DragonScaleProtection(), // 4件套
        new DragonSetBonus.DragonBreathBurst()      // 6件套
    ));
    
    /**
     * 套装中文名称
     */
    private final String nameCN;
    
    /**
     * 套装英文名称
     */
    private final String nameEN;
    
    /**
     * 套装效果列表（按件数排序）
     */
    private final List<SetBonus> bonuses;
    
    /**
     * 构造函数
     * 
     * @param nameCN 中文名称
     * @param nameEN 英文名称
     * @param bonuses 套装效果列表
     */
    SetType(String nameCN, String nameEN, List<SetBonus> bonuses) {
        this.nameCN = nameCN;
        this.nameEN = nameEN;
        this.bonuses = new ArrayList<>(bonuses);
    }
    
    /**
     * 获取中文名称
     */
    public String getNameCN() {
        return nameCN;
    }
    
    /**
     * 获取英文名称
     */
    public String getNameEN() {
        return nameEN;
    }
    
    /**
     * 获取套装效果列表
     */
    public List<SetBonus> getBonuses() {
        return bonuses;
    }
    
    /**
     * 根据件数获取应该激活的效果
     * 
     * @param pieceCount 装备的套装件数
     * @return 应该激活的效果列表
     */
    public List<SetBonus> getActiveBonuses(int pieceCount) {
        List<SetBonus> active = new ArrayList<>();
        for (SetBonus bonus : bonuses) {
            if (bonus.shouldActivate(pieceCount)) {
                active.add(bonus);
            }
        }
        return active;
    }
}

