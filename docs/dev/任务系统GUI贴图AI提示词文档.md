# 任务系统 GUI 贴图 AI 生成提示词文档（V2 · Minecraft 原版 GUI 风格）

> 本文件为任务系统 GUI 全部贴图的 AI 生成提示词合集，可直接投喂给 AI 绘图工具（Midjourney / DALL·E / 即梦 / 通义万相等）。
> 对应设计文档：`docs/dev/任务系统GUI设计文档.md`（坐标 / 验收标准）
> 贴图清单见设计文档 §8.3：**必做 7 张 + 可选 3 张**。
> ⚡ **推荐路径**：`tools/gen_task_textures.py` 已提供程序化生成脚本，逐像素精确绘制 MC 斜切风格 + 深蓝金外框，无需 AI 反复试错。本文件保留作为参考 / 手动调优备用。

---

## 〇、使用说明（先读这里）

### 0.1 风格基准（V2 改动核心）

> **V1 版本提示词描述为「扁平亮色卡片风 / light gray dashboard」，生成结果偏办公网页风。V2 统一改为：**

| 要求 | 说明 |
|------|------|
| **风格** | **Minecraft 原版游戏内 GUI 风格**（斜切/錾刻描边灰板），blocky 像素块状质感 |
| **参考物** | 观感等同原版工作台、熔炉、背包容器界面 |
| **边框** | 所有元素用 MC 经典斜切边：1px 亮边（左上）+ 1px 暗边（右下）+ 1px 黑色外描边 |
| **外框点缀** | 整图最外框：**深蓝 `#1E3A5F` 粗边框 + 金色 `#FFD700` 细内衬线 + 四角金色小方角** |
| **禁忌** | 禁止扁平办公风（浅灰底白卡片）、禁止圆角卡片、禁止渐变投影、禁止网页/仪表盘质感 |
| **文字** | 所有贴图内**不得出现任何文字、字母、数字、乱码、符号**，内容区一律留空 |

### 0.2 色板速查（V2 新色板，提示词里会直接引用色号）

| 用途 | 色值 | 用途 | 色值 |
|------|------|------|------|
| 面板主体（MC 灰） | `#8B8B8B` | 主操作/选中页签 | `#3B82F6` |
| 面板高光（左上） | `#C6C6C6` | 次操作 | `#A0A0A0` |
| 面板阴影（右下） | `#555555` | 按钮描边 | `#000000` |
| 面板内衬（更深） | `#6B6B6B` | 完成/在线/开关开 | `#55FF55` |
| 顶栏/底槽 | `#3C3C3C` | 开关关 | `#8B8B8B` |
| 外框深蓝 | `#1E3A5F` | 悬停行底 | `#3A5A7A` |
| 外框金色点缀 | `#FFD700` | 选中·完成行底 | `#2E5E3E` |
| 正文 | `#FFFFFF` | 奖励文字 | `#FFE066` |
| 次要文字 | `#D0D0D0` | 进行中 | `#55FFFF` |

### 0.3 出图顺序与通用技巧

1. **出图顺序**：先第 1 张整面板骨架 → 再 2~7 动态组件 → 最后可选 3 张，保证色调一致。
2. **出图尺寸**：按单张实际尺寸的 **2~4 倍**生成再缩小，保证边缘锐利。
3. **多态图**：AI 画不好"多态竖排/横排"，**每态单独生成**再用 Krita/GIMP 拼接。
4. **还是跑偏怎么办**：把 `Minecraft vanilla game UI, chiseled beveled pixel borders, like the vanilla crafting table container interface` 放在提示词**最前面**，或直接附一张原版工作台界面截图作为参考图（image prompt）。
5. **9-slice 贴图**（行背景/按钮）四边边框必须等宽，出图后放大检查。

### 0.4 通用尾部（可拼在每张提示词末尾）

```text
Minecraft vanilla game UI style, blocky pixelated, chiseled beveled 1 pixel borders, 1 pixel black outer outline, no text, no letters, no numbers, no symbols, no icons, no items, nothing inside the empty areas, all content areas left blank
```

---

