package dev.jaegyu.aegis.hearth.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.jaegyu.aegis.hearth.HearthConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;

public class HomeCommand {

    /*  This was added because I got bored walking back from little excursions.
     *  This needs abuse protection and a cost/cd.
     *
     *    - Jae, 02/24/26
     * */

    private final Map<UUID, Integer> pendingTeleports = new HashMap<>();

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                literal("home").executes(this::executeHome)
        );
    }

    private int executeHome(CommandContext<CommandSourceStack> ctx) {
        var source = ctx.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("You cannot use this command as console!"));
            return 0;
        }

        if (!HearthConfig.SERVER.homeCommand.get()) {
            player.sendSystemMessage(Component.literal("The home command is currently disabled."));
            return 0;
        }

        var respawnConfig = player.getRespawnConfig();
        if (respawnConfig == null) {
            player.sendSystemMessage(Component.literal("Cannot find your respawn location :("));
            player.sendSystemMessage(Component.literal("Make sure you've slept in a bed or used a respawn anchor!"));
            return 0;
        }

        UUID uuid = player.getUUID();
        pendingTeleports.put(uuid, 60); // 3 seconds = 60 ticks

        player.sendSystemMessage(Component.literal("Teleporting in 3 seconds. Hang on tight!"));

        var server = source.getServer();

        // Schedule via server tick loop — check every tick, fire at 0
        server.execute(new Runnable() {
            int ticksLeft = 60;

            @Override
            public void run() {
                if (!pendingTeleports.containsKey(uuid)) return; // cancelled by damage
                ticksLeft--;
                if (ticksLeft > 0) {
                    server.execute(this);
                    return;
                }
                pendingTeleports.remove(uuid);
                if (!player.isAlive() || player.hasDisconnected()) return;

                var targetLevel = server.getLevel(ServerPlayer.RespawnConfig.getDimensionOrDefault(respawnConfig));
                if (targetLevel == null) return;

                var pos = Vec3.atCenterOf(respawnConfig.respawnData().pos());
                player.teleportTo(targetLevel, pos.x, pos.y, pos.z,
                        java.util.Set.of(), player.getYRot(), player.getXRot(), true);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (pendingTeleports.remove(player.getUUID()) != null) {
            player.sendSystemMessage(Component.literal("Teleport cancelled due to damage :("));
        }
    }
}