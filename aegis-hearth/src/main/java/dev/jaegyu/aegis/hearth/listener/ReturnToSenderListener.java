package dev.jaegyu.aegis.hearth.listener;

import dev.jaegyu.aegis.hearth.HearthConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ReturnToSenderListener {

    /*
     *  Ghasts killed by their own reflected fireballs drop bonus loot.
     *
     *    - Jae, 02/24/26
     */

    @SubscribeEvent
    public void onGhastDeath(LivingDropsEvent event) {
        if (!HearthConfig.SERVER.returnToSenderLoot.get()) return;
        if (!(event.getEntity() instanceof Ghast ghast)) return;

        var lastDamage = ghast.getLastDamageSource();
        if (lastDamage == null) return;

        var directEntity = lastDamage.getDirectEntity();
        if (!(directEntity instanceof Fireball fireball)) return;
        if (!(fireball.getOwner() instanceof Ghast)) return;

        event.getDrops().clear();

        Random random = ThreadLocalRandom.current();
        int gunpowder = random.nextInt(3) + random.nextInt(4);
        int tears = random.nextInt(2) + random.nextInt(4);

        var pos = ghast.position();
        var level = ghast.level();

        if (gunpowder > 0) {
            event.getDrops().add(new ItemEntity(level, pos.x, pos.y, pos.z,
                    new ItemStack(Items.GUNPOWDER, gunpowder)));
        }
        if (tears > 0) {
            event.getDrops().add(new ItemEntity(level, pos.x, pos.y, pos.z,
                    new ItemStack(Items.GHAST_TEAR, tears)));
        }
    }
}