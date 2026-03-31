package dev.jaegyu.aegis.bulwark;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

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
        public final ModConfigSpec.ConfigValue<List<? extends String>> requiredMods;

        Server(ModConfigSpec.Builder builder) {
            builder.push("features").push("modcheck");

            packDownloadUrl = builder
                    .comment("URL shown to players who connect with an incompatible mod list.")
                    .define("packDownloadUrl", "https://example.com/hbpack");

            requiredMods = builder
                    .comment("Mod IDs that must be present on the client. Empty = check disabled.")
                    .defineListAllowEmpty("requiredMods", List.of(), String.class::isInstance);

            builder.pop().pop();
        }
    }
}