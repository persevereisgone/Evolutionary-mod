package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.system.task.TaskCategory;
import com.muyun.evolutionary_mod.system.task.TaskData;
import com.muyun.evolutionary_mod.system.task.TaskInfo;
import com.muyun.evolutionary_mod.system.task.TaskLogEntry;
import com.muyun.evolutionary_mod.system.task.TaskStatus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务系统正式界面（路线 B：空白骨架贴图 + 代码渲染文字/进度/交互）。
 *
 * 布局坐标严格对应 `docs/dev/任务系统GUI设计文档.md` §4.2：
 * 340×240 三栏面板，贴图全部取自 `textures/gui/`（程序化生成）。
 *
 * 交互：页签过滤 / 搜索框 / 任务行选中+滚动+Tooltip /
 * 接取·追踪·放弃 / 开关 / 推荐卡 / 关闭按钮。
 */
@OnlyIn(Dist.CLIENT)
public class TaskScreen extends Screen {

    // ------------------------------------------------------------- 贴图
    private static final ResourceLocation TEX_PANEL   = tex("task_screen.png");
    private static final ResourceLocation TEX_ROW     = tex("task_list_row.png");
    private static final ResourceLocation TEX_BTN_P   = tex("button_primary.png");
    private static final ResourceLocation TEX_BTN_S   = tex("button_secondary.png");
    private static final ResourceLocation TEX_TAB     = tex("task_tab.png");
    private static final ResourceLocation TEX_PROGRESS= tex("progress_track.png");
    private static final ResourceLocation TEX_SWITCH  = tex("task_switch.png");
    private static final ResourceLocation TEX_SCR_T   = tex("scrollbar_track.png");
    private static final ResourceLocation TEX_SCR_H   = tex("scrollbar_handle.png");

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "textures/gui/" + name);
    }

    // ------------------------------------------------------------- 面板
    private static final int PANEL_W = 340;
    private static final int PANEL_H = 240;

    // 顶栏
    private static final int TITLE_Y    = 5;
    private static final int CLOSE_X    = 322, CLOSE_Y = 2, CLOSE_S = 16;

    // 左栏 (2,22,76,214)
    private static final int LEFT_TITLE_X = 6, LEFT_TITLE_Y = 25;
    private static final int STATS_X = 7, STATS_Y = 38, STATS_ROW_H = 11, STATS_VALUE_R = 71;
    private static final int LOG_TITLE_X = 6, LOG_TITLE_Y = 110;
    private static final int LOG_AREA_X = 4, LOG_AREA_Y = 120, LOG_AREA_W = 72, LOG_AREA_H = 62;
    private static final int LOG_ROW_H = 11;
    private static final int SW1_X = 48, SW1_Y = 188, SW2_Y = 202, SW_W = 26, SW_H = 11;
    private static final int SW_LABEL_X = 6;
    private static final int ONLINE_DOT_X = 6, ONLINE_DOT_Y = 224, ONLINE_DOT_S = 6;
    private static final int ONLINE_TEXT_X = 14;

    // 中栏 (82,22,188,214)
    private static final int BANNER_X = 82, BANNER_Y = 22, BANNER_W = 188, BANNER_H = 34;
    private static final int BANNER_ICON_X = 87, BANNER_ICON_Y = 29;
    private static final int BANNER_NAME_X = 108, BANNER_NAME_Y = 27;
    private static final int BANNER_DESC_X = 108, BANNER_DESC_Y = 37;
    private static final int PROG_LABEL_X = 86, PROG_LABEL_Y = 62;
    private static final int PROG_X = 86, PROG_Y = 73, PROG_W = 180, PROG_H = 6;
    private static final int BTN_X = 88, BTN_Y = 82, BTN_W = 56, BTN_H = 14, BTN_GAP = 10;
    private static final int TAB_X = 82, TAB_Y = 100, TAB_W = 36, TAB_H = 16, TAB_GAP = 2;
    private static final int LIST_X = 84, LIST_Y = 122, LIST_W = 184, LIST_H = 110;
    private static final int ROW_H = 24;
    private static final int VISIBLE_ROWS = 4;

    // 右栏 (274,22,66,214)
    private static final int SEARCH_X = 276, SEARCH_Y = 24, SEARCH_W = 62, SEARCH_H = 13;
    private static final int RECO_TITLE_X = 278, RECO_TITLE_Y = 42;
    private static final int CARD_X = 276, CARD1_Y = 52, CARD2_Y = 80, CARD_W = 62, CARD_H = 26;
    private static final int EVENTS_TITLE_X = 278, EVENTS_TITLE_Y = 114;
    private static final int EVENTS_X = 278, EVENTS_Y = 124, EVENTS_ROW_H = 20;

    // ------------------------------------------------------------- 配色（V2 色板）
    private static final int C_WHITE  = 0xFFFFFFFF;
    private static final int C_TEXT   = 0xFFD0D0D0;
    private static final int C_GOLD   = 0xFFFFE066;
    private static final int C_GREEN  = 0xFF55FF55;
    private static final int C_CYAN   = 0xFF55FFFF;
    private static final int C_RED    = 0xFFFF5555;
    private static final int C_GRAY   = 0xFF888888;

    private static final String[] TAB_LANG = {
            "task.tab.all", "task.tab.main", "task.tab.side", "task.tab.daily", "task.tab.event"
    };

    // ------------------------------------------------------------- 状态
    private final TaskData data = TaskData.sample();
    private int currentTab = 0;          // 0=全部 1..4=分类
    private String searchFilter = "";
    private int scrollOffset = 0;
    private TaskInfo selected;
    private EditBox searchBox;

    public TaskScreen() {
        super(Component.translatable("screen.evolutionary_mod.task"));
    }

    private int px(int x) { return left() + x; }
    private int py(int y) { return top() + y; }
    private int left() { return (this.width - PANEL_W) / 2; }
    private int top() { return (this.height - PANEL_H) / 2; }

    private boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ------------------------------------------------------------- 生命周期
    @Override
    protected void init() {
        searchBox = new EditBox(this.font, px(SEARCH_X + 2), py(SEARCH_Y + 2), SEARCH_W - 4, SEARCH_H - 3,
                Component.translatable("task.search.hint"));
        searchBox.setMaxLength(24);
        searchBox.setBordered(false);
        searchBox.setTextColor(0xFFFFFFFF);
        searchBox.setHint(Component.translatable("task.search.hint"));
        searchBox.setResponder(s -> {
            searchFilter = s;
            scrollOffset = 0;
            refreshSelected();
        });
        refreshSelected();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ------------------------------------------------------------- 数据辅助
    private List<TaskInfo> visibleTasks() {
        List<TaskInfo> out = new ArrayList<>();
        for (TaskInfo t : data.tasks()) {
            if (currentTab != 0 && t.category() != TaskCategory.values()[currentTab - 1]) continue;
            String q = searchFilter.trim().toLowerCase();
            if (!q.isEmpty() && !t.name().getString().toLowerCase().contains(q)) continue;
            out.add(t);
        }
        return out;
    }

    private void refreshSelected() {
        List<TaskInfo> visible = visibleTasks();
        if (visible.isEmpty()) {
            selected = null;
            scrollOffset = 0;
            return;
        }
        if (!visible.contains(selected)) {
            selected = visible.get(0);
        }
        int maxScroll = Math.max(0, visible.size() - VISIBLE_ROWS);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
    }

    private int maxScroll() {
        return Math.max(0, visibleTasks().size() - VISIBLE_ROWS);
    }

    // ------------------------------------------------------------- 绘制工具
    private String clip(String s, int maxWidth) {
        if (this.font.width(s) <= maxWidth) return s;
        while (!s.isEmpty() && this.font.width(s + "…") > maxWidth) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "…";
    }

    private void blit(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h) {
        g.blit(tex, x, y, w, h, 0, 0, w, h, w, h);
    }

    /** 任务行 9-slice 横向拉伸（3px 边框）。v = 0 普通 / 24 悬停 / 48 完成。 */
    private void blitRow(GuiGraphics g, int x, int y, int w, int h, int v) {
        g.blit(TEX_ROW, x, y, 3, h, 0, v, 3, 24, 64, 72);
        g.blit(TEX_ROW, x + 3, y, w - 6, h, 3, v, 58, 24, 64, 72);
        g.blit(TEX_ROW, x + w - 3, y, 3, h, 61, v, 3, 24, 64, 72);
    }

    private void drawButton(GuiGraphics g, int x, int y, Component label,
                            ResourceLocation tex, boolean enabled, boolean hovered,
                            boolean greenOverlay) {
        blit(g, tex, x, y, BTN_W, BTN_H);
        if (greenOverlay) {
            g.fill(x, y, x + BTN_W, y + BTN_H, 0xB455FF55);
        }
        if (!enabled) {
            g.fill(x, y, x + BTN_W, y + BTN_H, 0x66000000);
        } else if (hovered) {
            g.fill(x, y, x + BTN_W, y + BTN_H, 0x22FFFFFF);
        }
        g.drawCenteredString(this.font, label,
                x + BTN_W / 2, y + (BTN_H - this.font.lineHeight) / 2,
                enabled ? (greenOverlay ? 0xFFFFFFFF : C_WHITE) : C_GRAY);
    }

    private void drawSwitch(GuiGraphics g, int x, int y, boolean on, boolean hovered) {
        int u = on ? 26 : 0;
        g.blit(TEX_SWITCH, x, y, SW_W, SW_H, u, 0, SW_W, SW_H, 52, 11);
        if (hovered) {
            g.fill(x, y, x + SW_W, y + SW_H, 0x22FFFFFF);
        }
        // 白色滑块（由代码绘制）
        int knobX = on ? x + SW_W - 9 : x + 1;
        g.fill(knobX, y + 1, knobX + 8, y + 1 + SW_H - 2, 0xFFFFFFFF);
        g.fill(knobX, y + 1, knobX + 8, y + 2, 0xFFCCCCCC);
    }

    private void drawCloseMark(GuiGraphics g, int x, int y, int s, int color) {
        for (int k = 0; k < s; k++) {
            g.fill(x + k, y + k, x + k + 1, y + k + 1, color);
            g.fill(x + s - 1 - k, y + k, x + s - k, y + k + 1, color);
        }
    }

    private int statusColor(TaskStatus status) {
        return switch (status) {
            case IN_PROGRESS -> C_CYAN;
            case COMPLETED -> C_GREEN;
            case FAILED -> C_RED;
            case AVAILABLE -> C_GRAY;
        };
    }

    // ------------------------------------------------------------- 渲染
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);

        blit(g, TEX_PANEL, px(0), py(0), PANEL_W, PANEL_H);
        renderTopBar(g, mouseX, mouseY);
        renderLeftColumn(g);
        renderMidColumn(g, mouseX, mouseY);
        renderRightColumn(g, mouseX, mouseY);

        searchBox.render(g, mouseX, mouseY, partialTick);

        g.drawCenteredString(this.font,
                Component.translatable("task.hint"),
                this.width / 2, py(PANEL_H) + 12, C_GRAY);

        TaskInfo hovered = hoveredRow(mouseX, mouseY);
        if (hovered != null) {
            renderTaskTooltip(g, hovered, mouseX, mouseY);
        }
    }

    private void renderTopBar(GuiGraphics g, int mouseX, int mouseY) {
        g.drawCenteredString(this.font, this.title,
                px(170), py(TITLE_Y), C_WHITE);
        boolean hover = in(mouseX, mouseY, px(CLOSE_X), py(CLOSE_Y), CLOSE_S, CLOSE_S);
        if (hover) {
            g.fill(px(CLOSE_X), py(CLOSE_Y), px(CLOSE_X) + CLOSE_S, py(CLOSE_Y) + CLOSE_S, 0x30FFFFFF);
        }
        drawCloseMark(g, px(CLOSE_X + 3), py(CLOSE_Y + 3), CLOSE_S - 6,
                hover ? 0xFFFF5555 : 0xFFAAAAAA);
    }

    private void renderLeftColumn(GuiGraphics g) {
        // 标题
        g.drawString(this.font, Component.translatable("task.console.title"),
                px(LEFT_TITLE_X), py(LEFT_TITLE_Y), C_WHITE);

        // 统计 5 行
        String[] labels = {
                Component.translatable("task.stats.in_progress").getString(),
                Component.translatable("task.stats.completed").getString(),
                Component.translatable("task.stats.failed").getString(),
                Component.translatable("task.stats.rate").getString(),
                Component.translatable("task.stats.today").getString(),
        };
        String[] values = {
                String.valueOf(data.countStatus(TaskStatus.IN_PROGRESS)),
                String.valueOf(data.countStatus(TaskStatus.COMPLETED)),
                String.valueOf(data.countStatus(TaskStatus.FAILED)),
                data.completionRatePct() + "%",
                String.valueOf(data.countToday()),
        };
        for (int i = 0; i < 5; i++) {
            int y = py(STATS_Y + i * STATS_ROW_H);
            g.drawString(this.font, clip(labels[i], 42), px(STATS_X), y, C_TEXT);
            int vx = px(STATS_VALUE_R) - this.font.width(values[i]);
            g.drawString(this.font, values[i], vx, y, C_WHITE);
        }

        // 日志标题 + 最近 5 条（新在下）
        g.drawString(this.font, Component.translatable("task.logs.title"),
                px(LOG_TITLE_X), py(LOG_TITLE_Y), C_WHITE);
        List<TaskLogEntry> recent = data.recentLogs(5);
        for (int i = 0; i < 5 && i < recent.size(); i++) {
            int slot = recent.size() - 1 - i;
            int y = py(LOG_AREA_Y + 1 + slot * LOG_ROW_H);
            g.drawString(this.font, clip(recent.get(i).message().getString(), LOG_AREA_W - 10),
                    px(LOG_AREA_X + 2), y, C_TEXT);
        }

        // 开关
        g.drawString(this.font, Component.translatable("task.switch.auto_track"),
                px(SW_LABEL_X), py(SW1_Y + 1), C_TEXT);
        drawSwitch(g, px(SW1_X), py(SW1_Y), data.autoTrack(), false);
        g.drawString(this.font, Component.translatable("task.switch.notify"),
                px(SW_LABEL_X), py(SW2_Y + 1), C_TEXT);
        drawSwitch(g, px(SW1_X), py(SW2_Y), data.newTaskNotify(), false);

        // 在线状态
        g.drawString(this.font, Component.translatable("task.online"),
                px(ONLINE_TEXT_X), py(ONLINE_DOT_Y + 1), C_GREEN);
    }

    private void renderMidColumn(GuiGraphics g, int mouseX, int mouseY) {
        renderBanner(g);
        renderProgress(g);
        renderButtons(g, mouseX, mouseY);
        renderTabs(g, mouseX, mouseY);
        renderTaskList(g, mouseX, mouseY);
    }

    private void renderBanner(GuiGraphics g) {
        if (selected == null) return;
        g.renderItem(selected.reward(), px(BANNER_ICON_X), py(BANNER_ICON_Y));
        g.drawString(this.font, clip(selected.name().getString(), 120),
                px(BANNER_NAME_X), py(BANNER_NAME_Y), C_WHITE);
        g.drawString(this.font, clip(selected.description().getString(), 120),
                px(BANNER_DESC_X), py(BANNER_DESC_Y), C_TEXT);
    }

    private void renderProgress(GuiGraphics g) {
        g.drawString(this.font, Component.translatable("task.progress",
                        selected == null ? 0 : selected.progress(),
                        selected == null ? 0 : selected.target()),
                px(PROG_LABEL_X), py(PROG_LABEL_Y), C_WHITE);
        blit(g, TEX_PROGRESS, px(PROG_X), py(PROG_Y), PROG_W, PROG_H);
        if (selected != null && selected.target() > 0) {
            int fill = (int) Math.round(selected.progressRatio() * (PROG_W - 4));
            g.fill(px(PROG_X + 2), py(PROG_Y + 1), px(PROG_X + 2) + fill, py(PROG_Y) + PROG_H - 1, C_GREEN);
        }
    }

    private void renderButtons(GuiGraphics g, int mouseX, int mouseY) {
        boolean has = selected != null;
        int bx = px(BTN_X), by = py(BTN_Y);
        boolean h1 = in(mouseX, mouseY, bx, by, BTN_W, BTN_H);
        boolean h2 = in(mouseX, mouseY, bx + BTN_W + BTN_GAP, by, BTN_W, BTN_H);
        boolean h3 = in(mouseX, mouseY, bx + 2 * (BTN_W + BTN_GAP), by, BTN_W, BTN_H);

        drawButton(g, bx, by,
                Component.translatable("task.btn.accept"), TEX_BTN_P,
                has && selected.canAccept(), h1, false);
        boolean tracking = selected != null && selected.tracking();
        drawButton(g, bx + BTN_W + BTN_GAP, by,
                Component.translatable(tracking ? "task.btn.tracking" : "task.btn.track"), TEX_BTN_S,
                has && selected.canTrack(), h2, tracking);
        drawButton(g, bx + 2 * (BTN_W + BTN_GAP), by,
                Component.translatable("task.btn.abandon"), TEX_BTN_S,
                has && selected.canAbandon(), h3, false);
    }

    private void renderTabs(GuiGraphics g, int mouseX, int mouseY) {
        for (int i = 0; i < 5; i++) {
            int x = px(TAB_X + i * (TAB_W + TAB_GAP));
            int v = (i == currentTab) ? 16 : 0;
            g.blit(TEX_TAB, x, py(TAB_Y), TAB_W, TAB_H, 0, v, TAB_W, TAB_H, 36, 32);
            boolean hover = in(mouseX, mouseY, x, py(TAB_Y), TAB_W, TAB_H);
            if (hover && i != currentTab) {
                g.fill(x, py(TAB_Y), x + TAB_W, py(TAB_Y) + TAB_H, 0x22FFFFFF);
            }
            g.drawCenteredString(this.font, Component.translatable(TAB_LANG[i]),
                    x + TAB_W / 2, py(TAB_Y) + (TAB_H - this.font.lineHeight) / 2,
                    i == currentTab ? C_WHITE : C_TEXT);
        }
    }

    private void renderTaskList(GuiGraphics g, int mouseX, int mouseY) {
        List<TaskInfo> visible = visibleTasks();
        int maxScroll = maxScroll();
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        if (visible.isEmpty()) {
            g.drawCenteredString(this.font, Component.translatable("task.empty"),
                    px(LIST_X) + LIST_W / 2, py(LIST_Y + 10), C_GRAY);
            return;
        }

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx >= visible.size()) break;
            TaskInfo t = visible.get(idx);
            int x = px(LIST_X), y = py(LIST_Y + i * ROW_H);
            boolean hover = in(mouseX, mouseY, x, y, LIST_W, ROW_H);
            int v = (t.status() == TaskStatus.COMPLETED) ? 48
                    : ((hover || t == selected) ? 24 : 0);
            blitRow(g, x, y, LIST_W, ROW_H, v);

            g.drawString(this.font, clip(t.name().getString(), 96), x + 5, y + 3, C_WHITE);
            String st = t.status().displayName().getString();
            g.drawString(this.font, st, x + LIST_W - 5 - this.font.width(st), y + 3,
                    statusColor(t.status()));
            String reward = Component.translatable("task.reward.xp", t.xpReward()).getString();
            g.drawString(this.font, clip(reward, 90), x + 5, y + 13, C_GOLD);
            String prog = t.progress() + "/" + t.target();
            g.drawString(this.font, prog, x + LIST_W - 5 - this.font.width(prog), y + 13, C_TEXT);
        }

        // 滚动条
        if (visible.size() > VISIBLE_ROWS) {
            int sx = px(LIST_X + LIST_W - 4);
            blit(g, TEX_SCR_T, sx, py(LIST_Y), 4, LIST_H);
            int trackH = LIST_H - 12;
            int hy = py(LIST_Y) + (int) Math.round(scrollOffset / (double) maxScroll * trackH);
            blit(g, TEX_SCR_H, sx, hy, 4, 12);
        }
    }

    private TaskInfo hoveredRow(int mouseX, int mouseY) {
        List<TaskInfo> visible = visibleTasks();
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx >= visible.size()) break;
            int x = px(LIST_X), y = py(LIST_Y + i * ROW_H);
            if (in(mouseX, mouseY, x, y, LIST_W, ROW_H)) {
                return visible.get(idx);
            }
        }
        return null;
    }

    private void renderRightColumn(GuiGraphics g, int mouseX, int mouseY) {
        // 可接取推荐
        g.drawString(this.font, Component.translatable("task.recommend.title"),
                px(RECO_TITLE_X), py(RECO_TITLE_Y), C_WHITE);
        List<TaskInfo> recs = data.recommendations();
        for (int i = 0; i < 2; i++) {
            int cy = py(CARD1_Y + i * (CARD_H + 2));
            boolean hover = in(mouseX, mouseY, px(CARD_X), cy, CARD_W, CARD_H);
            if (hover) {
                g.fill(px(CARD_X), cy, px(CARD_X) + CARD_W, cy + CARD_H, 0x22FFFFFF);
            }
            if (i < recs.size()) {
                TaskInfo t = recs.get(i);
                g.drawString(this.font, clip(t.name().getString(), CARD_W - 6),
                        px(CARD_X + 3), cy + 2, C_WHITE);
                g.drawString(this.font, clip(
                                Component.translatable("task.reward.xp", t.xpReward()).getString(),
                                CARD_W - 6),
                        px(CARD_X + 3), cy + 12, C_GOLD);
                if (hover) {
                    renderTaskTooltip(g, t, mouseX, mouseY);
                }
            } else {
                g.drawString(this.font, Component.translatable("task.recommend.empty"),
                        px(CARD_X + 3), cy + 8, C_GRAY);
            }
        }

        // 世界动态
        g.drawString(this.font, Component.translatable("task.events.title"),
                px(EVENTS_TITLE_X), py(EVENTS_TITLE_Y), C_WHITE);
        List<Component> events = TaskData.WORLD_EVENTS;
        for (int i = 0; i < events.size(); i++) {
            g.drawString(this.font, clip(events.get(i).getString(), 56),
                    px(EVENTS_X), py(EVENTS_Y + i * EVENTS_ROW_H), C_TEXT);
        }
    }

    private void renderTaskTooltip(GuiGraphics g, TaskInfo t, int mouseX, int mouseY) {
        List<Component> lines = new ArrayList<>();
        lines.add(t.name().copy().withStyle(s -> s.withColor(0xFFD700)));
        lines.add(t.description());
        lines.add(Component.translatable("task.tooltip.category", t.category().displayName()));
        lines.add(Component.translatable("task.tooltip.status", t.status().displayName()));
        lines.add(Component.translatable("task.tooltip.progress", t.progress(), t.target()));
        lines.add(Component.translatable("task.tooltip.reward",
                t.reward().getHoverName(), t.xpReward()));
        g.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    // ------------------------------------------------------------- 交互
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX, my = (int) mouseY;

        // 关闭按钮
        if (in(mx, my, px(CLOSE_X), py(CLOSE_Y), CLOSE_S, CLOSE_S)) {
            onClose();
            return true;
        }
        // 搜索框
        if (in(mx, my, px(SEARCH_X + 2), py(SEARCH_Y + 2), SEARCH_W - 4, SEARCH_H - 3)) {
            searchBox.setFocused(true);
            searchBox.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        searchBox.setFocused(false);

        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // 页签
        for (int i = 0; i < 5; i++) {
            if (in(mx, my, px(TAB_X + i * (TAB_W + TAB_GAP)), py(TAB_Y), TAB_W, TAB_H)) {
                currentTab = i;
                scrollOffset = 0;
                refreshSelected();
                return true;
            }
        }
        // 开关
        if (in(mx, my, px(SW1_X), py(SW1_Y), SW_W, SW_H)) {
            data.setAutoTrack(!data.autoTrack());
            return true;
        }
        if (in(mx, my, px(SW1_X), py(SW2_Y), SW_W, SW_H)) {
            data.setNewTaskNotify(!data.newTaskNotify());
            return true;
        }
        // 按钮
        if (selected != null) {
            int bx = px(BTN_X), by = py(BTN_Y);
            if (selected.canAccept()
                    && in(mx, my, bx, by, BTN_W, BTN_H)) {
                data.accept(selected);
                refreshSelected();
                return true;
            }
            if (selected.canTrack()
                    && in(mx, my, bx + BTN_W + BTN_GAP, by, BTN_W, BTN_H)) {
                data.track(selected, !selected.tracking());
                return true;
            }
            if (selected.canAbandon()
                    && in(mx, my, bx + 2 * (BTN_W + BTN_GAP), by, BTN_W, BTN_H)) {
                data.abandon(selected);
                refreshSelected();
                return true;
            }
        }
        // 任务行
        List<TaskInfo> visible = visibleTasks();
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx >= visible.size()) break;
            int x = px(LIST_X), y = py(LIST_Y + i * ROW_H);
            if (in(mx, my, x, y, LIST_W, ROW_H)) {
                selected = visible.get(idx);
                return true;
            }
        }
        // 推荐卡
        List<TaskInfo> recs = data.recommendations();
        for (int i = 0; i < 2 && i < recs.size(); i++) {
            if (in(mx, my, px(CARD_X), py(CARD1_Y + i * (CARD_H + 2)), CARD_W, CARD_H)) {
                currentTab = 0;
                selected = recs.get(i);
                refreshSelected();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (in((int) mouseX, (int) mouseY, px(LIST_X), py(LIST_Y), LIST_W, LIST_H)) {
            scrollOffset -= (int) scrollY;
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchBox.setFocused(false);
                return true;
            }
            searchBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        if (ClientHandlers.OPEN_TASK_SCREEN != null
                && ClientHandlers.OPEN_TASK_SCREEN.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox.isFocused()) {
            searchBox.charTyped(codePoint, modifiers);
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}
