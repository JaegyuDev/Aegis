package dev.jaegyu.aegis.bulwark;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BulwarkConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Server, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    public static class Server {

        public final ModConfigSpec.ConfigValue<String> packDownloadUrl;

        Server(ModConfigSpec.Builder builder) {
            builder.push("features").push("modcheck");

            packDownloadUrl = builder
                    .comment("URL sent to clients on connect, shown on the mod mismatch screen.")
                    .define("packDownloadUrl", "https://example.com/hbpack");

            builder.pop().pop();
        }
    }
}
