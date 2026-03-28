package com.muyun.evolutionary_mod.item.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * 饰品随机词条 Data Component
 *
 * 存储一件饰品上通过掉落随机滚动得到的属性词条。
 * 使用 Codec 自动序列化到物品的 DataComponents 中（替代旧版 NBT getTag/hasTag）。
 *
 * 所有百分比属性（movementSpeed、critChance、critDamage、damageReduction）
 * 均以小数形式存储，例如 0.05 表示 5%。
 */
public record AccessoryAttributes(
        double maxHealth,
        double attackDamage,
        double armor,
        double movementSpeed,
        double luck,
        double healthRegen,
        double armorPenetration,
        double critChance,
        double critDamage,
        double damageReduction
) {

    public static final AccessoryAttributes EMPTY = new AccessoryAttributes(
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    /** Codec：用于持久化到物品 DataComponents（NBT / SNBT）*/
    public static final Codec<AccessoryAttributes> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.optionalFieldOf("max_health", 0.0).forGetter(AccessoryAttributes::maxHealth),
            Codec.DOUBLE.optionalFieldOf("attack_damage", 0.0).forGetter(AccessoryAttributes::attackDamage),
            Codec.DOUBLE.optionalFieldOf("armor", 0.0).forGetter(AccessoryAttributes::armor),
            Codec.DOUBLE.optionalFieldOf("movement_speed", 0.0).forGetter(AccessoryAttributes::movementSpeed),
            Codec.DOUBLE.optionalFieldOf("luck", 0.0).forGetter(AccessoryAttributes::luck),
            Codec.DOUBLE.optionalFieldOf("health_regen", 0.0).forGetter(AccessoryAttributes::healthRegen),
            Codec.DOUBLE.optionalFieldOf("armor_penetration", 0.0).forGetter(AccessoryAttributes::armorPenetration),
            Codec.DOUBLE.optionalFieldOf("crit_chance", 0.0).forGetter(AccessoryAttributes::critChance),
            Codec.DOUBLE.optionalFieldOf("crit_damage", 0.0).forGetter(AccessoryAttributes::critDamage),
            Codec.DOUBLE.optionalFieldOf("damage_reduction", 0.0).forGetter(AccessoryAttributes::damageReduction)
    ).apply(inst, AccessoryAttributes::new));

    /** StreamCodec：用于网络同步 */
    public static final StreamCodec<FriendlyByteBuf, AccessoryAttributes> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public AccessoryAttributes decode(FriendlyByteBuf buf) {
                    double maxHealth        = buf.readDouble();
                    double attackDamage     = buf.readDouble();
                    double armor            = buf.readDouble();
                    double movementSpeed    = buf.readDouble();
                    double luck             = buf.readDouble();
                    double healthRegen      = buf.readDouble();
                    double armorPenetration = buf.readDouble();
                    double critChance       = buf.readDouble();
                    double critDamage       = buf.readDouble();
                    double damageReduction  = buf.readDouble();
                    return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed,
                            luck, healthRegen, armorPenetration, critChance, critDamage, damageReduction);
                }

                @Override
                public void encode(FriendlyByteBuf buf, AccessoryAttributes a) {
                    buf.writeDouble(a.maxHealth());
                    buf.writeDouble(a.attackDamage());
                    buf.writeDouble(a.armor());
                    buf.writeDouble(a.movementSpeed());
                    buf.writeDouble(a.luck());
                    buf.writeDouble(a.healthRegen());
                    buf.writeDouble(a.armorPenetration());
                    buf.writeDouble(a.critChance());
                    buf.writeDouble(a.critDamage());
                    buf.writeDouble(a.damageReduction());
                }
            };

    /** 判断是否所有词条均为 0（即未经过随机滚动）*/
    public boolean isEmpty() {
        return maxHealth == 0 && attackDamage == 0 && armor == 0
                && movementSpeed == 0 && luck == 0 && healthRegen == 0
                && armorPenetration == 0 && critChance == 0
                && critDamage == 0 && damageReduction == 0;
    }

    // -----------------------------------------------------------------------
    // Builder 风格工厂方法，方便在 AttributeRollerEvents 中构造实例
    // -----------------------------------------------------------------------

    public AccessoryAttributes withMaxHealth(double v)        { return new AccessoryAttributes(v, attackDamage, armor, movementSpeed, luck, healthRegen, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withAttackDamage(double v)     { return new AccessoryAttributes(maxHealth, v, armor, movementSpeed, luck, healthRegen, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withArmor(double v)            { return new AccessoryAttributes(maxHealth, attackDamage, v, movementSpeed, luck, healthRegen, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withMovementSpeed(double v)    { return new AccessoryAttributes(maxHealth, attackDamage, armor, v, luck, healthRegen, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withLuck(double v)             { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, v, healthRegen, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withHealthRegen(double v)      { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, luck, v, armorPenetration, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withArmorPenetration(double v) { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, luck, healthRegen, v, critChance, critDamage, damageReduction); }
    public AccessoryAttributes withCritChance(double v)       { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, luck, healthRegen, armorPenetration, v, critDamage, damageReduction); }
    public AccessoryAttributes withCritDamage(double v)       { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, luck, healthRegen, armorPenetration, critChance, v, damageReduction); }
    public AccessoryAttributes withDamageReduction(double v)  { return new AccessoryAttributes(maxHealth, attackDamage, armor, movementSpeed, luck, healthRegen, armorPenetration, critChance, critDamage, v); }
}

