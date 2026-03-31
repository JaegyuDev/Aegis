package dev.jaegyu.aegis.hearth;

import dev.jaegyu.aegis.common.network.BulwarkHandshakePayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod("aegis_hearth")
public class Hearth {

    public Hearth(IEventBus modBus, ModContainer container) {
        modBus.addListener(this::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(this::onDisconnect);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("aegis_bulwark").optional();
        registrar.configurationToClient(
                BulwarkHandshakePayload.TYPE,
                BulwarkHandshakePayload.STREAM_CODEC,
                (payload, context) -> BulwarkHandshakeCache.set(payload.packDownloadUrl())
        );
    }

    private void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        BulwarkHandshakeCache.clear();
    }
}
