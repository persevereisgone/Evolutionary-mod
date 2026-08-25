# Blockbench Boss 建模执行手册（MCP 步骤版）

> 适用于：用 Blockbench MCP（`mcp__blockbench__*`）一刀一步把 Boss 从空项目建到可导出模型。
> 所有步骤按 `.cursor/rules/blockbench-modeling.mdc` 编排：**先 `blockbench-use` 前置检查 → 建骨骼分组 → checkpoint → 建模 → 贴图 → 动画 → 截图验证 → 按需导出**。
> 坐标单位：1 = 1 Minecraft 格。方向：X 左右、Y 上下、Z 前后（正 Z 朝观察者）。所有模型以脚底/下沉中心为 root origin `[0,0,0]`，朝向 +Z。

---

## 0. 通用启动（每个 Boss 都一样）

```text
# 1. 加载编排 skill（每个 Boss 第一条）
mcp__blockbench__... (Skill: blockbench-use)  → 确认项目已打开、格式为 modded_entity
mcp__blockbench__... (Skill: blockbench-mcp-overview)  → 确认工具面（建模/贴图/动画）

# 2. 预检
list_outline            → 确认当前项目为空或可复用
list_textures           → 确认纹理列表状态

# 3. 建项目（若没有）
create_project: name="boss_<name>", format="modded_entity"

# 4. 建根骨骼
add_group: name="root", origin=[0, 0, 0]

# 5. 复杂任务开工前打点（永远第一个 checkpoint）
save_checkpoint: name="start_<name>"
```

**通用硬规则（全程）**

- 每个部件都要 `place_cube` 到对应 group（`group="xxx"`），命名描述性（`body`、`head`、`arm_l`…）。
- 对称件用一次 `place_cube` + `duplicate_element: id="..._l", newName="..._r", offset=[-W, 0, 0]`（W = 两倍中心偏移）。
- 大改动前 `save_checkpoint`；出错 `undo: steps=N`。
- 里程碑 `capture_screenshot`，不只事后截。
- 任何 group 用在动画前先 `rename_element` 确认命名唯一。

---

## 1) 熔核巨兽 Magma Colossus（火焰 / 岩浆系）

### 1.1 设计要点（对齐提示词）

- 四足重甲兽 + 半直立，黑曜石黑体积感，熔岩橙发光裂缝集中在胸口 / 背脊 / 眼睛。
- 部件分组：body / head / jaw / horn_l / horn_r / spine_1~3 / leg_fl / leg_fr / leg_bl / leg_br / tail / core。
- 材质：岩石（粗糙、低反光）、熔岩（发光，MER G=1）。

### 1.2 骨骼层级（建组顺序）

```text
root [0,0,0]
├─ body        (躯干，原点 [0, 14, 0])
│   ├─ core    (胸口熔岩核心，发光，原点 [0, 18, 4])
│   ├─ spine_1 (背脊前段，原点 [0, 22, 0])
│   ├─ spine_2 (背脊中段)
│   ├─ spine_3 (背脊后段)
│   └─ tail    (尾巴，原点 [0, 12, -8])
├─ head        (头，原点 [0, 26, 6])
│   ├─ jaw     (下颚，原点 [0, 24, 8])
│   ├─ horn_l  (左岩角，原点 [4, 30, 4])
│   └─ horn_r  (右岩角，原点 [-4, 30, 4])
├─ leg_fl / leg_fr (前腿，原点 [±5, 8, 6])
└─ leg_bl / leg_br (后腿，原点 [±6, 8, -6])
```

### 1.3 cube 配置表（place_cube）

