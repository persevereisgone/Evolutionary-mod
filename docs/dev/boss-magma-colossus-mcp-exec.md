# 熔核巨兽 Magma Colossus —— MCP 可执行脚本

> 对应手册：`docs/dev/BlockbenchBoss建模执行手册.md` §1。
> 环境：Blockbench MCP 已连接（项目格式 modded_entity）。
> 执行方式：以下每条是一个 `mcp__blockbench__*` 调用，从上到下逐步执行；每 3~4 步后 `capture_screenshot` 确认；出错用 `undo` 回退到上一个 checkpoint。

---

## Phase A. 启动与项目准备

```text
# 1. 加载编排 skill
mcp__blockbench__load_skill: skill="blockbench-use"
mcp__blockbench__load_skill: skill="blockbench-modeling"
mcp__blockbench__load_skill: skill="blockbench-texturing"
mcp__blockbench__load_skill: skill="blockbench-animation"

# 2. 预检
mcp__blockbench__list_outline
mcp__blockbench__list_textures

# 3. 若没有项目
mcp__blockbench__create_project: name="magma_colossus", format="modded_entity"

# 4. 根骨骼
mcp__blockbench__add_group: name="root", origin=[0, 0, 0]

# 5. 打点（开工检查点）
mcp__blockbench__save_checkpoint: name="start_magma"
```

---

## Phase B. 骨骼层级（先全部建组）

```text
mcp__blockbench__add_group: name="body", parent="root", origin=[0, 14, 0]
mcp__blockbench__add_group: name="core", parent="body", origin=[0, 18, 4]
mcp__blockbench__add_group: name="spine_1", parent="body", origin=[0, 22, 0]
mcp__blockbench__add_group: name="spine_2", parent="body", origin=[0, 23, -3]
mcp__blockbench__add_group: name="spine_3", parent="body", origin=[0, 22, -6]
mcp__blockbench__add_group: name="tail", parent="body", origin=[0, 12, -8]
mcp__blockbench__add_group: name="head", parent="root", origin=[0, 26, 6]
mcp__blockbench__add_group: name="jaw", parent="head", origin=[0, 24, 8]
mcp__blockbench__add_group: name="horn_l", parent="head", origin=[4, 30, 4]
mcp__blockbench__add_group: name="horn_r", parent="head", origin=[-4, 30, 4]
mcp__blockbench__add_group: name="leg_fl", parent="root", origin=[5, 8, 6]
mcp__blockbench__add_group: name="leg_fr", parent="root", origin=[-5, 8, 6]
mcp__blockbench__add_group: name="leg_bl", parent="root", origin=[6, 8, -6]
mcp__blockbench__add_group: name="leg_br", parent="root", origin=[-6, 8, -6]

mcp__blockbench__save_checkpoint: name="bones_magma"
```

---

## Phase C. 几何（主躯干 + 头 + 角）

```text
# 躯干
mcp__blockbench__place_cube: elements=[{name: "body", from: [-8, 10, -6], to: [8, 26, 8]}], group="body", faces=true

# 胸口熔岩核心（发光，稍突出）
mcp__blockbench__place_cube: elements=[{name: "core", from: [-2, 16, 4], to: [2, 20, 8]}], group="core", faces=true

# 背脊三段
mcp__blockbench__place_cube: elements=[{name: "spine_1", from: [-2, 24, -2], to: [2, 28, 2]}], group="spine_1", faces=true
mcp__blockbench__place_cube: elements=[{name: "spine_2", from: [-2.5, 23, -4.5], to: [2.5, 27, -1.5]}], group="spine_2", faces=true
mcp__blockbench__place_cube: elements=[{name: "spine_3", from: [-2, 22, -7], to: [2, 26, -4]}], group="spine_3", faces=true

# 尾巴（两段）
mcp__blockbench__place_cube: elements=[{name: "tail_base", from: [-2, 10, -10], to: [2, 14, -6]}], group="tail", faces=true
mcp__blockbench__place_cube: elements=[{name: "tail_tip", from: [-1, 8, -14], to: [1, 12, -10]}], group="tail", faces=true

# 头部
mcp__blockbench__place_cube: elements=[{name: "head", from: [-5, 24, 6], to: [5, 30, 12]}], group="head", faces=true
mcp__blockbench__place_cube: elements=[{name: "jaw", from: [-4, 22, 8], to: [4, 24, 14]}], group="jaw", faces=true

# 左角
mcp__blockbench__place_cube: elements=[{name: "horn_l", from: [3, 30, 4], to: [5, 36, 6]}], group="horn_l", faces=true
# 右角（镜像）
mcp__blockbench__duplicate_element: id="horn_l", newName="horn_r", offset=[-8, 0, 0]
```

