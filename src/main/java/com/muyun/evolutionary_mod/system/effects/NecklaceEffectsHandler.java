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
 * 项链饰品特效处理器 - Necklace Accessory Effects Handler
 */
public class NecklaceEffectsHandler extends AbstractAccessoryEffectHandler<NecklaceEffectsHandler.NecklaceEffect> {

    private static final ResourceLocation NECKLACE_MAX_HEALTH_ID     = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_max_health");
    private static final ResourceLocation NECKLACE_ATTACK_DAMAGE_ID  = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_attack_damage");
    private static final ResourceLocation NECKLACE_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_movement_speed");
    private static final ResourceLocation NECKLACE_LUCK_ID           = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_luck");
    private static final ResourceLocation NECKLACE_ARMOR_ID          = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "necklace_armor");

    private static final NecklaceEffectsHandler INSTANCE = new NecklaceEffectsHandler();
    public static NecklaceEffectsHandler getInstance() { return INSTANCE; }

    private NecklaceEffectsHandler() {
        AccessoryEffectCalculator.registerHandler(this);
    }

    @Override protected String getSlotPrefix() { return "NECKLACE"; }
    @Override protected NecklaceEffect createEffect() { return new NecklaceEffect(); }

    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        double totalMaxHealth = 0, totalAttackDamage = 0, totalMovementSpeed = 0, totalLuck = 0, totalArmor = 0;
        ItemStack stack = cap.getStack(AccessorySlot.NECKLACE);
        if (!stack.isEmpty()) {
            // 仅叠加随机词条
            AccessoryAttributes rolled = getRolledAttributes(stack);
            if (!rolled.isEmpty()) {
                totalMaxHealth     += rolled.maxHealth();
                totalAttackDamage  += rolled.attackDamage();
                totalMovementSpeed += rolled.movementSpeed();
                totalLuck          += rolled.luck();
                totalArmor         += rolled.armor();
            }
        }
        float currentHealth = player.getHealth();
        applyMod(player, Attributes.MAX_HEALTH,     NECKLACE_MAX_HEALTH_ID,     totalMaxHealth,     AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  NECKLACE_ATTACK_DAMAGE_ID,  totalAttackDamage,  AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, NECKLACE_MOVEMENT_SPEED_ID, totalMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyMod(player, Attributes.LUCK,           NECKLACE_LUCK_ID,           totalLuck,          AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ARMOR,          NECKLACE_ARMOR_ID,          totalArmor,         AttributeModifier.Operation.ADD_VALUE);
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
        if (maxHealth != null) maxHealth.removeModifier(NECKLACE_MAX_HEALTH_ID);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) attackDamage.removeModifier(NECKLACE_ATTACK_DAMAGE_ID);
        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null) moveSpeed.removeModifier(NECKLACE_MOVEMENT_SPEED_ID);
        AttributeInstance luck = player.getAttribute(Attributes.LUCK);
        if (luck != null) luck.removeModifier(NECKLACE_LUCK_ID);
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) armor.removeModifier(NECKLACE_ARMOR_ID);
    }

    @Override
    protected void handlePerTickEffects(Player player, PlayerAccessories cap) {
        ItemStack stack = cap.getStack(AccessorySlot.NECKLACE);
        if (!stack.isEmpty()) {
            double totalRegen = getRolledAttributes(stack).healthRegen();
            if (totalRegen > 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal((float) totalRegen);
            }
        }
    }

    public static class NecklaceEffect {
        public double maxHealth = 0, attackDamage = 0, movementSpeed = 0, luck = 0, armor = 0, healthRegen = 0;
        public NecklaceEffect maxHealth(double v)     { maxHealth = v;     return this; }
        public NecklaceEffect attackDamage(double v)  { attackDamage = v;  return this; }
        public NecklaceEffect movementSpeed(double v) { movementSpeed = v; return this; }
        public NecklaceEffect luck(double v)          { luck = v;          return this; }
        public NecklaceEffect armor(double v)         { armor = v;         return this; }
        public NecklaceEffect healthRegen(double v)   { healthRegen = v;   return this; }
    }

    public static void applyNecklaceEffects(Player player) { getInstance().applyEffects(player); }
}
