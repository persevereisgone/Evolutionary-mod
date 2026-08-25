# 新增「优秀 FINE」品阶修改方案 V1.1

> 状态：🚧 待实施（方案已确认 V1.1）
> 适用版本：Minecraft 1.21.1 + NeoForge（Evolutionary Mod）
> 变更点：饰品品阶由 **6 阶 → 7 阶**，新增「优秀 FINE」（`fine_` 前缀）；**原 EXCELLENT 中文名全局改名「优秀」→「精良」**；全链路接入（枚举 / 物品注册 / 掉落 / 锻造 / 数据驱动 JSON / 语言 / 贴图 / 文档）
> 决策来源（本仓库会话确认，V1.1 复审）：
> 1. **新档枚举名/前缀**：`FINE` / `fine_`；**中文名 = 优秀**，原 EXCELLENT 改名 = **精良**（色阶 白→绿→蓝→紫→橙→红）
> 2. **残破档现状保留**：愈合（healing）与破甲（armor_breaker）两种戒指**无残破档**（作者设计如此），其余 6 种有残破档；**FINE 档做满 8 系列**（与其他高品阶档一致），残破档保持现状 6 系列（详见 §二-B1）
> 3. **带品阶物品命名统一**：一律「品阶·名称」，如「残破·生机之戒」「精良·破甲之戒」
> 4. **删除未使用的旧 lang key**：`armor_break_ring` / `life_source_ring` / `firm_shield_ring` / `regeneration_ring` / `eternal_blood_ring` / `cracked_blood_ring` / `dull_iron_ring` / `damaged_guard_ring` / `slow_ring` / `dim_ring` / `immortal_ring` / `creation_blood_ring` / `divine_might_ring` / `vajra_ring` / `void_ring` / `destiny_ring` / `eternal_ring` / `annihilation_ring` / `magic_break_ring` / `true_damage_ring` / `heavy_strike_ring` 等（物品注册名已改为 `*_armor_breaker_ring` 等，旧 key 无物品引用）
> 5. **普通档统一无前缀**：`normal_sharp_edge_ring` → **`sharp_edge_ring`**（去掉 `normal_` 前缀），普通档物品名统一无前缀
> 6. **数值复审（V1.1）**：掉落权重 FINE=20 **接受**；强化系数与标准区间**按 7 档重切**（全局最高/最低不变，只改中间区间大小）；碎片权重建议值**接受**
> 关联文档：`FORGE_SYSTEM_V1.md`（锻造底座）、`COMPONENT_SYSTEM_V1.md`（组件化改造，预留 FINE）、`docs/dev/修改同步索引表.md`（强制收尾检查项）

---

## 〇、决策摘要

| 项 | 决策 |
|----|------|
| 新增品阶 | **优秀 FINE**（枚举 `FINE`，前缀 `fine_`，中文「优秀」） |
| 插入位置 | **NORMAL（普通·白）之后、EXCELLENT 之前**（枚举 ordinal=2，绿色在蓝色之前） |
| 中文名改版 | **EXCELLENT 中文名「优秀」→「精良」**（枚举名与物品前缀不变，仍 `excellent_`） |
| 品阶碎片 | `rank_shard_fine`（优秀品阶碎片）；原 `rank_shard_excellent` 中文名 →「精良品阶碎片」 |
| 掉落权重 | FINE = **20**（介于 NORMAL 30 与 EXCELLENT 12 之间）✅ 已确认 |
| 每级强化系数 | FINE = **6.0%**；全系 7 档等差 **7.0/6.5/6.0/5.5/5.0/4.5/4.0**（BROKEN 最高、MYTHIC 最低不变，仅 NORMAL 由 6.0%→6.5%、FINE 取 6.0%） |
| Tooltip 颜色 | FINE = 绿（GREEN）；EXCELLENT 保持蓝 |
| 掉落渠道 | **普通怪物池**：`allowed_rarities` 加入 `FINE`；精英池/宝箱默认不加（可后续扩展） |
| 戒指制作 | **8 个系列全部做 FINE**（与其他高品阶档一致）；**残破档保持现状仅 6 系列**——愈合/破甲从普通档起（作者设计，不补残破） |
| 普通档命名 | **统一无前缀**：`normal_sharp_edge_ring` → `sharp_edge_ring` |
| 旧 lang key | **全部删除**（无物品引用） |
| 套装 SET | 保持独立特殊品质（工具提示枚举内），不参与掉落权重 |