| name | group | from | to | 说明 |
|---|---|---|---|---|
| body | body | [-8, 10, -6] | [8, 26, 8] | 主躯干大方块 |
| core | core | [-2, 16, 4] | [2, 20, 8] | 胸口发光熔岩核心（稍突出） |
| spine_1 | spine_1 | [-2, 24, -2] | [2, 28, 2] | 背脊1（块状） |
| spine_2 | spine_2 | [-2.5, 23, -4] | [2.5, 27, -0.5] | 背脊2 |
| spine_3 | spine_3 | [-2, 22, -6] | [2, 26, -3] | 背脊3 |
| tail | tail | [-2, 10, -10] | [2, 14, -6] | 尾基 |
| tail_tip | tail | [-1, 8, -14] | [1, 12, -10] | 尾尖 |
| head | head | [-5, 24, 6] | [5, 30, 12] | 兽头 |
| jaw | jaw | [-4, 22, 8] | [4, 24, 14] | 下颚（略前伸） |
| horn_l | horn_l | [3, 30, 4] | [5, 36, 6] | 左角 |
| horn_r | horn_r | [-5, 30, 4] | [-3, 36, 6] | 右角 |
| leg_fl | leg_fl | [4, 0, 4] | [7, 12, 7] | 前左腿 |
| leg_fr | leg_fr | [-7, 0, 4] | [-4, 12, 7] | 前右腿（duplicate 左腿） |
| leg_bl | leg_bl | [4, 0, -8] | [7, 10, -5] | 后左腿 |
| leg_br | leg_br | [-7, 0, -8] | [-4, 10, -5] | 后右腿 |

> 对称件（horn_r / leg_fr / leg_br）建议：先建左件，`duplicate_element` 生成右件（新名带 `_r`），offset 取 X 轴镜像 `[-DX, 0, 0]`。

### 1.4 贴图（32×32 主缩放，可 64×64 精细）

| 通道 | 目标 | 做法 |
|---|---|---|
| color | body/spine/legs | `create_texture: width=64, height=64, fill_color="#2B2B2B"`（黑曜石灰）→ 大面 `paint_fill_tool: color="#3A3A3A"`（亮面）、`#1A1A1A`（暗面） |
| color | core | `paint_fill_tool: color="#FF6A00"`（熔岩橙）→ 加 `#FFD24A` 高光点 |
| color | eyes | `paint_with_brush: brush_settings={color:"#FFAA00", size:1}` 点眼 |
| MER | core/eyes | `create_texture: name="core_mer"`，发光区域填 `#00FF00`（G 通道）→ 用 `pbr-materials` 挂 `texture_set.json` 若目标为 Bedrock  |

`apply_texture`：body 全套 `applyTo="all"`，core/eyes 单独贴发光纹理。

### 1.5 动画（4 个）

```text
create_animation: name="idle", animation_length=2.0, loop=true
  → manage_keyframes: body 轻起伏（position y ±0.25），core 呼吸缩放
create_animation: name="roar", animation_length=1.6, loop=false
  → head 后仰 → 前压；jaw 张合（rotation x 0→30→10）；spine 顺次小跳
create_animation: name="stomp", animation_length=1.2, loop=false
  → leg_fl/fr 同时抬压；body 前倾；tail 甩动
create_animation: name="death", animation_length=2.5, loop=false
  → body 整体下沉 + 下压 pp 至地面；core 变暗（用 scale 收缩）
```

---

## 2) 古树守卫 Ancient Tree Guardian（自然 / 森林系）

### 2.1 设计要点

- 人形树灵 + 老树，深棕树皮、苔藓绿、发光苔核心（额头 + 胸口）。
- 四肢 = 盘根枝条；肩甲 = 厚树皮板；藤蔓少量。
- 动画节奏慢、沉稳。

### 2.2 骨骼层级

```text
root [0,0,0]
├─ trunk         (躯干，原点 [0, 16, 0])
│   ├─ core      (胸口苔藓核心，原点 [0, 20, 3])
│   └─ vines     (藤蔓组，原点 [4, 26, 0])
├─ head          (树形面具头，原点 [0, 30, 0])
│   └─ brow_l/brow_r (眉枝，动画点)
├─ shoulder_l / shoulder_r (肩甲树皮，原点 [±6, 28, 0])
├─ arm_l / arm_r (树枝手臂，上段原点 [±7, 24, 0])
│   └─ forearm_l / forearm_r (前臂，下段可细分成枝)
├─ leg_l / leg_r (树根腿，原点 [±3, 8, 0])
└─ root_splay    (根部外展须，贴地)
```

### 2.3 cube 配置表

