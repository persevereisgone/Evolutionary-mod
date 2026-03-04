package com.muyun.evolutionary_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import com.muyun.evolutionary_mod.system.effects.AnkletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BeltEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BraceletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.EarringEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.GloveEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.HeadwearEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.NecklaceEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.RingEffectsHandler;
import com.muyun.evolutionary_mod.system.sets.DragonSetBonus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PlayerAttributesScreen extends Screen {

    private final List<Component> lines = new ArrayList<>();
    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "textures/gui/accessory_gui_3.png");

    public PlayerAttributesScreen() {
        super(Component.translatable("screen.evolutionary_mod.player_attributes"));
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 收集并更新玩家属性列表
     * 在每次渲染时调用，确保显示最新的属性值（包括从饰品添加的属性）
     */
    private void updateAttributes() {
        lines.clear();

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        // 添加自定义属性（暴击率、暴击伤害、生命恢复、伤害减免、护甲穿透等）
        addCustomAttributes(player);
    }

    /**
     * 添加自定义属性到显示列表
     * 包括：暴击率、暴击伤害、生命恢复速度、伤害减免、护甲穿透
     * 即使值为0也会显示
     */
    private void addCustomAttributes(Player player) {
        List<Component> customLines = new ArrayList<>();

        // 暴击率（即使为0也显示）
        double critChance = CritSystem.getTotalCritChance(player);
        customLines.add(Component.literal(
                "暴击率: " + String.format("%.2f", critChance * 100) + "%"
        ));

        // 暴击伤害加成（即使为0也显示）
        double critDamageBonus = CritSystem.getTotalCritDamageBonus(player);
        double baseCritMultiplier = 1.5; // CritSystem.BASE_CRIT_DAMAGE_MULTIPLIER
        double totalCritMultiplier = baseCritMultiplier + critDamageBonus;
        customLines.add(Component.literal(
                "暴击伤害: " + String.format("%.1f", totalCritMultiplier * 100) + "%" +
                " (基础: " + String.format("%.1f", baseCritMultiplier * 100) + "%, 额外: +" + String.format("%.1f", critDamageBonus * 100) + "%)"
        ));

        // 生命恢复速度（每秒恢复的生命值，即使为0也显示）
        double healthRegenPerSecond = calculateTotalHealthRegen(player);
        customLines.add(Component.literal(
                "生命恢复: " + String.format("%.2f", healthRegenPerSecond) + " 点/秒"
        ));

        // 伤害减免（即使为0也显示）
        double damageReduction = DamageReductionSystem.getTotalDamageReduction(player);
        customLines.add(Component.literal(
                "伤害减免: " + String.format("%.2f", damageReduction * 100) + "%"
        ));

        // 护甲穿透（即使为0也显示）
        double armorPenetration = calculateTotalArmorPenetration(player);
        customLines.add(Component.literal(
                "护甲穿透: " + String.format("%.2f", armorPenetration)
        ));

        // 按优先级排序自定义属性
        customLines.sort(Comparator.comparingInt(c -> {
            String text = c.getString();
            return getCustomAttributePriority(text);
        }));

        // 添加到主列表
        lines.addAll(customLines);
    }

    /**
     * 计算总生命恢复速度（每秒恢复的生命值）
     * 目前只从戒指中计算，如果其他饰品类型也有生命恢复，可以扩展此方法
     */
    private double calculateTotalHealthRegen(Player player) {
        return 0;
    }

    /**
     * 计算总护甲穿透
     * 目前只从戒指中计算，如果其他饰品类型也有护甲穿透，可以扩展此方法
     */
    private double calculateTotalArmorPenetration(Player player) {
        return 0;
    }

    /**
     * 从ItemStack获取属性值（优先从NBT读取随机属性，否则使用默认效果）
     */
    private double getAttributeValueFromStack(ItemStack stack, String attributeName, double defaultValue) {
        if (stack.isEmpty()) return 0;

        // 优先从NBT读取随机属性值

        // 如果没有随机属性，使用默认效果
        return defaultValue;
    }

    /**
     * 计算所有饰品的总攻击伤害加成
     */
    private double calculateTotalAttackDamage(Player player) {
        return 0;
    }

    /**
     * 计算所有饰品的总最大生命值加成
     */
    private double calculateTotalMaxHealth(Player player) {
        return 0;
    }

    /**
     * 计算所有饰品的总护甲加成
     */
    private double calculateTotalArmor(Player player) {
        return 0;
    }

    /**
     * 计算所有饰品的总移动速度加成
     */
    private double calculateTotalMovementSpeed(Player player) {
        return 0;
    }

    /**
     * 计算所有饰品的总幸运加成
     */
    private double calculateTotalLuck(Player player) {
        return 0;
    }

    // 自定义属性显示顺序：先核心战斗属性，再其他
    private int getPriority(AttributeInstance instance) {
        if (instance == null || instance.getAttribute() == null) return 100;
        if (instance.getAttribute() == Attributes.MAX_HEALTH) return 0;
        if (instance.getAttribute() == Attributes.ATTACK_DAMAGE) return 1;
        if (instance.getAttribute() == Attributes.ATTACK_SPEED) return 2;
        if (instance.getAttribute() == Attributes.ARMOR) return 3;
        if (instance.getAttribute() == Attributes.ARMOR_TOUGHNESS) return 4;
        if (instance.getAttribute() == Attributes.MOVEMENT_SPEED) return 5;
        if (instance.getAttribute() == Attributes.LUCK) return 6;
        if (instance.getAttribute() == Attributes.KNOCKBACK_RESISTANCE) return 7;
        // 其他未特别指定的属性排在后面
        return 50;
    }

    /**
     * 获取自定义属性的显示优先级
     * 用于在标准属性之后按顺序显示自定义属性
     */
    private int getCustomAttributePriority(String attributeName) {
        // 自定义属性显示在标准属性之后，按重要性排序
        if (attributeName.contains("暴击率")) return 8;
        if (attributeName.contains("暴击伤害")) return 9;
        if (attributeName.contains("生命恢复")) return 10;
        if (attributeName.contains("伤害减免")) return 11;
        if (attributeName.contains("护甲穿透")) return 12;
        return 100;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 在每次渲染时更新属性列表，确保显示最新的属性值（包括从饰品添加的属性）
        updateAttributes();

        // 背景仍然使用游戏默认的暗色背景
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 面板大小：250x250，居中
        int panelWidth = 250;
        int panelHeight = 250;
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;
        // 面板背景：使用自定义黑色背景图片，并按面板大小等比缩放整张贴图而不是裁剪
        // 这里将整张纹理 (texWidth x texHeight) 缩放绘制到 panelWidth x panelHeight 的区域
        int texWidth = 1366; // 背景贴图原始宽度，如更换贴图请同步修改
        int texHeight = 768; // 背景贴图原始高度
        guiGraphics.blit(BG_TEXTURE,
                left, top,                       // 目标左上角
                panelWidth, panelHeight,         // 目标尺寸
                0.0F, 0.0F,                      // 纹理起始 UV
                texWidth, texHeight,             // 采样区域大小（整张纹理）
                texWidth, texHeight);            // 纹理实际大小

        // 标题（中文：属性面板），水平居中在面板顶部
        int titleY = top + 24;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, titleY, 0xFFFFFFFF);

        // 属性列表，从标题下方开始
        int y = titleY + 16;
        int textLeft = left + 20;
        for (Component line : lines) {
            if (y > top + panelHeight - 10) break; // 简单防止越界
            // 控制在面板内部绘制
            guiGraphics.drawString(this.font, line, textLeft, y, 0xFF000000, false);
            y += 12;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}


