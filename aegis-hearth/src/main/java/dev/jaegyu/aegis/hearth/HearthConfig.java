package dev.jaegyu.aegis.hearth;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class HearthConfig {

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Server, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    public static class Server {

        // features.enchantments
        public final ModConfigSpec.BooleanValue harvesting;

        // features.mobs
        public final ModConfigSpec.BooleanValue creeperGriefingDisabled;
        public final ModConfigSpec.BooleanValue creeperFireIgnite;
        public final ModConfigSpec.BooleanValue returnToSenderLoot;

        // features.commands
        public final ModConfigSpec.BooleanValue homeCommand;

        // features.misc
        public final ModConfigSpec.BooleanValue anvilXpCapped;
        public final ModConfigSpec.BooleanValue heartFirework;

        Server(ModConfigSpec.Builder builder) {
            builder.push("features");

            builder.push("enchantments");
            harvesting = builder
                    .comment("Enable the Harvesting enchantment for hoes.")
                    .define("harvesting", true);
            builder.pop();

            builder.push("mobs");
            creeperGriefingDisabled = builder
                    .comment("Prevent creepers from destroying blocks when they explode.")
                    .define("disable_creeper_griefing", true);
            creeperFireIgnite = builder
                    .comment("Ignite creepers when they take fire or lava damage.")
                    .define("creeper_fire_ignite", true);
            returnToSenderLoot = builder
                    .comment("Ghasts killed by their own fireball drop bonus loot.")
                    .define("return_to_sender_loot", true);
            builder.pop();

            builder.push("commands");
            homeCommand = builder
                    .comment("Enable the /home command.")
                    .define("home", true);
            builder.pop();

            builder.push("misc");
            anvilXpCapped = builder
                    .comment("Cap anvil repair cost to prevent 'Too Expensive!'.")
                    .define("anvil_xp_capped", true);
            heartFirework = builder
                    .comment("Enable heart-shaped firework explosions.")
                    .define("heart_firework", true);
            builder.pop();

            builder.pop(); // features
        }
    }
}