| name | group | from | to |
|---|---|---|---|
| trunk | trunk | [-5, 12, -3] | [5, 28, 3] |
| bark_l | trunk | [-5, 16, 3] | [-3, 26, 4] | （左树皮层，略厚） |
| bark_r | trunk | [3, 16, 3] | [5, 26, 4] |
| core | core | [-2, 19, 3] | [2, 22, 5] |
| head | head | [-3.5, 28, -2] | [3.5, 34, 2] |
| eye_l | head | [-2, 31, -2] | [-0.5, 32, -1] | （发光裂隙） |
| eye_r | head | [0.5, 31, -2] | [2, 32, -1] |
| shoulder_l | shoulder_l | [-8, 26, -3] | [-4, 30, 3] |
| shoulder_r | shoulder_r | [4, 26, -3] | [8, 30, 3] |
| arm_l | arm_l | [-8, 20, -2] | [-5, 26, 2] |
| forearm_l | forearm_l | [-9, 12, -1.5] | [-6, 20, 1.5] |
| arm_r | arm_r | [5, 20, -2] | [8, 26, 2] |
| forearm_r | forearm_r | [6, 12, -1.5] | [9, 20, 1.5] |
| leg_l | leg_l | [-4, 0, -2] | [-1, 14, 2] |
| leg_r | leg_r | [1, 0, -2] | [4, 14, 2] |
| root_splay | root_splay | [-6, -1, -1] | [6, 0, 1] | （薄板贴地） |

### 2.4 贴图

- 主色 `#5B3A21`（深棕树皮），局部 `#6B4226` / `#4A2E18` 形成木纹块。
- 苔藓：`#3E7C3A` 覆盖肩甲、胸口、腿根；core `#6EDB52` 发光（MER G=1）。
- 眼睛裂隙 `#B8FF8A`。
- `draw_shape_tool` 画几条横纹模拟树皮节；`paint_with_brush` 加苔点。

### 2.5 动画

```text
idle  (慢，2.5s loop)：head 左右微摆、手臂轻垂晃、core 呼吸光
summon (3s, once)：双手上抬 → 地面根部树根外展（root_splay 缩放）
smash (1.6s, once)：双臂高举 → 砸地（front 下压 + root_splay 抖动）
regrow (2s, loop)：vines 摆动 + trunk 轻微生长缩放
death (2.2s, once)：整体下沉、逐渐 no 下压至贴地
```

---

## 3) 深海晶鳞兽 Abyssal Crystal Beast（水系 / 晶体系）

### 3.1 设计要点

- 长条形半龙深海怪兽，背鳍晶体大面块、晶体鳞片几何化、青蓝发光核心。
- 剪影细长，`fin_*` 全部独立可动画。

### 3.2 骨骼层级

```text
root [0,0,0]
├─ body         (长躯干，原点 [0, 10, 4])
│   ├─ fin_1..fin_4 (背鳍晶片组，from 前到后，可做波动)
│   └─ core      (腹部/鳃下发光颗，原点 [0, 10, 0])
├─ head         (深海龙头，原点 [0, 14, 16])
│   └─ jaw       (下颚，原点 [0, 13, 18])
├─ fin_side_l / fin_side_r (胸鳍，原点 [±6, 12, 8])
├─ tail         (长尾，原点 [0, 10, -12])
│   └─ fin_tail  (分叉尾鳍，原点 [0, 10, -22])
└─ fin_back_low (腰侧腹鳍，薄片，贴下沿)
```

### 3.3 cube 配置表

| name | group | from | to |
|---|---|---|---|
| body | body | [-4, 6, -10] | [4, 18, 16] |
| belly | body | [-3, 6, -8] | [3, 9, 14] | （浅色腹面） |
| fin_1 | fin_1 | [-1.5, 18, 12] | [1.5, 28, 15] |
| fin_2 | fin_2 | [-1.5, 17, 4] | [1.5, 27, 7] |
| fin_3 | fin_3 | [-1.5, 16, -4] | [1.5, 26, -1] |
| fin_4 | fin_4 | [-1.5, 15, -9] | [1.5, 24, -6] |
| core | core | [-1.5, 9, -1] | [1.5, 12, 2] | （发光核心） |
| head | head | [-4, 12, 14] | [4, 20, 22] |
| jaw | jaw | [-3.5, 10, 18] | [3.5, 12, 24] |
| fin_side_l | fin_side_l | [3, 10, 4] | [8, 14, 10] |
| fin_side_r | fin_side_r | [-8, 10, 4] | [-3, 14, 10] |
| tail | tail | [-3, 7, -16] | [3, 13, -10] |
| tail_mid | tail | [-2, 7, -22] | [2, 12, -16] |
| fin_tail | fin_tail | [-1, 8, -29] | [1, 12, -22] |
| fin_tail_l | fin_tail | [1, 8, -30] | [6, 12, -26] | （分叉左） |
| fin_tail_r | fin_tail | [-6, 8, -30] | [-1, 12, -26] | （分叉右） |

