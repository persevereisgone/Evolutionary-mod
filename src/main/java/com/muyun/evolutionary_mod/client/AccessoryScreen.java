package com.muyun.evolutionary_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AccessoryScreen extends Screen {
    public AccessoryScreen() {
        super(Component.translatable("screen.accessories.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("Close"), (button) -> onClose()).bounds(this.width/2 - 50, this.height - 30, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            guiGraphics.drawString(this.font, Component.literal("Accessories"), this.width/2 - 30, 8, 0xFFFFFF);
        }
    }
}


