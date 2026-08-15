# 饰品锻造台 GUI 设计文档（B 风格 · 紫金史诗王座）

> 本文件为可直接投喂给 AI 进行 GUI 设计的完整需求文档。  
> 适用项目：Evolutionary Mod（Minecraft 1.21.1 / NeoForge）  
> 设计对象：`evolutionary_mod:accessories_table`（饰品锻造台）交互界面  
> 对应策划案：`docs/planning/system/FORGE_SYSTEM_V1.md`（V1.16）

---

## 一、文档目的

为「饰品锻造台」设计一套华丽、精致、可直接落地的 GUI 视觉方案，风格定为 **B 风格 · 紫金史诗王座（Imperial Purple-Gold）**。

产出物要求：
1. 一套完整 GUI 贴图设计（含公共面板基底 + 4 个页签状态）。
2. 每一部分附可直接生成图片的英文 Prompt。
3. 布局必须符合下述硬性约束，保证实装时能直接切割为游戏贴图。

---

## 二、系统背景（AI 需要知道的语境）

这是一个 Minecraft 模组的锻造台系统，玩家将"饰品 + 锻造材料"放入界面，执行四类操作：

| 页签 | 功能 | 输入 | 输出 |
|------|------|------|------|
| 强化 | 给饰品叠加强化层，上限 +10 | 装备 + 类型精华×1 + 品阶碎片×1~10 | 同物品强化 +1，成功率见能量条 |
| 重锻 | 重随机物品词条 | 装备 + 普通/高级重锻石 +（可选）锁定石 | 词条重滚，100% 成功 |
| 粉碎 | 销毁装备回收材料 | 已强化装备 | 品阶碎片返还约 50%、类型精华约 25% |
| 升阶 | 白名单专属升阶（预留） | 白名单装备 + 升阶材料 | 下一品阶同主题物品 |

- 品阶共 6 档：残破 → 普通 → 优秀 → 史诗 → 传说 → 至臻。
- 强化次数以物品名后缀 `+N` 显示（如 `史诗·生命精华之戒 +3`）。
- 成功率由「物品品阶 vs 材料品阶 + 碎片数量」实时计算，是界面的核心反馈元素。
- 重锻页锁定词条最多 2 条，每锁 1 条消耗 1 个锁定石。

**设计基调**：这是游戏中的"可控成长"核心玩法，界面应传达"神圣、珍贵、值得托付神器"的仪式感。

---

## 三、设计总纲

### 3.1 风格定位

- **一句话**：高阶神祇祭坛 / 王座宝库中的神器锻造圣殿。
- **氛围**：深紫丝绒为底，鎏金雕花缠绕，紫水晶镶嵌。所有材料入槽都像"供奉一件传说级饰品"。
- **情绪**：华丽但不轻浮，贵气而克制；庄重、神圣、值钱。

### 3.2 配色方案（全系统统一，禁止外溢）

| 用途 | 色值 | 说明 |
|------|------|------|
| 面板底 | `#2B0F3D` | 深紫丝绒 / 磨光紫玉 |
| 紫罗兰渐变 | `#9B59B6 → #6C3483` | 选中态光效、能量条中段 |
| 主金 | `#FFD97B` | 鎏金高光、选中页签 |
| 暗金 | `#B8860B` | 描边、未选中元素 |
| 淡紫白 | `#E8D9FF` | 文字底光、光尘 |
| 警示红 | `#E74C3C` | 仅用于粉碎页销毁警示 |

### 3.3 质感与细节语言

- 材质：紫绒布纹 + 磨光紫玉 + 鎏金（拉丝金面）+ 紫水晶（高光切面）。
- 纹饰：金丝卷草纹、狮鹫浮雕、菱形紫水晶宝石、细云状金线。
- 光效：金色光尘缓慢漂浮；选中元素"点亮 + 金边膨胀发光"。
- 禁忌：不使用现代科技线条、不使用冷蓝绿、不出现水墨风、不出现原版 MC 灰板质感。

---

## 四、硬性布局约束（必须严格遵守）

> 以下数值来自实际代码 `ForgeTableMenu` / `ForgeTableScreen`，设计稿必须与此一致，否则无法直接实装。

### 4.1 画布

- 容器贴图尺寸：**256×256**（游戏标准 GUI 贴图规格）。
- 可见面板区域：**176×166** 像素，位于贴图左上角（x0~175, y0~165）。
- 面板圆角：允许 2~4px 圆角；面板主体必须完全落在 176×166 范围内。

