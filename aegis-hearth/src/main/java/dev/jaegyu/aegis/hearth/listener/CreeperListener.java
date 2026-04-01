package dev.jaegyu.aegis.hearth.listener;

import dev.jaegyu.aegis.hearth.HearthConfig;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.level.Explosion;
import net.neoforged.neoforge.event.level.ExplosionEvent;

public class CreeperListener {

    @SubscribeEvent
    public void onCreeperFireDamage(LivingDamageEvent.Pre event) {
        if (!HearthConfig.SERVER.creeperFireIgnite.get()) return;
        if (!(event.getEntity() instanceof Creeper creeper)) return;

        var source = event.getSource();
        if (!source.is(DamageTypes.IN_FIRE)
                && !source.is(DamageTypes.ON_FIRE)
                && !source.is(DamageTypes.LAVA)) return;

        if (!creeper.isIgnited()) {
            creeper.ignite();
        }
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!HearthConfig.SERVER.creeperGriefingDisabled.get()) return;

        Explosion explosion = event.getExplosion();
        if (!(explosion.getDirectSourceEntity() instanceof Creeper)) return;

        event.getAffectedBlocks().clear();
    }
}