## 一、整面板骨架 `task_screen.png`（必做 · 第 1 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 340×240（出图 680×480 或 1360×960，再缩小） |
| 界面位置 | 全屏居中 (0,0,340,240)，整图外框即「深蓝+金」装饰框 |
| 包含内容 | 页面深灰底、顶栏+关闭按钮位、三栏灰面板、深蓝金横幅、分隔线、搜索框底、推荐卡底×2、状态点 |
| 留空区 | 顶栏标题、横幅文字两行、横幅图标位、统计 5 行、日志区、按钮位×3、页签位×5、进度条、搜索框文字、推荐卡文字×2、动态文字区 |

**中文描述**：游戏任务系统 GUI 完整空白面板，**Minecraft 原版容器界面风格**，blocky 像素块状质感。整张图最外圈是一条 **4px 粗的装饰边框：深蓝 `#1E3A5F` 为主色，内侧一条 1px 金色 `#FFD700` 细线，四角各有一个金色小方角点缀**。边框内部是 MC 经典灰板：顶部一条 18px 深灰黑顶栏 `#3C3C3C`（带斜切亮/暗边），中央标题位置留空，右端一个 16×16 方形关闭按钮位。顶栏下方三块**斜切描边灰板面板**（`#8B8B8B` 底、左上 1px `#C6C6C6` 亮边、右下 1px `#555555` 暗边、1px 黑色外描边），被 4px 深灰间隔分开：左窄栏 76px、中宽栏 188px、右窄栏 66px。中栏顶部是一块 188×34 的深蓝横幅 `#1E3A5F`，底部一条 2px 金色横线装饰，横幅左上角留 16×16 图标空位、右侧留两行文字空位；横幅下方一条黑色内凹进度条底槽 180×6；再往下是一排三个 MC 按钮空位（各 56×14）和一排五个 MC 页签空位（各 36×16）。左栏从上到下：一行标题空位、五行统计文字空位、一块 72×62 内凹日志区空位、两个 26×11 开关空位、底部一个 6×6 绿色小方点。右栏顶部一个 62×13 内凹搜索框空位、两张 62×26 斜切灰板卡片空位。**整张图没有任何文字、数字、字母、图标、物品**。

**英文提示词**：

```text
Blank UI panel for a Minecraft mod task system, Minecraft vanilla game UI style, blocky pixelated, like the vanilla crafting table container interface, chiseled beveled 1 pixel borders with 1 pixel black outer outline, canvas 340 by 240 pixels, the outermost edge is a decorative 4 pixel frame: dark navy blue #1E3A5F border with a thin 1 pixel gold #FFD700 inner trim line and small gold #FFD700 square corner ornaments in the four corners, inside the frame a dark top bar 18 pixels tall in dark gray #3C3C3C with beveled edges, an empty centered title area and a small 16 by 16 square close button placeholder on the right end, below the top bar three beveled gray-brown panels in classic minecraft container gray #8B8B8B with 1 pixel light highlight #C6C6C6 on the top and left edges and 1 pixel dark shadow #555555 on the bottom and right edges, separated by 4 pixel gaps of dark background, a narrow left panel, a wide center panel and a narrow right panel, the wide center panel has at its top a dark navy blue banner 188 by 34 pixels #1E3A5F with a 2 pixel gold line decoration at its bottom edge, an empty 16 by 16 square icon slot on the left and two empty text lines on the right, under the banner an empty inset dark progress bar slot 180 by 6 pixels, below that a row of three empty minecraft beveled button placeholders each 56 by 14 pixels and a row of five empty minecraft beveled tab placeholders each 36 by 16 pixels, the narrow left panel contains one empty section title line, five empty statistic text rows, an empty inset 72 by 62 pixel log area, two empty 26 by 11 pixel switch placeholders and a small green square status dot near the bottom, the narrow right panel contains an empty inset search box 62 by 13 pixels at the top and two empty beveled card placeholders each 62 by 26 pixels, crisp pixel art, no gradients, no shadows, no rounded corners, no text, no letters, no numbers, no symbols, no icons, no items, no characters, nothing inside the empty areas, all content areas left blank
```