> ⚠️ **与 COMPONENT 文档的关系**：`COMPONENT_SYSTEM_V1.md` §4.3 预留文案为「在优秀与史诗之间插入**精良（FINE）**」。
> 本次确认的命名将其调整为「**FINE = 优秀**、EXCELLENT = 精良、位置在普通与精良之间」。实施时**必须同步修订 COMPONENT 文档预留说明**，避免两案描述矛盾（见 J2）。

---

## 一、目标品阶全表（7 阶）

| 档位 | 品阶 | 枚举 | 前缀 | 掉落权重 | 颜色 | 碎片 ID | 中文碎片名 | 每级强化系数 |
|:---:|------|------|------|:---:|------|------|------|:---:|
| 0 | 残破 | BROKEN | `broken_` | 50 | 灰 GRAY | `rank_shard_broken` | 残破品阶碎片 | **7.0%** |
| 1 | 普通 | NORMAL | （无前缀） | 30 | 白 WHITE | `rank_shard_normal` | 普通品阶碎片 | **6.5%** |
| 2 | **优秀** | **FINE** | **`fine_`** | **20** | **绿 GREEN** | **`rank_shard_fine`** | **优秀品阶碎片** | **6.0%** |
| 3 | **精良** | EXCELLENT | `excellent_` | 12 | 蓝 BLUE | `rank_shard_excellent` | **精良品阶碎片**（原优秀） | **5.5%** |
| 4 | 史诗 | EPIC | `epic_` | 5 | 紫 DARK_PURPLE | `rank_shard_epic` | 史诗品阶碎片 | **5.0%** |
| 5 | 传说 | LEGENDARY | `legendary_` | 2 | 橙/金 GOLD | `rank_shard_legendary` | 传说品阶碎片 | **4.5%** |
| 6 | 至臻 | MYTHIC | `mythic_` | 1 | 红 RED | `rank_shard_mythic` | 至臻品阶碎片 | **4.0%** |

> **强化系数设计说明（V1.1 复审）**：原 6 档系数 7.0/6.0/5.5/5.0/4.5/4.0。改为 7 档等差后 BROKEN(7.0%) 与 MYTHIC(4.0%) 不变，仅 NORMAL 由 6.0%→6.5%，FINE 取 6.0%；EXCELLENT/EPIC/LEGENDARY 保持原值。全局最高/最低不变，中间区间重新均分。

---

## 二、修改点清单（按系统分块）

> 全部路径相对于仓库根；「说明」一列为具体改动与注意事项。

### A. 核心枚举（3 个文件）

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| A1 | `src/main/java/com/muyun/evolutionary_mod/loot/AccessoryDropRarity.java` | ① 枚举插入 `FINE(20)`（NORMAL 与 EXCELLENT 之间）<br>② `registryPrefix()` 加 `case FINE -> "fine_";`<br>③ 类注释「品质对应物品注册名前缀」表补 FINE 行 | **枚举顺序 = ordinal = 强化 delta 计算依据**（§四-1）；插入位置在 NORMAL 之后，EXCELLENT/EPIC/LEGENDARY/MYTHIC ordinal 全部 +1。 |
| A2 | `src/main/java/com/muyun/evolutionary_mod/item/base/AccessoryTooltipHelper.java` | ① `AccessoryRarity` 枚举加 `FINE(GREEN)`（NORMAL 与 EXCELLENT 之间）<br>② `getRarityFromRegistryName` 加 `if (n.startsWith("fine_")) return FINE;` | `fine_` 与既有前缀无冲突；注意在 `return NORMAL` 兜底之前命中；颜色与 SET(AQUA 青) 区分。 |
| A3 | `src/main/java/com/muyun/evolutionary_mod/client/ClientHandlers.java` | `onRenderTooltip` 的 switch 加 `case FINE -> 0xFF00AA00;`（绿色，可按需调整） | 与 A2 颜色一致；漏改则优秀物品 Tooltip 边框无着色。 |

