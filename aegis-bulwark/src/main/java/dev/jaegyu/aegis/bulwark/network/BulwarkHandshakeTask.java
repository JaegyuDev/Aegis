package dev.jaegyu.aegis.bulwark.network;

import dev.jaegyu.aegis.bulwark.BulwarkConfig;
import dev.jaegyu.aegis.common.network.BulwarkHandshakePayload;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;

import java.util.function.Consumer;

public record BulwarkHandshakeTask(ServerConfigurationPacketListener listener) implements ConfigurationTask {

    public static final Type TYPE = new Type(
            Identifier.fromNamespaceAndPath("aegis_bulwark", "handshake_task"));

    @Override
    public void start(Consumer<Packet<?>> sender) {
        if (listener.getConnection().isMemoryConnection()) {
            listener.finishCurrentTask(TYPE);
            return;
        }

        // Only send to NeoForge clients — vanilla clients won't have a handler for this payload.
        // Registered as optional so vanilla clients aren't rejected just for missing it.
        if (!listener.getConnectionType().isNeoForge()) {
            listener.finishCurrentTask(TYPE);
            return;
        }

        sender.accept(new BulwarkHandshakePayload(BulwarkConfig.SERVER.packDownloadUrl.get())
                .toVanillaClientbound());
        listener.finishCurrentTask(TYPE);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