### 4.2 槽位坐标（像素，相对于面板左上角）

| 槽位 | 坐标 (x, y) | 用途 |
|------|------------|------|
| 装备槽 | (62, 17) | 饰品（核心槽，视觉上可放大 1.5 倍） |
| 类型精华槽 | (80, 17) | 饰品精华（金色光球图标） |
| 品阶碎片槽 | (98, 17) | 品阶碎片（宝石碎块图标） |
| 属性精华槽 | (116, 17) | 属性精华（彩色小水晶图标） |
| 重锻石槽 | (80, 35) | 普通/高级重锻石 |
| 锁定石槽 | (98, 35) | 锁定重锻石 |

槽位本体（可放置区）为 16×16，外框装饰可扩至 18×18~20×20。

### 4.3 固定区域

| 区域 | 位置 | 说明 |
|------|------|------|
| 标题栏 | 面板顶部 y0~13 | 中央留空（标题文字游戏内渲染） |
| 页签区 | 标题栏下方，左起 | 4 个页签：强化 / 重锻 / 粉碎 / 升阶 |
| 玩家背包 | 面板底部 | 3 行×9 列（y84 起，每格 18px）+ 快捷栏 1 行（y142 起） |
| 背包分隔条 | 操作区与背包区之间 | 鎏金雕花横档 |

### 4.4 必须保留的功能元素（任何页签都不删）

1. **成功率能量条**：水平条，淡紫→紫罗兰→鎏金渐变，位于确认按钮上方。
2. **强化次数徽记**：圆金牌 `+N`，位于能量条旁（游戏内渲染数字）。
3. **确认按钮**：皇冠徽章形，置于操作区底部。
4. **页签选中态**：4 个页签中 1 个高亮、其余压暗，对比必须明显。

---

## 五、公共组件设计规范（所有页签共享）

### 5.1 面板框架

- 深紫丝绒底，四周鎏金卷草纹雕花边框（宽 3~4px）。
- 四角各嵌一枚菱形紫水晶，带强高光点（切面宝石画法）。
- 顶部标题栏两端对称狮鹫浮雕，中央标题留空。
- 四角漂浮极细金色光尘（2~3px 光点，每角 3~5 粒）。

### 5.2 页签

- 造型：卷起一角的羊皮纸 / 勋章牌。
- 选中态：鎏金点亮 + 边缘发光 + 微微外扩。
- 未选中：暗金剪影、压暗 40%。
- 图标（页签上，游戏内无需文字）：
  - 强化：铁砧 + 星芒
  - 重锻：旋转双箭头 + 宝石
  - 粉碎：碎裂宝石 / 锤子
  - 升阶：锁链环绕的权杖（置灰）

### 5.3 槽位

- **装备槽（核心）**：放大 1.5 倍六边形神龛造型，金框 + 旋转紫晶光环，外圈 6 颗小星点刻度（呼应 6 品阶）。
- **材料槽**：小型金边圣龛（18×18 装饰框），比装备槽小一圈。
- 槽底：深紫凹面，微有内阴影。

### 5.4 按钮

- 主操作（确认强化 / 高级重锻）：**皇冠徽章形**，金色主体 + 紫金光晕，亮度最高。
- 次操作（普通重锻）：金边圆钮。
- 危险操作（粉碎确认）：方形、暗红描边、四角裂纹装饰。
- 按钮发光表示"可点击"，压暗表示"条件不满足"。

### 5.5 成功率能量条

- 三段渐变：淡紫 → 紫罗兰 → 鎏金。
- 填充比例代表成功率百分比；末端一颗小星星光点。
- 能量条底槽：深紫半透明凹槽 + 暗金描边。

### 5.6 玩家背包区

- 3×9 + 1×9 槽位，每格金边小槽（18×18 内 16×16 可放区）。
- 与操作区间用鎏金雕花横档分隔。
- 背包区底色比操作区略深半档，体现"储物"层级。

---

## 六、分页签详细设计 + 出图 Prompt

> 出图统一要求（附在每段 Prompt 末尾）：
> - 比例按 176:166 主面板；放大 4 倍出图（约 704×664）。
> - 元素说明：游戏 UI 平面面板、清晰像素/矢量插画风。
> - 禁止：真实文字、字母、槽内物品（所有文字/数字/物品由游戏内渲染）。
> - 语言：Prompt 用英文，AI 兼容性最好。