**验收要点**：□ 最外框深蓝+金四角点缀清晰 □ 三栏灰板 76/188/66 比例正确 □ 顶栏 18px + 关闭按钮位 □ 无任何文字/图标 □ 斜切描边（亮/暗边）完整。

---

## 二、任务行背景三态 `task_list_row.png`（必做 · 第 2 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 64×72（三态竖排，每态 64×24；建议每态单独生成后拼接） |
| 界面位置 | 中栏任务列表，每行 184×24（9-slice 拉伸，边框 3px） |
| 三态 | 上：普通态；中：悬停态；下：选中/完成态 |

**中文描述**：
- 普通态（上）：MC 灰板行，`#6B6B6B` 底，左上 1px `#C6C6C6` 亮边、右下 1px `#555555` 暗边、1px 黑色外描边。
- 悬停态（中）：深蓝底 `#3A5A7A`，同款斜切描边，亮边 `#5A7A9A`。
- 选中/完成态（下）：深绿底 `#2E5E3E`，同款斜切描边，亮边 `#4E7E5E`。
- 三态上下紧密排列、无间隔。**无任何文字、图标**。

**英文提示词（分别生成三态，再竖向拼接）**：

```text
Normal state: flat pixel list row background for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 64 by 24 pixels, dark gray-brown fill #6B6B6B, 1 pixel light highlight #C6C6C6 on the top and left edges, 1 pixel dark shadow #555555 on the bottom and right edges, 1 pixel black outer outline, completely empty inside, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

```text
Hover state: flat pixel list row background for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 64 by 24 pixels, dark blue fill #3A5A7A, 1 pixel lighter blue highlight #5A7A9A on the top and left edges, 1 pixel dark shadow #1E3A5F on the bottom and right edges, 1 pixel black outer outline, completely empty inside, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

```text
Selected completed state: flat pixel list row background for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 64 by 24 pixels, dark green fill #2E5E3E, 1 pixel lighter green highlight #4E7E5E on the top and left edges, 1 pixel dark shadow #1E3E2E on the bottom and right edges, 1 pixel black outer outline, completely empty inside, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 四边斜切边框等宽（供 9-slice）□ 三态配色符合新色板 □ 无文字/图标。

---

## 三、主操作按钮底 `button_primary.png`（必做 · 第 3 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 56×14（建议 224×56 生成后缩小） |
| 界面位置 | 中栏 [接取] (6,60,56,14) |
| 说明 | 尺寸固定不变形；悬停高亮由代码叠加 |

**中文描述**：MC 经典按钮底，**蓝色**：`#3B82F6` 底，左上 1px 亮蓝 `#7AB8FF`、右下 1px 暗蓝 `#1E4E7A`、1px 黑色外描边，内部留空（文字由游戏渲染）。

**英文提示词**：

```text
Flat pixel button base for a Minecraft mod GUI, Minecraft vanilla game UI style like vanilla buttons, chiseled beveled 1 pixel borders, size 56 by 14 pixels, blue fill #3B82F6, 1 pixel light blue highlight #7AB8FF on the top and left edges, 1 pixel dark blue shadow #1E4E7A on the bottom and right edges, 1 pixel black outer outline, completely empty center for text, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 蓝色斜切描边完整 □ 内部无内容。

---

## 四、次操作按钮底 `button_secondary.png`（必做 · 第 4 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 56×14 |
| 界面位置 | 中栏 [追踪] (66,60,56,14) / [放弃] (126,60,56,14) 共用 |
| 说明 | 追踪开启变绿由代码覆盖，本图只需灰色态 |

**中文描述**：MC 经典按钮底，**灰色**：`#A0A0A0` 底，左上 1px `#D0D0D0` 亮边、右下 1px `#555555` 暗边、1px 黑色外描边，内部留空。

**英文提示词**：