### B. 物品注册（3 个文件）

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| B1 | `src/main/java/com/muyun/evolutionary_mod/item/types/Rings.java` | 新增 8 个 `FINE_*_RING`：`fine_life_essence_ring` / `fine_battle_power_ring` / `fine_iron_shield_ring` / `fine_gale_ring` / `fine_good_fortune_ring` / `fine_healing_ring` / `fine_sharp_edge_ring` / `fine_armor_breaker_ring`（**8 个系列全部做**，与普通/精良/史诗/传说/至臻档一致）<br>另：`NORMAL_SHARP_EDGE_RING` 注册名 **`normal_sharp_edge_ring` → `sharp_edge_ring`** | 残破档保持现状 6 系列（愈合/破甲无残破，不补）；FINE 档做满 8 系列；插在 Normal 段与 Excellent 段之间；类注释「6 个品质等级」→「7 档（残破 6 系列、其余 8 系列）」；改名影响 `ranges.json`/lang/命令/创造页签（见 C4/E1/F/I）。 |
| B2 | `src/main/java/com/muyun/evolutionary_mod/item/forge/ForgeMaterials.java` | 新增 `RANK_SHARD_FINE = ITEMS.register("rank_shard_fine", ...)` | 「6 品阶碎片」注释 → 7；碎片为普通 Item（非 AccessoryItem）。 |
| B3 | `src/main/java/com/muyun/evolutionary_mod/item/tabs/ModCreativeModelTabs.java` | ① `ACCESSORIES_TAB` / `RINGS_TAB`：每系列在 NORMAL 与 EXCELLENT 之间插入 `Rings.FINE_XXX_RING.get()`（8 系列全部插入）<br>② 利刃系列引用 `NORMAL_SHARP_EDGE_RING` 改名后常量位置不变<br>③ `MATERIALS_TAB`：碎片序列插入 `ForgeMaterials.RANK_SHARD_FINE.get()` | 两个页签都要加；顺序与枚举一致（残破→普通→**优秀**→**精良**→史诗→传说→至臻）。 |

### C. 掉落系统（4 个文件）

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| C1 | `src/main/java/com/muyun/evolutionary_mod/loot/AccessoryDropTable.java` | Java 回退表：**普通怪物池（19 只原版怪）** 的 `allowed_rarities` 加入 `AccessoryDropRarity.FINE`（如 `[BROKEN, NORMAL]` → `[BROKEN, NORMAL, FINE]`） | **回退表与 drop_table.json 必须同步**（否则 JSON 未命中时回退池品阶集合不同）。 |
| C2 | `src/main/resources/data/evolutionary_mod/loot/drop_table.json` | 同上，普通怪物池（zombie/skeleton/creeper 等 19 只）的 `allowed_rarities` 加 `"FINE"` | 数据驱动主配置；与 C1 对齐。 |
| C3 | `src/main/java/com/muyun/evolutionary_mod/loot/AccessoryGlobalLootModifier.java` | `pickRandomAccessory` 中 `otherPrefixes` 数组追加 `"fine_"` | **必须改**：`otherPrefixes` 用于排除「NORMAL 无前缀」池里的带前缀物品；漏加 `fine_` 会导致优秀物品被误判进普通池。 |
| C4 | `src/main/resources/data/evolutionary_mod/loot/drop_table.template.json` | 模板示例补 FINE 相关说明/条目 | 维护模板，同步索引表已登记。 |

> 「替换部分 NORMAL/EXCELLENT 权重」落地口径：普通怪物池 `allowed_rarities` 由 `[BROKEN, NORMAL]` 扩展为 `[BROKEN, NORMAL, FINE]`；权重由枚举（50/30/20）自动归一化，等效于 NORMAL 的相对占比下降。若想进一步把部分 EXCELLENT 权重让给 FINE（精英池场景），属**可选项**，本次默认不加，见 §六。

