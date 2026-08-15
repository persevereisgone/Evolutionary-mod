# 游戏内游玩指南系统设计方案 V1.3

## 一、项目概述

### 1.1 设计目标

| 目标 | 说明 |
|------|------|
| **按键呼出** | 玩家通过自定义按键打开指南菜单 |
| **分类导航** | 支持多标签页分类，便于信息检索 |
| **可扩展架构** | 模块化设计，便于后续添加新分类内容 |
| **双语支持** | 根据游戏语言设置自动切换中英文 |
| **仅模组内容** | 仅展示模组自定义内容，不含原版内容 |

---

## 二、标签页结构

```
游玩指南
│
├── 🔧 合成配方
│   ├── 负重装备
│   │   ├── 基础背包
│   │   ├── 皮革背包
│   │   └── 铁制背包
│   ├── 特殊工具
│   │   ├── xxx
│   │   └── xxx
│   └── 装饰物品
│       ├── xxx
│       └── xxx
│
├── ⚙️ 系统机制
│   ├── 负重系统 ⭐
│   │   ├── 系统介绍
│   │   ├── 上限计算
│   │   ├── 惩罚机制
│   │   └── 提升方法
│   ├── 成就系统
│   │   ├── 已解锁成就
│   │   └── 待解锁目标
│   └── 等级系统
│       ├── 经验来源
│       └── 等级奖励
│
└── 📦 物品获取
    ├── 模组资源
    │   ├── 负重精华获取
    │   ├── 强化材料获取
    │   └── 特殊材料获取
    ├── 装备获取
    │   ├── 背包装备获取
    │   └── 饰品装备获取
    └── 消耗品获取
        ├── 药水材料获取
        └── 食物材料获取
```

---

## 三、界面设计

### 3.1 界面规格

| 元素 | 尺寸 | 说明 |
|------|------|------|
| 主窗口 | 500 × 400 px | 居中显示 |
| 标题栏 | 500 × 35 px | 包含标题和关闭按钮 |
| 标签栏 | 500 × 30 px | 水平排列标签 |
| 列表区域 | 180 × 290 px | 左侧，可滚动 |
| 详情面板 | 300 × 290 px | 右侧，显示选中内容 |
| 底部提示 | 500 × 25 px | 操作提示 |

### 3.2 配色方案（简约风格）

| 元素 | 颜色 | 说明 |
|------|------|------|
| 背景 | #1A1A1A (深灰) | 主背景色 |
| 面板背景 | #2D2D2D (中灰) | 内容区域背景 |
| 标题文字 | #FFFFFF (白色) | 标题和重要文字 |
| 普通文字 | #CCCCCC (浅灰) | 普通说明文字 |
| 高亮选中 | #3B82F6 (蓝色) | 选中项背景 |
| 边框颜色 | #4A4A4A (灰色) | 面板边框 |
| 锁定状态 | #666666 (暗灰) | 未解锁内容 |

### 3.3 界面布局

```
┌──────────────────────────────────────────────────────┐
│  🎮 游玩指南                               [X] 关闭   │
├──────────────────────────────────────────────────────┤
│  [🔧 合成配方]  [⚙️ 系统机制]  [📦 物品获取]          │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌─────────────┐  ┌─────────────────────────────┐   │
│  │ 分类列表     │  │ 详情面板                     │   │
│  │             │  │                             │   │
│  │ 🧪 负重装备  │  │ 标题：基础背包               │   │
│  │   • 基础背包 │  │                             │   │
│  │   • 皮革背包 │  │ 分类：合成配方 > 负重装备    │   │
│  │   • 铁制背包 │  │                             │   │
│  │             │  │ ┌───┐ ┌───┐       ┌───┐    │   │
│  │ ⚙️ 特殊工具  │  │ │皮革│ │皮革│ ... │铁锭│    │   │
│  │   • xxx     │  │ │ x8 │ │ x8 │       │ x2 │    │   │
│  │             │  │ └───┘ └───┘       └───┘    │   │
│  │             │  │           ↓                 │   │
│  │             │  │        ┌───┐                │   │
│  │             │  │        │背包│                │   │
│  │             │  │        └───┘                │   │
│  │             │  │                             │   │
│  │             │  │ 说明：提供+20负重上限...     │   │
│  └─────────────┘  └─────────────────────────────┘   │
│                                                      │
├──────────────────────────────────────────────────────┤
│  按 [G] 打开指南  |  ↑↓ 浏览列表  |  Enter 确认     │
└──────────────────────────────────────────────────────┘
```