```text
Flat pixel button base for a Minecraft mod GUI, Minecraft vanilla game UI style like vanilla buttons, chiseled beveled 1 pixel borders, size 56 by 14 pixels, gray fill #A0A0A0, 1 pixel light highlight #D0D0D0 on the top and left edges, 1 pixel dark shadow #555555 on the bottom and right edges, 1 pixel black outer outline, completely empty center for text, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 与主按钮同尺寸同描边，仅底色不同 □ 内部无内容。

---

## 五、页签底两态 `task_tab.png`（必做 · 第 5 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 36×32（两态竖排，每态 36×16） |
| 界面位置 | 中栏页签行 (0,78,188,16)，5 个页签 x=0/38/76/114/152 |
| 两态 | 上：未选中；下：选中 |

**中文描述**：
- 未选中态（上）：MC 灰板，`#8B8B8B` 底，左上 `#C6C6C6`、右下 `#555555`、黑色外描边。
- 选中态（下）：蓝色 `#3B82F6` 底，左上亮蓝 `#7AB8FF`、右下暗蓝 `#1E4E7A`、黑色外描边。
- 两态上下排列无间隔。**无文字**（页签名由游戏渲染）。

**英文提示词（分别生成两态，再竖向拼接）**：

```text
Unselected tab: flat pixel tab background for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 36 by 16 pixels, gray-brown fill #8B8B8B, 1 pixel light highlight #C6C6C6 on the top and left edges, 1 pixel dark shadow #555555 on the bottom and right edges, 1 pixel black outer outline, completely empty inside, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

```text
Selected tab: flat pixel tab background for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 36 by 16 pixels, blue fill #3B82F6, 1 pixel light blue highlight #7AB8FF on the top and left edges, 1 pixel dark blue shadow #1E4E7A on the bottom and right edges, 1 pixel black outer outline, completely empty inside, blocky pixel art, no rounded corners, no gradients, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 选中/未选中对比明显（蓝 vs 灰）□ 同尺寸 □ 无文字。

---

## 六、进度条底槽 `progress_track.png`（必做 · 第 6 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 180×6（或 32×6 拉伸） |
| 界面位置 | 中栏 (4,51,180,6) |
| 说明 | 绿色填充由代码按比例绘制，底槽只画内凹背景槽 |

**中文描述**：MC 内凹底槽：`#3C3C3C` 深灰底，左上 1px `#1E1E1E` 暗边、右下 1px `#555555` 亮边（反向斜切 = 凹陷效果），无外描边或极暗外描边。

**英文提示词**：

```text
Inset progress bar slot for a Minecraft mod GUI, Minecraft vanilla game UI style, size 180 by 6 pixels, flat horizontal bar, dark gray fill #3C3C3C, 1 pixel darker shadow #1E1E1E on the top and left edges and 1 pixel lighter highlight #555555 on the bottom and right edges to create a recessed inset look, completely empty and plain, blocky pixel art, no rounded corners, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 纯色无图案 □ 凹陷斜切（明暗方向与面板相反）□ 高度 6px 恒定。

---

## 七、开关两态 `task_switch.png`（必做 · 第 7 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 52×11（两态横排，每态 26×11） |
| 界面位置 | 左栏 (46,166,26,11) / (46,180,26,11) |
| 说明 | 白色滑块由代码绘制，**贴图内不要画滑块** |

**中文描述**：
- 关闭态（左）：MC 灰板，`#8B8B8B` 底，左上 `#C6C6C6`、右下 `#555555`、黑色外描边。
- 开启态（右）：MC 绿色，`#55FF55` 底，左上亮绿 `#A0FFA0`、右下暗绿 `#2E8E2E`、黑色外描边。
- 内部整体留空（滑块由代码画白色方块）。

**英文提示词（分别生成两态，再横向拼接）**：

```text
Off state: flat pixel toggle switch base for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 26 by 11 pixels, gray-brown fill #8B8B8B, 1 pixel light highlight #C6C6C6 on the top and left edges, 1 pixel dark shadow #555555 on the bottom and right edges, 1 pixel black outer outline, completely empty inside with no sliding knob, blocky pixel art, no rounded corners, no text, no letters, no numbers, no icons
```

```text
On state: flat pixel toggle switch base for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 26 by 11 pixels, green fill #55FF55, 1 pixel light green highlight #A0FFA0 on the top and left edges, 1 pixel dark green shadow #2E8E2E on the bottom and right edges, 1 pixel black outer outline, completely empty inside with no sliding knob, blocky pixel art, no rounded corners, no text, no letters, no numbers, no icons
```