### D. 锻造系统（7 个文件）

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| D1 | `src/main/java/com/muyun/evolutionary_mod/system/forge/ForgeSystem.java` | ① `rarityOf()`：加 `if (path.startsWith("fine_")) return AccessoryDropRarity.FINE;`<br>② `rarityOfShard()`：加 `case "evolutionary_mod:rank_shard_fine" -> AccessoryDropRarity.FINE;` | `isRankShard` 自动覆盖新碎片（依赖 `rarityOfShard`），锻造台碎片槽 `mayPlace` 自动放行。 |
| D2 | `src/main/java/com/muyun/evolutionary_mod/system/forge/ForgeConfig.java` | ① 回退 `rarityFactor` 加 `"FINE", 0.06`，且 **NORMAL 0.06 → 0.065**（与 JSON 对齐）<br>② 回退 `successBonus` 表补 **-6 与 +6** 两行（见 §四-1）<br>③ 回退 `putRange` 的 **10 组区间数组全部加第 3 个元素（FINE）**，并按 §二-D5 重切 | 此文件是 JSON 加载失败时的回退，与 D3~D5 的 JSON 必须保持同步。 |
| D3 | `src/main/resources/data/evolutionary_mod/forge/enhance_rolls.json` | `rarity_factor` 改为 7 档：`BROKEN 0.07 / NORMAL 0.065 / FINE 0.06 / EXCELLENT 0.055 / EPIC 0.05 / LEGENDARY 0.045 / MYTHIC 0.04` | 与 D2 ① 一致；`_comment` 同步更新。 |
| D4 | `src/main/resources/data/evolutionary_mod/forge/enhance_costs.json` | `success_bonus` 加 `"-6"` 与 `"6"` 两行（数值建议：-6→0.75、6→0.005，或按 §4.4.3 趋势外推） | **7 阶后 delta 范围变成 -6..+6**，原表只有 -5..+5；漏加时跨 6 阶组合会落到默认 0 加成。 |
| D5 | `src/main/resources/data/evolutionary_mod/forge/attribute_essence_ranges.json` | 10 个属性全部改为 **7 档**（插入 FINE），**全局最低（BROKEN）与最高（MYTHIC）不变**，中间区间重切，建议值见下表 | 与 D2 ③ 回退一致；文件 `_comment` 中的「10×6」→「10×7」。 |

**D5 品阶标准区间重切建议（V1.1；BROKEN 下限与 MYTHIC 上限保持原值）：**

| 属性 | BROKEN（不变） | NORMAL | FINE（新） | EXCELLENT | EPIC | LEGENDARY | MYTHIC（不变） |
|------|------|------|------|------|------|------|------|
| max_health | [1, 3] | [3, 5] | [5, 7] | [7, 10] | [10, 13] | [13, 17] | [17, 30] |
| attack_damage | [0.1, 0.5] | [0.5, 1] | [1, 1.5] | [1.5, 2.2] | [2.2, 3.2] | [3.2, 4.5] | [4.5, 9] |
| armor | [0.1, 0.5] | [0.5, 0.9] | [0.9, 1.4] | [1.4, 2] | [2, 2.8] | [2.8, 4] | [4, 8] |
| movement_speed | [0.01, 0.02] | [0.02, 0.04] | [0.04, 0.06] | [0.06, 0.09] | [0.09, 0.13] | [0.13, 0.18] | [0.18, 0.4] |
| luck | [0.05, 0.15] | [0.15, 0.3] | [0.3, 0.5] | [0.5, 0.8] | [0.8, 1.2] | [1.2, 2] | [2, 5] |
| health_regen | [0.05, 0.12] | [0.12, 0.2] | [0.2, 0.3] | [0.3, 0.4] | [0.4, 0.6] | [0.6, 0.8] | [0.8, 1.5] |
| armor_penetration | [0.1, 0.3] | [0.3, 0.5] | [0.5, 0.8] | [0.8, 1.2] | [1.2, 1.8] | [1.8, 2.5] | [2.5, 4] |
| crit_chance | [0.005, 0.015] | [0.015, 0.025] | [0.025, 0.04] | [0.04, 0.06] | [0.06, 0.08] | [0.08, 0.12] | [0.12, 0.2] |
| crit_damage | [0.01, 0.05] | [0.05, 0.08] | [0.08, 0.12] | [0.12, 0.18] | [0.18, 0.28] | [0.28, 0.4] | [0.4, 0.55] |
| damage_reduction | [0.005, 0.01] | [0.01, 0.015] | [0.015, 0.025] | [0.025, 0.04] | [0.04, 0.06] | [0.06, 0.09] | [0.09, 0.15] |

> 上表为**建议值**：保证每档区间单调递增、相邻档衔接，且全局最低/最高与旧值一致。实施时可微调，但需保持 BROKEN 下限、MYTHIC 上限不变。

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| D6 | `src/main/resources/data/evolutionary_mod/forge/material_drops.json` | `subtype_weights.shard` 由 6 个权重改为 7 个（建议：broken 6 / normal 5 / **fine 4** / excellent 3 / epic 2 / legendary 1 / mythic 1，或按 §六待定） | 权重方向：越高品阶越低；FINE 应介于普通与精良之间。 |
| D7 | `src/main/resources/data/evolutionary_mod/loot_tables/forge/elite_materials_t{1,2,3}.json` | 三个文件的碎片池（shard pool）各加 `rank_shard_fine` 条目并调整权重 | 与 D6 权重口径一致；只改碎片池，不动精华/重锻石池。 |