### 3.4 贴图

- 主色深海蓝 `#123A5E`；鳞面 `#1C5E8F` / `#0E2A45` 交替大块。
- 腹面 `#7FD4F0`；背鳍晶体 `#2BB8D9` → 高光 `#A8F0FF`。
- core / 鳃 `#4DFFC4`（青色发光，MER G=1）。
- `gradient_tool`：背鳍纵向 深蓝→青 渐变；eye 两个小亮点。

### 3.5 动画

```text
swim (1.8s, loop)：body 沿 Z 轴 S 形摆动（fin_1~4 相位差 0.4s 依次起伏），tail 甩动，fin_tail 拍动
lunge (1.2s, once)：整体前冲 + jaw 猛张（attack）
flash (1.0s, loop)：fin_1~4 + core 亮度闪烁（scale 呼吸）
death (2.4s, once)：下沉 + 侧翻 + fin 全垂
```

---

## 4) 机械王座巨像 Mechanical Throne Colossus（机械 / 蒸汽系）

### 4.1 设计要点

- 蒸汽机械人形，铁灰 + 黄铜 + 深红警示 + 蓝白能源光。
- 机械分件感强：液压臂分段、背后齿轮组可独立旋转。
- 动画：砸地、放炮、展开装甲。

### 4.2 骨骼层级

```text
root [0,0,0]
├─ torso        (躯干甲，原点 [0, 18, 0])
│   ├─ reactor  (胸口反应炉，发光，原点 [0, 20, 3])
│   ├─ vent_l / vent_r (排气管组，原点 [±6, 24, -2]，动画冒气用 scale)
│   └─ gear_tower (背后齿轮组 root，原点 [0, 16, -6])
│       ├─ gear_1 (大齿轮)
│       ├─ gear_2 (中齿轮)
│       └─ gear_3 (小齿轮)
├─ head         (面罩头，原点 [0, 28, 0])
├─ shoulder_l / shoulder_r (肩甲，原点 [±8, 26, 0])
├─ arm_ul_l / arm_ul_r (上臂，液压段，原点 [±9, 22, 0])
│   └─ arm_fl_l / arm_fl_r (前臂，原点 [±10, 14, 0])
├─ leg_ul_l / leg_ul_r (大腿，原点 [±4, 10, 0])
│   └─ leg_ll_l / leg_ll_r (小腿+足，原点 [±4, 2, 0])
└─ base         (履带/底盘，原点 [0, 0, 0])
```

### 4.3 cube 配置表（节选，对称件走 duplicate）

| name | group | from | to |
|---|---|---|---|
| torso | torso | [-7, 12, -4] | [7, 26, 5] |
| torso_plate_r | torso | [-7, 20, 5] | [-4, 25, 6] | （装甲板） |
| torso_plate_l | torso | [4, 20, 5] | [7, 25, 6] |
| reactor | reactor | [-2.5, 18, 5] | [2.5, 22, 8] | （发光反应堆） |
| vent_ul | vent_l | [5, 24, -4] | [7, 27, -1] | （上排气管） |
| gear_1 | gear_1 | [-4, 13, -7] | [4, 21, -4] | （大齿轮块） |
| gear_2 | gear_2 | [6, 10, -7] | [10, 14, -4] |
| gear_3 | gear_3 | [-9, 8, -7] | [-5, 12, -4] |
| head | head | [-4, 26, -4] | [4, 32, 4] |
| visor | head | [-3, 28, -4] | [3, 30, -3] | （发光面罩缝） |
| shoulder_l | shoulder_l | [-10, 24, -4] | [-6, 29, 4] |
| shoulder_r | shoulder_r | [6, 24, -4] | [10, 29, 4] |
| arm_ul_l | arm_ul_l | [-10, 17, -3] | [-7, 24, 3] |
| arm_fl_l | arm_fl_l | [-10, 8, -3] | [-7, 17, 3] | （液压前臂） |
| fist_l | arm_fl_l | [-10, 5, -3] | [-7, 8, 3] | （拳头块） |
| leg_ul_l | leg_ul_l | [-5, 8, -3] | [-1, 18, 3] |
| leg_ll_l | leg_ll_l | [-5, 0, -3] | [-1, 8, 3] |
| foot_l | leg_ll_l | [-4, -1, -4] | [0, 0, 2] |
| base | base | [-8, -2, -6] | [8, 0, 6] | （履带底盘） |

