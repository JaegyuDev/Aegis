package dev.jaegyu.aegis.hearth.listener.migrations;

import dev.jaegyu.aegis.hearth.enchantments.HarvestingEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HarvestingKeyMigration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HarvestingKeyMigration.class);
    private static final Identifier OLD_KEY =
            Identifier.fromNamespaceAndPath("homebase", "harvesting");

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var level = (net.minecraft.server.level.ServerLevel) player.level();
        var enchantRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var newEnchantHolder = enchantRegistry.get(HarvestingEnchantment.KEY).orElse(null);

        if (newEnchantHolder == null) {
            LOGGER.warn("[Hearth] Could not find aegis_hearth:harvesting in registry — migration skipped.");
            return;
        }

        List<ItemStack> allItems = new ArrayList<>();
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            allItems.add(inv.getItem(i));
        }

        int migrated = 0;
        for (ItemStack stack : allItems) {
            if (migrate(stack, newEnchantHolder)) migrated++;
        }

        if (migrated > 0) {
            LOGGER.info("[Hearth] Migrated {} item(s) for {} from homebase:harvesting to aegis_hearth:harvesting.",
                    migrated, player.getName().getString());
        }
    }

    private boolean migrate(ItemStack stack,
                            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> newEnchant) {
        if (stack.isEmpty()) return false;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        int level = 0;
        boolean found = false;

        for (var entry : enchantments.entrySet()) {
            var keyOpt = entry.getKey().unwrapKey();
            if (keyOpt.isPresent() && keyOpt.get().identifier().equals(OLD_KEY)) {
                level = entry.getIntValue();
                found = true;
                break;
            }
        }

        if (!found) return false;

        final int enchLevel = level;
        var mutable = new ItemEnchantments.Mutable(enchantments);

        for (var holder : enchantments.keySet()) {
            var keyOpt = holder.unwrapKey();
            if (keyOpt.isPresent() && keyOpt.get().identifier().equals(OLD_KEY)) {
                mutable.set(holder, 0);
            }
        }
        mutable.set(newEnchant, enchLevel);

        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
        return true;
    }
}