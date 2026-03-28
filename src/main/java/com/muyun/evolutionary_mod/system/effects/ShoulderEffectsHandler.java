package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;
import net.minecraft.world.item.ItemStack;

public class ShoulderEffectsHandler extends AbstractAccessoryEffectHandler<ShoulderEffectsHandler.ShoulderEffect> {

    private static final ResourceLocation SHOULDER_MAX_HEALTH_ID    = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "shoulder_max_health");
    private static final ResourceLocation SHOULDER_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "shoulder_attack_damage");
    private static final ResourceLocation SHOULDER_MOVEMENT_SPEED_ID= ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "shoulder_movement_speed");
    private static final ResourceLocation SHOULDER_LUCK_ID          = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "shoulder_luck");

    private static final ShoulderEffectsHandler INSTANCE = new ShoulderEffectsHandler();
    public static ShoulderEffectsHandler getInstance() { return INSTANCE; }

    private ShoulderEffectsHandler() {
        AccessoryEffectCalculator.registerHandler(this);
    }

    @Override protected String getSlotPrefix() { return "ACCESSORY"; }
    @Override protected ShoulderEffect createEffect() { return new ShoulderEffect(); }

    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        double totalMaxHealth = 0, totalAttackDamage = 0, totalMovementSpeed = 0, totalLuck = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (!slot.name().startsWith("ACCESSORY")) continue;
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            ShoulderEffect e = getEffect(stack.getItem());
            if (e != null) {
                totalMaxHealth += e.maxHealth; totalAttackDamage += e.attackDamage;
                totalMovementSpeed += e.movementSpeed; totalLuck += e.luck;
            }
            // 叠加随机词条
            AccessoryAttributes rolled = getRolledAttributes(stack);
            if (!rolled.isEmpty()) {
                totalMaxHealth     += rolled.maxHealth();
                totalAttackDamage  += rolled.attackDamage();
                totalMovementSpeed += rolled.movementSpeed();
                totalLuck          += rolled.luck();
            }
        }
        float currentHealth = player.getHealth();
        applyMod(player, Attributes.MAX_HEALTH,     SHOULDER_MAX_HEALTH_ID,     totalMaxHealth,     AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  SHOULDER_ATTACK_DAMAGE_ID,  totalAttackDamage,  AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, SHOULDER_MOVEMENT_SPEED_ID, totalMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyMod(player, Attributes.LUCK,           SHOULDER_LUCK_ID,           totalLuck,          AttributeModifier.Operation.ADD_VALUE);
        if (currentHealth < player.getMaxHealth()) player.setHealth(Math.min(currentHealth, player.getMaxHealth()));
    }

    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        applyMod(player, Attributes.MAX_HEALTH,     SHOULDER_MAX_HEALTH_ID,     0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  SHOULDER_ATTACK_DAMAGE_ID,  0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, SHOULDER_MOVEMENT_SPEED_ID, 0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.LUCK,           SHOULDER_LUCK_ID,           0, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyMod(Player player, Holder<Attribute> attr, ResourceLocation id, double value, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (value != 0) inst.addTransientModifier(new AttributeModifier(id, value, op));
    }

    public static class ShoulderEffect {
        public double maxHealth = 0, attackDamage = 0, movementSpeed = 0, luck = 0;
        public ShoulderEffect maxHealth(double v)     { maxHealth = v;     return this; }
        public ShoulderEffect attackDamage(double v)  { attackDamage = v;  return this; }
        public ShoulderEffect movementSpeed(double v) { movementSpeed = v; return this; }
        public ShoulderEffect luck(double v)          { luck = v;          return this; }
    }

    public static void applyShoulderEffects(Player player) { getInstance().applyEffects(player); }
}
