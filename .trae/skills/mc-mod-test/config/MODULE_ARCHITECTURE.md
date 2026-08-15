# Evolutionary Mod 模块架构

> 📅 更新时间: 2026-05-28
> 🔧 配置版本: 2.0.1

## 📊 总览

| 指标 | 数值 |
|------|------|
| 总模块数 | 16 |
| 已启用测试 | 15 |
| 已禁用测试 | 1 |
| 总文件数 | 96 |

---

## ✅ 已启用测试的模块

### 🟢 1. 负重系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.weight` |
| 文件数 | 3 |
| 测试重点 | `getTotalWeight`, `getMaxWeight`, `getSpeedPenalty`, `getHungerMultiplier` |

**文件列表**:
- `WeightSystem.java`
- `WeightConfig.java`
- `WeightEventHandler.java`

---

### 🟢 2. 饰品系统

| 项目 | 内容 |
|------|------|
| 包路径 | `item` |
| 文件数 | 13 |
| 测试重点 | 饰品注册、属性加成、槽位管理 |

**文件列表**:
- `base/AccessoryItem.java`
- `base/AccessoryAttributes.java`
- `base/AccessoryTooltipHelper.java`
- `base/GeneralAccessoryItem.java`
- `core/AccessorySlot.java`
- `core/AccessoryRules.java`
- `capability/PlayerAccessories.java`
- `capability/PlayerAccessoriesItemHandler.java`
- `capability/AccessoryCapabilityProvider.java`
- `registry/AccessoryRegistry.java`
- `registry/ModItems.java`
- `sets/DragonItems.java`
- `tabs/ModCreativeModelTabs.java`

---

### 🟢 3. 饰品效果系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.effects` |
| 文件数 | 12 |
| 测试重点 | 各槽位效果处理器 |

**文件列表**:
- `AbstractAccessoryEffectHandler.java`
- `AccessoryEffectCalculator.java`
- `BeltEffectsHandler.java`
- `BraceletEffectsHandler.java`
- `EarringEffectsHandler.java`
- `GloveEffectsHandler.java`
- `HeadwearEffectsHandler.java`
- `NecklaceEffectsHandler.java`
- `RingEffectsHandler.java`
- `ShoulderEffectsHandler.java`
- `AnkletEffectsHandler.java`
- `CacheManager.java`

---

### 🟢 4. 套装系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.sets` |
| 文件数 | 7 |
| 测试重点 | 套装判定、套装奖励触发 |

**文件列表**:
- `SetSystem.java`
- `SetBonus.java`
- `SetType.java`
- `SetBalanceConfig.java`
- `SetEffectHandler.java`
- `DragonSetBonus.java`
- `handlers/DragonSetEffectHandler.java`

---

### 🟢 5. 战斗系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.combat` |
| 文件数 | 3 |
| 测试重点 | 暴击、护甲穿透、伤害减免 |

**文件列表**:
- `CritSystem.java`
- `ArmorPenetrationSystem.java`
- `DamageReductionSystem.java`

---

### 🟢 6. 元素系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.elements` |
| 文件数 | 4 |
| 测试重点 | 元素属性、光环 |

**文件列表**:
- `ElementSystem.java`
- `ElementType.java`
- `ElementEvents.java`
- `ElementAura.java`

---

### 🟢 7. 法师属性系统

| 项目 | 内容 |
|------|------|
| 包路径 | `system.mage` |
| 文件数 | 2 |
| 测试重点 | 法师属性计算 |

**文件列表**:
- `MageAttributeSystem.java`
- `MageAttributeEvents.java`

---

### 🟢 8. 客户端处理

| 项目 | 内容 |
|------|------|
| 包路径 | `client` |
| 文件数 | 6 |
| 测试重点 | 事件注册、UI渲染、按键输入 |

**文件列表**:
- `ClientHandlers.java`
- `ClientInputEvents.java`
- `AccessoryScreen.java`
- `AccessoryContainerScreen.java`
- `PlayerAttributesScreen.java`
- `LayoutConfig.java`

---

### 🟢 9. 网络通信

| 项目 | 内容 |
|------|------|
| 包路径 | `network` |
| 文件数 | 6 |
| 测试重点 | 客户端-服务器同步 |

**文件列表**:
- `NetworkHandler.java`
- `AccessorySyncS2CPayload.java`
- `AccessoryOpenMenuC2SPayload.java`
- `AccessoryAutoEquipC2SPayload.java`
- `AccessorySlotModifyC2SPayload.java`
- `AccessoryEquipFromInvC2SPayload.java`

