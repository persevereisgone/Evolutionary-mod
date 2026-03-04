package com.muyun.evolutionary_mod.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.muyun.evolutionary_mod.ModMenus;
import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.client.LayoutConfig;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.item.base.AccessoryItem;
import com.muyun.evolutionary_mod.menu.AccessoryMenu;

@OnlyIn(Dist.CLIENT)
public class AccessoryContainerScreen extends AbstractContainerScreen<AccessoryMenu> {
    // Constants for colors and text
    private static final int HOVER_OVERLAY_COLOR = 0x80FFFFFF; // semi-transparent white

    private LayoutConfig.Layout layout;
    private ResourceLocation backgroundTexture;
    public AccessoryContainerScreen(AccessoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    /**
     * Check if mouse is hovering over a slot area
     */
    private boolean isMouseOverSlot(int mouseX, int mouseY, int slotX, int slotY) {
        return mouseX >= slotX && mouseX < slotX + layout.cellSize &&
               mouseY >= slotY && mouseY < slotY + layout.cellSize;
    }

    /**
     * Get the accessory slot that the mouse is currently hovering over
     */
    private AccessorySlot getHoveredAccessorySlot(int mouseX, int mouseY) {
        for (int i = 0; i < this.menu.slots.size(); i++) {
            net.minecraft.world.inventory.Slot slot = this.menu.slots.get(i);
            int sx = this.leftPos + slot.x;
            int sy = this.topPos + slot.y;
            if (isMouseOverSlot(mouseX, mouseY, sx, sy)) {
                // Assuming slots are ordered by AccessorySlot enum order
                AccessorySlot[] slots = AccessorySlot.values();
                if (i < slots.length) {
                    return slots[i];
                }
            }
        }
        return null;
    }

    /**
     * Get the display name for an accessory slot
     */
    private Component getAccessorySlotDisplayName(AccessorySlot slot) {
        if (slot == null) return null;
        // Use the slot name directly to build the translation key
        // This matches the keys in the language files (e.g., "accessory_slot.earring_1")
        return Component.translatable("accessory_slot." + slot.name().toLowerCase());
    }

    private void drawSlotOverlays(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw hover overlay using the actual slot positions from the menu (no outlines)
        for (int i = 0; i < this.menu.slots.size(); i++) {
            net.minecraft.world.inventory.Slot slot = this.menu.slots.get(i);
            int sx = this.leftPos + slot.x;
            int sy = this.topPos + slot.y;
            // hover overlay: cover default highlight by drawing on top
            if (isMouseOverSlot(mouseX, mouseY, sx, sy)) {
                guiGraphics.fill(sx, sy, sx + layout.cellSize - 1, sy + layout.cellSize - 1, HOVER_OVERLAY_COLOR);
            }
        }
        // (outlines removed per user request)
    }

    @Override
    protected void init() {
        // Load layout: always use bundled assets layout on client (match server-side assets)
        try {
            try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("assets/evolutionary_mod/layout/accessories_layout.json")) {
                if (is != null) {
                    String bundledJson = new String(is.readAllBytes());
                    this.layout = LayoutConfig.loadFromString(bundledJson);
                } else {
                    this.layout = LayoutConfig.defaultLayout();
                }
            }
        } catch (Exception e) {
            this.layout = LayoutConfig.defaultLayout();
        }
        // Use layout's configured dimensions directly (background image should match these dimensions)
        this.imageWidth = layout.pageWidth;
        this.imageHeight = layout.pageHeight;

        if (layout.background != null && !layout.background.isEmpty()) {
            this.backgroundTexture = ResourceLocation.tryParse("evolutionary_mod:" + layout.background);
        }

        super.init();

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Draw our custom hover highlight and debug outlines on top of everything
        drawSlotOverlays(guiGraphics, mouseX, mouseY);

        // Render accessory slot tooltip if hovering over a slot
        AccessorySlot hoveredSlot = getHoveredAccessorySlot(mouseX, mouseY);
        if (hoveredSlot != null) {
            Component slotName = getAccessorySlotDisplayName(hoveredSlot);
            if (slotName != null) {
                // Draw the slot name tooltip near the mouse cursor
                guiGraphics.renderTooltip(this.font, slotName, mouseX, mouseY - 20);
            }
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Render background texture if available
        if (backgroundTexture != null) {
            guiGraphics.blit(backgroundTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        } else {
            // Fallback: simple colored background
            guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF202020);
        }

        // (debug outlines and custom hover are drawn after super.render to ensure they sit on top)
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Override to prevent rendering of inventory and container titles
        // 覆盖此方法以防止渲染物品栏和容器标题
    }

}


