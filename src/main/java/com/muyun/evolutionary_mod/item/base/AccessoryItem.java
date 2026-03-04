package com.muyun.evolutionary_mod.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 饰品物品基类 - Accessory Item Base Class
 *
 * 为所有饰品提供统一的工具提示显示功能，使用AccessoryTooltipHelper来处理所有工具提示逻辑。
 * Provides unified tooltip display functionality for all accessories,
 * using AccessoryTooltipHelper to handle all tooltip logic.
 */
public class AccessoryItem extends Item {

    public AccessoryItem(Properties properties) {
        super(properties);
    }

    // 先处理名称颜色 - Handle name color first
    // 将默认的首行（物品名）替换为彩色名称；若首行不存在则添加 - Replace first tooltip line (item name) with colored name, or add if empty
    // 使用通用的工具提示助手类添加所有饰品效果 - Use the universal tooltip helper to add all accessory effects
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
