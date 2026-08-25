#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
任务系统 GUI 贴图程序化生成工具（V2 · Minecraft 原版斜切描边风格 + 深蓝金外框）

按「任务系统GUI设计文档.md」§4.2 坐标 +「任务系统GUI贴图AI提示词文档.md」V2 色板，
逐像素精确绘制全部 10 张贴图，输出到 assets/evolutionary_mod/textures/gui/。

用法：
    python tools/gen_task_textures.py
生成后可加 --preview 输出一张 3 倍放大预览图便于核对。
"""

import os
import sys

from PIL import Image

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
GUI_DIR = os.path.join(
    ROOT, "src", "main", "resources", "assets", "evolutionary_mod", "textures", "gui"
)

# ---------------------------------------------------------------- 色板（V2）
NAVY      = (0x1E, 0x3A, 0x5F)   # 外框深蓝
GOLD      = (0xFF, 0xD7, 0x00)   # 金色点缀
BG        = (0x26, 0x26, 0x26)   # 页面深灰底
BAR       = (0x3C, 0x3C, 0x3C)   # 顶栏
BAR_L     = (0x5A, 0x5A, 0x5A)   # 顶栏高光
BAR_D     = (0x2A, 0x2A, 0x2A)   # 顶栏阴影
PANEL     = (0x8B, 0x8B, 0x8B)   # 面板主体（MC 灰）
PANEL_L   = (0xC6, 0xC6, 0xC6)   # 高光
PANEL_D   = (0x55, 0x55, 0x55)   # 阴影
INNER     = (0x6B, 0x6B, 0x6B)   # 内衬更深（列表/行/卡）
INNER_D   = (0x4A, 0x4A, 0x4A)   # 内衬凹陷暗边
INNER_L   = (0x85, 0x85, 0x85)   # 内衬凹陷亮边
BLACK     = (0x00, 0x00, 0x00)   # 描边
BLUE      = (0x3B, 0x82, 0xF6)   # 主操作
BLUE_L    = (0x7A, 0xB8, 0xFF)
BLUE_D    = (0x1E, 0x4E, 0x7A)
GRAY_BTN  = (0xA0, 0xA0, 0xA0)   # 次操作
GRAY_BTN_L = (0xD0, 0xD0, 0xD0)
GREEN     = (0x55, 0xFF, 0x55)   # 完成/开关开
GREEN_L   = (0xA0, 0xFF, 0xA0)
GREEN_D   = (0x2E, 0x8E, 0x2E)
HOVER     = (0x3A, 0x5A, 0x7A)   # 行悬停
HOVER_L   = (0x5A, 0x7A, 0x9A)
HOVER_D   = (0x1E, 0x3A, 0x5F)
DONE      = (0x2E, 0x5E, 0x3E)   # 行选中/完成
DONE_L    = (0x4E, 0x7E, 0x5E)
DONE_D    = (0x1E, 0x3E, 0x2E)
LOG       = (0x4A, 0x4A, 0x4A)   # 日志行
LOG_D     = (0x3C, 0x3C, 0x3C)
TRACK     = (0x3C, 0x3C, 0x3C)   # 进度条底槽
TRACK_D   = (0x1E, 0x1E, 0x1E)
TRACK_L   = (0x55, 0x55, 0x55)
STAT      = (0x83, 0x83, 0x83)   # 统计行底
STAT_B    = (0x77, 0x77, 0x77)
STRIPE    = (0x60, 0x60, 0x60)   # 日志行底纹
STRIPE_D  = (0x57, 0x57, 0x57)
BANNER    = (0x1E, 0x3A, 0x5F)   # 横幅深蓝
BANNER_L  = (0x2A, 0x4A, 0x6F)
BANNER_D  = (0x12, 0x25, 0x3C)
SLOT_D    = (0x0A, 0x15, 0x24)   # 图标槽最暗


# ---------------------------------------------------------------- 基础绘图
def rect(img, x, y, w, h, color):
    for yy in range(y, y + h):
        for xx in range(x, x + w):
            img.putpixel((xx, yy), color)


def hline(img, x, y, w, color):
    for xx in range(x, x + w):
        img.putpixel((xx, y), color)


def vline(img, x, y, h, color):
    for yy in range(y, y + h):
        img.putpixel((x, yy), color)


def bevel_rect(img, x, y, w, h, fill, light, dark, outline=None):
    """MC 斜切描边矩形：黑色外描边 + 左上亮边 + 右下暗边。"""
    if outline is not None:
        rect(img, x, y, w, h, outline)
    for yy in range(y + 1, y + h - 1):
        for xx in range(x + 1, x + w - 1):
            img.putpixel((xx, yy), fill)
    hline(img, x + 1, y + 1, w - 2, light)
    vline(img, x + 1, y + 1, h - 2, light)
    hline(img, x + 1, y + h - 2, w - 2, dark)
    vline(img, x + w - 2, y + 2, max(0, h - 4), dark)


def inset_rect(img, x, y, w, h, fill, dark, light, outline=None):
    """MC 内凹矩形（方向与 bevel_rect 相反）：左上暗边 + 右下亮边。"""
    if outline is not None:
        rect(img, x, y, w, h, outline)
    for yy in range(y + 1, y + h - 1):
        for xx in range(x + 1, x + w - 1):
            img.putpixel((xx, yy), fill)
    hline(img, x + 1, y + 1, w - 2, dark)
    vline(img, x + 1, y + 1, h - 2, dark)
    hline(img, x + 1, y + h - 2, w - 2, light)
    vline(img, x + w - 2, y + 2, max(0, h - 4), light)


def new(w, h):
    return Image.new("RGBA", (w, h), (0, 0, 0, 0))


# ---------------------------------------------------------------- 1 整面板骨架
def gen_task_screen():
    img = Image.new("RGBA", (340, 240), BG + (255,))

    # 最外框：外 1px 深蓝 + 内 1px 金
    for x in range(340):
        img.putpixel((x, 0), NAVY + (255,))
        img.putpixel((x, 1), GOLD + (255,))
        img.putpixel((x, 238), NAVY + (255,))
        img.putpixel((x, 239), GOLD + (255,))
    for y in range(240):
        img.putpixel((0, y), NAVY + (255,))
        img.putpixel((1, y), GOLD + (255,))
        img.putpixel((338, y), NAVY + (255,))
        img.putpixel((339, y), GOLD + (255,))

    # 四角金色方角（6×6 金块 + 1px 深蓝内环），最后绘制盖在最上层
    def corner(cx, cy):
        rect(img, cx, cy, 6, 6, GOLD + (255,))
        hline(img, cx + 1, cy + 1, 4, NAVY + (255,))
        hline(img, cx + 1, cy + 4, 4, NAVY + (255,))
        vline(img, cx + 1, cy + 1, 4, NAVY + (255,))
        vline(img, cx + 4, cy + 1, 4, NAVY + (255,))

    # 顶栏 (0,0,340,18) → 框内 y2..17
    bevel_rect(img, 2, 2, 336, 16, BAR, BAR_L, BAR_D, BLACK)
    # 关闭按钮位 (322,2,16,16)
    bevel_rect(img, 322, 2, 16, 16, PANEL, PANEL_L, PANEL_D, BLACK)

    # 三栏 (2,22,76,214) / (82,22,188,214) / (274,22,66,214)
    bevel_rect(img, 2, 22, 76, 214, PANEL, PANEL_L, PANEL_D, BLACK)
    bevel_rect(img, 82, 22, 188, 214, PANEL, PANEL_L, PANEL_D, BLACK)
    bevel_rect(img, 274, 22, 66, 214, PANEL, PANEL_L, PANEL_D, BLACK)

    # ---- 中栏内部 ----
    # 横幅 (0,0,188,34) + 底部 2px 金色横线 + 图标槽
    bevel_rect(img, 82, 22, 188, 34, BANNER, BANNER_L, BANNER_D, BLACK)
    hline(img, 84, 52, 184, GOLD + (255,))
    hline(img, 84, 53, 184, GOLD + (255,))
    inset_rect(img, 87, 29, 16, 16, BANNER_D, SLOT_D, BANNER_L, BANNER_L + (255,))
    # 页签下分隔线 (0,94,188,1)
    hline(img, 82, 116, 188, BLACK)
    hline(img, 82, 117, 188, PANEL_L)
    # 进度条 / 按钮 / 页签 / 任务列表：留空（运行时由组件贴图+代码绘制）

    # ---- 左栏内部 ----
    hline(img, 4, 34, 72, BLACK)      # 标题下分隔线 (2,12,72,1)
    hline(img, 4, 35, 72, PANEL_L)
    for i in range(5):                # 统计区 5 行 (4,16+i*11,68,10)
        y0 = 38 + i * 11
        rect(img, 4, y0, 68, 10, STAT + (255,))
        hline(img, 4, y0, 68, STAT_B + (255,))
        hline(img, 4, y0 + 9, 68, STAT_B + (255,))
        vline(img, 4, y0, 10, STAT_B + (255,))
        vline(img, 71, y0, 10, STAT_B + (255,))
    hline(img, 4, 106, 72, BLACK)     # 日志上分隔线 (2,84,72,1)
    hline(img, 4, 107, 72, PANEL_L)
    inset_rect(img, 4, 120, 72, 62, INNER, INNER_D, INNER_L, BLACK)  # 日志区
    for i in range(5):                # 日志行底纹 5 行 × 11px
        y0 = 122 + i * 11
        rect(img, 6, y0, 68, 9, STRIPE + (255,))
        hline(img, 6, y0 + 8, 68, STRIPE_D + (255,))
    hline(img, 4, 182, 72, BLACK)     # 开关上分隔线 (2,160,72,1)
    hline(img, 4, 183, 72, PANEL_L)
    # 两个开关位留空（运行时 blit task_switch.png）
    # 状态点 (4,202,6,6)：绿块 + 黑描边
    rect(img, 6, 224, 6, 6, GREEN + (255,))
    hline(img, 6, 224, 6, BLACK)
    hline(img, 6, 229, 6, BLACK)
    vline(img, 6, 224, 6, BLACK)
    vline(img, 11, 224, 6, BLACK)

    # ---- 右栏内部 ----
    inset_rect(img, 276, 24, 62, 13, INNER, INNER_D, INNER_L, BLACK)  # 搜索框
    bevel_rect(img, 276, 52, 62, 26, INNER, PANEL_L, PANEL_D, BLACK)  # 推荐卡1
    bevel_rect(img, 276, 80, 62, 26, INNER, PANEL_L, PANEL_D, BLACK)  # 推荐卡2

    # 最后统一重绘外框（四边连续，右侧不被右栏盖住），再画四角金块
    for x in range(340):
        img.putpixel((x, 0), NAVY + (255,))
        img.putpixel((x, 1), GOLD + (255,))
        img.putpixel((x, 238), NAVY + (255,))
        img.putpixel((x, 239), GOLD + (255,))
    for y in range(240):
        img.putpixel((0, y), NAVY + (255,))
        img.putpixel((1, y), GOLD + (255,))
        img.putpixel((338, y), NAVY + (255,))
        img.putpixel((339, y), GOLD + (255,))
    corner(0, 0)
    corner(334, 0)
    corner(0, 234)
    corner(334, 234)

    img.save(os.path.join(GUI_DIR, "task_screen.png"))


# ---------------------------------------------------------------- 2 任务行三态
def gen_task_list_row():
    img = new(64, 72)
    bevel_rect(img, 0, 0, 64, 24, INNER, PANEL_L, PANEL_D, BLACK)    # 普通
    bevel_rect(img, 0, 24, 64, 24, HOVER, HOVER_L, HOVER_D, BLACK)   # 悬停
    bevel_rect(img, 0, 48, 64, 24, DONE, DONE_L, DONE_D, BLACK)      # 选中/完成
    img.save(os.path.join(GUI_DIR, "task_list_row.png"))


# ---------------------------------------------------------------- 3/4 按钮
def gen_buttons():
    img = new(56, 14)
    bevel_rect(img, 0, 0, 56, 14, BLUE, BLUE_L, BLUE_D, BLACK)
    img.save(os.path.join(GUI_DIR, "button_primary.png"))
    img2 = new(56, 14)
    bevel_rect(img2, 0, 0, 56, 14, GRAY_BTN, GRAY_BTN_L, PANEL_D, BLACK)
    img2.save(os.path.join(GUI_DIR, "button_secondary.png"))


# ---------------------------------------------------------------- 5 页签两态
def gen_task_tab():
    img = new(36, 32)
    bevel_rect(img, 0, 0, 36, 16, PANEL, PANEL_L, PANEL_D, BLACK)    # 未选中
    bevel_rect(img, 0, 16, 36, 16, BLUE, BLUE_L, BLUE_D, BLACK)      # 选中
    img.save(os.path.join(GUI_DIR, "task_tab.png"))


# ---------------------------------------------------------------- 6 进度条底槽
def gen_progress_track():
    img = new(180, 6)
    inset_rect(img, 0, 0, 180, 6, TRACK, TRACK_D, TRACK_L)
    img.save(os.path.join(GUI_DIR, "progress_track.png"))


# ---------------------------------------------------------------- 7 开关两态
def gen_task_switch():
    img = new(52, 11)
    bevel_rect(img, 0, 0, 26, 11, PANEL, PANEL_L, PANEL_D, BLACK)    # 关
    bevel_rect(img, 26, 0, 26, 11, GREEN, GREEN_L, GREEN_D, BLACK)   # 开
    img.save(os.path.join(GUI_DIR, "task_switch.png"))


# ---------------------------------------------------------------- 8 日志行
def gen_task_log_row():
    img = new(72, 11)
    rect(img, 0, 0, 72, 10, LOG + (255,))
    hline(img, 0, 10, 72, LOG_D)
    img.save(os.path.join(GUI_DIR, "task_log_row.png"))


# ---------------------------------------------------------------- 9/10 滚动条
def gen_scrollbar():
    img = new(4, 12)
    rect(img, 0, 0, 4, 12, PANEL_D + (255,))
    vline(img, 0, 0, 12, BLACK)
    vline(img, 3, 0, 12, BLACK)
    img.save(os.path.join(GUI_DIR, "scrollbar_track.png"))
    img2 = new(4, 12)
    bevel_rect(img2, 0, 0, 4, 12, GRAY_BTN, GRAY_BTN_L, PANEL_D, BLACK)
    img2.save(os.path.join(GUI_DIR, "scrollbar_handle.png"))


# ---------------------------------------------------------------- 预览
def make_preview():
    """把全部贴图按 3 倍放大拼成一张预览图。"""
    canvas = new(340 * 3 + 20, 240 * 3 + 70)
    img = Image.open(os.path.join(GUI_DIR, "task_screen.png"))
    canvas.paste(img.resize((340 * 3, 240 * 3), Image.NEAREST), (10, 10))
    y0 = 240 * 3 + 30
    x = 10
    for name in ["task_list_row.png", "button_primary.png", "button_secondary.png",
                 "task_tab.png", "progress_track.png", "task_switch.png",
                 "task_log_row.png", "scrollbar_track.png", "scrollbar_handle.png"]:
        im = Image.open(os.path.join(GUI_DIR, name))
        canvas.paste(im.resize((im.width * 3, im.height * 3), Image.NEAREST), (x, y0))
        x += im.width * 3 + 12
    canvas.save(os.path.join(ROOT, "docs", "dev", "task_textures_preview.png"))


def main():
    os.makedirs(GUI_DIR, exist_ok=True)
    gen_task_screen()
    gen_task_list_row()
    gen_buttons()
    gen_task_tab()
    gen_progress_track()
    gen_task_switch()
    gen_task_log_row()
    gen_scrollbar()
    for name in sorted(os.listdir(GUI_DIR)):
        if name.endswith(".png"):
            im = Image.open(os.path.join(GUI_DIR, name))
            print(f"{name:24s} {im.width}x{im.height}")
    if "--preview" in sys.argv:
        make_preview()
        print("preview -> docs/dev/task_textures_preview.png")


if __name__ == "__main__":
    main()