> D6/D7 同步时，`ForgeMaterialDrops.java` 的 `FALLBACK_SHARD_WEIGHTS`（`src/main/java/com/muyun/evolutionary_mod/loot/ForgeMaterialDrops.java`）也要加 `rank_shard_fine` 回退权重。

### E. 数据驱动词条区间（1 个文件）

| # | 文件 | 改动 | 注意事项 |
|---|------|------|----------|
| E1 | `src/main/resources/data/evolutionary_mod/attributes/ranges.json` | ① 新增 8 个 `fine_*_ring` 条目（`"rarity": "FINE"`，词条区间介于 NORMAL 与 EXCELLENT 之间，建议值见下）<br>② **`normal_sharp_edge_ring` → `sharp_edge_ring`**（键名去 `normal_` 前缀） | 漏加则优秀戒指掉落时**无词条**（`rollFromDataDriven` 未命中 → 走代码通用回退，数值不符品阶）；`sharp_edge_ring` 键名与 B1 注册名同步。 |

**E1 词条区间建议（V1.1；介于该系列 NORMAL 与 EXCELLENT 之间）：**

| 物品 | 词条建议区间 |
|------|------|
| fine_life_essence_ring | max_health [5, 8]；health_regen per_second [0.2, 0.45] |
| fine_battle_power_ring | attack_damage [1, 2.2] |
| fine_iron_shield_ring | armor [1, 2.2]；damage_reduction [0.005, 0.015] |
| fine_gale_ring | movement_speed [0.04, 0.09] |
| fine_good_fortune_ring | luck [0.3, 0.7] |
| fine_healing_ring | health_regen per_second [0.25, 0.5] |
| fine_sharp_edge_ring | crit_chance [0.025, 0.05]；crit_damage [0.08, 0.15] |
| fine_armor_breaker_ring | armor_penetration [0.5, 0.9] |

### F. 语言文件（2 个文件）★ 含中文名改名 + 旧 key 清理

| # | 文件 | 改动 |
|---|------|------|
| F1 | `src/main/resources/assets/evolutionary_mod/lang/zh_cn.json` | ① 新增 8 个 `item.evolutionary_mod.fine_*_ring` + `.description`（如「优秀·生机之戒」）<br>② 新增 `item.evolutionary_mod.rank_shard_fine` = 「优秀品阶碎片」<br>③ **改名：所有 `excellent_*` 物品中文名「优秀·xxx」→「精良·xxx」**（8 个戒指）；`rank_shard_excellent` = 「优秀品阶碎片」→「**精良品阶碎片**」<br>④ **统一带品阶命名**：全部「品阶·名称」格式（已符合，复核即可）<br>⑤ `normal_sharp_edge_ring` → `sharp_edge_ring` key（普通·锋芒之戒 无前缀）<br>⑥ **删除全部无引用旧 key**（§〇 决策 4 清单：armor_break_ring / heavy_strike_ring / magic_break_ring / true_damage_ring / annihilation_ring 等）<br>⑦ 引导书 `patchouli.evolutionary_mod.page.rarity.1` 改为「破损 / 普通 / **优秀** / **精良** / 史诗 / 传说 / 至臻」（顺带统一「破损」→「残破」）<br>⑧ `page.materials.1` 如提到品阶数量同步 |
| F2 | `src/main/resources/assets/evolutionary_mod/lang/en_us.json` | ① 新增 8 个 `fine_*_ring` 英文（`Fine·xxx Ring`）<br>② 新增 `rank_shard_fine`（`Fine Rank Shard`）<br>③ rarity 页 `Broken / Normal / Fine / Excellent / Epic / Legendary / Mythic`<br>④ `normal_sharp_edge_ring` → `sharp_edge_ring` key<br>⑤ **删除全部无引用旧 key**（§〇 决策 4 清单：cracked_blood_ring / dull_iron_ring / damaged_guard_ring / slow_ring / dim_ring / life_source_ring / firm_shield_ring / regeneration_ring / armor_break_ring / eternal_blood_ring / immortal_ring / creation_blood_ring / divine_might_ring / vajra_ring / void_ring / destiny_ring / eternal_ring / annihilation_ring / magic_break_ring / true_damage_ring 等）<br>⑥ 英文 `Excellent·` 前缀无需改名（枚举名就是 EXCELLENT） |

> 英文无需改名；**中文改名仅影响 zh_cn.json**，但引导书 zh_cn 文案、命令文案、文档都要同步（见 I1 / J 各节）。