---

## 四、模块详细设计

### 4.1 合成配方模块

**功能说明：** 仅展示模组新增的合成配方

**数据结构：**
```java
public class CustomRecipe {
    private String id;                    // 配方唯一ID
    private String category;              // 所属分类
    private String nameKey;               // 名称翻译键
    private String descKey;               // 描述翻译键
    private ItemStack result;             // 产物
    private Ingredient[] ingredients;      // 材料
    private CraftingStation station;       // 合成台类型
    private boolean unlocked;              // 是否解锁
}
```

**示例配方数据：**
| ID | 名称 | 分类 | 材料 | 合成台 |
|----|------|------|------|--------|
| backpack_basic | 基础背包 | 负重装备 | 皮革×8 + 铁锭×2 | 工作台 |
| backpack_leather | 皮革背包 | 负重装备 | 皮革×12 + 铁锭×4 | 工作台 |
| backpack_iron | 铁制背包 | 负重装备 | 皮革×12 + 铁锭×6 + 锁链×4 | 工作台 |

---

### 4.2 系统机制模块

**功能说明：** 详细解释模组系统机制

**页面示例 - 负重系统：**
```
═══════════════════════════════════════════════════
                 ⭐ 负重系统 ⭐
═══════════════════════════════════════════════════

【系统介绍】
负重系统为玩家添加了背包重量限制机制。
携带过多物品会影响移动速度和饱食度消耗。

【负重上限】
  • 基础负重：120 单位
  • 等级加成：每级 +2 单位
  • 背包装备可进一步提升上限

【惩罚机制】
  负重状态    负重比例   效果
  ──────────────────────────────
  正常        < 60%     无惩罚
  负重        60%-85%   速度略微下降
  超重        85%-100%  速度下降 + 饱食加速
  超载        ≥ 100%   速度显著下降 + 饱食加倍

【提升方法】
  ① 通过获得经验值提升等级
  ② 制作更高级的背包装备
  ③ 完成特定成就解锁加成

═══════════════════════════════════════════════════
```

---

### 4.3 物品获取模块

**功能说明：** 介绍模组物品的获取途径

**数据结构：**
```java
public class ItemSource {
    private String itemId;                // 物品ID（模组物品）
    private String nameKey;              // 名称翻译键
    private List<SourceEntry> sources;   // 获取途径
}

public class SourceEntry {
    private SourceType type;             // 获取类型
    private String description;          // 描述
    private int rarity;                  // 稀有度 1-5
}

public enum SourceType {
    CRAFTING,    // 合成获取
    MOB_DROP,    // 生物掉落
    CHEST_LOOT,  // 宝箱奖励
    TRADE,       // 村民交易
    QUEST,       // 成就奖励
    SPECIAL      // 特殊方式
}
```

**示例数据：**
| 物品 | 获取途径 | 描述 | 稀有度 |
|------|----------|------|--------|
| **负重精华** | 生物掉落 | 击杀高级怪物概率掉落 | ⭐⭐⭐ |
| | 成就奖励 | 完成特定成就解锁 | - |
| **强化晶体** | 宝箱奖励 | 模组专属宝箱概率获得 | ⭐⭐ |
| **高级皮革** | 合成获取 | 胶原蛋白 + 皮革 | ⭐ |

---

## 五、按键绑定

| 按键 | 功能 | 可配置 |
|------|------|--------|
| `G` | 打开/关闭指南 | ✅ |
| `K` | 打开/关闭指南（备用） | ✅ |
| `Esc` | 关闭指南 | ❌ |
| `Tab` | 切换标签页 | ❌ |
| `↑` / `↓` | 浏览列表 | ❌ |
| `←` / `→` | 快速切换标签 | ❌ |
| `Enter` | 确认/展开 | ❌ |
| `Backspace` | 返回上级 | ❌ |

---

## 六、国际化支持

### 6.1 翻译文件结构

```
assets/
└── evolutionary_mod/
    └── lang/
        ├── zh_cn.json    # 简体中文
        └── en_us.json    # 英语
```

### 6.2 翻译键命名规则

| 键名格式 | 示例 |
|----------|------|
| 指南标题 | `guide.title` |
| 模块标题 | `guide.module.synthesis` |
| 配方名称 | `guide.recipe.backpack_basic.name` |
| 配方描述 | `guide.recipe.backpack_basic.desc` |
| 系统说明 | `guide.system.weight.intro` |
| 物品获取 | `guide.item_source.weight_essence.name` |
| 操作提示 | `guide.hint.open` |

