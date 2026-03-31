package dev.jaegyu.aegis.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BulwarkHandshakePayload(String packDownloadUrl) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BulwarkHandshakePayload> TYPE =
            new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath("aegis_bulwark", "handshake"));

    public static final StreamCodec<ByteBuf, BulwarkHandshakePayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    BulwarkHandshakePayload::new,
                    BulwarkHandshakePayload::packDownloadUrl);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}