### G. 模型与贴图（新增 ~18 个文件）

| # | 位置 | 新增内容 | 注意事项 |
|---|------|----------|----------|
| G1 | `assets/evolutionary_mod/models/item/` | 8 个 `fine_*_ring.json` + `rank_shard_fine.json`；另确认 `sharp_edge_ring.json` 存在（改名后复用，删除 `normal_sharp_edge_ring.json`） | 结构参考现有 `excellent_life_essence_ring.json` / `rank_shard_epic.json` |
| G2 | `assets/evolutionary_mod/textures/item/` | 对应 8 张戒指贴图 + `rank_shard_fine.png`；`sharp_edge_ring.png` 如无则新建 | 贴图风格与现有品阶体系一致（如颜色偏绿系）；无贴图则游戏内灰紫方块图标。 |

### H. 引导书（Patchouli，1 个条目文件 + lang 已列于 F）

| # | 文件 | 改动 |
|---|------|------|
| H1 | `src/main/resources/assets/evolutionary_mod/patchouli_books/guide/en_us/entries/accessories/rarity.json` | 若页面内容引用了品阶列表文案则更新（当前为引用 lang key，主要改 F1/F2） |
| H2 | `src/main/resources/assets/evolutionary_mod/patchouli_books/guide/en_us/entries/systems/materials.json` | 若材料页提到碎片与品阶一一对应（6 种），补 FINE |

### I. 命令（1 个文件）

| # | 文件 | 改动 |
|---|------|------|
| I1 | `src/main/java/com/muyun/evolutionary_mod/command/AccessoryCommands.java` | ① `showAccessoryStats` 的「支持品阶：破损、普通、优秀、史诗、传说、至臻」→「残破、普通、**优秀**、**精良**、史诗、传说、至臻」（统一「破损」→「残破」）<br>② `getRarityFromName` 加 `if (name.startsWith("fine_")) return "优秀";`，`excellent_` 分支返回由「优秀」改为「**精良**」，`broken_` 返回「**残破**」<br>③ `writePoolData` 中的 `normal_sharp_edge_ring` → `sharp_edge_ring`；按需补 fine 物品（可选） |

### J. 文档（7 个文件）

| # | 文件 | 改动 |
|---|------|------|
| J1 | `docs/planning/system/FORGE_SYSTEM_V1.md` | 「六品阶」→「七品阶」；§3.2 品阶表插入 FINE 行且「优秀 EXCELLENT」改「精良 EXCELLENT」；§4.4.2 品阶索引插入 FINE；§4.4.4 全量表 36 行 → 49 行（物品×材料=7×7）；§8.2 碎片权重 6/5/4/3/2/1 → 7 档；§13 平衡锚点品阶系数表插入 FINE（优秀）并改 EXCELLENT 为精良、系数 7 档重切；§〇 物品清单碎片 6 → 7 |
| J2 | `docs/planning/system/COMPONENT_SYSTEM_V1.md` | **修订 §4.3 预留说明**：原「在优秀与史诗之间插入精良（FINE）」→「FINE=优秀、EXCELLENT=精良、插入位置为普通与精良之间」；槽位表插入 FINE 行（自身档位基准 T2.5 或并入相邻档，视 T1~T7 体系取舍）；组件档位门槛相关表述 |
| J3 | `docs/player/游玩指南.md` | 「品阶（稀有度）」列表插入 **优秀** 与 **精良**（原「优秀」改「精良」，优秀为新档）；「破损」→「残破」 |
| J4 | `docs/dev/修改同步索引表.md` | 品阶枚举行补 FINE 联动项（已部分覆盖，见 §四-3） |
| J5 | `docs/dev/开发者指南-精简版.md` | 若提到品阶列表/碎片数量则同步 |
| J6 | `docs/README.md` | 策划案目录登记本方案文档（替换/并列 FINE 方案） |
| J7 | `docs/progress/forge-system.md` | 进度文档补一行「新增优秀品阶」待办（可选） |

---

## 三、分步实施顺序（建议）