### 6.1 公共面板基底（先出这张，确认后再出各页签）

**中文描述**：完整的锻造台容器面板。深紫丝绒底，鎏金卷草纹边框，四角菱形紫水晶。顶部金线分隔 + 两端狮鹫浮雕，中央标题留空。下方一排 4 个页签（第 1 个点亮，其余暗金置灰）。中央操作区留空为淡紫星空辉光占位。底部背包区 3×9 + 1×9 金边槽，鎏金雕花横档分隔。四角金色光尘。

```text
Empty base panel UI concept art for a Minecraft mod forge table, imperial purple and gold theme, 176:166 game container ratio, deep violet velvet panel, ornate golden vine filigree border, four diamond amethyst gems with highlights on the corners, gold divider line across the top with lion griffin reliefs on both ends, empty title slot in the middle, a row of four tab buttons below the title bar, the first tab lit and selected glowing gold, the other three dim dark-gold silhouette tabs, center work area left empty with a faint purple starry glow placeholder, bottom player inventory area with 3x9 gold-edged slots plus 1x9 hotbar row, separated by a golden filigree divider bar, fine golden dust particles floating at the corners, luxurious royal fantasy game UI, flat panel, crisp pixel-style illustration, no text, no letters, no items
```

### 6.2 强化页（默认展示页）

**中文描述**：第 1 个页签点亮（铁砧+星芒图标）。中央操作区左侧放大版六边形神龛装备槽（金框 + 旋转紫晶光环 + 6 星点刻度）。右侧竖排 3 个金边小圣龛材料槽（类型精华=金色光球、品阶碎片=菱形宝石碎块、属性精华=彩色小水晶）。材料槽下方是 1~10 十格金边数量选择条（第 5 格亮金）。再下方成功率能量条（约 75%，末端星点）。能量条旁圆金牌 `+3` 徽记。底部皇冠徽章形确认按钮，亮金发光可点击。金尘漂浮。

```text
Enhance tab state of a Minecraft mod forge table UI, imperial purple and gold theme, 176:166 game container, base panel with violet velvet and golden filigree border, amethyst corner gems, first tab lit glowing gold with anvil and star icon, other tabs dim, center work area: on the left a large hexagonal shrine equipment slot with golden frame and rotating amethyst halo with six star marks, on the right three smaller gilded material shrine slots stacked vertically for essence orb icon, gem shard icon and colored crystal icon, below them a horizontal selector strip of ten small gold-edged squares with the fifth square lit, below that a horizontal success-rate energy bar fading from pale violet to violet to gold at about 75 percent with a tiny star spark at the tip, a round gold badge with a plus-3 mark beside the bar, at the bottom a glowing golden crown-shaped confirm button, floating fine gold dust particles, luxurious royal fantasy game UI, flat panel, crisp pixel-style illustration, no text, no letters, no items in slots
```

### 6.3 重锻页

**中文描述**：第 2 个页签点亮（旋转双箭头宝石图标）。左侧仍是六边形神龛装备槽。右侧上方两个材料槽：重锻石槽（紫金圆润磨石）、锁定石槽（带锁孔金方石，稍小）。材料槽下方是半透明紫水晶词条锁定面板：三行条目，每行 = 小勾选框 + 属性图标 + 数值占位条，第一行金勾点亮；列表上方王冠小图标（提示最多锁 2 条）。底部并排两按钮：普通重锻（金边圆钮）+ 高级重锻（大而亮的皇冠徽章钮，紫金光晕）。紫金双色细光带漂浮，仪式感。

```text
Reroll tab state of a Minecraft mod forge table UI, imperial purple and gold theme, 176:166 game container, base panel with violet velvet and golden filigree border, amethyst corner gems, second tab lit glowing gold with rotating double-arrow gem icon, other tabs dim, center work area: hexagonal shrine equipment slot on the left with golden frame and amethyst halo, on the right two material slots, a large rounded purple-gold reroll stone with orb icon and a smaller square lock-stone slot with padlock icon, below them a translucent amethyst glass list panel with three rows of lockable trait entries, each row a small checkbox with attribute icon and value placeholder bar, the first checkbox lit with a golden checkmark, a small crown icon above the list indicating max two locks, at the bottom two buttons side by side: a normal gold-edged round button for standard reroll and a larger brighter glowing crown-shaped button for advanced reroll with purple-gold aura, thin purple-gold light ribbons floating around, ritualistic luxurious royal fantasy game UI, flat panel, crisp pixel-style illustration, no text, no letters, no items in slots
```