---

## 七、扩展性设计

### 7.1 模块化架构

```
guide/
├── GuideRegistry.java              # 核心注册器
├── GuideConfig.java                # 配置文件
│
├── module/
│   ├── GuideModule.java            # 模块基类
│   ├── SynthesisModule.java        # 合成配方模块
│   ├── SystemModule.java           # 系统机制模块
│   └── ItemSourceModule.java       # 物品获取模块
│
├── data/
│   ├── RecipeEntry.java            # 配方条目
│   ├── SystemInfoEntry.java        # 系统信息条目
│   └── ItemSourceEntry.java         # 物品获取条目
│
├── gui/
│   ├── GuideScreen.java            # 主界面
│   ├── TabBar.java                 # 标签栏
│   ├── CategoryList.java           # 分类列表
│   └── DetailPanel.java             # 详情面板
│
└── util/
    └── I18nUtil.java               # 国际化工具
```

### 7.2 添加新分类示例

```java
// 添加新的"生物图鉴"分类
public class BiomesModule extends GuideModule {

    public BiomesModule() {
        super("biomes",                    // 模块ID
              "guide.module.biomes",       // 翻译键
              new ResourceLocation("evolutionary_mod:textures/gui/biomes.png"));
    }

    @Override
    protected void loadContent() {
        addEntry(new BiomeEntry("custom_biome", "guide.biome.custom_biome.name", "guide.biome.custom_biome.desc"));
    }
}
```

---

## 八、文件结构总览

```
src/main/java/com/muyun/evolutionary_mod/
└── guide/
    ├── GuideMod.java                    # 模块主类
    ├── GuideRegistry.java               # 注册器
    ├── GuideConfig.java                 # 配置
    │
    ├── module/
    │   ├── GuideModule.java            # 模块基类
    │   ├── SynthesisModule.java        # 合成配方模块
    │   ├── SystemModule.java            # 系统机制模块
    │   └── ItemSourceModule.java        # 物品获取模块
    │
    ├── data/
    │   ├── CustomRecipe.java           # 自定义配方
    │   ├── SystemInfo.java             # 系统信息
    │   └── ItemSource.java             # 物品获取信息
    │
    └── gui/
        ├── GuideScreen.java            # 主界面
        └── components/
            ├── TabBar.java
            ├── CategoryList.java
            └── DetailPanel.java

src/main/resources/
└── assets/evolutionary_mod/
    └── lang/
        ├── zh_cn.json
        └── en_us.json
```

---

## 九、实施计划

### 阶段一：核心框架
- [ ] 创建模块架构
- [ ] 实现基础GUI框架
- [ ] 添加按键绑定
- [ ] 实现标签页切换

### 阶段二：内容填充
- [ ] 实现合成配方模块
- [ ] 实现系统机制模块
- [ ] 实现物品获取模块
- [ ] 添加国际化支持

### 阶段三：完善功能
- [ ] 添加解锁追踪（未来功能）
- [ ] 优化列表滚动
- [ ] 界面细节打磨

---

## 十、方案总结

| 项目 | 内容 |
|------|------|
| **模块1** | 合成配方 - 仅模组合成配方 |
| **模块2** | 系统机制 - 负重/成就/等级系统说明 |
| **模块3** | 物品获取 - 模组物品获取途径 |
| **界面风格** | 简约风格 |
| **国际化** | 中英文支持 |
| **扩展性** | 模块化架构 |

---

## 十一、搁置说明与实装注意事项

### 11.1 当前状态

| 项目 | 状态 |
|------|------|
| **策划案版本** | V1.3 |
| **设计日期** | 2026-05-15 |
| **实装状态** | ⚠️ 自研 UI 搁置；已改用 **Patchouli** 引导书（`evolutionary_mod:guide`） |
| **下次读取** | 可直接按此文档实装 |

---

### 11.2 实装前提条件

在开始实装之前，请确保以下内容已完成：

| 优先级 | 前提条件 | 说明 |
|--------|----------|------|
| P0 | 按键绑定系统 | 项目中已有KeyMapping机制 |
| P0 | GUI基础框架 | 了解现有Screen实现方式 |
| P1 | 国际化系统 | 已有lang文件结构 |
| P1 | 配置系统 | 已有ModConfig使用经验 |
| P2 | 负重系统 | 必须先实装（系统机制模块依赖） |
| P2 | 物品注册系统 | 已有自定义物品注册 |

---

### 11.3 实装注意事项

#### 技术注意事项