| 步骤 | 内容 | 涉及 |
|:---:|------|------|
| 1 | 枚举 + 工具提示 + 客户端颜色 | A1 A2 A3 |
| 2 | 物品注册 + 碎片 + 创造页签 + `sharp_edge_ring` 改名 | B1 B2 B3 |
| 3 | 掉落池（JSON + Java 回退 + 前缀过滤） | C1 C2 C3 C4 |
| 4 | 锻造逻辑 + 数值配置（系数 7 档/成功率 ±6/标准区间 7 档/碎片权重） | D1~D7 + `ForgeMaterialDrops.java` |
| 5 | 词条区间（含 `sharp_edge_ring` 键名） | E1 |
| 6 | 语言（**含中文改名 + 旧 key 清理**）+ 引导书 + 模型贴图 | F1 F2 G1 G2 H1 H2 |
| 7 | 命令 + 文档 | I1 J1~J7 |
| 8 | 验证 | §五 |

---

## 四、注意事项（易漏点 / 联动风险）

1. **枚举 ordinal = 强化 delta 核心（最关键）**：`ForgeSystem.enhance` 用 `itemRarity.ordinal() - shardRarity.ordinal()` 查 `success_bonus`。插入 FINE（在 NORMAL 之后）后：EXCELLENT/EPIC/LEGENDARY/MYTHIC 的 ordinal 全部 +1 → **新旧存档的碎片-物品跨档组合 delta 全部变化**，属预期但需在 `enhance_costs.json` 和 `ForgeConfig` 回退表补 **-6 / +6** 两行，否则跨 6 阶（残破←至臻、至臻←残破）无加成。
2. **`putRange` 按枚举序填数组（极易错位）**：`ForgeConfig.loadFallbackEssenceRanges` 的 `putRange` 按 `AccessoryDropRarity.values()` 顺序填充，**10 组回退区间数组必须全部在第 3 位插入 FINE 元素并按 D5 重切**；漏补会整表错位（EXCELLENT 及以后套到错误区间）。
3. **品阶枚举联动面**（`docs/dev/修改同步索引表.md` §2 已有约束）：改 `AccessoryDropRarity` 必须同步 ① Tooltip 颜色枚举 ② 物品注册前缀 ③ `AccessoryGlobalLootModifier.pickRandomAccessory` ④ 实体/宝箱品阶池 ⑤ `ForgeSystem.rarityOf` / `rarityOfShard` ⑥ `ForgeConfig` 回退。
4. **中文名改名波及面（本次新增风险点）**：「优秀」从 EXCELLENT 移到 FINE，凡是写死「优秀」的地方都要核对：
   - `zh_cn.json`：8 个 `excellent_*` 戒指「优秀·」→「精良·」；`rank_shard_excellent` 中文名；引导书 rarity/materials 页文案
   - `AccessoryCommands`：「支持品阶」文案 + `getRarityFromName` 返回值
   - `游玩指南.md`、`FORGE_SYSTEM_V1.md`、`COMPONENT_SYSTEM_V1.md` 等文档
   - 英文端不受影响（枚举名即英文）
5. **`otherPrefixes` 必须加 `fine_`**：否则 NORMAL 池筛选会把 `fine_*` 物品当作无前缀普通物品放进普通掉落池（A/B 类问题，直接影响掉落正确性）。
6. **回退与 JSON 双写**：`drop_table.json` ↔ `AccessoryDropTable.java`、`attribute_essence_ranges.json` ↔ `ForgeConfig`、`enhance_rolls/costs.json` ↔ `ForgeConfig`、`material_drops.json` ↔ `ForgeMaterialDrops.java`，四处 JSON/Java 回退必须同步，否则 strict 模式或 JSON 缺失时行为不一致。
7. **新物品四件套**：`fine_*_ring` 8 个物品必须同时有 注册 + 模型 + 纹理 + lang，缺一即灰图标或原名。
8. **`ranges.json` 缺条目 → 优秀戒指无词条**：词条区间 E1 不补则掉落时回退到通用区间，数值与品阶不匹配。
9. **SET 品质不参与**：工具提示枚举中的 `SET` 是套装专属特殊品质，本次新增的 FINE 是掉落品阶，两者互不混淆。
10. **COMPONENT 文档预留名冲突**：`COMPONENT_SYSTEM_V1.md` §4.3 的「精良（FINE）」与本方案「FINE=优秀、EXCELLENT=精良」相反，**必须同步修订**（J2），否则两案打架。
11. **`normal_sharp_edge_ring` 改名 `sharp_edge_ring`（V1.1 新风险）**：影响 注册名 + `ranges.json` 键名 + 双语 lang + `AccessoryCommands.writePoolData` + 模型（删 `normal_sharp_edge_ring.json`，复用 `sharp_edge_ring.json`）。**旧存档中的 `normal_sharp_edge_ring` 物品将失效**（物品 ID 变化，原物品变空气/丢失）——测试档可接受，正式发布前需提醒。
12. **旧 lang key 清理**：`zh_cn.json` / `en_us.json` 中无物品引用的旧命名 key 必须删除（§〇 决策 4 清单），避免改名后新旧 key 并存造成显示混乱；**删除前确认无代码引用**（`rg "armor_break_ring|life_source_ring|..." src` 应为空）。
13. **旧存档兼容**：品阶存储在物品注册名前缀（`fine_` 新物品），旧档不自动把老装备「升级」成优秀；锻造 `FORGE_ENHANCEMENT` 组件结构不变。

