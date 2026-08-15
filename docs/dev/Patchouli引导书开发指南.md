# Patchouli 引导书开发指南

适用版本：Evolutionary Mod + Patchouli `1.21.1-93-NEOFORGE`  
书 ID：`evolutionary_mod:guide`  
书名（中文）：进化指南

本指南说明：**现有 JSON / lang 在哪、怎么改、怎么加分类/条目、怎么验证**。  
官方完整文档：[Patchouli Wiki](https://vazkiimods.github.io/Patchouli/)

---

## 1. 心智模型（先读这 4 句）

1. **结构用 JSON**（分类、条目、页列表）放在 `assets/.../patchouli_books/guide/en_us/`。
2. **文案用 lang**（标题、正文）放在 `zh_cn.json` / `en_us.json`，因为 `book.json` 开了 `"i18n": true`。
3. **书本体注册**用 `data/.../patchouli_books/guide/book.json`（改这个一般要重进游戏）。
4. **改条目内容可热重载**：游戏内打开书 → 左下角铅笔 → **Shift+点击** 重载（不必关客户端）。

改文案优先改 lang；改结构（新增分类/条目/页）改 JSON + 补 lang。

---

## 2. 目录总览

```text
src/main/resources/
├── data/evolutionary_mod/
│   ├── patchouli_books/guide/
│   │   └── book.json                          # 书注册与外观
│   └── recipe/
│       └── guide_book.json                    # 合成：书 + 金粒
│
└── assets/evolutionary_mod/
    ├── lang/
    │   ├── zh_cn.json                         # 中文文案（patchouli.evolutionary_mod.*）
    │   └── en_us.json                         # 英文文案
    └── patchouli_books/guide/
        └── en_us/                             # 主内容语言目录（必须有）
            ├── categories/                    # 分类
            │   ├── getting_started.json
            │   ├── accessories.json
            │   ├── combat.json
            │   ├── loot.json
            │   ├── systems.json
            │   └── faq.json
            └── entries/                       # 条目（可分子目录，仅便于整理）
                ├── getting_started/
                ├── accessories/
                ├── combat/
                ├── loot/
                ├── systems/
                └── faq/
```

依赖与版本：

| 文件 | 作用 |
|------|------|
| `gradle.properties` → `patchouli_version` | Patchouli 版本号 |
| `build.gradle` | Maven + `compileOnly` API / `runtimeOnly` 本体 |
| `META-INF/neoforge.mods.toml` | `patchouli` 标记为 required 依赖 |

---

## 3. 现有内容清单

### 3.1 分类（categories）

| 文件 | 分类 ID | sortnum | 用途 |
|------|---------|---------|------|
| `getting_started.json` | `evolutionary_mod:getting_started` | 0 | 快速入门 |
| `accessories.json` | `evolutionary_mod:accessories` | 1 | 饰品系统 |
| `combat.json` | `evolutionary_mod:combat` | 2 | 战斗与套装 |
| `loot.json` | `evolutionary_mod:loot` | 3 | 掉落与获取 |
| `systems.json` | `evolutionary_mod:systems` | 4 | 系统机制（负重等） |
| `faq.json` | `evolutionary_mod:faq` | 5 | 界面与 FAQ |

### 3.2 条目（entries）

| 文件 | 归属分类 | 条目主题 |
|------|----------|----------|
| `entries/getting_started/quickstart.json` | getting_started | 30 秒上手 |
| `entries/getting_started/gameplay_loop.json` | getting_started | 核心玩法循环 |
| `entries/accessories/slots.json` | accessories | 饰品槽位 |
| `entries/accessories/rarity.json` | accessories | 品阶与掉落池 |
| `entries/combat/attributes.json` | combat | 词条与战斗 |
| `entries/combat/sets.json` | combat | 套装效果 |
| `entries/loot/elites.json` | loot | 精英怪 |
| `entries/loot/chests.json` | loot | 箱子掉落 |
| `entries/systems/weight.json` | systems | 负重系统 |
| `entries/faq/controls.json` | faq | 界面与按键 |
| `entries/faq/troubleshooting.json` | faq | 常见问题 |

### 3.3 Lang 键命名约定

前缀一律：`patchouli.evolutionary_mod.`

| 用途 | 键格式 | 示例 |
|------|--------|------|
| 书名/封面 | `guide.name` / `guide.landing` / `guide.subtitle` | `patchouli.evolutionary_mod.guide.name` |
| 分类名 | `category.{id}` | `...category.accessories` |
| 分类描述 | `category.{id}.desc` | `...category.accessories.desc` |
| 条目标题 | `entry.{entry_id}` | `...entry.slots` |
| 页正文 | `page.{entry_id}.{n}` | `...page.slots.1` |

**必须双语同步**：改中文就改 `zh_cn.json`，同时补/改 `en_us.json`。

---

## 4. 怎么改（常见场景）

### 4.1 只改一段说明文字

1. 打开 `assets/evolutionary_mod/lang/zh_cn.json`（及 `en_us.json`）。
2. 找到对应 `patchouli.evolutionary_mod.page.*`。
3. 保存后进游戏，打开书 → **Shift+点击铅笔** 重载。

正文可用 Patchouli 格式码（常用）：

| 码 | 含义 |
|----|------|
| `$(br)` | 换行 |
| `$(br2)` | 空一行 |
| `$(l)文字$()` | 加粗 |
| `$(li)` | 列表项 |
| `$(#RRGGBB)文字$()` | 颜色 |

### 4.2 改书封面名 / 落地页介绍

改 lang：

- `patchouli.evolutionary_mod.guide.name`
- `patchouli.evolutionary_mod.guide.landing`
- `patchouli.evolutionary_mod.guide.subtitle`

外观（颜色书皮、创造标签等）改：

`data/evolutionary_mod/patchouli_books/guide/book.json`

重要字段：

| 字段 | 当前值 | 说明 |
|------|--------|------|
| `i18n` | `true` | 名称/正文走 lang |
| `use_resource_pack` | `true` | 内容必须在 assets |
| `creative_tab` | `evolutionary_mod:accessories_tab` | 出现在「饰品」创造页 |
| `show_progress` | `false` | 不显示进度条 |
| `version` | `1` | 大改内容可 +1，用于提示新版本 |

**改 `book.json` 后通常需要重启客户端**（条目内容热重载不够）。

### 4.3 改条目图标 / 排序 / 所属分类

编辑对应 `entries/**/*.json`：

```json
{
  "name": "patchouli.evolutionary_mod.entry.slots",
  "icon": "evolutionary_mod:strength_bracelet",
  "category": "evolutionary_mod:accessories",
  "sortnum": 0,
  "pages": [ ... ]
}
```

- `icon`：物品 ID，或指向 `.png` 的资源路径（贴图需以 `.png` 结尾）。
- `category`：必须是 `evolutionary_mod:{分类文件名}`。
- `sortnum`：同分类内越小越靠前。

### 4.4 给某条目多加一页

1. 在该 entry 的 `pages` 数组末尾追加：

```json
{
  "type": "patchouli:text",
  "text": "patchouli.evolutionary_mod.page.slots.2"
}
```

2. 在 `zh_cn.json` / `en_us.json` 增加键 `patchouli.evolutionary_mod.page.slots.2`。
3. Shift+点击铅笔重载。

常用页类型：

| type | 用途 |
|------|------|
| `patchouli:text` | 纯文字（当前主用） |
| `patchouli:crafting` | 显示合成配方（需 `recipe` 字段） |
| `patchouli:image` | 插图 |
| `patchouli:entity` | 展示实体 |
| `patchouli:spotlight` | 高亮某个物品 + 说明 |

合成页示例：

```json
{
  "type": "patchouli:crafting",
  "recipe": "evolutionary_mod:guide_book",
  "text": "patchouli.evolutionary_mod.page.guide_recipe.1"
}
```

---

## 5. 怎么添加（逐步清单）

### 5.1 新增一个分类

1. 新建  
   `assets/evolutionary_mod/patchouli_books/guide/en_us/categories/{new_id}.json`

```json
{
  "name": "patchouli.evolutionary_mod.category.{new_id}",
  "description": "patchouli.evolutionary_mod.category.{new_id}.desc",
  "icon": "minecraft:book",
  "sortnum": 6
}
```

2. 在 `zh_cn.json` / `en_us.json` 增加 `category.{new_id}` 与 `.desc`。
3. （可选）建 `entries/{new_id}/` 放该分类条目。
4. 热重载验证。

分类 ID = `evolutionary_mod:{new_id}`（与文件名一致，不含 `.json`）。

### 5.2 新增一个条目

1. 新建  
   `assets/.../en_us/entries/{category_folder}/{entry_id}.json`

```json
{
  "name": "patchouli.evolutionary_mod.entry.{entry_id}",
  "icon": "evolutionary_mod:dragon_ring",
  "category": "evolutionary_mod:{category_id}",
  "sortnum": 10,
  "pages": [
    {
      "type": "patchouli:text",
      "text": "patchouli.evolutionary_mod.page.{entry_id}.1"
    }
  ]
}
```

2. 双语 lang 补：
   - `entry.{entry_id}`
   - `page.{entry_id}.1`（以及更多页）
3. 热重载验证。

注意：

- 条目资源 ID 不依赖子文件夹名，但**建议子文件夹与分类同名**，方便维护。
- `category` 写错会导致条目不显示或报错。

### 5.3 新增整本书（一般不需要）

若真要第二本书：

1. `data/evolutionary_mod/patchouli_books/{book_name}/book.json`
2. `assets/evolutionary_mod/patchouli_books/{book_name}/en_us/...`
3. 新配方，result 里：

```json
"components": {
  "patchouli:book": "evolutionary_mod:{book_name}"
}
```

4. 重启游戏。

---

## 6. 获取书的方式（玩家 / 测试）

| 方式 | 说明 |
|------|------|
| 创造 | 「饰品」标签（`accessories_tab`） |
| 合成 | `minecraft:book` + `minecraft:gold_nugget` → 配方 `evolutionary_mod:guide_book` |
| 指令 | `/give @s patchouli:guide_book[patchouli:book="evolutionary_mod:guide"]` |

改合成：编辑 `data/evolutionary_mod/recipe/guide_book.json`。

---

## 7. 验证清单

发布前或大改后建议过一遍：

- [ ] 客户端能进游戏，无 Patchouli / 配方报错
- [ ] 创造栏能看到书，合成能做出书
- [ ] 打开书后 6 个分类都在，顺序符合 `sortnum`
- [ ] 切换中/英文，标题与正文都正确（非 raw key）
- [ ] 新增条目：Shift+重载后立即出现
- [ ] 改 `book.json` 后已重启验证封面/标签
- [ ] `zh_cn` / `en_us` 键一一对应，无漏译

日志排查：客户端日志搜 `patchouli` / `evolutionary_mod:guide`。

---

## 8. 常见坑

| 现象 | 原因 / 处理 |
|------|-------------|
| 书是空的 / 缺分类 | `use_resource_pack: true` 但内容不在 `assets/.../en_us/` |
| 显示 `patchouli.evolutionary_mod.xxx` 原键 | lang 漏键，或 `i18n` 未开；检查双语文件 |
| 改了 entry 没变化 | 未 Shift+点击铅笔；或改的是错误语言目录 |
| 改了 `book.json` 没变化 | 需要重启，不能只热重载 |
| 条目不出现 | `category` 写错命名空间或 ID |
| 图标是紫黑块 | 物品 ID 不存在 / 未注册 |
| 只有英文 | 游戏语言非中文，或 `zh_cn` 未覆盖对应键（本项目正文在模组 lang，随游戏语言切换） |

本项目采用 **i18n + 模组 lang**，因此：

- **不要**再在 `assets/.../zh_cn/entries/` 复制一整套条目 JSON（除非你要覆盖结构本身）。
- 翻译只维护 `lang/zh_cn.json` 与 `lang/en_us.json`。

---

## 9. 推荐工作流

1. 先在 `docs/player/游玩指南.md` 或策划里定好要写的主题。
2. 需要新分类/条目 → 按第 5 节加 JSON。
3. 文案只写进 lang（中英一起）。
4. `runClient` → 拿书 → Shift+重载迭代正文。
5. 大改封面或配方后再重启确认。
6. 同步更新本文件「§3 现有内容清单」（加了分类/条目就追加表格行）。

---

## 10. 相关文件速查

| 目标 | 改哪里 |
|------|--------|
| 书名/落地页/副标题 | `lang/*` 的 `guide.*` |
| 分类标题/描述 | `lang/*` 的 `category.*` |
| 条目标题/正文 | `lang/*` 的 `entry.*` / `page.*` |
| 分类顺序/图标 | `.../categories/*.json` |
| 条目结构/页列表 | `.../entries/**/*.json` |
| 书皮/创造标签/i18n | `data/.../book.json` |
| 合成获取 | `data/.../recipe/guide_book.json` |
| 依赖版本 | `gradle.properties` → `patchouli_version` |

精简版总手册中的交叉引用见：`docs/dev/开发者指南-精简版.md` §8。
