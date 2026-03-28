package com.muyun.evolutionary_mod;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.system.effects.AccessoryEffectCalculator;
import com.muyun.evolutionary_mod.system.effects.AnkletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BeltEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BraceletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.EarringEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.GloveEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.HeadwearEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.NecklaceEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.RingEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.ShoulderEffectsHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 饰品属性处理器 - Accessory Attribute Handler
 *
 * 负责处理饰品栏中物品装备/卸下时的属性效果应用与移除。
 * 从ItemStack的NBT中读取随机属性值并应用相应的AttributeModifier。
 *
 * Handles attribute effects when items are equipped/unequipped in accessory slots.
 * Reads random attribute values from ItemStack NBT and applies corresponding AttributeModifiers.
 */
@EventBusSubscriber(modid = EvolutionaryMod.MODID)
public class AccessoryAttributeHandler {

    // 为每个饰品栏位和属性定义唯一的ID - Define unique IDs for each accessory slot and attribute
    private static final Map<String, ResourceLocation> ATTRIBUTE_IDS = new HashMap<>();

    static {
        // 为每个饰品栏位生成稳定且合法的UUID - generate stable, valid UUIDs per slot+attribute
        for (AccessorySlot slot : AccessorySlot.values()) {
            String slotName = slot.name().toLowerCase();

                String[] attrs = new String[] {
                "max_health",
                "attack_damage",
                "armor",
                "movement_speed",
                "luck",
                "armor_penetration",
                "crit_chance",
                "crit_damage",
                "damage_reduction",
                "intelligence",
                "spirit",
                "mana",
                "elemental_affinity"
            };

            for (String attr : attrs) {
                String key = slotName + "_" + attr;
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, key);
                ATTRIBUTE_IDS.put(key, id);
            }
        }
    }

    /**
     * 监听玩家登录事件，应用已装备饰品的属性效果 - Listen for player login event and apply equipped accessory effects
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        // 延迟到下一 tick 执行，确保 Attachment 数据已从 NBT 反序列化完毕
        serverPlayer.getServer().execute(() -> applyAllAccessoryEffects(serverPlayer));
    }

    /**
     * 监听玩家重生事件，重新应用饰品效果 - Listen for player respawn event and reapply accessory effects
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        serverPlayer.getServer().execute(() -> applyAllAccessoryEffects(serverPlayer));
    }

    /**
     * 应用所有已装备饰品的属性效果 - Apply all equipped accessory effects
     */
    private static void applyAllAccessoryEffects(Player player) {
        if (player.level().isClientSide) return;
        AccessoryEffectCalculator.calculateAndApplyAllEffects(player);
    }

    /**
     * 从ItemStack应用属性效果 - Apply attribute effects from ItemStack
     */
    private static void applyAccessoryEffects(Player player, ItemStack stack, AccessorySlot slot) {
        // 1.21+ 的 ItemStack NBT API 已大改，为简化迁移，这里暂时只应用“默认效果”
        // Random NBT-based attributes are temporarily disabled; only default effects are applied.
        applyDefaultEffects(player, stack.getItem(), slot);
    }

    /**
     * 移除ItemStack的属性效果 - Remove attribute effects from ItemStack
     */
    private static void removeAccessoryEffects(Player player, ItemStack stack, AccessorySlot slot) {
        String slotName = slot.name().toLowerCase();

        // 移除基础属性modifier - Remove base attribute modifiers
        removeAttributeModifier(player, Attributes.MAX_HEALTH, ATTRIBUTE_IDS.get(slotName + "_max_health"));
        removeAttributeModifier(player, Attributes.ATTACK_DAMAGE, ATTRIBUTE_IDS.get(slotName + "_attack_damage"));
        removeAttributeModifier(player, Attributes.ARMOR, ATTRIBUTE_IDS.get(slotName + "_armor"));
        removeAttributeModifier(player, Attributes.MOVEMENT_SPEED, ATTRIBUTE_IDS.get(slotName + "_movement_speed"));
        removeAttributeModifier(player, Attributes.LUCK, ATTRIBUTE_IDS.get(slotName + "_luck"));

        // 如果移除的物品有默认效果，需要重新计算相应系统的效果
        // If the removed item has default effects, need to recalculate effects for the corresponding system
        Item item = stack.getItem();
        
        // 检查物品类型并重新计算相应系统的效果
        // Check item type and recalculate effects for the corresponding system
        // 对于所有饰品类型，使用新的处理器实例来重新计算效果
        // For all accessory types, use new handler instances to recalculate effects
        RingEffectsHandler.getInstance().applyEffects(player);
        BraceletEffectsHandler.getInstance().applyEffects(player);
        NecklaceEffectsHandler.getInstance().applyEffects(player);
        EarringEffectsHandler.getInstance().applyEffects(player);
        HeadwearEffectsHandler.getInstance().applyEffects(player);
        BeltEffectsHandler.getInstance().applyEffects(player);
        GloveEffectsHandler.getInstance().applyEffects(player);
        ShoulderEffectsHandler.getInstance().applyEffects(player);
        AnkletEffectsHandler.getInstance().applyEffects(player);

        // 通知其他系统 - Notify other systems
        // 暴击和伤害减免系统会自动处理装备变化 - Crit and damage reduction systems handle equipment changes automatically
    }

    /**
     * 应用默认效果（向后兼容） - Apply default effects (backward compatibility)
     */
    private static void applyDefaultEffects(Player player, Item item, AccessorySlot slot) {
        // 应用戒指效果 - Apply ring effects
        if (RingEffectsHandler.getInstance().getEffect(item) != null) {
            RingEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用手镯效果 - Apply bracelet effects
        if (BraceletEffectsHandler.getInstance().getEffect(item) != null) {
            BraceletEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用项链效果 - Apply necklace effects
        if (NecklaceEffectsHandler.getInstance().getEffect(item) != null) {
            NecklaceEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用耳环效果 - Apply earring effects
        if (EarringEffectsHandler.getInstance().getEffect(item) != null) {
            EarringEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用头饰效果 - Apply headwear effects
        if (HeadwearEffectsHandler.getInstance().getEffect(item) != null) {
            HeadwearEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用腰带效果 - Apply belt effects
        if (BeltEffectsHandler.getInstance().getEffect(item) != null) {
            BeltEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用手套效果 - Apply glove effects
        if (GloveEffectsHandler.getInstance().getEffect(item) != null) {
            GloveEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用肩饰效果 - Apply shoulder effects
        if (ShoulderEffectsHandler.getInstance().getEffect(item) != null) {
            ShoulderEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用脚饰效果 - Apply anklet effects
        if (AnkletEffectsHandler.getInstance().getEffect(item) != null) {
            AnkletEffectsHandler.getInstance().applyEffects(player);
        }

        // 应用暴击和伤害减免效果 - Apply crit and damage reduction effects
        // 暴击和伤害减免系统会自动处理装备变化 - Crit and damage reduction systems handle equipment changes automatically
    }

    /**
     * 应用属性modifier - Apply attribute modifier
     */
    private static void applyAttributeModifier(Player player, Holder<Attribute> attribute,
                                             ResourceLocation id, double value, AttributeModifier.Operation operation) {
        if (value == 0) return;

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            AttributeModifier modifier = new AttributeModifier(id, value, operation);
            if (!instance.hasModifier(id)) {
                instance.addTransientModifier(modifier);
            }
        }
    }

    /**
     * 移除属性modifier - Remove attribute modifier
     * 注意：对于最大生命值，不在这里调整生命值，因为可能还有其他修饰符正在被移除/应用
     * Note: For max health, don't adjust health here as other modifiers may still be removed/applied
     */
    private static void removeAttributeModifier(Player player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
            // 生命值调整由调用者在所有修饰符处理完成后统一处理
            // Health adjustment is handled by the caller after all modifiers are processed
        }
    }

    // 缓存管理 - Cache management
    private static final Map<String, ItemStack> PREVIOUS_STACKS = new HashMap<>();

    private static String getCacheKey(Player player, AccessorySlot slot) {
        return player.getUUID() + "_" + slot.name();
    }

    private static ItemStack getPreviousStack(Player player, AccessorySlot slot) {
        return PREVIOUS_STACKS.getOrDefault(getCacheKey(player, slot), ItemStack.EMPTY);
    }

    private static void setPreviousStack(Player player, AccessorySlot slot, ItemStack stack) {
        PREVIOUS_STACKS.put(getCacheKey(player, slot), stack);
    }
}