---

## 五、验证清单

- [ ] `gradlew build` 编译通过，无 `AccessoryDropRarity`/Tooltip 相关报错
- [ ] `rg "normal_sharp_edge_ring" src` 无结果（已全部改为 `sharp_edge_ring`）
- [ ] `rg "armor_break_ring|life_source_ring|firm_shield_ring|regeneration_ring|eternal_blood_ring|cracked_blood_ring|dull_iron_ring|damaged_guard_ring|slow_ring|dim_ring|immortal_ring|creation_blood_ring|divine_might_ring|vajra_ring|void_ring|destiny_ring|eternal_ring|annihilation_ring|magic_break_ring|true_damage_ring|heavy_strike_ring" src` 无结果（旧 key 已清）
- [ ] `/acc stats` 显示品阶含「优秀」「精良」且顺序正确、无「破损」（统一「残破」）
- [ ] `/acc drop query minecraft:entities/zombie` 输出 `allowed_rarities` 含 FINE
- [ ] `/acc attr query evolutionary_mod:fine_life_essence_ring` 命中 JSON（非回退）
- [ ] 游戏内创造模式：两个饰品页签出现优秀戒指，材料页签出现优秀碎片，顺序正确；利刃系列普通档显示 `sharp_edge_ring`（无前缀）
- [ ] 优秀戒指 Tooltip 名称/边框为绿色系；精良（excellent_）戒指仍为蓝色系且中文名显示「精良·」
- [ ] 词条数值：优秀介于普通与精良之间；`attribute_essence_ranges.json` 各属性 BROKEN 下限 / MYTHIC 上限与旧值一致
- [ ] 锻造台：放入 `fine_*` 戒指 + `rank_shard_fine` 可正常强化；跨档（如残破 + 至臻碎片、至臻 + 残破碎片）成功率符合新表
- [ ] 粉碎优秀戒指返还 `rank_shard_fine` 正常
- [ ] 掉落测试：普通怪（zombie 等）有概率掉 `fine_*` 物品，且普通池不掉 `excellent_*` 以上品质；`fine_*` 不误入 NORMAL 池
- [ ] `material_drops.json` / `elite_materials_t{1,2,3}.json` 可掉 `rank_shard_fine`
- [ ] 双语 lang 无缺失（F1/F2 对照补全）；`excellent_*` 中文名全部改为「精良」；旧 key 全部清除
- [ ] 引导书「品阶与掉落池」页面显示 7 品阶（优秀/精良顺序正确）

---

## 六、后续决策待定（不阻塞实施）

| 项 | 现状 | 建议 |
|----|------|------|
| FINE 掉落权重 | 20 ✅ 已确认 | — |
| FINE 强化系数 | 6.0%（等差 7 档） ✅ 已确认 | 全局 7.0%→4.0% 不变 |
| FINE 标准区间 / 词条区间 | 本文 §二-D5 / §二-E1 建议值 | 保持 BROKEN 下限 / MYTHIC 上限不变；实施时微调区间宽度 |
| FINE 品阶碎片权重 | 建议 fine=4 | 方案：broken 6 / normal 5 / fine 4 / excellent 3 / epic 2 / legendary 1 / mythic 1；或保持原 6/5/4/3/2/1 后插入 fine=4 并下移，两者皆可，实施时定 |
| FINE Tooltip 颜色 | 绿色 | GREEN，与白/蓝区分 |
| 精英池 / 宝箱是否出 FINE | 默认不加 | 「替换部分 EXCELLENT 权重」如需扩展到精英池，加 C1/C2 对应条目即可，结构已支持 |
| 组件化体系（COMPONENT_SYSTEM）槽位表 | T1~T5 档位（改版中 T1~T7） | FINE 可映射为「T2.5」或与 NORMAL 同档基准，实施时按组件案取舍 |
