package dev.jaegyu.aegis.hearth.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.jaegyu.aegis.hearth.HearthConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HearthCommand {

    // Registry of all config keys → their BooleanValue, built once at construction.
    // Ordered so /hearth config list output is stable.
    private final Map<String, ModConfigSpec.BooleanValue> configKeys = new LinkedHashMap<>();

    public HearthCommand() {
        var s = HearthConfig.SERVER;
        configKeys.put("features.enchantments.harvesting",      s.harvesting);
        configKeys.put("features.mobs.disable_creeper_griefing", s.creeperGriefingDisabled);
        configKeys.put("features.mobs.creeper_fire_ignite",      s.creeperFireIgnite);
        configKeys.put("features.mobs.return_to_sender_loot",    s.returnToSenderLoot);
        configKeys.put("features.commands.home",                 s.homeCommand);
        configKeys.put("features.misc.anvil_xp_capped",         s.anvilXpCapped);
        configKeys.put("features.misc.heart_firework",           s.heartFirework);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("hearth")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(literal("config")
                        .then(literal("list")
                            .executes(this::executeList))
                        .then(literal("set")
                            .then(argument("key", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    configKeys.keySet().forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .then(argument("value", StringArgumentType.string())
                                    .suggests((ctx, builder) -> {
                                        builder.suggest("true");
                                        builder.suggest("false");
                                        return builder.buildFuture();
                                    })
                                    .executes(this::executeSet)))))
        );
    }

    private int executeList(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("--- Aegis: Hearth Config ---"), false);

        String currentSection = null;
        for (var entry : configKeys.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("\\.");
            String section = parts.length > 1 ? parts[parts.length - 2] : "";

            if (!section.equals(currentSection)) {
                currentSection = section;
                final String s = section;
                src.sendSuccess(() -> Component.literal(" [" + s + "]"), false);
            }

            String property = parts[parts.length - 1];
            boolean value = entry.getValue().get();
            src.sendSuccess(() -> Component.literal("   " + property + " = " + value), false);
        }
        return 1;
    }

    private int executeSet(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        String key = StringArgumentType.getString(ctx, "key");
        String rawValue = StringArgumentType.getString(ctx, "value");

        if (!rawValue.equalsIgnoreCase("true") && !rawValue.equalsIgnoreCase("false")) {
            src.sendFailure(Component.literal("Value must be true or false."));
            return 0;
        }

        var configValue = configKeys.get(key);
        if (configValue == null) {
            src.sendFailure(Component.literal("Unknown config key: " + key));
            return 0;
        }

        boolean newValue = Boolean.parseBoolean(rawValue);
        configValue.set(newValue);
        configValue.save();

        src.sendSuccess(() -> Component.literal("Set " + key + " to " + newValue), true);
        return 1;
    }
}
