package dev.jaegyu.aegis.hearth.listener;

import dev.jaegyu.aegis.hearth.HearthConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class AnvilListener {

    private static final int MAX_COST = 40;
    private static final int CAPPED_COST = 32;

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!HearthConfig.SERVER.anvilXpCapped.get()) return;
        if (event.getXpCost() > MAX_COST) {
            event.setXpCost(CAPPED_COST);
        }
    }
}