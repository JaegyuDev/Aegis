package dev.jaegyu.aegis.hearth;

import org.jspecify.annotations.Nullable;

/**
 * Stores the pack download URL received from Aegis: Bulwark during the
 * configuration handshake. Null if Bulwark is not installed on the server.
 * Must be cleared on disconnect to avoid stale URLs persisting across sessions.
 */
public final class BulwarkHandshakeCache {

    @Nullable
    private static String packDownloadUrl = null;

    private BulwarkHandshakeCache() {}

    public static void set(String url) {
        packDownloadUrl = url;
    }

    public static void clear() {
        packDownloadUrl = null;
    }

    @Nullable
    public static String get() {
        return packDownloadUrl;
    }
}