| 序号 | 注意事项 | 说明 |
|------|----------|------|
| 1 | **NeoForge GUI架构** | 使用`Screen`类实现主界面，避免使用原版容器 |
| 2 | **按键冲突检测** | 使用`KeyConflictContext`避免与原版按键冲突 |
| 3 | **资源加载顺序** | 翻译文件需在GUI之前加载 |
| 4 | **性能优化** | 列表内容多时使用虚拟滚动 |
| 5 | **缓存机制** | 已加载的内容应缓存，避免重复解析 |

#### 内容注意事项

| 序号 | 注意事项 | 说明 |
|------|----------|------|
| 1 | **仅模组内容** | 合成配方、物品获取仅包含自定义内容 |
| 2 | **翻译完整性** | 所有显示文本必须有中英文翻译 |
| 3 | **数据分离** | 配方数据与界面代码分离，便于维护 |
| 4 | **预留扩展** | 架构支持后续添加新模块 |

---

### 11.4 快速实装检查清单

实装时按以下顺序检查：

```
□ 1. 创建包结构
    □ guide/
    □ guide/module/
    □ guide/data/
    □ guide/gui/
    □ guide/util/

□ 2. 实现核心类
    □ GuideRegistry.java - 注册器
    □ GuideConfig.java - 配置
    □ GuideScreen.java - 主界面

□ 3. 实现模块类
    □ GuideModule.java - 模块基类
    □ SynthesisModule.java - 合成配方
    □ SystemModule.java - 系统机制
    □ ItemSourceModule.java - 物品获取

□ 4. 实现数据类
    □ CustomRecipe.java
    □ SystemInfo.java
    □ ItemSource.java

□ 5. 实现GUI组件
    □ TabBar.java
    □ CategoryList.java
    □ DetailPanel.java

□ 6. 添加按键绑定
    □ KeyMapping注册
    □ ClientSetup事件

□ 7. 添加翻译
    □ zh_cn.json
    □ en_us.json

□ 8. 注册到主类
    □ EvolutionaryMod.java中注册

□ 9. 测试验证
    □ G键打开/关闭
    □ 标签页切换
    □ 列表滚动
    □ 详情显示
    □ 中英文切换
```

---

### 11.5 依赖关系图

```
┌─────────────────────────────────────────────────────┐
│                    游玩指南系统                        │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌───────────┐    ┌───────────┐                   │
│  │ Synthesis │    │  System   │                   │
│  │  Module   │    │  Module   │                   │
│  └─────┬─────┘    └─────┬─────┘                   │
│        │                │                          │
│        ▼                ▼                          │
│  ┌─────────────────────────────────┐              │
│  │      依赖负重系统数据             │              │
│  │   (WeightSystem已实装)           │              │
│  └─────────────────────────────────┘              │
│                      │                              │
│                      ▼                              │
│  ┌─────────────────────────────────┐              │
│  │      依赖物品注册数据             │              │
│  │   (自定义物品已注册)             │              │
│  └─────────────────────────────────┘              │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

### 11.6 关键代码片段

#### 按键注册示例
```java
// 在ClientSetup中注册
@SubscribeEvent
public static void onKeyRegister(RegisterKeyMappingsEvent event) {
    event.register(GUIDE_KEY);
}

// 在ClientTick中检测
@SubscribeEvent
public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END && GUIDE_KEY.consumeClick()) {
        // 打开/关闭指南界面
    }
}
```

#### Screen打开/关闭
```java
// 打开指南
Minecraft.getInstance().setScreen(new GuideScreen());

// 关闭指南（返回游戏）
Minecraft.getInstance().setScreen(null);
```

#### 翻译获取
```java
// 获取翻译文本
String title = Component.translatable("guide.title").getString();
String name = Component.translatable("guide.recipe.backpack_basic.name").getString();
```

---

### 11.7 搁置记录

| 日期 | 版本 | 状态 | 备注 |
|------|------|------|------|
| 2026-05-15 | V1.3 | 搁置 | 等待后续实装 |

---

### 11.8 下次实装前确认

> ⚠️ **实装前请确认以下内容已更新：**
>
> - [ ] 策划案版本号
> - [ ] 依赖系统状态（负重系统、物品系统）
> - [ ] 界面UI设计是否有更新
> - [ ] 翻译文件是否完整
> - [ ] NeoForge版本是否有API变更

---

**策划案版本**：V1.3
**设计日期**：2026-05-15
**状态**：⚠️ 暂时搁置，待后续实装
**下次实装**：可直接按此文档进行实装