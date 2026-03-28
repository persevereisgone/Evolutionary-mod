package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.ModMenus;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import com.muyun.evolutionary_mod.item.base.AccessoryTooltipHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import org.lwjgl.glfw.GLFW;

/**
 * 客户端事件处理器 - Client Handlers
 *
 * HUD 布局（从下往上）：
 *   sh - 39   热栏
 *   sh - 49   自定义数字血条（原版心形位置）
 *   sh - 59   护甲图标（上移 10px，位于血条上方）
 *   sh - 49   饥饿/氧气（原版不变，右侧）
 */
public class ClientHandlers {

    public static KeyMapping OPEN_ACCESSORIES;
    public static KeyMapping OPEN_PLAYER_ATTRIBUTES;

    // -----------------------------------------------------------------------
    // MOD 总线：按键注册、菜单屏幕注册
    // -----------------------------------------------------------------------
    @EventBusSubscriber(modid = EvolutionaryMod.MODID, value = Dist.CLIENT)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            OPEN_ACCESSORIES = new KeyMapping(
                    "key.accessories.open",
                    GLFW.GLFW_KEY_K,
                    "key.categories.evolutionary_mod"
            );
            event.register(OPEN_ACCESSORIES);

            OPEN_PLAYER_ATTRIBUTES = new KeyMapping(
                    "key.accessories.open_attributes",
                    GLFW.GLFW_KEY_J,
                    "key.categories.evolutionary_mod"
            );
            event.register(OPEN_PLAYER_ATTRIBUTES);
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenus.ACCESSORY_MENU.get(), AccessoryContainerScreen::new);
        }
    }

    // -----------------------------------------------------------------------
    // 游戏总线：Tooltip 着色 + 自定义 HUD
    // -----------------------------------------------------------------------
    @EventBusSubscriber(modid = EvolutionaryMod.MODID, value = Dist.CLIENT)
    public static class GameBusEvents {

        private static final ResourceLocation ARMOR_FULL  = ResourceLocation.withDefaultNamespace("hud/armor_full");
        private static final ResourceLocation ARMOR_HALF  = ResourceLocation.withDefaultNamespace("hud/armor_half");
        private static final ResourceLocation ARMOR_EMPTY = ResourceLocation.withDefaultNamespace("hud/armor_empty");

        // ---- Tooltip 边框着色 ----
        @SubscribeEvent
        public static void onRenderTooltip(RenderTooltipEvent.Color event) {
            if (!(event.getItemStack().getItem() instanceof AccessoryItem)) return;
            var rarity = AccessoryTooltipHelper.getItemRarity(event.getItemStack().getItem());
            int borderColor = switch (rarity) {
                case BROKEN    -> 0xFFAAAAAA;
                case NORMAL    -> 0xFFFFFFFF;
                case EXCELLENT -> 0xFF5555FF;
                case EPIC      -> 0xFFAA00AA;
                case LEGENDARY -> 0xFFFFAA00;
                case MYTHIC    -> 0xFFFF0000;
                case SET       -> 0xFF00CCCC; // 套装：青色边框
            };
            event.setBorderStart(borderColor);
            event.setBorderEnd(borderColor);
        }

        // ---- 自定义数字血条（sh-49，替换原版心形）----
        @SubscribeEvent
        public static void onPreRenderHealth(RenderGuiLayerEvent.Pre event) {
            if (!event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) return;

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null || mc.options.hideGui) return;
            // 旁观者模式/创意模式无生命消耗，遵循原版设定隐藏血条
            if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;

            event.setCanceled(true);

            float hp    = player.getHealth();
            float maxHp = player.getMaxHealth();
            float pct   = Math.min(hp / maxHp, 1.0f);

            int sw = mc.getWindow().getGuiScaledWidth();
            int sh = mc.getWindow().getGuiScaledHeight();

            // 与护甲图标左端对齐，宽 81px
            int barW = 81;
            int barH = 9;
            int barX = (sw - 182) / 2;
            int barY = sh - 40;

            GuiGraphics g = event.getGuiGraphics();

            int fillColor = pct > 0.6f ? 0xFF22CC22
                          : pct > 0.3f ? 0xFFDDAA00 : 0xFFFF2222;

            g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF000000);
            g.fill(barX, barY, barX + barW, barY + barH, 0xFF3A1010);
            int fw = (int)(barW * pct);
            if (fw > 0) g.fill(barX, barY, barX + fw, barY + barH, fillColor);

            String text = Math.round(hp) + " / " + Math.round(maxHp);
            Component comp = Component.literal(text);
            int tw = mc.font.width(comp);
            int tx = barX + (barW - tw) / 2;
            int ty = barY + (barH - mc.font.lineHeight) / 2;
            g.drawString(mc.font, comp, tx + 1, ty,     0xFF000000, false);
            g.drawString(mc.font, comp, tx - 1, ty,     0xFF000000, false);
            g.drawString(mc.font, comp, tx,     ty + 1, 0xFF000000, false);
            g.drawString(mc.font, comp, tx,     ty - 1, 0xFF000000, false);
            g.drawString(mc.font, comp, tx,     ty,     0xFFFFFFFF, false);
        }

        // ---- 护甲图标上移 10px（sh-49 → sh-59）----
        @SubscribeEvent
        public static void onPreRenderArmor(RenderGuiLayerEvent.Pre event) {
            if (!event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL)) return;

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null || mc.options.hideGui) return;
            // 旁观者模式下隐藏护甲图标
            if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;

            event.setCanceled(true);

            int armorValue = player.getArmorValue();
            if (armorValue <= 0) return;

            int sw = mc.getWindow().getGuiScaledWidth();
            int sh = mc.getWindow().getGuiScaledHeight();
            GuiGraphics g = event.getGuiGraphics();

            int leftX = (sw - 182) / 2;
            int y = sh - 50; // 上移 10px，位于血条上方                                                             

            for (int i = 0; i < 10; i++) {
                int x = leftX + i * 8;
                g.blitSprite(ARMOR_EMPTY, x, y, 9, 9);
                int remaining = armorValue - i * 2;
                if (remaining >= 2) {
                    g.blitSprite(ARMOR_FULL, x, y, 9, 9);
                } else if (remaining == 1) {
                    g.blitSprite(ARMOR_HALF, x, y, 9, 9);
                }
            }
        }
    }
}