**验收要点**：□ 两态同尺寸 □ **没有滑块**（滑块由代码画）□ 无文字。

---

## 八、日志行底纹 `task_log_row.png`（可选 · 第 8 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 72×11 |
| 界面位置 | 左栏日志区 (2,98,72,62)，每行 72×11 |
| 说明 | 不画则用交替色 `fill()` 代替 |

**中文描述**：极暗灰条 `#4A4A4A`，底部一条 1px 暗分隔线 `#3C3C3C`，几乎不可见，用于区分相邻日志行。

**英文提示词**：

```text
Very subtle log row stripe for a Minecraft mod GUI, Minecraft vanilla game UI style, size 72 by 11 pixels, very dark gray fill #4A4A4A, one faint 1 pixel bottom divider line #3C3C3C, barely visible, blocky pixel art, no bevel, no text, no letters, no numbers, no icons, nothing inside
```

**验收要点**：□ 颜色极暗不抢眼 □ 无文字。

---

## 九、滚动条轨道 `scrollbar_track.png`（可选 · 第 9 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 4×12 |
| 界面位置 | 任务列表右侧 |

**中文描述**：竖向半透明深灰条 `#555555`，1px 黑色左右描边，内凹感。

**英文提示词**：

```text
Scrollbar track for a Minecraft mod GUI, Minecraft vanilla game UI style, size 4 by 12 pixels, vertical dark gray bar #555555 with 1 pixel black side borders, recessed inset look, blocky pixel art, no rounded corners, no text, no letters, no numbers, no icons
```

**验收要点**：□ 宽度 4px 恒定 □ 无文字。

---

## 十、滚动条滑块 `scrollbar_handle.png`（可选 · 第 10 张）

| 项目 | 内容 |
|------|------|
| 尺寸 | 4×12 |
| 界面位置 | 任务列表右侧 |

**中文描述**：竖向 MC 灰板滑块 `#A0A0A0`，左上 `#D0D0D0` 亮边、右下 `#555555` 暗边、黑色外描边，比轨道亮以区分。

**英文提示词**：

```text
Scrollbar handle for a Minecraft mod GUI, Minecraft vanilla game UI style, chiseled beveled 1 pixel borders, size 4 by 12 pixels, vertical gray bar #A0A0A0, 1 pixel light highlight #D0D0D0 on the top and left edges, 1 pixel dark shadow #555555 on the bottom and right edges, 1 pixel black outer outline, blocky pixel art, no rounded corners, no text, no letters, no numbers, no icons
```

**验收要点**：□ 与轨道同宽 □ 带斜切描边 □ 无文字。

---

## 十一、产出文件清单（出完对照勾选）

| # | 文件 | 尺寸 | 生成方式 |
|---|------|------|---------|
| 1 | `task_screen.png` | 340×240 | 一次生成整图（含深蓝+金外框） |
| 2 | `task_list_row.png` | 64×72 | 三态各生成一次，竖向拼接 |
| 3 | `button_primary.png` | 56×14 | 一次生成（蓝色） |
| 4 | `button_secondary.png` | 56×14 | 一次生成（灰色） |
| 5 | `task_tab.png` | 36×32 | 两态各生成一次，竖向拼接 |
| 6 | `progress_track.png` | 180×6 | 一次生成（内凹底槽） |
| 7 | `task_switch.png` | 52×11 | 两态各生成一次，横向拼接 |
| 8 | `task_log_row.png` | 72×11 | 一次生成（可选） |
| 9 | `scrollbar_track.png` | 4×12 | 一次生成（可选） |
| 10 | `scrollbar_handle.png` | 4×12 | 一次生成（可选） |

拼接建议：Krita / GIMP / Photopea 均可；拼接后放大 200% 检查接缝是否对齐。

---

**文档版本**：V2.0（Minecraft 原版 GUI 风格 + 深蓝金外框）
**关联文档**：`docs/dev/任务系统GUI设计文档.md`
**状态**：提示词齐备，待出图