---

## Phase D. 四肢（左腿建好后镜像）

```text
mcp__blockbench__place_cube: elements=[{name: "leg_fl", from: [4, 0, 4], to: [7, 12, 7]}], group="leg_fl", faces=true
mcp__blockbench__duplicate_element: id="leg_fl", newName="leg_fr", offset=[-11, 0, 0]

mcp__blockbench__place_cube: elements=[{name: "leg_bl", from: [4, 0, -8], to: [7, 10, -5]}], group="leg_bl", faces=true
mcp__blockbench__duplicate_element: id="leg_bl", newName="leg_br", offset=[-11, 0, 0]

mcp__blockbench__save_checkpoint: name="geometry_magma"
mcp__blockbench__capture_screenshot
```

---

## Phase E. 贴图（黑曜石黑 + 熔岩橙发光）

```text
# 主纹理
mcp__blockbench__create_texture: name="magma_body", width=64, height=64, fill_color="#2B2B2B"

# 亮面/暗面
mcp__blockbench__paint_fill_tool: texture_id="magma_body", x=0, y=0, color="#3A3A3A", fill_mode="face"
# 暗部
mcp__blockbench__paint_with_brush: texture_id="magma_body", coordinates=[{x: 8, y: 8}, {x: 16, y: 8}], brush_settings={color: "#1A1A1A", size: 4, shape: "circle"}

# 熔岩核心橙色
mcp__blockbench__paint_fill_tool: texture_id="magma_body", x=32, y=32, color="#FF6A00", fill_mode="face"
mcp__blockbench__paint_with_brush: texture_id="magma_body", coordinates=[{x: 40, y: 40}], brush_settings={color: "#FFD24A", size: 2, shape: "circle"}

# 眼睛（头正前方两格）
mcp__blockbench__paint_with_brush: texture_id="magma_body", coordinates=[{x: 30, y: 30}, {x: 34, y: 30}], brush_settings={color: "#FFAA00", size: 1}

# 发光核心 MER（若目标 RTX）
mcp__blockbench__create_texture: name="magma_mer", width=64, height=64, fill_color=[0, 0, 128, 255]
mcp__blockbench__paint_fill_tool: texture_id="magma_mer", x=32, y=32, color="#00FF00", fill_mode="face"

# 应用贴图
mcp__blockbench__apply_texture: id="body", texture="magma_body", applyTo="all"
mcp__blockbench__apply_texture: id="core", texture="magma_body", applyTo="all"
mcp__blockbench__apply_texture: id="head", texture="magma_body", applyTo="all"
mcp__blockbench__apply_texture: id="spine_1", texture="magma_body", applyTo="all"
mcp__blockbench__apply_texture: id="spine_2", texture="magma_body", applyTo="all"
mcp__blockbench__apply_texture: id="spine_3", texture="magma_body", applyTo="all"
```

---

## Phase F. 动画（4 个）