---

### 🟢 10. 战利品系统

| 项目 | 内容 |
|------|------|
| 包路径 | `loot` |
| 文件数 | 5 |
| 测试重点 | 掉落表、随机属性 |

**文件列表**:
- `AccessoryDropTable.java`
- `AccessoryDropRarity.java`
- `AccessoryGlobalLootModifier.java`
- `AttributeRollRanges.java`
- `AttributeRollerEvents.java`

---

### 🟢 11. 实体系统

| 项目 | 内容 |
|------|------|
| 包路径 | `entity` |
| 文件数 | 2 |
| 测试重点 | 实体注册 |

**文件列表**:
- `EliteEntityEvents.java`
- `ModEntities.java`

---

### 🟢 12. 方块系统

| 项目 | 内容 |
|------|------|
| 包路径 | `block` |
| 文件数 | 2 |
| 测试重点 | 方块注册 |

**文件列表**:
- `ModBlocks.java`
- `AccessoriesTableBlock.java`

---

### 🟢 13. 菜单系统

| 项目 | 内容 |
|------|------|
| 包路径 | `menu` |
| 文件数 | 2 |
| 测试重点 | 菜单注册 |

**文件列表**:
- `AccessoryMenu.java`
- `ModMenus.java`

---

### 🟢 14. 核心与主类

| 项目 | 内容 |
|------|------|
| 包路径 | (根目录) |
| 文件数 | 5 |
| 测试重点 | 模组初始化 |

**文件列表**:
- `EvolutionaryMod.java`
- `EvolutionaryModClient.java`
- `AccessoryEvents.java`
- `AccessoryAttributeHandler.java`
- `Config.java`

---

### 🟢 15. 工具类

| 项目 | 内容 |
|------|------|
| 包路径 | `tools` |
| 文件数 | 1 |
| 测试重点 | 布局辅助 |

**文件列表**:
- `LayoutHelper.java`

---

## ⏭️ 已禁用测试的模块

### 🔴 1. 命令系统

| 项目 | 内容 |
|------|------|
| 包路径 | `command` |
| 文件数 | 1 |
| 禁用原因 | `enabled: false` |

**文件列表**:
- `AccessoryCommands.java`

---

## 🖥️ UI 元素

### 已启用

| UI名称 | Screen类 | Menu类 | 打开按键 |
|--------|----------|--------|----------|
| 饰品界面 | AccessoryScreen.java | AccessoryMenu.java | K |
| 饰品容器界面 | AccessoryContainerScreen.java | AccessoryMenu.java | - |
| 玩家属性界面 | PlayerAttributesScreen.java | - | P |

---

## 👾 实体

| 实体名称 | 包路径 | 状态 |
|----------|--------|------|
| 精英怪物 | `entity.monster` | ✅ 已启用 (15个实体) |

---

## 📁 完整文件清单

