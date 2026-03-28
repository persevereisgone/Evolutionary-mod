package com.muyun.evolutionary_mod.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

/**
 * 饰品物品基类 - Accessory Item Base Class
 */
public class AccessoryItem extends Item {

    public AccessoryItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        // 物品名称使用对应品质颜色
        ChatFormatting color = AccessoryTooltipHelper.getItemRarityColor(stack.getItem());
        MutableComponent name = (MutableComponent) super.getName(stack);
        return name.withStyle(color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        AccessoryTooltipHelper.addAccessoryTooltips(tooltipComponents, stack);
    }
}
