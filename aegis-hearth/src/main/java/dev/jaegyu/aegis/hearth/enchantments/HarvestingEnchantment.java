package dev.jaegyu.aegis.hearth.enchantments;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public final class HarvestingEnchantment {

    public static final int MAX_LEVEL = 4;
    public static final double BONUS_PER_LEVEL = 0.125; // 12.5% per level

    public static final ResourceKey<Enchantment> KEY = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath("aegis_hearth", "harvesting")
    );

    private HarvestingEnchantment() {}

    /** Calculates the drop bonus multiplier for a given level. e.g. level 2 → 1.25 */
    public static double getMultiplier(int level) {
        return 1.0 + (BONUS_PER_LEVEL * level);
    }
}
