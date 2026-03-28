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

public class BeltEffectsHandler extends AbstractAccessoryEffectHandler<BeltEffectsHandler.BeltEffect> {

    private static final ResourceLocation BELT_MAX_HEALTH_ID     = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_max_health");
    private static final ResourceLocation BELT_ATTACK_DAMAGE_ID  = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_attack_damage");
    private static final ResourceLocation BELT_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_movement_speed");
    private static final ResourceLocation BELT_LUCK_ID           = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_luck");
    private static final ResourceLocation BELT_ARMOR_ID          = ResourceLocation.fromNamespaceAndPath("evolutionary_mod", "belt_armor");

    private static final BeltEffectsHandler INSTANCE = new BeltEffectsHandler();
    public static BeltEffectsHandler getInstance() { return INSTANCE; }

    private BeltEffectsHandler() {
        AccessoryEffectCalculator.registerHandler(this);
    }

    @Override protected String getSlotPrefix() { return "BELT"; }
    @Override protected BeltEffect createEffect() { return new BeltEffect(); }

    @Override
    protected void calculateAndApplyEffects(Player player, PlayerAccessories cap) {
        double totalMaxHealth = 0, totalAttackDamage = 0, totalMovementSpeed = 0, totalLuck = 0, totalArmor = 0;
        ItemStack stack = cap.getStack(AccessorySlot.BELT);
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
        applyMod(player, Attributes.MAX_HEALTH,     BELT_MAX_HEALTH_ID,     totalMaxHealth,     AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  BELT_ATTACK_DAMAGE_ID,  totalAttackDamage,  AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, BELT_MOVEMENT_SPEED_ID, totalMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        applyMod(player, Attributes.LUCK,           BELT_LUCK_ID,           totalLuck,          AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ARMOR,          BELT_ARMOR_ID,          totalArmor,         AttributeModifier.Operation.ADD_VALUE);
        if (currentHealth < player.getMaxHealth()) player.setHealth(Math.min(currentHealth, player.getMaxHealth()));
    }

    @Override
    protected void resetModifiersWithoutHealthAdjust(Player player) {
        applyMod(player, Attributes.MAX_HEALTH,     BELT_MAX_HEALTH_ID,     0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ATTACK_DAMAGE,  BELT_ATTACK_DAMAGE_ID,  0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.MOVEMENT_SPEED, BELT_MOVEMENT_SPEED_ID, 0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.LUCK,           BELT_LUCK_ID,           0, AttributeModifier.Operation.ADD_VALUE);
        applyMod(player, Attributes.ARMOR,          BELT_ARMOR_ID,          0, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void applyMod(Player player, Holder<Attribute> attr, ResourceLocation id, double value, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (value != 0) inst.addTransientModifier(new AttributeModifier(id, value, op));
    }

    public static class BeltEffect {
        public double maxHealth = 0, attackDamage = 0, movementSpeed = 0, luck = 0, armor = 0;
        public BeltEffect maxHealth(double v)     { maxHealth = v;     return this; }
        public BeltEffect attackDamage(double v)  { attackDamage = v;  return this; }
        public BeltEffect movementSpeed(double v) { movementSpeed = v; return this; }
        public BeltEffect luck(double v)          { luck = v;          return this; }
        public BeltEffect armor(double v)         { armor = v;         return this; }
    }

    public static void applyBeltEffects(Player player) { getInstance().applyEffects(player); }
}
