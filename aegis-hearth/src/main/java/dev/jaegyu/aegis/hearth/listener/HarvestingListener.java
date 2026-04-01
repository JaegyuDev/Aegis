package dev.jaegyu.aegis.hearth.listener;

import dev.jaegyu.aegis.hearth.HearthConfig;
import dev.jaegyu.aegis.hearth.enchantments.HarvestingEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Random;
import java.util.Set;

public class HarvestingListener {

    private static final Random RANDOM = new Random();

    private static final Set<Block> HOE_BLOCKS = Set.of(
            Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS,
            Blocks.NETHER_WART, Blocks.COCOA, Blocks.SWEET_BERRY_BUSH,
            Blocks.PUMPKIN, Blocks.MELON, Blocks.SUGAR_CANE,
            Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN,
            Blocks.LARGE_FERN, Blocks.DEAD_BUSH, Blocks.MOSS_BLOCK,
            Blocks.SCULK, Blocks.WARPED_WART_BLOCK, Blocks.NETHER_WART_BLOCK,
            Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.SPONGE, Blocks.WET_SPONGE
    );

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!HearthConfig.SERVER.harvesting.get()) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        var level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        var enchantmentRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var harvestingHolder = enchantmentRegistry.get(HarvestingEnchantment.KEY).orElse(null);
        if (harvestingHolder == null) return;

        int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(harvestingHolder, tool);
        if (enchLevel <= 0) return;

        Block block = event.getState().getBlock();
        if (!HOE_BLOCKS.contains(block)) return;

        double bonusChance = HarvestingEnchantment.BONUS_PER_LEVEL * enchLevel;

        var drops = Block.getDrops(event.getState(), serverLevel,
                event.getPos(), serverLevel.getBlockEntity(event.getPos()), player, tool);

        for (ItemStack drop : drops) {
            if (RANDOM.nextDouble() < bonusChance) {
                Block.popResource(serverLevel, event.getPos(), drop.copy());
            }
        }
    }
}
