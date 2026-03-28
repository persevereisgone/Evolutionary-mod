package com.muyun.evolutionary_mod;

import com.muyun.evolutionary_mod.menu.AccessoryMenu;
import net.minecraft.client.Minecraft;
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
     * 客户端工厂：从本地玩家的 Attachment 读取真实饰品数据构造菜单。
     * 服务端通过 AccessoryOpenMenuC2SPayload 使用三参数构造函数，此处仅用于客户端渲染。
     */
    public static final DeferredHolder<MenuType<?>, MenuType<AccessoryMenu>> ACCESSORY_MENU =
            MENUS.register("accessory_menu",
                    () -> IMenuTypeExtension.create(
                            (containerId, inventory, buf) -> {
                                var player = inventory.player;
                                var data = player.getData(EvolutionaryMod.PLAYER_ACCESSORIES);
                                return new AccessoryMenu(containerId, inventory, data);
                            }
                    ));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
