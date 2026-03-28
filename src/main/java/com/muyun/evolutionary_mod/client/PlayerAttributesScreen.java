package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import com.muyun.evolutionary_mod.system.effects.AbstractAccessoryEffectHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PlayerAttributesScreen extends Screen {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "textures/gui/accessory_gui_3.png");

    private static final int PANEL_W     = 240;
    private static final int PANEL_H     = 230;
    private static final int TEX_W       = 1366;
    private static final int TEX_H       = 768;
    private static final int COLOR_TITLE    = 0xFFFFD700;
    private static final int COLOR_HEADER   = 0xFFADD8E6;
    private static final int COLOR_VALUE    = 0xFFFFFFFF;
    private static final int COLOR_ZERO     = 0xFF888888;
    private static final int COLOR_BG_PANEL = 0xCC111118;
    private static final int COLOR_BG_ROW   = 0x22FFFFFF;

    // 翻页状态
    private int currentPage = 0;
    // 每页分组（每个分组含标题行 + 若干属性行）
    private final List<List<AttrLine>> pages = new ArrayList<>();

    private record AttrLine(String label, String value, int color) {}

    private final List<AttrLine> lines = new ArrayList<>();

    public PlayerAttributesScreen() {
        super(Component.translatable("screen.evolutionary_mod.player_attributes"));
    }

    @Override
    protected void init() {
        super.init();
        currentPage = 0;
    }

    // -----------------------------------------------------------------------
    // 分页逻辑
    // -----------------------------------------------------------------------

    /** 将 lines 按分类标题强制切分为独立页：每遇到分类标题开启新页 */
    private void rebuildPages() {
        pages.clear();
        List<AttrLine> current = new ArrayList<>();
        for (AttrLine line : lines) {
            boolean isHeader = !line.label().isEmpty() && line.value().isEmpty();
            // 每个分类标题开启新页（第一个标题直接放入当前空页）
            if (isHeader && !current.isEmpty()) {
                pages.add(current);
                current = new ArrayList<>();
            }
            current.add(line);
        }
        if (!current.isEmpty()) pages.add(current);
        if (currentPage >= pages.size()) currentPage = Math.max(0, pages.size() - 1);
    }

    // -----------------------------------------------------------------------
    // 数据收集
    // -----------------------------------------------------------------------

    private void rebuildLines() {
        lines.clear();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // 基础属性
        lines.add(new AttrLine("\u00a7n基础属性", "", COLOR_HEADER));
        lines.add(new AttrLine("最大生命值", Math.round(player.getMaxHealth()) + " \u2665", COLOR_VALUE));
        lines.add(new AttrLine("当前生命值", Math.round(player.getHealth()) + " \u2665", COLOR_VALUE));
        addAttrVanilla(player, Attributes.ATTACK_DAMAGE,       "攻击伤害", false, "\u2694");
        addAttrVanilla(player, Attributes.ARMOR,               "护甲值",   false, "");
        addAttrVanilla(player, Attributes.ARMOR_TOUGHNESS,     "护甲韧性", false, "");
        double spd    = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
        double spdPct = (spd / 0.1 - 1.0) * 100.0;
        lines.add(new AttrLine("移动速度",
                String.format("%+.1f%%  (%.1f)", spdPct, spd * 100),
                spdPct >= 0 ? COLOR_VALUE : 0xFFFF6666));
        addAttrVanilla(player, Attributes.LUCK,                "幸运",     false, "\u2726");
        addAttrVanilla(player, Attributes.KNOCKBACK_RESISTANCE,"击退抗性", true,  "");

        // 饰品词条汇总
        lines.add(new AttrLine("\u00a7n饰品词条汇总", "", COLOR_HEADER));
        double bonusMaxHealth = sumAccessoryAttr(player, AccessoryAttrType.MAX_HEALTH);
        int color = bonusMaxHealth > 0 ? 0xFF88FF88 : (bonusMaxHealth < 0 ? 0xFFFF6666 : COLOR_ZERO);
        lines.add(new AttrLine("+最大生命值", String.format("%+d \u2665", Math.round(bonusMaxHealth)), color));
        addBonusLine("+攻击伤害",    sumAccessoryAttr(player, AccessoryAttrType.ATTACK_DAMAGE),      false, "\u2694");
        addBonusLine("+护甲",        sumAccessoryAttr(player, AccessoryAttrType.ARMOR),              false, "");
        addBonusLine("+移动速度",    sumAccessoryAttr(player, AccessoryAttrType.MOVEMENT_SPEED) * 100.0, true, "%");
        addBonusLine("+幸运",        sumAccessoryAttr(player, AccessoryAttrType.LUCK),               false, "\u2726");
        addBonusLine("生命恢复/tick",sumAccessoryAttr(player, AccessoryAttrType.HEALTH_REGEN),       false, "\u2665/t");
        addBonusLine("护甲穿透",     sumAccessoryAttr(player, AccessoryAttrType.ARMOR_PENETRATION),  false, "");

        // 战斗属性
        lines.add(new AttrLine("\u00a7n战斗属性", "", COLOR_HEADER));
        double critChance   = CritSystem.getTotalCritChance(player);
        double critDmgBonus = CritSystem.getTotalCritDamageBonus(player);
        double dmgReduce    = DamageReductionSystem.getTotalDamageReduction(player);
        lines.add(new AttrLine("暴击率",
                String.format("%.1f%%", critChance * 100),
                critChance > 0 ? 0xFFFF9944 : COLOR_ZERO));
        lines.add(new AttrLine("暴击伤害倍率",
                String.format("%.1f%%  (+%.1f%%)", (1.5 + critDmgBonus) * 100, critDmgBonus * 100),
                critDmgBonus > 0 ? 0xFFFFCC44 : COLOR_ZERO));
        lines.add(new AttrLine("伤害减免",
                String.format("%.1f%%", dmgReduce * 100),
                dmgReduce > 0 ? 0xFF44DDFF : COLOR_ZERO));
    }

    private enum AccessoryAttrType {
        MAX_HEALTH, ATTACK_DAMAGE, ARMOR, MOVEMENT_SPEED, LUCK, HEALTH_REGEN, ARMOR_PENETRATION
    }

    private double sumAccessoryAttr(Player player, AccessoryAttrType type) {
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        double total = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            AccessoryAttributes rolled = AbstractAccessoryEffectHandler.getRolledAttributes(stack);
            total += switch (type) {
                case MAX_HEALTH        -> rolled.maxHealth();
                case ATTACK_DAMAGE     -> rolled.attackDamage();
                case ARMOR             -> rolled.armor();
                case MOVEMENT_SPEED    -> rolled.movementSpeed();
                case LUCK              -> rolled.luck();
                case HEALTH_REGEN      -> rolled.healthRegen();
                case ARMOR_PENETRATION -> rolled.armorPenetration();
            };
        }
        return total;
    }

    private void addAttr(String label, double value, boolean pct, String unit) {
        String text = pct
                ? String.format("%.1f%% %s", value * 100, unit)
                : String.format("%.1f %s", value, unit);
        lines.add(new AttrLine(label, text.trim(), COLOR_VALUE));
    }

    private void addAttrVanilla(Player player,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
            String label, boolean pct, String unit) {
        if (player.getAttribute(attr) == null) return;
        addAttr(label, player.getAttributeValue(attr), pct, unit);
    }

    private void addBonusLine(String label, double value, boolean pct, String unit) {
        String text = pct
                ? String.format("%+.1f%% %s", value, unit)
                : String.format("%+.1f %s", value, unit);
        int color = value > 0 ? 0xFF88FF88 : (value < 0 ? 0xFFFF6666 : COLOR_ZERO);
        lines.add(new AttrLine(label, text.trim(), color));
    }

    // -----------------------------------------------------------------------
    // 渲染
    // -----------------------------------------------------------------------

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        rebuildLines();
        rebuildPages();
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        int left = (this.width  - PANEL_W) / 2;
        int top  = (this.height - PANEL_H) / 2;

        // 背景纹理 + 遮罩
        g.blit(BG_TEXTURE, left, top, PANEL_W, PANEL_H, 0f, 0f, TEX_W, TEX_H, TEX_W, TEX_H);
        g.fill(left, top, left + PANEL_W, top + PANEL_H, COLOR_BG_PANEL);
        g.fill(left + 8, top + 20, left + PANEL_W - 8, top + 21, 0xFFFFD700);

        // 标题 + 页码
        int totalPages = Math.max(1, pages.size());
        String pageStr = (currentPage + 1) + " / " + totalPages;
        g.drawCenteredString(this.font, this.title, this.width / 2, top + 8, COLOR_TITLE);
        g.drawString(this.font, Component.literal(pageStr),
                left + PANEL_W - this.font.width(pageStr) - 6, top + 8, 0xFFAAAAAA, false);

        // 内容区
        int contentLeft  = left + 10;
        int contentRight = left + PANEL_W - 10;
        int y    = top + 26;
        int rowH = this.font.lineHeight + 3;
        int maxY = top + PANEL_H - 20;

        List<AttrLine> pageLines = pages.isEmpty() ? List.of() : pages.get(currentPage);
        int oddRow = 0;
        for (AttrLine line : pageLines) {
            if (y + rowH > maxY) break;

            if (line.label().isEmpty()) {
                y += rowH / 2;
                continue;
            }

            if (line.value().isEmpty()) {
                // 分类标题行
                g.fill(contentLeft, y - 1, contentRight, y + this.font.lineHeight + 1, 0x33FFFFFF);
                String display = line.label().startsWith("\u00a7n") ? line.label().substring(2) : line.label();
                g.drawString(this.font, Component.literal(display), contentLeft + 2, y, line.color(), false);
                y += rowH + 1;
                oddRow = 0;
                continue;
            }

            // 普通属性行
            if (oddRow % 2 == 0) g.fill(contentLeft, y, contentRight, y + this.font.lineHeight, COLOR_BG_ROW);
            oddRow++;
            g.drawString(this.font, Component.literal(line.label()),
                    contentLeft + 4, y, 0xFFCCCCCC, false);
            int valW = this.font.width(line.value());
            g.drawString(this.font, Component.literal(line.value()),
                    contentRight - valW - 2, y, line.color(), false);
            y += rowH;
        }

        // 底部翻页栏
        int barY = top + PANEL_H - 16;
        g.fill(left + 8, barY - 1, left + PANEL_W - 8, barY, 0xFF444444);

        // 左箭头
        boolean hasPrev = currentPage > 0;
        int arrowColor = hasPrev ? 0xFFFFD700 : 0xFF555555;
        g.drawCenteredString(this.font, Component.literal("❮"),
                left + 20, barY + 3, arrowColor);

        // 右箭头
        boolean hasNext = currentPage < totalPages - 1;
        arrowColor = hasNext ? 0xFFFFD700 : 0xFF555555;
        g.drawCenteredString(this.font, Component.literal("❯"),
                left + PANEL_W - 20, barY + 3, arrowColor);

        // 提示
        g.drawCenteredString(this.font, Component.literal("← →  |  J: 关闭"),
                this.width / 2, barY + 3, 0xFF888888);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int left = (this.width  - PANEL_W) / 2;
            int barY = (this.height - PANEL_H) / 2 + PANEL_H - 16;
            int totalPages = Math.max(1, pages.size());
            // 左箭头区域
            if (mouseX >= left + 8 && mouseX < left + 35
                    && mouseY >= barY && mouseY < barY + 14) {
                if (currentPage > 0) currentPage--;
                return true;
            }
            // 右箭头区域
            if (mouseX >= left + PANEL_W - 35 && mouseX < left + PANEL_W - 8
                    && mouseY >= barY && mouseY < barY + 14) {
                if (currentPage < totalPages - 1) currentPage++;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // J 关闭
        if (ClientHandlers.OPEN_PLAYER_ATTRIBUTES != null
                && ClientHandlers.OPEN_PLAYER_ATTRIBUTES.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        // 左右方向键翻页
        int totalPages = Math.max(1, pages.size());
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT  && currentPage > 0) {
            currentPage--; return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT && currentPage < totalPages - 1) {
            currentPage++; return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