| 状态 | 模块 | 文件 | 路径 |
|------|------|------|------|
| ✅ | 负重系统 | WeightSystem.java | system/weight/ |
| ✅ | 负重系统 | WeightConfig.java | system/weight/ |
| ✅ | 负重系统 | WeightEventHandler.java | system/weight/ |
| ✅ | 饰品系统 | AccessoryItem.java | item/base/ |
| ✅ | 饰品系统 | AccessoryAttributes.java | item/base/ |
| ✅ | 饰品系统 | AccessoryTooltipHelper.java | item/base/ |
| ✅ | 饰品系统 | GeneralAccessoryItem.java | item/base/ |
| ✅ | 饰品系统 | AccessorySlot.java | item/core/ |
| ✅ | 饰品系统 | AccessoryRules.java | item/core/ |
| ✅ | 饰品系统 | PlayerAccessories.java | item/capability/ |
| ✅ | 饰品系统 | PlayerAccessoriesItemHandler.java | item/capability/ |
| ✅ | 饰品系统 | AccessoryCapabilityProvider.java | item/capability/ |
| ✅ | 饰品系统 | AccessoryRegistry.java | item/registry/ |
| ✅ | 饰品系统 | ModItems.java | item/registry/ |
| ✅ | 饰品系统 | DragonItems.java | item/sets/ |
| ✅ | 饰品系统 | ModCreativeModelTabs.java | item/tabs/ |
| ✅ | 饰品效果系统 | AbstractAccessoryEffectHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | AccessoryEffectCalculator.java | system/effects/ |
| ✅ | 饰品效果系统 | BeltEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | BraceletEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | EarringEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | GloveEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | HeadwearEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | NecklaceEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | RingEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | ShoulderEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | AnkletEffectsHandler.java | system/effects/ |
| ✅ | 饰品效果系统 | CacheManager.java | system/effects/ |
| ✅ | 套装系统 | SetSystem.java | system/sets/ |
| ✅ | 套装系统 | SetBonus.java | system/sets/ |
| ✅ | 套装系统 | SetType.java | system/sets/ |
| ✅ | 套装系统 | SetBalanceConfig.java | system/sets/ |
| ✅ | 套装系统 | SetEffectHandler.java | system/sets/ |
| ✅ | 套装系统 | DragonSetBonus.java | system/sets/ |
| ✅ | 套装系统 | DragonSetEffectHandler.java | system/sets/handlers/ |
| ✅ | 战斗系统 | CritSystem.java | system/combat/ |
| ✅ | 战斗系统 | ArmorPenetrationSystem.java | system/combat/ |
| ✅ | 战斗系统 | DamageReductionSystem.java | system/combat/ |
| ✅ | 元素系统 | ElementSystem.java | system/elements/ |
| ✅ | 元素系统 | ElementType.java | system/elements/ |
| ✅ | 元素系统 | ElementEvents.java | system/elements/ |
| ✅ | 元素系统 | ElementAura.java | system/elements/ |
| ✅ | 法师属性系统 | MageAttributeSystem.java | system/mage/ |
| ✅ | 法师属性系统 | MageAttributeEvents.java | system/mage/ |
| ✅ | 客户端处理 | ClientHandlers.java | client/ |
| ✅ | 客户端处理 | ClientInputEvents.java | client/ |
| ✅ | 客户端处理 | AccessoryScreen.java | client/ |
| ✅ | 客户端处理 | AccessoryContainerScreen.java | client/ |
| ✅ | 客户端处理 | PlayerAttributesScreen.java | client/ |
| ✅ | 客户端处理 | LayoutConfig.java | client/ |
| ✅ | 网络通信 | NetworkHandler.java | network/ |
| ✅ | 网络通信 | AccessorySyncS2CPayload.java | network/ |
| ✅ | 网络通信 | AccessoryOpenMenuC2SPayload.java | network/ |
| ✅ | 网络通信 | AccessoryAutoEquipC2SPayload.java | network/ |
| ✅ | 网络通信 | AccessorySlotModifyC2SPayload.java | network/ |
| ✅ | 网络通信 | AccessoryEquipFromInvC2SPayload.java | network/ |
| ✅ | 战利品系统 | AccessoryDropTable.java | loot/ |
| ✅ | 战利品系统 | AccessoryDropRarity.java | loot/ |
| ✅ | 战利品系统 | AccessoryGlobalLootModifier.java | loot/ |
| ✅ | 战利品系统 | AttributeRollRanges.java | loot/ |
| ✅ | 战利品系统 | AttributeRollerEvents.java | loot/ |
| ✅ | 实体系统 | EliteEntityEvents.java | entity/ |
| ✅ | 实体系统 | ModEntities.java | entity/ |
| ✅ | 方块系统 | ModBlocks.java | block/ |
| ✅ | 方块系统 | AccessoriesTableBlock.java | block/ |
| ✅ | 菜单系统 | AccessoryMenu.java | menu/ |
| ✅ | 菜单系统 | ModMenus.java | menu/ |
| ✅ | 核心与主类 | EvolutionaryMod.java | / |
| ✅ | 核心与主类 | EvolutionaryModClient.java | / |
| ✅ | 核心与主类 | AccessoryEvents.java | / |
| ✅ | 核心与主类 | AccessoryAttributeHandler.java | / |
| ✅ | 核心与主类 | Config.java | / |
| ✅ | 工具类 | LayoutHelper.java | tools/ |
| ⏭️ | 命令系统 | AccessoryCommands.java | command/ |

---

## 🔧 API 兼容性检查

| 旧API (弃用) | 新API | 状态 |
|--------------|-------|------|
| `AttributeModifier.Operation.ADDITION` | `ADD_VALUE` | ⚠️ 需检查 |
| `AttributeModifier.Operation.MULTIPLY_TOTAL` | `ADD_MULTIPLIED_TOTAL` | ⚠️ 需检查 |
| `event.setCanceled(true)` | 新事件处理方式 | ⚠️ 需检查 |

---

> 💡 **提示**: 如需修改测试配置，请编辑 `.trae/skills/mc-mod-test/config/mod_structure.json`
