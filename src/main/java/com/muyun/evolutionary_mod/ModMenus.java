package com.muyun.evolutionary_mod;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.menu.AccessoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, EvolutionaryMod.MODID);

    /**
     * 使用 NeoForge 提供的 IMenuTypeExtension.create 工厂方法创建菜单类型。
     * 现在能力系统还未迁移完毕，这里先为每次打开界面创建一个新的 PlayerAccessories 占位实例。
     */
    public static final DeferredHolder<MenuType<?>, MenuType<AccessoryMenu>> ACCESSORY_MENU =
            MENUS.register("accessory_menu",
                    () -> IMenuTypeExtension.create(
                            // IContainerFactory<T>: (containerId, inventory, buf) -> T
                            (containerId, inventory, buf) -> new AccessoryMenu(containerId, inventory, new PlayerAccessories())
                    ));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}