### 4.4 贴图

- 铁灰 `#4A5058` 主甲；黄铜 `#B08D3E` 用于齿轮/接缝；警示红 `#A02020` 小网格；能源蓝 `#39C5FF` 管线。
- reactor / visor / 管线端点发光（MER G=1）。
- `draw_shape_tool: rectangle_h` 画铆钉行（2px 间隔），`copy_brush_tool` 复制到多块装甲。

### 4.5 动画

```text
idle (2.2s, loop)：齿轮缓慢自转（gear_1~3 每秒 30°）、排气管 scale 冒汽
stomp_crush (1.4s, once)：双臂交替砸地 + base 震动（position 抖）
cannon (1.8s, once)：前臂前伸 → 反应炉爆闪（scale 大）→ 拳炮口闪白
expand (2.6s, once)：肩甲展开（shoulder 外移）+ 齿轮塔升起 + visor 变亮
death (3s, once)：齿轮塔散落下沉、炉芯熄灭（scale 收缩 + 暗）
```

---

## 5) 虚空冠冕主宰 Void Crown Sovereign（最终 Boss / 黑暗魔法系）

### 5.1 设计要点

- 漂浮黑暗君王，**无传统双腿**：悬浮核心下身 + 破碎冠冕 + 披风碎片 + 能量手臂。
- 黑紫主调、银白高光、紫黑发光核心（全场最亮焦点）。
- 动画：漂浮、展开、召唤、爆发。

### 5.2 骨骼层级

```text
root [0,0,0]                      (悬浮，原点即模型中心偏上)
├─ upper                         (上身枢纽，原点 [0, 20, 0])
│   ├─ torso                    (黑紫躯干，原点 [0, 20, 0])
│   │   └─ core                 (胸口紫黑核心，强发光，原点 [0, 20, 3])
│   ├─ head                     (核心面/冠座，原点 [0, 26, 0])
│   │   └─ mask                 (发光裂隙面具)
│   ├─ crown                     (断裂冠冕，原点 [0, 30, 0])
│   │   ├─ crown_broken_l / _r  (断裂侧翼)
│   │   └─ crown_gem            (冠顶宝石，发光)
│   ├─ arm_l / arm_r            (能量长臂，原点 [±7, 22, 0])
│   └─ cloak_l / cloak_r        (披风碎片阵列，原点 [±6, 18, -2]，每侧 2~3 片)
├─ energy_ring                  (悬浮能量环，原点 [0, 16, 0]，可整体旋转)
└─ lower                        (漂浮下身/雾核，原点 [0, 8, 0])
```

### 5.3 cube 配置表（节选）

| name | group | from | to |
|---|---|---|---|
| torso | torso | [-5, 16, -3] | [5, 26, 3] |
| mantle | torso | [-6, 22, -3] | [6, 25, 0] | （领甲） |
| core | core | [-2, 18, 3] | [2, 22, 6] | （紫黑发光核心） |
| head | head | [-3, 24, -2] | [3, 30, 2] |
| mask | mask | [-2.5, 25, -2] | [2.5, 28, -1.5] | （裂隙面具，发光） |
| crown_base | crown | [-4, 30, -4] | [4, 31, 1] |
| crown_broken_l | crown_broken_l | [-7, 29, -3] | [-4, 34, -1] | （断裂左翼） |
| crown_broken_r | crown_broken_r | [4, 29, -3] | [7, 34, -1] |
| crown_gem | crown_gem | [-1, 31, -3] | [1, 34, -1] | （冠顶发光宝石） |
| arm_l | arm_l | [-7, 18, -1.5] | [-4, 26, 1.5] | （能量臂，可加 ray 小块） |
| arm_r | arm_r | [4, 18, -1.5] | [7, 26, 1.5] |
| cloak_piece_l1 | cloak_l | [-8, 15, -4] | [-3, 22, -0.5] | （披风碎片1） |
| cloak_piece_l2 | cloak_l | [-11, 10, -4] | [-5, 18, -1] | （披风碎片2，下移） |
| cloak_piece_r1 | cloak_r | [3, 15, -4] | [8, 22, -0.5] |
| cloak_piece_r2 | cloak_r | [5, 10, -4] | [11, 18, -1] |
| ring_1 | energy_ring | [-9, 15, -2] | [9, 16, 1] | （环前半，扁条） |
| ring_2 | energy_ring | [-9, 15, -2] | [9, 16, 1] | （环后半，旋转 180° 或原位偏移） |
| lower | lower | [-6, 2, -5] | [6, 12, 5] | （雾核下身，材质半透明） |
| lower_trail | lower | [-4, -4, -3] | [4, 2, 3] | （拖尾） |

