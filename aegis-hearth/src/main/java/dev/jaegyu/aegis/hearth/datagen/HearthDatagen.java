// aegis-hearth/src/main/java/dev/jaegyu/aegis/hearth/datagen/HearthDatagen.java
package dev.jaegyu.aegis.hearth.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class HearthDatagen {

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                true,
                new HeartFireworkRecipeProvider.Runner(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider()
                )
        );
    }
}