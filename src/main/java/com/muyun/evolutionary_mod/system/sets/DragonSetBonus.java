package com.muyun.evolutionary_mod.system.sets;

import com.muyun.evolutionary_mod.EvolutionaryMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * 龙族套装效果 - Dragon Set Bonus
 *
 * NeoForge 1.21.1: AttributeModifier 改用 ResourceLocation 替代 UUID。
 */
public class DragonSetBonus {

    public static final ResourceLocation DRAGON_BLOODLINE_HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "dragon_bloodline_health");
    public static final ResourceLocation DRAGON_SCALE_ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(EvolutionaryMod.MODID, "dragon_scale_armor");

    /**
     * 2件套：龙之血脉 - 最大生命值 +20%，生命恢复速度 +50%
     */
    public static class DragonBloodline extends SetBonus {
        public DragonBloodline() {
            super("龙之血脉", "龙族的血脉在你体内流淌，赋予你强大的生命力", 2);
        }

        @Override
        public void apply(Player player, int pieceCount) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth == null) return;

            if (maxHealth.getModifier(DRAGON_BLOODLINE_HEALTH_ID) != null) {
                maxHealth.removeModifier(DRAGON_BLOODLINE_HEALTH_ID);
            }

            float currentHealth = player.getHealth();

            maxHealth.addTransientModifier(new AttributeModifier(
                    DRAGON_BLOODLINE_HEALTH_ID,
                    0.20,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));

            float newMax = player.getMaxHealth();
            player.setHealth(Math.min(currentHealth, newMax));
        }

        @Override
        public void remove(Player player) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth == null) return;
            float currentHealth = player.getHealth();
            maxHealth.removeModifier(DRAGON_BLOODLINE_HEALTH_ID);
            float newMax = player.getMaxHealth();
            if (currentHealth > newMax) player.setHealth(newMax);
        }
    }

    /**
     * 4件套：龙鳞护体 - 护甲 +15，伤害减免 +15%
     */
    public static class DragonScaleProtection extends SetBonus {
        public DragonScaleProtection() {
            super("龙鳞护体", "龙鳞覆盖你的身体，提供强大的防护", 4);
        }

        @Override
        public void apply(Player player, int pieceCount) {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor != null && armor.getModifier(DRAGON_SCALE_ARMOR_ID) == null) {
                armor.addTransientModifier(new AttributeModifier(
                        DRAGON_SCALE_ARMOR_ID,
                        15.0,
                        AttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        @Override
        public void remove(Player player) {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor != null) armor.removeModifier(DRAGON_SCALE_ARMOR_ID);
        }
    }

    /**
     * 6件套：龙息爆发 - 攻击时触发（逻辑在 DragonSetEffectHandler 中）
     */
    public static class DragonBreathBurst extends SetBonus {
        public DragonBreathBurst() {
            super("龙息爆发", "龙息从你体内爆发，焚烧一切敌人", 6);
        }

        @Override
        public void apply(Player player, int pieceCount) { /* 事件触发，无需在此处理 */ }

        @Override
        public void remove(Player player) { /* 无需清理 */ }
    }
}
