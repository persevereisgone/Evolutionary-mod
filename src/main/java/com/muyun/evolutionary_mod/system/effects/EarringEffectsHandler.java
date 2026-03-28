package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;

/**
 * 耳环饰品特效处理器 - Earring Accessory Effects Handler
 */
public class EarringEffectsHandler extends AbstractAccessoryEffectHandler<EarringEffectsHandler.EarringEffect> {

    private static final ResourceLocation EARRING_MAX_HEALTH_ID     = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_max_health");
    private static final ResourceLocation EARRING_ATTACK_DAMAGE_ID  = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_attack_damage");
    private static final ResourceLocation EARRING_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_movement_speed");
    private static final ResourceLocation EARRING_LUCK_ID           = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "earring_luck");

    private static final EarringEffectsHandler INSTANCE = new EarringEffectsHandler();
    public static EarringEffectsHandler getInstance() { return INSTANCE; }

    private EarringEffectsHandler() {
        AccessoryEffectCalculator.registerHandler(this);
    }

    @Override protected String getSlotPrefix() { return "EARRING"; }
    @Override protected EarringEffect createEffect() { return new EarringEffect(); }

    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        double totalMaxHealth = 0, totalAttackDamage = 0, totalMovementSpeed = 0, totalLuck = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (!slot.name().startsWith("EARRING")) continue;
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            // 仅叠加随机词条
            AccessoryAttributes rolled = getRolledAttributes(stack);
            if (!rolled.isEmpty()) {
                totalMaxHealth     += rolled.maxHealth();
                totalAttackDamage  += rolled.attackDamage();
                totalMovementSpeed += rolled.movementSpeed();
                totalLuck          += rolled.luck();
            }
        }
        float currentHealth = player.getHealth();
        applyMod(player, Attributes.MAX_HEALTH,     EARRING_MAX_HEALTH_ID,     totalMaxHealth,     AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  EARRING_ATTACK_DAMAGE_ID,  totalAttackDamage,  AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, EARRING_MOVEMENT_SPEED_ID, totalMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyMod(player, Attributes.LUCK,           EARRING_LUCK_ID,           totalLuck,          AttributeModifier.Operation.ADD_VALUE);
        if (currentHealth < player.getMaxHealth()) player.setHealth(Math.min(currentHealth, player.getMaxHealth()));
    }

    private static void applyMod(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                  ResourceLocation id, double value, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (value != 0) inst.addTransientModifier(new AttributeModifier(id, value, op));
    }

    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) maxHealth.removeModifier(EARRING_MAX_HEALTH_ID);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) attackDamage.removeModifier(EARRING_ATTACK_DAMAGE_ID);
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null) moveSpeed.removeModifier(EARRING_MOVEMENT_SPEED_ID);
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null) luck.removeModifier(EARRING_LUCK_ID);
    }

    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        double totalHealthRegen = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (!slot.name().startsWith("EARRING")) continue;
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            totalHealthRegen += getRolledAttributes(stack).healthRegen();
        }
        if (totalHealthRegen > 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal((float) totalHealthRegen);
        }
    }

    public static class EarringEffect {
        public double maxHealth = 0, attackDamage = 0, movementSpeed = 0, luck = 0, healthRegen = 0;
        public EarringEffect maxHealth(double v)     { maxHealth = v;     return this; }
        public EarringEffect attackDamage(double v)  { attackDamage = v;  return this; }
        public EarringEffect movementSpeed(double v) { movementSpeed = v; return this; }
        public EarringEffect luck(double v)          { luck = v;          return this; }
        public EarringEffect healthRegen(double v)   { healthRegen = v;   return this; }
    }

    public static void applyEarringEffects(Player player) { getInstance().applyEffects(player); }
}