> `energy_ring` 建议用 `place_cube` 扁板 ×2（Z 方向偏移 ±2）形成环感；若要更细可用 `create_cylinder` 再压扁，或 4 个小块围成。

### 5.4 贴图

- 黑紫 `#1A0F2E` 主色；`#2B1B4A` 亮面；银白 `#E0E6F2` 高光条（冠冕 / 领甲边缘）。
- core / crown_gem / mask / ring 全发光：`#B84CFF` 紫光（MER G=1，R=0 非金属、B=0.3 较低粗糙度显镜面）。
- 披风碎片加 `gradient_tool` 顶部深 → 底部略透明（若 Bedrock 支持 alpha surface）。

### 5.5 动画

```text
float (3s, loop)：upper 上下漂浮 ±0.5、左右微摇；cloak 碎片摆动相位差
crown_reveal (2.2s, once)：crown_broken_* 展开 + energy_ring 升起 + mask 亮起（爆发前奏）
summon_ring (2s, loop)：energy_ring 绕 Y 轴持续旋转（rotation y 每帧 +5°）
nova (2.8s, once)：core 从内到外 scale 爆闪 → 光针（用长薄 cube 隐藏/显示）
death (3s, once)：核心暗灭（scale 收缩）、披风碎片逐片掉落下沉、冠冕倾倒
```

---

## 6. 通用收尾（每个 Boss 完成后）

```text
# 1. 全模型截图验证（正面 + 侧面）
set_camera_angle: position=[0, 20, 40], rotation=[0, 0, 0], projection="perspective"
capture_screenshot

# 2. 列表核对命名与层级
list_outline

# 3. 需要交付时导出（先查 codec）
list_export_formats: only_current_format=true
export_model: codec_id="project", path="<项目目录>/<name>.bbmodel"     # 存档
# 实体交付 → bedrock entity / java entity 对应 codec；Bedrock RTX → json + texture_set
```

## 7. 检查清单（每 Boss 交付前自查）

- [ ] `blockbench-use` 已加载，项目格式为 `modded_entity`
- [ ] 骨骼层级按本文建好，命名唯一、无嵌套错位
- [ ] 所有 cube 归属正确 group，无游离元素
- [ ] 对称件由 duplicate 生成，命名带 `_l/_r`
- [ ] 贴图尺寸合理（默认 64×64），发光点已画并挂 MER（如需）
- [ ] 每 Boss 至少 4 个动画（含 idle / attack / death）
- [ ] 动画帧插值合理（生物用 catmullrom，机械用 linear/step）
- [ ] 每个里程碑截图确认，最终正侧面截图已留存
- [ ] 导出格式与目标（实体 / 物品 / RTX）匹配
- [ ] 若用于模组实体：联动 `.cursor/rules/blockbench-modeling.mdc` → `docs/dev/修改同步索引表.md` §4/§14（实体注册 + 渲染器 + lang）

---

## 附录 A：MCP 踩坑速查表（实战验证）

> 以下均为实际建模中踩过的坑，按频率排序。每一条都对应过一次返工。

### A.1 高频错误

