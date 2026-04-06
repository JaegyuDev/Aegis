// aegis-hearth/src/main/java/dev/jaegyu/aegis/hearth/Hearth.java
package dev.jaegyu.aegis.hearth;

import dev.jaegyu.aegis.common.network.BulwarkHandshakePayload;
import dev.jaegyu.aegis.hearth.commands.HearthCommand;
import dev.jaegyu.aegis.hearth.commands.HomeCommand;
import dev.jaegyu.aegis.hearth.datagen.HearthDatagen;
import dev.jaegyu.aegis.hearth.listener.*;
import dev.jaegyu.aegis.hearth.listener.migrations.HarvestingKeyMigration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod("aegis_hearth")
public class Hearth {

    public Hearth(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, HearthConfig.SERVER_SPEC);

        modBus.addListener(this::onRegisterPayloads);
        modBus.register(new HearthDatagen());

        var homeCommand = new HomeCommand();
        var hearthCommand = new HearthCommand();

        NeoForge.EVENT_BUS.register(new CreeperListener());
        NeoForge.EVENT_BUS.register(new ReturnToSenderListener());
        NeoForge.EVENT_BUS.register(new AnvilListener());
        NeoForge.EVENT_BUS.register(new HarvestingListener());
        NeoForge.EVENT_BUS.register(new ArmorStandListener());
        NeoForge.EVENT_BUS.register(new HarvestingKeyMigration());
        NeoForge.EVENT_BUS.register(new HeartFireworkListener());
        NeoForge.EVENT_BUS.register(homeCommand);
        NeoForge.EVENT_BUS.register(hearthCommand);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("aegis_bulwark").optional();
        registrar.configurationToClient(
                BulwarkHandshakePayload.TYPE,
                BulwarkHandshakePayload.STREAM_CODEC,
                (payload, ctx) -> BulwarkHandshakeCache.set(payload.packDownloadUrl())
        );
    }
}