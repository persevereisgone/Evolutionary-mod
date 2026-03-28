package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 饰品总览界面 - Accessory Overview Screen
 *
 * 纯客户端界面，展示当前玩家所有饰品槽的装备状态。
 * 与 AccessoryContainerScreen（容器界面）不同，此界面不支持装备操作，
 * 仅用于快速查看当前饰品配置。
 */
@OnlyIn(Dist.CLIENT)
public class AccessoryScreen extends Screen {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "textures/gui/accessory_gui_3.png");

    private static final int PANEL_W  = 240;
    private static final int PANEL_H  = 280;
    private static final int TEX_W    = 1366;
    private static final int TEX_H    = 768;
    private static final int SLOT_SZ  = 18;   // 槽位图标大小
    private static final int SLOT_GAP = 4;    // 槽位间距
    private static final int COLS     = 3;    // 每行显示列数

    // 槽位中文名（与 AccessorySlot 枚举顺序一致）
    private static final String[] SLOT_LABELS = {
        "头饰",
        "耳环1", "耳环2",
        "项链",
        "手套1", "手套2",
        "手镯1", "手镯2",
        "戒指1", "戒指2", "戒指3", "戒指4",
        "腰带",
        "靴子1", "靴子2",
        "配饰1", "配饰2", "配饰3"
    };

    public AccessoryScreen() {
        super(Component.translatable("screen.accessories.title"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        int left = (this.width  - PANEL_W) / 2;
        int top  = (this.height - PANEL_H) / 2;

        // 背景
        g.blit(BG_TEXTURE, left, top, PANEL_W, PANEL_H,
                0f, 0f, TEX_W, TEX_H, TEX_W, TEX_H);
        g.fill(left, top, left + PANEL_W, top + PANEL_H, 0xCC111118);

        // 标题
        g.fill(left + 8, top + 19, left + PANEL_W - 8, top + 20, 0xFFFFD700);
        g.drawCenteredString(this.font,
                Component.translatable("screen.accessories.title"),
                this.width / 2, top + 7, 0xFFFFD700);

        // 读取玩家饰品数据
        PlayerAccessories cap = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
        AccessorySlot[] slots = AccessorySlot.values();

        int startX = left + 12;
        int startY = top + 24;
        int step   = SLOT_SZ + SLOT_GAP + this.font.lineHeight + 2;
        int hoveredSlot = -1;

        for (int i = 0; i < slots.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int cellW = (PANEL_W - 24) / COLS;
            int sx = startX + col * cellW;
            int sy = startY + row * step;

            // 槽位背景框
            boolean hovered = mouseX >= sx && mouseX < sx + SLOT_SZ
                    && mouseY >= sy && mouseY < sy + SLOT_SZ;
            if (hovered) hoveredSlot = i;

            g.fill(sx - 1, sy - 1, sx + SLOT_SZ + 1, sy + SLOT_SZ + 1,
                    hovered ? 0xFFFFD700 : 0xFF555555);
            g.fill(sx, sy, sx + SLOT_SZ, sy + SLOT_SZ, 0xFF1A1A2E);

            // 物品图标
            ItemStack stack = cap.getStack(slots[i]);
            if (!stack.isEmpty()) {
                g.renderItem(stack, sx + 1, sy + 1);
                g.renderItemDecorations(this.font, stack, sx + 1, sy + 1);
            } else {
                // 空槽位显示首字母占位
                String initial = SLOT_LABELS[i].substring(0, 1);
                g.drawString(this.font, Component.literal(initial),
                        sx + (SLOT_SZ - this.font.width(initial)) / 2,
                        sy + (SLOT_SZ - this.font.lineHeight) / 2,
                        0xFF444466, false);
            }

            // 槽位名称（图标下方）
            String label = SLOT_LABELS[i];
            int labelW = this.font.width(label);
            int labelX = sx + (SLOT_SZ - labelW) / 2;
            g.drawString(this.font, Component.literal(label),
                    labelX, sy + SLOT_SZ + 2,
                    stack.isEmpty() ? 0xFF666666 : 0xFFCCCCCC, false);
        }

        // 悬浮 tooltip
        if (hoveredSlot >= 0) {
            ItemStack stack = cap.getStack(slots[hoveredSlot]);
            if (!stack.isEmpty()) {
                g.renderTooltip(this.font, stack, mouseX, mouseY);
            } else {
                g.renderTooltip(this.font,
                        Component.literal(SLOT_LABELS[hoveredSlot] + "（空）"),
                        mouseX, mouseY);
            }
        }

        // 底部提示
        g.drawCenteredString(this.font,
                Component.literal("按 K 打开完整装备界面"),
                this.width / 2, top + PANEL_H - 10, 0xFF666666);
        }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ClientHandlers.OPEN_ACCESSORIES != null
                && ClientHandlers.OPEN_ACCESSORIES.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
