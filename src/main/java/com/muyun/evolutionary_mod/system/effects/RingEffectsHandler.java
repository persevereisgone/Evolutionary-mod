package com.muyun.evolutionary_mod.system.effects;

import com.muyun.evolutionary_mod.capability.PlayerAccessories;
import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.system.combat.ArmorPenetrationSystem;
import com.muyun.evolutionary_mod.system.combat.CritSystem;
import com.muyun.evolutionary_mod.system.combat.DamageReductionSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.muyun.evolutionary_mod.item.base.AccessoryAttributes;

/**
 * 戒指饰品特效处理器 - Ring Accessory Effects Handler
 */
public class RingEffectsHandler extends AbstractAccessoryEffectHandler<RingEffectsHandler.RingEffect> {

    private static final ResourceLocation RING_MAX_HEALTH_ID     = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_max_health");
    private static final ResourceLocation RING_ATTACK_DAMAGE_ID  = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_attack_damage");
    private static final ResourceLocation RING_ARMOR_ID          = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_armor");
    private static final ResourceLocation RING_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_movement_speed");
    private static final ResourceLocation RING_LUCK_ID           = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "ring_luck");

    private static final RingEffectsHandler INSTANCE = new RingEffectsHandler();
    public static RingEffectsHandler getInstance() { return INSTANCE; }

    private RingEffectsHandler() {
        AccessoryEffectCalculator.registerHandler(this);
    }

    @Override protected String getSlotPrefix() { return "RING"; }
    @Override protected RingEffect createEffect() { return new RingEffect(); }

    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        double totalMaxHealth = 0, totalAttackDamage = 0, totalArmor = 0, totalMovementSpeed = 0, totalLuck = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (!slot.name().startsWith("RING")) continue;
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            // 仅叠加随机词条
            AccessoryAttributes rolled = getRolledAttributes(stack);
            if (!rolled.isEmpty()) {
                totalMaxHealth     += rolled.maxHealth();
                totalAttackDamage  += rolled.attackDamage();
                totalArmor         += rolled.armor();
                totalMovementSpeed += rolled.movementSpeed();
                totalLuck          += rolled.luck();
            }
        }
        float currentHealth = player.getHealth();
        applyModifier(player, Attributes.MAX_HEALTH,     RING_MAX_HEALTH_ID,     totalMaxHealth,     AttributeModifier.Operation.ADD_VALUE);
        applyModifier(player, Attributes.ATTACK_DAMAGE,  RING_ATTACK_DAMAGE_ID,  totalAttackDamage,  AttributeModifier.Operation.ADD_VALUE);
        applyModifier(player, Attributes.ARMOR,          RING_ARMOR_ID,          totalArmor,         AttributeModifier.Operation.ADD_VALUE);
        applyModifier(player, Attributes.MOVEMENT_SPEED, RING_MOVEMENT_SPEED_ID, totalMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyModifier(player, Attributes.LUCK,           RING_LUCK_ID,           totalLuck,          AttributeModifier.Operation.ADD_VALUE);
        if (currentHealth < player.getMaxHealth()) player.setHealth(Math.min(currentHealth, player.getMaxHealth()));
    }

    private static void applyModifier(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                      ResourceLocation id, double value, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (value != 0) inst.addTransientModifier(new AttributeModifier(id, value, op));
    }

    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) maxHealth.removeModifier(RING_MAX_HEALTH_ID);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) attackDamage.removeModifier(RING_ATTACK_DAMAGE_ID);
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) armor.removeModifier(RING_ARMOR_ID);
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null) moveSpeed.removeModifier(RING_MOVEMENT_SPEED_ID);
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null) luck.removeModifier(RING_LUCK_ID);
    }

    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        double totalHealthRegen = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (!slot.name().startsWith("RING")) continue;
            ItemStack stack = cap.getStack(slot);
            if (stack.isEmpty()) continue;
            totalHealthRegen += getRolledAttributes(stack).healthRegen();
        }
        if (totalHealthRegen > 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal((float) totalHealthRegen);
        }
    }

    public static class RingEffect {
        public double maxHealth = 0, attackDamage = 0, armor = 0, movementSpeed = 0, luck = 0,
                      healthRegen = 0, armorPenetration = 0, damageReduction = 0;
        public RingEffect maxHealth(double v)        { maxHealth = v;        return this; }
        public RingEffect attackDamage(double v)     { attackDamage = v;     return this; }
        public RingEffect armor(double v)            { armor = v;            return this; }
        public RingEffect movementSpeed(double v)    { movementSpeed = v;    return this; }
        public RingEffect luck(double v)             { luck = v;             return this; }
        public RingEffect healthRegen(double v)      { healthRegen = v;      return this; }
        public RingEffect armorPenetration(double v) { armorPenetration = v; return this; }
        public RingEffect damageReduction(double v)  { damageReduction = v;  return this; }
    }

    public static void applyRingEffects(Player player) { getInstance().applyEffects(player); }
}