### 6.4 粉碎页

**中文描述**：第 3 个页签点亮（碎裂宝石/锤子图标）。整体紫色调略压暗（回收仪式庄严感）。左侧六边形神龛装备槽光环改暗红微光（警示销毁）。右侧深紫底金边返还预览面板：两行内容，品阶碎片（菱形碎块 + 半圆 50% 标记）、类型精华（金色光球 + 四分之一圆 25% 标记）；面板下方金色天平小图标（免费无消耗）。底部方形暗红描边按钮，四角裂纹装饰。金尘比强化页少。

```text
Smash tab state of a Minecraft mod forge table UI, imperial purple and gold theme, 176:166 game container, base panel with violet velvet and golden filigree border, amethyst corner gems, third tab lit glowing gold with cracked gem and hammer icon, other tabs dim, slightly darker purple background tone for solemn recycling ritual, center work area: hexagonal shrine equipment slot on the left with its halo glowing dark red warning, on the right a refund preview panel with dark purple background and golden border containing two rows, first row a diamond gem shard icon with a half-circle 50 percent mark, second row a golden orb icon with a quarter-circle 25 percent mark, a small golden balance scale icon below the panel indicating free no cost, at the bottom a square button with dark red outline and cracked edge decorations for destruction warning, fewer floating gold dust particles than other tabs, luxurious royal fantasy game UI, flat panel, crisp pixel-style illustration, no text, no letters, no items in slots
```

### 6.5 升阶页（预留置灰态）

**中文描述**：第 4 个页签点亮但整体呈未解锁状态。中央操作区压暗 60%，蒙深紫半透明纱幕。纱幕中央漂浮金色封印徽记（锁链环绕权杖/王冠），微光脉动。下方一行 6 枚暗金菱形符文（对应 6 品阶，全部未点亮）。不展示槽位与按钮。

```text
Ascension tab state of a Minecraft mod forge table UI, imperial purple and gold theme, 176:166 game container, base panel with violet velvet and golden filigree border, amethyst corner gems, fourth tab lit but the whole center area locked and dimmed by a translucent dark purple veil, a golden seal emblem with chains around a crown floating in the center glowing with slow pulse, below it a row of six dark-gold unlit diamond rune symbols representing six tiers, no slots and no buttons visible in the locked area, mysterious locked future content atmosphere, luxurious royal fantasy game UI, flat panel, crisp pixel-style illustration, no text, no letters
```

---

## 七、出图规格与验收清单

### 7.1 出图规格

| 项目 | 要求 |
|------|------|
| 单张出图尺寸 | 建议 704×664（176×166 的 4 倍） |
| 出图顺序 | 先公共基底 → 再 4 页签（保证边框/色调一致） |
| 格式 | 透明底 PNG 优先；若 AI 只能画不透明底，需保证面板外为纯色以便抠图 |
| 文字 | 全部留空/占位，由游戏内字体渲染 |

### 7.2 验收清单（每张图对照检查）

- [ ] 面板主体落在 176×166 内，无元素溢出画布
- [ ] 6 个槽位坐标与 4.2 节一致（装备槽可放大，但中心点对齐）
- [ ] 页签选中态对比明显，4 页签图标可辨
- [ ] 成功率能量条 + 强化徽记 + 确认按钮齐全（升阶页除外）
- [ ] 背包区 3×9 + 1×9 完整、有分隔条
- [ ] 配色严格使用 3.2 节色板，无冷蓝/水墨/科技风混入
- [ ] 无任何真实文字/字母/乱码

---

## 八、实装衔接说明（供开发侧参考）

1. 选定图片后切片为 `assets/evolutionary_mod/textures/gui/forge_table.png`（256×256，含 `panel` / `tab_*` / `button_*` / `energy_bar` / `slot_*` 等区域）。
2. 改造 `ForgeTableScreen.java`：将现灰底渲染替换为贴图渲染，页签点击切换对应子贴图。
3. 页签切换逻辑对应策划案 §2 四模式（V1 实装强化/重锻/粉碎，升阶置灰）。
4. 所有数字文本（成功率 %、碎片数量、+N 徽记）由代码绘制，不进贴图。
