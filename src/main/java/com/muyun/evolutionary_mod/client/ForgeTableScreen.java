package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.menu.ForgeTableMenu;
import com.muyun.evolutionary_mod.network.ForgeOperationC2SPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 锻造台界面 - Forge Table Screen
 *
 * 三个页签：强化 / 重锻 / 粉碎。
 * 强化页含碎片数量选择（1~10）与属性精华操作；重锻页含锁定词条勾选。
 * 所有操作通过 ForgeOperationC2SPayload 发送到服务端执行。
 */
@OnlyIn(Dist.CLIENT)
public class ForgeTableScreen extends AbstractContainerScreen<ForgeTableMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "textures/gui/forge_table.png");

    private static final int[] SHARD_COUNTS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private String tab = "enhance"; // enhance / reroll / smash
    private int shardCount = 1;
    private boolean advancedReroll = false;
    private final List<String> lockedKeys = new ArrayList<>();

    private static final List<String> LOCKABLE_KEYS = List.of(
            "max_health", "attack_damage", "armor", "movement_speed", "luck",
            "health_regen", "armor_penetration", "crit_chance", "crit_damage", "damage_reduction");

    public ForgeTableScreen(ForgeTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int cx = this.leftPos;
        int cy = this.topPos;

        this.addRenderableWidget(Button.builder(Component.translatable("forge.tab.enhance"),
                        b -> { tab = "enhance"; refreshButtons(); })
                .bounds(cx + 6, cy + 4, 52, 16).build());
        this.addRenderableWidget(Button.builder(Component.translatable("forge.tab.reroll"),
                        b -> { tab = "reroll"; refreshButtons(); })
                .bounds(cx + 62, cy + 4, 52, 16).build());
        this.addRenderableWidget(Button.builder(Component.translatable("forge.tab.smash"),
                        b -> { tab = "smash"; refreshButtons(); })
                .bounds(cx + 118, cy + 4, 52, 16).build());

        refreshButtons();
    }

    private void refreshButtons() {
        // 移除旧的按钮（保留页签按钮：前 3 个 widget）
        List<net.minecraft.client.gui.components.Renderable> old = new ArrayList<>();
        for (var w : this.renderables) {
            if (!(w instanceof Button b)) continue;
            if (b.getX() == this.leftPos + 6 || b.getX() == this.leftPos + 62 || b.getX() == this.leftPos + 118) continue;
            old.add(w);
        }
        this.renderables.removeAll(old);

        int cx = this.leftPos;
        int cy = this.topPos;
        int y = cy + 58;

        switch (tab) {
            case "enhance" -> {
                this.addRenderableWidget(Button.builder(
                                Component.translatable("forge.enhance.button"),
                                b -> sendEnhance())
                        .bounds(cx + 62, y, 52, 16).build());
                // 碎片数量选择 1~10
                for (int i = 0; i < SHARD_COUNTS.length; i++) {
                    int count = SHARD_COUNTS[i];
                    int fx = cx + 8 + i * 11;
                    this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(count)),
                                    b -> { shardCount = count; refreshButtons(); })
                            .bounds(fx, y + 20, 10, 12)
                            .build());
                }
                // 属性精华操作
                this.addRenderableWidget(Button.builder(
                                Component.translatable("forge.attr_essence.button"),
                                b -> sendAttrEssence())
                        .bounds(cx + 118, y, 52, 16).build());
            }
            case "reroll" -> {
                this.addRenderableWidget(Button.builder(
                                Component.translatable("forge.reroll.normal_button"),
                                b -> { advancedReroll = false; sendReroll(); })
                        .bounds(cx + 8, y, 76, 16).build());
                this.addRenderableWidget(Button.builder(
                                Component.translatable("forge.reroll.advanced_button"),
                                b -> { advancedReroll = true; sendReroll(); })
                        .bounds(cx + 88, y, 76, 16).build());
                // 锁定词条勾选（每锁 1 条消耗 1 锁定石，最多 2 条）
                for (int i = 0; i < Math.min(4, LOCKABLE_KEYS.size()); i++) {
                    String key = LOCKABLE_KEYS.get(i);
                    int fx = cx + 8 + (i % 2) * 80;
                    int fy = y + 20 + (i / 2) * 14;
                    boolean selected = lockedKeys.contains(key);
                    this.addRenderableWidget(Button.builder(
                                    Component.translatable("forge.attr." + key + (selected ? " ✓" : "")),
                                    b -> {
                                        if (lockedKeys.contains(key)) {
                                            lockedKeys.remove(key);
                                        } else if (lockedKeys.size() < 2) {
                                            lockedKeys.add(key);
                                        }
                                        refreshButtons();
                                    })
                            .bounds(fx, fy, 76, 12)
                            .build());
                }
            }
            case "smash" -> {
                this.addRenderableWidget(Button.builder(
                                Component.translatable("forge.smash.button"),
                                b -> sendSmash())
                        .bounds(cx + 62, y, 52, 16).build());
            }
            default -> {}
        }
    }

    private void sendEnhance() {
        PacketDistributor.sendToServer(new ForgeOperationC2SPayload("enhance", shardCount, List.of(), 0));
    }

    private void sendAttrEssence() {
        PacketDistributor.sendToServer(new ForgeOperationC2SPayload("attr_essence", 0, List.of(), 0));
    }

    private void sendReroll() {
        PacketDistributor.sendToServer(new ForgeOperationC2SPayload(
                advancedReroll ? "reroll_advanced" : "reroll_normal", 0, lockedKeys, lockedKeys.size()));
    }

    private void sendSmash() {
        PacketDistributor.sendToServer(new ForgeOperationC2SPayload("smash", 0, List.of(), 0));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + imageWidth, this.topPos + imageHeight, 0xFF2B2B2B);
        // 页签提示
        guiGraphics.drawString(this.font, Component.translatable("forge.current_tab." + tab),
                this.leftPos + 8, this.topPos + 26, 0xFFAAAAAA, false);
        // 强化页显示当前碎片数量与锁定状态
        if ("enhance".equals(tab)) {
            guiGraphics.drawString(this.font,
                    Component.translatable("forge.shard_count", shardCount),
                    this.leftPos + 8, this.topPos + 44, 0xFFFFFFFF, false);
        }
        if ("reroll".equals(tab)) {
            guiGraphics.drawString(this.font,
                    Component.translatable("forge.lock_count", lockedKeys.size()),
                    this.leftPos + 8, this.topPos + 44, 0xFFFFFFFF, false);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFFFFFFF, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 0xFFFFFFFF, false);
    }
}