```text
# idle：待机呼吸
mcp__blockbench__create_animation: name="idle", animation_length=2.0, loop=true, bones={
  "body": [{time: 0, position: [0, 0, 0]}, {time: 1, position: [0, 0.25, 0]}, {time: 2, position: [0, 0, 0]}],
  "core": [{time: 0, scale: [1, 1, 1]}, {time: 1, scale: [1.1, 1.1, 1.1]}, {time: 2, scale: [1, 1, 1]}]
}

# roar：怒吼（先仰后压 + 下颚张合）
mcp__blockbench__create_animation: name="roar", animation_length=1.6, loop=false, bones={
  "head": [{time: 0, rotation: [0, 0, 0]}, {time: 0.4, rotation: [-20, 0, 0]}, {time: 0.8, rotation: [10, 0, 0]}, {time: 1.2, rotation: [0, 0, 0]}],
  "jaw":  [{time: 0.4, rotation: [40, 0, 0]}, {time: 0.6, rotation: [10, 0, 0]}, {time: 0.8, rotation: [40, 0, 0]}],
  "spine_1": [{time: 0.4, position: [0, 0.3, 0]}, {time: 0.6, position: [0, 0, 0]}],
  "spine_2": [{time: 0.5, position: [0, 0.3, 0]}, {time: 0.7, position: [0, 0, 0]}],
  "spine_3": [{time: 0.6, position: [0, 0.3, 0]}, {time: 0.8, position: [0, 0, 0]}]
}

# stomp：前腿踏地
mcp__blockbench__create_animation: name="stomp", animation_length=1.2, loop=false, bones={
  "leg_fl": [{time: 0, rotation: [0, 0, 0]}, {time: 0.3, rotation: [-30, 0, 0]}, {time: 0.5, rotation: [40, 0, 0]}, {time: 0.7, rotation: [0, 0, 0]}],
  "leg_fr": [{time: 0.1, rotation: [0, 0, 0]}, {time: 0.4, rotation: [-30, 0, 0]}, {time: 0.6, rotation: [40, 0, 0]}, {time: 0.8, rotation: [0, 0, 0]}],
  "body":   [{time: 0.5, position: [0, -0.8, 0]}, {time: 0.7, position: [0, -0.2, 0]}, {time: 0.9, position: [0, 0, 0]}]
}

# death：倒地
mcp__blockbench__create_animation: name="death", animation_length=2.5, loop=false, bones={
  "body": [{time: 0, rotation: [0, 0, 0]}, {time: 1.2, rotation: [90, 0, 0]}, {time: 1.8, rotation: [95, 0, 0]}, {time: 2.5, rotation: [95, 0, 0]}],
  "core": [{time: 1.0, scale: [1, 1, 1]}, {time: 1.8, scale: [0.2, 0.2, 0.2]}],
  "tail": [{time: 0, rotation: [0, 0, 0]}, {time: 1.0, rotation: [0, 0, 30]}, {time: 2.0, rotation: [0, 0, 10]}],
  "head": [{time: 0, rotation: [0, 0, 0]}, {time: 1.0, rotation: [0, 0, 20]}, {time: 2.0, rotation: [0, 0, 20]}]
}
```

---

## Phase G. 收尾

```text
mcp__blockbench__set_camera_angle: position=[0, 20, 40], rotation=[0, 0, 0], projection="perspective"
mcp__blockbench__capture_screenshot
mcp__blockbench__list_outline
mcp__blockbench__save_checkpoint: name="done_magma"

# 需要交付时：
mcp__blockbench__list_export_formats: only_current_format=true
mcp__blockbench__export_model: codec_id="project", path="<你的目录>/magma_colossus.bbmodel"
```

---

## 交付前自查

- [ ] 全部骨骼与 cube 名称正确、层级以 root 为根
- [ ] 核心/眼睛为发光色，躯干为黑曜石灰
- [ ] 4 个动画存在：idle / roar / stomp / death
- [ ] 截图正背面确认剪影（粗壮四足 + 兽头）
- [ ] 若用于模组实体：联动 `docs/dev/修改同步索引表.md` §4（ModEntities + 渲染器 + lang）