| 错误现象 | 根因 | 正确做法 |
|----------|------|----------|
| 动画时肢体不跟着父骨骼动 | modded_entity 格式用了 `add_group` 建组，没有骨骼属性 | **modded_entity 必须用 `bone_rigging` 系列工具**（`add_armature_bone`）建骨骼 |
| `create_texture` 出来总是 16×16 | 工具的 width/height 参数可能被忽略 | 用 `risky_eval` + canvas 手动创建指定尺寸贴图，再 `fromDataURL` 写入 |
| 导出的 Java 类 UV 全错 | `Project.texture_width/height` 仍是默认 16 | **导出前必须设置** `Project.texture_width = 64; Project.texture_height = 64;` |
| `apply_texture` 只能贴一个元素 | 工具设计限制 | 用 `risky_eval` 遍历 `Outliner.elements` 批量给所有 cube 赋纹理 |
| 骨骼旋转绕奇怪的点转 | origin（枢轴点）放错位置 | 骨骼 origin 必须放在**关节处**：上臂→肩、前臂→肘、腿→髋 |
| `create_animation` 报 bones 必填 | 以为 name 就够了 | `bones` 参数是**必填**的，至少传一个骨骼的关键帧 |
| 选中动画时报 "not found" | 动画名带前缀 | 实际名字是 `animation.xxx`，用 `Animation.all.find()` 按前缀查找 |
| 模型在 Minecraft 里上下颠倒 | modded_entity 的 flip_y 设置 | 检查 `Project.modded_entity_flip_y`，默认 true 即可 |

### A.2 贴图尺寸选择参考

| 模型类型 | 推荐尺寸 | 说明 |
|----------|---------|------|
| 普通小怪 | 32×32 | 够用，文件小 |
| 中型生物 | 64×64 | 主流选择，细节足够 |
| Boss 级 | 64×64 ~ 128×128 | 细节多，发光区域需要空间 |
| 装饰/道具 | 16×16 ~ 32×32 | 不需要大贴图 |

### A.3 动画数量与时长参考

| 动画 | 用途 | 时长 | 循环 |
|------|------|------|------|
| `idle` | 待机呼吸 | 2~3s | 是 |
| `walk` / `swim` / `float` | 移动 | 0.6~1.8s | 是 |
| `attack` / `smash` / `stomp` | 普攻 | 1~1.6s | 否 |
| `summon` / `cast` / `cannon` | 技能 | 2~3s | 否 |
| `hurt` | 受伤 | 0.3~0.5s | 否 |
| `death` | 死亡 | 2~3s | 否 |

> 生物动画插值用 **catmullrom**（自然曲线），机械动画用 **linear/step**（硬边感）。

---

## 附录 B：Checkpoint 策略

每个大阶段完成后存一个 checkpoint，出错直接回退，不从头再来。

| 阶段 | checkpoint 名称 | 回退价值 |
|------|----------------|----------|
| 项目 + 骨骼框架完成 | `skeleton_done` | 高——层级错了从头搭很费时间 |
| 主体建模完成 | `modeling_basic_done` | 高——大形确认后再加细节 |
| 全部建模完成 | `modeling_all_done` | 中——细节调整可以回退到这里 |
| 贴图绘制完成 | `texturing_done` | 高——贴图重画最费时间 |
| 动画全部完成 | `animations_done` | 中——动画调整可以分步回退 |
| 导出前最终检查 | `before_export` | 低——最后保险 |

---

## 附录 C：效率原则

1. **先骨架后肉**：先把骨骼层级和 origin 全部摆对，再填 cube
2. **先大后小**：先做躯干/头/四肢主要形体，再加装饰细节
3. **先左后右**：一侧做好 → `duplicate_element` 镜像 → 改名，绝不一侧侧手搓
4. **验证驱动**：每个阶段完成后截图/播放确认，别堆到最后发现大问题
5. **大胆试，勤存点**：拿不准的效果先试，试之前存 checkpoint，不行就 undo

---

## 附录 D：命名规范速查

```
root                    # 根骨骼，origin [0,0,0]
trunk / torso / body    # 躯干
head                    # 头部
  jaw / mask / brow_l / brow_r   # 头部子部件
core                    # 核心/发光中心
shoulder_l / shoulder_r # 肩
arm_l / arm_r           # 上臂
  forearm_l / forearm_r # 前臂
    hand_l / hand_r     # 手（可选）
leg_l / leg_r           # 大腿/腿
  calf_l / calf_r       # 小腿
    foot_l / foot_r     # 脚
vines / fins / wings    # 特殊部件（按实际命名）
```

> 命名一律小写 + 下划线，导出到 Java 后直接变成字段名，可读性直接影响开发效率。