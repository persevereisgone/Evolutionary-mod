package com.muyun.evolutionary_mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import com.mojang.serialization.MapCodec;
import com.muyun.evolutionary_mod.block.ModBlocks;
import com.muyun.evolutionary_mod.entity.ModEntities;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import com.muyun.evolutionary_mod.item.registry.ModItems;
import com.muyun.evolutionary_mod.item.tabs.ModCreativeModelTabs;
import com.muyun.evolutionary_mod.loot.AccessoryGlobalLootModifier;
import com.muyun.evolutionary_mod.network.NetworkHandler;
import com.muyun.evolutionary_mod.system.effects.AnkletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BeltEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.BraceletEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.EarringEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.GloveEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.HeadwearEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.NecklaceEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.RingEffectsHandler;
import com.muyun.evolutionary_mod.system.effects.ShoulderEffectsHandler;
import com.muyun.evolutionary_mod.system.sets.SetSystem;

import java.util.function.Supplier;

@Mod(EvolutionaryMod.MODID)
public class EvolutionaryMod {

    public static final String MODID = "evolutionary_mod";
    public static final Logger LOGGER = LogUtils.getLogger();

    // -----------------------------------------------------------------------
    // AttachmentType 注册（替代旧版 Capability）
    // -----------------------------------------------------------------------
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    /**
     * 玩家饰品数据 Attachment。
     * 使用 serializable() 确保数据持久化到玩家 NBT 中（跨存档保留）。
     */
    public static final Supplier<AttachmentType<PlayerAccessories>> PLAYER_ACCESSORIES =
            ATTACHMENT_TYPES.register("player_accessories",
                    () -> AttachmentType.serializable(PlayerAccessories::new).build());

    // -----------------------------------------------------------------------
    // DataComponentType 注册（替代旧版 ItemStack NBT）
    // -----------------------------------------------------------------------
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    /**
     * 饰品随机词条 DataComponent。
     * 存储掉落时随机滚动的属性值，持久化到 ItemStack 的 DataComponents 中。
     */
    public static final Supplier<DataComponentType<AccessoryAttributes>> ACCESSORY_ATTRIBUTES =
            DATA_COMPONENTS.register("accessory_attributes",
                    () -> DataComponentType.<AccessoryAttributes>builder()
                            .persistent(AccessoryAttributes.CODEC)
                            .networkSynchronized(AccessoryAttributes.STREAM_CODEC)
                            .build());

    // -----------------------------------------------------------------------
    // GlobalLootModifierSerializer 注册
    // -----------------------------------------------------------------------
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AccessoryGlobalLootModifier>>
            ACCESSORY_DROPS_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
                    "accessory_drops", () -> AccessoryGlobalLootModifier.CODEC);

    // -----------------------------------------------------------------------
    // 其他 DeferredRegister
    // -----------------------------------------------------------------------
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // -----------------------------------------------------------------------
    // 构造函数
    // -----------------------------------------------------------------------
    public EvolutionaryMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册 AttachmentType
        ATTACHMENT_TYPES.register(modEventBus);

        // 注册 DataComponentType
        DATA_COMPONENTS.register(modEventBus);

        // 注册 GlobalLootModifierSerializer
        LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        // 注册网络 Payload
        modEventBus.addListener(NetworkHandler::registerPayloads);

        // 注册各子系统
        ModItems.register(modEventBus);
        ModCreativeModelTabs.register(modEventBus);
        ModMenus.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册通用 Setup
        modEventBus.addListener(this::commonSetup);

        // 注册 NeoForge 游戏事件总线
        NeoForge.EVENT_BUS.register(this);

        // 注册 Mod 配置
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        SetSystem.initialize();

        // 强制触发所有效果处理器单例初始化，确保 handlers 列表在登录前已填充
        RingEffectsHandler.getInstance();
        NecklaceEffectsHandler.getInstance();
        BraceletEffectsHandler.getInstance();
        EarringEffectsHandler.getInstance();
        HeadwearEffectsHandler.getInstance();
        BeltEffectsHandler.getInstance();
        GloveEffectsHandler.getInstance();
        ShoulderEffectsHandler.getInstance();
        AnkletEffectsHandler.getInstance();

        LOGGER.info("[EvolutionaryMod] Common setup complete.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[EvolutionaryMod] Server starting.");
    }
}
