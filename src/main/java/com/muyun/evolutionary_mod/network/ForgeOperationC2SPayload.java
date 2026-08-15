package com.muyun.evolutionary_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import com.muyun.evolutionary_mod.block.ForgeTableBlockEntity;
import com.muyun.evolutionary_mod.menu.ForgeTableMenu;
import com.muyun.evolutionary_mod.system.forge.ForgeSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * C2S 数据包：客户端请求执行锻造台操作（强化 / 属性精华 / 重锻 / 粉碎）。
 *
 * mode：
 * - "enhance"            强化（消耗 1 类型精华 + 1~10 碎片）
 * - "attr_essence"       属性精华（消耗 1 属性精华，必成功）
 * - "reroll_normal"      普通重锻（消耗 1 普通石 + 可选锁定石）
 * - "reroll_advanced"    高级重锻（消耗 1 高级石 + 可选锁定石）
 * - "smash"              粉碎（销毁装备，返还材料）
 *
 * shardCount：强化用碎片数量；lockKeys：重锻锁定词条；lockCount：锁定石数量。
 */
public record ForgeOperationC2SPayload(
        String mode,
        int shardCount,
        List<String> lockKeys,
        int lockCount
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ForgeOperationC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "forge_operation_c2s"));

    public static final StreamCodec<FriendlyByteBuf, ForgeOperationC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeUtf(payload.mode());
                        buf.writeInt(payload.shardCount());
                        buf.writeCollection(payload.lockKeys(), FriendlyByteBuf::writeUtf);
                        buf.writeInt(payload.lockCount());
                    },
                    buf -> new ForgeOperationC2SPayload(
                            buf.readUtf(),
                            buf.readInt(),
                            buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf),
                            buf.readInt()
                    )
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            if (!(serverPlayer.containerMenu instanceof ForgeTableMenu menu)) return;
            if (menu.containerId < 0) return;

            ForgeSystem.ForgeResult result;
            switch (mode()) {
                case "enhance" -> {
                    ItemStack essence = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_ESSENCE);
                    ItemStack shard = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_SHARD);
                    if (essence.isEmpty() || shard.isEmpty()) return;
                    String essenceId = essence.getItemHolder().unwrapKey()
                            .map(k -> k.location().toString()).orElse("");
                    String shardId = shard.getItemHolder().unwrapKey()
                            .map(k -> k.location().toString()).orElse("");
                    int count = Math.min(shardCount(), shard.getCount());
                    ItemStack equipment = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_EQUIPMENT);

                    result = ForgeSystem.enhance(equipment, essenceId, shardId, count);
                    if (result != null) {
                        // 消耗材料：1 精华 + count 碎片（无论成败）
                        essence.shrink(1);
                        shard.shrink(count);
                        menu.setForgeSlot(ForgeTableBlockEntity.SLOT_ESSENCE, essence);
                        menu.setForgeSlot(ForgeTableBlockEntity.SLOT_SHARD, shard);
                        sendFeedback(serverPlayer, result);
                    }
                }
                case "attr_essence" -> {
                    ItemStack attrEssence = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_ATTR_ESSENCE);
                    if (attrEssence.isEmpty()) return;
                    String id = attrEssence.getItemHolder().unwrapKey()
                            .map(k -> k.location().toString()).orElse("");
                    if (!id.startsWith("evolutionary_mod:attribute_essence_")) return;
                    String attrKey = id.substring("evolutionary_mod:attribute_essence_".length());
                    ItemStack equipment = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_EQUIPMENT);

                    result = ForgeSystem.addAttributeEssence(equipment, attrKey);
                    if (result != null && result.success()) {
                        attrEssence.shrink(1);
                        menu.setForgeSlot(ForgeTableBlockEntity.SLOT_ATTR_ESSENCE, attrEssence);
                        sendFeedback(serverPlayer, result);
                    } else if (result != null) {
                        sendFeedback(serverPlayer, result);
                    }
                }
                case "reroll_normal", "reroll_advanced" -> {
                    ItemStack stone = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_REROLL_STONE);
                    ItemStack lockStone = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_LOCK_STONE);
                    if (stone.isEmpty()) return;
                    String stoneId = stone.getItemHolder().unwrapKey()
                            .map(k -> k.location().toString()).orElse("");
                    boolean advanced = mode().equals("reroll_advanced");
                    if (advanced && !"evolutionary_mod:reroll_stone_advanced".equals(stoneId)) return;
                    if (!advanced && !"evolutionary_mod:reroll_stone".equals(stoneId)) return;

                    int locks = Math.min(lockCount(), lockStone.isEmpty() ? 0 : lockStone.getCount());
                    if (locks > 0 && locks > lockKeys().size()) locks = lockKeys().size();
                    ItemStack equipment = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_EQUIPMENT);

                    result = ForgeSystem.reroll(equipment, stoneId, lockKeys(), locks);
                    if (result != null && result.success()) {
                        stone.shrink(1);
                        menu.setForgeSlot(ForgeTableBlockEntity.SLOT_REROLL_STONE, stone);
                        if (locks > 0) {
                            lockStone.shrink(locks);
                            menu.setForgeSlot(ForgeTableBlockEntity.SLOT_LOCK_STONE, lockStone);
                        }
                        sendFeedback(serverPlayer, result);
                    } else if (result != null) {
                        sendFeedback(serverPlayer, result);
                    }
                }
                case "smash" -> {
                    ItemStack equipment = menu.getForgeSlot(ForgeTableBlockEntity.SLOT_EQUIPMENT);
                    result = ForgeSystem.smash(equipment);
                    if (result != null && result.success()) {
                        // 按 ForgeSystem 概率结算的返还发放
                        for (var entry : result.returns().entrySet()) {
                            ItemStack returned = new ItemStack(
                                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                                            net.minecraft.resources.ResourceLocation.parse(entry.getKey())),
                                    entry.getValue());
                            giveOrDrop(serverPlayer, returned);
                        }
                        // 销毁装备
                        menu.setForgeSlot(ForgeTableBlockEntity.SLOT_EQUIPMENT, ItemStack.EMPTY);
                        sendFeedback(serverPlayer, result);
                    } else if (result != null) {
                        sendFeedback(serverPlayer, result);
                    }
                }
                default -> { return; }
            }
        });
    }

    private static void sendFeedback(ServerPlayer player, ForgeSystem.ForgeResult result) {
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.translatable(result.messageKey()));
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
